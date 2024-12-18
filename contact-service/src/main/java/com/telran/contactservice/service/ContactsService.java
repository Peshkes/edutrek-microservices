package com.telran.contactservice.service;

import com.telran.contactservice.dto.*;
import com.telran.contactservice.error.DatabaseException.*;
import com.telran.contactservice.error.Exception.*;
import com.telran.contactservice.feign.StudentFeignClient;
import com.telran.contactservice.logging.Loggable;
import com.telran.contactservice.persistence.AbstractContacts;
import com.telran.contactservice.persistence.ContactsFilterSpecifications;
import com.telran.contactservice.persistence.IContactRepository;
import com.telran.contactservice.persistence.archive.ContactArchiveEntity;
import com.telran.contactservice.persistence.archive.ContactsArchiveRepository;
import com.telran.contactservice.persistence.current.ContactsEntity;
import com.telran.contactservice.persistence.current.ContactsRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ContactsService {

    private final ContactsRepository contactRepository;
    private final ContactsArchiveRepository contactArchiveRepository;
    private final ContactRabbitProducer rabbitProducer;
    private final StudentFeignClient studentFeignClient;
    private static final boolean IS_CURRENT_STUDENT_REPOSITORY = true;
    private static final boolean IS_ARCHIVE_STUDENT_REPOSITORY = false;

    @Loggable
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public ContactSearchDto getAll(int page, int pageSize, String search, Integer statusId, UUID courseId) {
        String statusName = statusId == null ? null : rabbitProducer.sendGetStatusNameById(statusId);
        Pageable pageable = PageRequest.of(page, pageSize);
        if (statusName != null && statusName.equalsIgnoreCase("Archive")) {
            List<AbstractContacts> foundContacts = findContactsAndStudents(pageable, search, statusId, courseId, contactArchiveRepository, IS_ARCHIVE_STUDENT_REPOSITORY, pageSize);
            return new ContactSearchDto(foundContacts, page, pageSize, foundContacts.size());
        } else if (statusName != null && statusName.equalsIgnoreCase("Student")) {
            List<AbstractStudentDto> foundStudents = studentFeignClient.findStudents(new FindStudentsDto(pageable, search, statusId, null, courseId, true));
            if (foundStudents.size() < pageSize) {
                List<AbstractStudentDto> foundStudentArchive = studentFeignClient.findStudentsForContacts(new FindStudentsForContactsDto(pageSize - foundStudents.size(), search, statusId, null, courseId, false));
                if (!foundStudentArchive.isEmpty())
                    foundStudents.addAll(foundStudentArchive);
            }
            List<? extends AbstractContacts> contactFromStudents = foundStudents.stream().map(ContactsEntity::new).collect(Collectors.toList());
            return new ContactSearchDto(contactFromStudents, page, pageSize, contactFromStudents.size());
        } else {
            List<AbstractContacts> foundContacts = findContactsAndStudents(pageable, search, statusId, courseId, contactRepository, IS_CURRENT_STUDENT_REPOSITORY, pageSize);
            if (foundContacts.size() < pageSize) {
                List<AbstractContacts> foundContactsArchive = findContacts(PageRequest.of(page, pageSize - foundContacts.size()), search, statusId, courseId, contactArchiveRepository);
                if (!foundContactsArchive.isEmpty()) {
                    foundContacts.addAll(foundContactsArchive);
                }
            }
            int size = foundContacts.size();
            if (size < pageSize)
                addStudents(search, statusId, courseId, IS_ARCHIVE_STUDENT_REPOSITORY, pageSize - size);
            return new ContactSearchDto(foundContacts, page, pageSize, foundContacts.size());
        }
    }

    public AbstractContacts getById(UUID id) {
        return contactRepository.getByContactId(id).or(() -> contactArchiveRepository.findById(id)).orElseThrow(() -> new ContactNotFoundException(id.toString()));
    }

    public boolean existsById(UUID id) {
        return contactRepository.existsById(id);
    }

    public AbstractContacts findByPhoneOrEmail(String phone, String email) {
        return contactRepository.findByPhoneOrEmail(phone, email).orElseThrow(() -> new ContactNotFoundException(email));
    }

    public boolean existsByPhoneOrEmail(String phone, String email) {
        return contactRepository.existsByPhoneOrEmail(phone, email);
    }

    @Loggable
    @Transactional
    public void addEntity(@Valid ContactsDataDto contactData) {
        List<String> properties = checkStatusCourseBranch(contactData.getBranchId(), contactData.getTargetCourseId(), contactData.getStatusId());
        if (!contactRepository.existsByPhoneOrEmail(contactData.getPhone(), contactData.getEmail())) {
            ContactsEntity newEntity = new ContactsEntity(contactData.getContactName(), contactData.getPhone(), contactData.getEmail(), contactData.getStatusId(), contactData.getBranchId(), contactData.getTargetCourseId(), contactData.getComment());
            try {
                contactRepository.save(newEntity);
                String log = contactData.getLogText();
                rabbitProducer.sendAddLog(
                        newEntity.getContactId(),
                        log != null ? log : "New contact added. Status: " + properties.get(0) + ", course: " + properties.get(1) + ", branch: " + properties.get(2));
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
        if (!contactRepository.existsById(id)) {
            if (!contactArchiveRepository.existsById(id))
                throw new ContactNotFoundException(id.toString());
            else {
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
        rabbitProducer.sendDeleteLogById(id);
    }

    @Loggable
    @Transactional
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public void updateById(UUID id, @Valid ContactsDataDto contactData) {
        List<String> properties = checkStatusCourseBranch(contactData.getBranchId(), contactData.getTargetCourseId(), contactData.getStatusId());
        AbstractContacts entity = contactRepository.getByContactId(id).or(() -> contactArchiveRepository.findById(id)).orElseThrow(() -> new ContactNotFoundException(id.toString()));
        List<String> updates = updateEntity(contactData, entity);
        if (entity.getStatusId() != contactData.getStatusId() && entity instanceof ContactArchiveEntity) {
            contactArchiveRepository.deleteById(id);
            contactRepository.save(new ContactsEntity(entity));
        }
        String log = contactData.getLogText();
        if (!updates.isEmpty())
            rabbitProducer.sendAddLog(id, log != null ? log : "Contact updated. Updated info: " + updates);
    }

    private <T extends AbstractContacts> List<String> updateEntity(ContactsDataDto contactData, T entity) {
        List<String> updates = new ArrayList<>();

        String name = contactData.getContactName();
        if (!entity.getContactName().equals(name)) {
            entity.setContactName(name);
            updates.add("name");
        }

        String phone = contactData.getPhone();
        if (!entity.getPhone().equals(phone)) {
            entity.setPhone(phone);
            updates.add("phone");
        }

        String email = contactData.getEmail();
        if (!entity.getPhone().equals(email)) {
            entity.setEmail(email);
            updates.add("email");
        }

        String comment = contactData.getComment();
        if (!entity.getComment().equals(comment)) {
            entity.setComment(comment);
            updates.add("comment");
        }

        int status = contactData.getStatusId();
        if (entity.getStatusId() != status) {
            entity.setStatusId(status);
            updates.add("status");
        }

        int branch = contactData.getBranchId();
        if (entity.getBranchId() != branch) {
            entity.setBranchId(branch);
            updates.add("branch");
        }

        UUID course = contactData.getTargetCourseId();
        if (!entity.getTargetCourseId().equals(course)) {
            entity.setTargetCourseId(course);
            updates.add("course");
        }
        return updates;
    }


    @Loggable
    @Transactional
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public void moveToArchiveById(UUID id, String reason) {
        ContactsEntity contact = contactRepository.findById(id).orElseThrow(() -> new ContactNotFoundException(id.toString()));
        int statusId = rabbitProducer.sendGetStatusIdByName("Archive");
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
        rabbitProducer.sendAddLog(
                id,
                "Contact archived. Reason: " + reason);
    }

    @Loggable
    @Transactional
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public void promoteContactToStudentById(@Valid StudentsFromContactDataDto studentData) {
        UUID id = studentData.getContactId();
        ContactsEntity contact = contactRepository.findById(id).orElseThrow(() -> new ContactNotFoundException(id.toString()));
        if (!studentFeignClient.existsById(id)) {
            contact.setStatusId(rabbitProducer.sendGetStatusIdByName("Student"));
            try {
                contactRepository.deleteById(id);
            } catch (Exception e) {
                throw new DatabaseDeletingException(e.getMessage());
            }
            try {
                studentFeignClient.promote(new StudentsDataDto(contact, studentData));
            } catch (Exception e) {
                throw new DatabaseAddingException(e.getMessage());
            }
            rabbitProducer.sendAddLog(id, "Contact " + contact.getContactName() + " promoted to student");
        }
    }

    @Loggable
    private List<String> checkStatusCourseBranch(Integer branchId, UUID targetCourseId, Integer statusId) {
        List<CompletableFuture<String>> list = new ArrayList<>();
        if (branchId != null) createFuture(branchId, list, rabbitProducer::sendGetBranchNameById);
        if (targetCourseId != null) createFuture(targetCourseId, list, rabbitProducer::sendGetCourseNameById);
        if (statusId != null) createFuture(statusId, list, rabbitProducer::sendGetStatusNameById);

        try {
            return CompletableFuture.allOf(list.toArray(new CompletableFuture[0]))
                    .thenApply(v -> list.stream().map(CompletableFuture::join).toList()).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error extracting data", e);
        }
    }

    private <T> void createFuture(T id, List<CompletableFuture<String>> list, Function<T, String> getFunction) {
        list.add(CompletableFuture.supplyAsync(() -> {
            String entity = getFunction.apply(id);
            if (entity == null) throw new EntityNotFoundException(String.valueOf(id));
            return entity;
        }));
    }


    @Loggable
    public static <C extends AbstractContacts, E extends IContactRepository<C>> List<AbstractContacts> findContacts(Pageable pageable, String search, Integer statusId, UUID courseId, E repository) {
        Specification<C> contactSpecs = new ContactsFilterSpecifications<C>().getSpecifications(search, statusId, courseId);
        return new ArrayList<>(repository.findAll(contactSpecs, pageable).getContent());
    }

    @Loggable
    private <C extends AbstractContacts, CR extends IContactRepository<C>> List<AbstractContacts> findContactsAndStudents(Pageable pageable, String search, Integer statusId, UUID courseId, CR contactsRepository, boolean isCurrentStudentRepository, int pageSize) {
        List<AbstractContacts> foundContact = findContacts(pageable, search, statusId, courseId, contactsRepository);
        int size = foundContact.size();
        if (size < pageSize)
            foundContact.addAll(addStudents(search, statusId, courseId, isCurrentStudentRepository, size));
        return foundContact;
    }

    @Loggable
    private List<? extends AbstractContacts> addStudents(String search, Integer statusId, UUID courseId, boolean isCurrentStudentRepository, int size) {
        List<AbstractStudentDto> foundStudents = studentFeignClient.findStudentsForContacts(new FindStudentsForContactsDto(size, search, statusId, null, courseId, isCurrentStudentRepository));
        if (!foundStudents.isEmpty())
            return foundStudents.stream().map(ContactsEntity::new).toList();
        return new ArrayList<>();
    }

    @Loggable
    public AbstractContacts findByPhoneOrEmailAndDelete(String phone, String email) {
        try {
            return contactRepository.deleteByPhoneOrEmail(phone, email);
        } catch (Exception e) {
            throw new DatabaseDeletingException(e.getMessage());
        }
    }
}
