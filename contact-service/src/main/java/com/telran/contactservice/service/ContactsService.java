package com.telran.contactservice.service;

import com.telran.contactservice.dto.*;
import com.telran.contactservice.error.DatabaseException.DatabaseAddingException;
import com.telran.contactservice.error.DatabaseException.DatabaseDeletingException;
import com.telran.contactservice.error.Exception.*;
import com.telran.contactservice.feign.*;
import com.telran.contactservice.logging.Loggable;
import com.telran.contactservice.persistence.AbstractContacts;
import com.telran.contactservice.persistence.ContactsFilterSpecifications;
import com.telran.contactservice.persistence.IContactRepository;
import com.telran.contactservice.persistence.archive.ContactArchiveEntity;
import com.telran.contactservice.persistence.archive.ContactsArchiveRepository;
import com.telran.contactservice.persistence.current.ContactsEntity;
import com.telran.contactservice.persistence.current.ContactsRepository;
import feign.FeignException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ContactsService {

    private final ContactsArchiveRepository contactArchiveRepository;
    private final ContactsRepository contactRepository;
    private final StudentFeignClient studentFeignClient;
    private final StatusFeignClient statusFeignClient;
    private final BranchFeignClient branchFeignClient;
    private final CourseFeignClient courseFeignClient;
    private final LogFeignClient logFeignClient;
    private static final boolean IS_CURRENT_STUDENT_REPOSITORY = true;
    private static final boolean IS_ARCHIVE_STUDENT_REPOSITORY = false;

    @Loggable
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public ContactSearchDto getAll(int page, int pageSize, String search, Integer statusId, UUID courseId) {
        String statusName = statusId == null ? null : statusFeignClient.getStatusById(statusId).getStatusName();
        Pageable pageable = PageRequest.of(page, pageSize);
        if (statusName != null && statusName.equalsIgnoreCase("Archive")) {
            List<AbstractContacts> foundContacts = findContactsAndStudents(pageable, search, statusId, courseId, contactArchiveRepository, IS_ARCHIVE_STUDENT_REPOSITORY, page, pageSize);
            return new ContactSearchDto(foundContacts, page, pageSize, foundContacts.size());
        } else if (statusName != null && statusName.equalsIgnoreCase("Student")) {
            List<AbstractStudentDto> foundStudents = studentFeignClient.findStudents(new FindStudentsDto(pageable, search, statusId, null, courseId, true));
            if (foundStudents.size() < pageSize) {
                List<AbstractStudentDto> foundStudentArchive = studentFeignClient.findStudents(new FindStudentsDto(PageRequest.of(page, pageSize - foundStudents.size()), search, statusId, null, courseId, false));
                if (!foundStudentArchive.isEmpty())
                    foundStudents.addAll(foundStudentArchive);
            }
            List<? extends AbstractContacts> contactFromStudents = foundStudents.stream().map(AbstractContacts::new).collect(Collectors.toList());
            return new ContactSearchDto(contactFromStudents, page, pageSize, contactFromStudents.size());
        } else {
            List<AbstractContacts> foundContacts = findContactsAndStudents(pageable, search, statusId, courseId, contactRepository, IS_CURRENT_STUDENT_REPOSITORY, page, pageSize);
            if (foundContacts.size() < pageSize) {
                List<AbstractContacts> foundContactsArchive = findContacts(PageRequest.of(page, pageSize - foundContacts.size()), search, statusId, courseId, contactArchiveRepository);
                if (!foundContactsArchive.isEmpty()) {
                    foundContacts.addAll(foundContactsArchive);
                }
            }
            if (foundContacts.size() < pageSize)
                addStudents(search, statusId, courseId, IS_ARCHIVE_STUDENT_REPOSITORY, page, pageSize, foundContacts);
            return new ContactSearchDto(foundContacts, page, pageSize, foundContacts.size());
        }
    }

    public AbstractContacts getById(UUID id) {
        return contactRepository.getByContactId(id).or(() -> contactArchiveRepository.findById(id)).orElseThrow(() -> new ContactNotFoundException(id.toString()));
    }

    public AbstractContacts findByPhoneOrEmail(String phone, String email) {
        return contactRepository.findByPhoneOrEmail(phone, email).orElseThrow(() -> new ContactNotFoundException(email));
    }

    @Loggable
    @Transactional
    public void addEntity(ContactsDataDto contactData) {
        checkStatusCourseBranch(contactData.getBranchId(), contactData.getTargetCourseId(), contactData.getStatusId());
        if (!contactRepository.existsByPhoneOrEmail(contactData.getPhone(), contactData.getEmail())) {
            ContactsEntity newEntity = new ContactsEntity(contactData.getContactName(), contactData.getPhone(), contactData.getEmail(), contactData.getStatusId(), contactData.getBranchId(), contactData.getTargetCourseId(), contactData.getComment());
            try {
                contactRepository.save(newEntity);
                logFeignClient.add(newEntity.getContactId(), "New contact created ");
            } catch (Exception e) {
                throw new DatabaseAddingException(e.getMessage());
            }
        } else
            throw new ContactAlreadyExistsException(contactData.getPhone(), contactData.getEmail());
    }

    @Loggable
    @Transactional
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public void deleteById(UUID id) {
        if (!contactRepository.existsById(id)){
            if(!contactArchiveRepository.existsById(id))
                throw new ContactNotFoundException(id.toString());
            else{
                try {
                    contactArchiveRepository.deleteById(id);
                } catch (Exception e) {
                    throw new DatabaseDeletingException(e.getMessage());
                }
            }
        }
        try {
            contactRepository.deleteById(id);
        } catch (Exception e) {
            throw new DatabaseDeletingException(e.getMessage());
        }
        logFeignClient.deleteById(id);
    }

    @Loggable
    @Transactional
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public void updateById(UUID id, @Valid ContactsDataDto contactData) {
        checkStatusCourseBranch(contactData.getBranchId(), contactData.getTargetCourseId(), contactData.getStatusId());
        AbstractContacts entity = contactRepository.getByContactId(id).or(() -> contactArchiveRepository.findById(id)).orElseThrow(() -> new ContactNotFoundException(id.toString()));
        String statusName = statusFeignClient.getStatusById(contactData.getStatusId()).getStatusName();
        updateEntity(contactData, entity);
        if (entity instanceof ContactArchiveEntity && statusName != null && !statusName.equals("Archive")) {
            contactArchiveRepository.deleteById(id);
            contactRepository.save(new ContactsEntity(entity));
        }
        logFeignClient.add(id, "New contact created ");
    }

    private <T extends AbstractContacts> void updateEntity(ContactsDataDto contactData, T entity) {
        entity.setContactName(contactData.getContactName());
        entity.setPhone(contactData.getPhone());
        entity.setEmail(contactData.getEmail());
        entity.setComment(contactData.getComment());
        entity.setStatusId(contactData.getStatusId());
        entity.setBranchId(contactData.getBranchId());
        entity.setTargetCourseId(contactData.getTargetCourseId());
    }

    @Loggable
    @Transactional
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public void moveToArchiveById(UUID id, String reason) {
        ContactsEntity contact = contactRepository.findById(id).orElseThrow(() -> new ContactNotFoundException(id.toString()));
        int statusId = statusFeignClient.findStatusEntityByStatusName("Archive").getStatusId();
        contact.setStatusId(statusId);
        ContactArchiveEntity contactArchEntity = new ContactArchiveEntity(contact, reason);
        try {
            contactRepository.deleteContactById(id);
        } catch (Exception e) {
            throw new DatabaseDeletingException(e.getMessage());
        }
        try {
            contactArchiveRepository.save(contactArchEntity);
        } catch (Exception e) {
            throw new DatabaseAddingException(e.getMessage());
        }
    }

    @Loggable
    @Transactional
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public void promoteContactToStudentById(UUID id, @Valid StudentsFromContactDataDto studentData) {
        ContactsEntity contact = contactRepository.findById(id).orElseThrow(() -> new ContactNotFoundException(id.toString()));
        if (!studentFeignClient.existsById(id)) {//TODO make one (if exists save)
            contact.setStatusId(statusFeignClient.findStatusEntityByStatusName("Student").getStatusId());
            try {
                studentFeignClient.save(new StudentsDataDto(contact, studentData));
            } catch (Exception e) {
                throw new DatabaseAddingException(e.getMessage());
            }
            try {
                contactRepository.deleteById(id);
            } catch (Exception e) {
                throw new DatabaseDeletingException(e.getMessage());
            }
        }
    }

    @Loggable
    private void checkStatusCourseBranch(int branchId, UUID targetCourseId, int statusId) {
        if (!branchFeignClient.existsById(branchId))
            throw new BranchNotFoundException(String.valueOf(branchId));
        if (!courseFeignClient.existsById(targetCourseId))
            throw new CourseNotFoundException(String.valueOf(targetCourseId));
        if (!statusFeignClient.existsById(statusId))
            throw new StatusNotFoundException(statusId);
    }


    @Loggable
    @SuppressWarnings("unchecked")
    public static <C extends AbstractContacts, E extends IContactRepository<C>> List<AbstractContacts> findContacts(Pageable pageable, String search, Integer statusId, UUID courseId, E repository) {
        Specification<C> contactSpecs = new ContactsFilterSpecifications<C>().getSpecifications(search, statusId, courseId);
        Page<? extends AbstractContacts> pageContactEntity = repository.findAll(contactSpecs, pageable);
        return (List<AbstractContacts>) pageContactEntity.getContent();
    }

    @Loggable
    private <C extends AbstractContacts, CR extends IContactRepository<C>> List<AbstractContacts> findContactsAndStudents(Pageable pageable, String search, Integer statusId, UUID courseId, CR contactsRepository, boolean isCurrentStudentRepository, int page, int pageSize) {
        List<AbstractContacts> foundContact = findContacts(pageable, search, statusId, courseId, contactsRepository);
        if (foundContact.size() < pageSize) {
            addStudents(search, statusId, courseId, isCurrentStudentRepository, page, pageSize, foundContact);
        }
        return foundContact;
    }

    @Loggable
    private void addStudents(String search, Integer statusId, UUID courseId, boolean isCurrentStudentRepository, int page, int pageSize, List<AbstractContacts> foundContact) {
        List<AbstractStudentDto> foundStudents = studentFeignClient.findStudents(new FindStudentsDto(PageRequest.of(page, pageSize - foundContact.size()), search, statusId, null, courseId, isCurrentStudentRepository));
        if (!foundStudents.isEmpty()) {
            List<? extends AbstractContacts> contactFromStudents = foundStudents.stream().map(AbstractContacts::new).toList();
            foundContact.addAll(contactFromStudents);
        }
    }

    @Loggable
    public AbstractContacts findByPhoneOrEmailAndDelete(String phone, String email) {
        try{
            return contactRepository.deleteByPhoneOrEmail(phone, email);
        }catch (Exception e) {
            throw new DatabaseDeletingException(e.getMessage());
        }
    }
}
