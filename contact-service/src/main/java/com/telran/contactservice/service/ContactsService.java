package com.telran.contactservice.service;

import com.goodquestion.edutrek_server.error.DatabaseException.DatabaseAddingException;
import com.goodquestion.edutrek_server.error.DatabaseException.DatabaseDeletingException;
import com.goodquestion.edutrek_server.error.ShareException;
import com.goodquestion.edutrek_server.modules.branch.persistence.BranchRepository;
import com.goodquestion.edutrek_server.modules.contacts.dto.ContactSearchDto;
import com.goodquestion.edutrek_server.modules.contacts.dto.ContactsDataDto;
import com.goodquestion.edutrek_server.modules.contacts.persistence.AbstractContacts;
import com.goodquestion.edutrek_server.modules.contacts.persistence.archive.ContactArchiveEntity;
import com.goodquestion.edutrek_server.modules.contacts.persistence.archive.ContactsArchiveRepository;
import com.goodquestion.edutrek_server.modules.contacts.persistence.current.ContactsEntity;
import com.goodquestion.edutrek_server.modules.contacts.persistence.current.ContactsRepository;
import com.goodquestion.edutrek_server.modules.course.persistence.CourseRepository;
import com.goodquestion.edutrek_server.modules.log.service.LogService;
import com.goodquestion.edutrek_server.modules.statuses.persistence.StatusEntity;
import com.goodquestion.edutrek_server.modules.statuses.persistence.StatusRepository;
import com.goodquestion.edutrek_server.modules.students.dto.FoundEntitiesDto;
import com.goodquestion.edutrek_server.modules.students.dto.StudentsFromContactDataDto;
import com.goodquestion.edutrek_server.modules.students.persistence.AbstractStudent;


import com.goodquestion.edutrek_server.modules.students.persistence.archive.StudentsArchiveRepository;
import com.goodquestion.edutrek_server.modules.students.persistence.current.StudentEntity;
import com.goodquestion.edutrek_server.modules.students.persistence.current.StudentsRepository;

import static com.goodquestion.edutrek_server.utility_service.CommonUtilityMethods.checkStatusCourseBranch;
import static com.goodquestion.edutrek_server.utility_service.SearchUtilityMethods.*;

import com.goodquestion.edutrek_server.modules.students.service.StudentsService;
import com.goodquestion.edutrek_server.utility_service.CommonUtilityMethods;
import com.goodquestion.edutrek_server.utility_service.logging.Loggable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.goodquestion.edutrek_server.error.ShareException.ContactAlreadyExistsException;
import static com.goodquestion.edutrek_server.error.ShareException.ContactNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactsService {

    private final ContactsArchiveRepository contactArchiveRepository;
    private final StudentsArchiveRepository studentArchiveRepository;
    private final ContactsRepository contactRepository;
    private final StudentsRepository studentRepository;
    private final StatusRepository statusRepository;
    private final BranchRepository branchRepository;
    private final CourseRepository courseRepository;
    private final LogService logService;



    public ContactSearchDto getAll(int page, int pageSize, String search, Integer statusId, UUID courseId) {
        StatusEntity status = statusId == null ? null : statusRepository.findById(statusId).orElse(null);
        Pageable pageable = PageRequest.of(page, pageSize);
        if (status != null && status.getStatusName().equalsIgnoreCase("Archive")) {
            FoundEntitiesDto foundContactArchive = findContactsAndStudents(pageable, search, statusId, courseId, contactArchiveRepository, studentArchiveRepository, page, pageSize);
            List<AbstractContacts> foundContacts = foundContactArchive.getFoundContacts();
            return new ContactSearchDto(foundContacts, page, pageSize, foundContacts.size());
        } else if (status != null && status.getStatusName().equalsIgnoreCase("Student")) {
            FoundEntitiesDto foundStudentEntities = findStudents(pageable, search, statusId, null, courseId, studentRepository);
            List<AbstractStudent> foundStudents = foundStudentEntities.getFoundStudents();
            if (foundStudents.size() < pageSize) {
                FoundEntitiesDto foundStudentArchiveEntities = findStudents(PageRequest.of(page, pageSize - foundStudents.size()), search, statusId, null, courseId, studentArchiveRepository);
                List<AbstractStudent> foundStudentArchive = foundStudentArchiveEntities.getFoundStudents();
                if (!foundStudentArchive.isEmpty())
                    foundStudents.addAll(foundStudentArchive);
            }
            List<? extends AbstractContacts> contactFromStudents = foundStudents.stream().map(AbstractContacts::new).collect(Collectors.toList());
            return new ContactSearchDto(contactFromStudents, page, pageSize, contactFromStudents.size());
        } else {
            FoundEntitiesDto foundContactDto = findContactsAndStudents(pageable, search, statusId, courseId, contactRepository, studentRepository, page, pageSize);
            List<AbstractContacts> foundContacts = foundContactDto.getFoundContacts();
            if (foundContacts.size() < pageSize) {
                FoundEntitiesDto foundContactArchiveEntities = findContacts(PageRequest.of(page, pageSize - foundContacts.size()), search, statusId, courseId, contactArchiveRepository);
                List<AbstractContacts> foundContactsArchive = foundContactArchiveEntities.getFoundContacts();
                if (!foundContactsArchive.isEmpty()) {
                    foundContacts.addAll(foundContactsArchive);
                }
            }
            if (foundContacts.size() < pageSize)
                addStudents(search, statusId, courseId, studentArchiveRepository, page, pageSize, foundContacts);
            return new ContactSearchDto(foundContacts, page, pageSize, foundContacts.size());
        }
    }

    public AbstractContacts getById(UUID id) {
        return contactRepository.getByContactId(id).or(() -> contactArchiveRepository.findById(id)).orElseThrow(() -> new ContactNotFoundException(id.toString()));
    }

    @Transactional
    public void addEntity(ContactsDataDto contactData) {
        checkStatusCourseBranch(contactData.getBranchId(), contactData.getTargetCourseId(), contactData.getStatusId(), branchRepository, courseRepository,statusRepository);
        if (!contactRepository.existsByPhoneOrEmail(contactData.getPhone(), contactData.getEmail())) {
            ContactsEntity newEntity = new ContactsEntity(contactData.getContactName(), contactData.getPhone(), contactData.getEmail(), contactData.getStatusId(), contactData.getBranchId(), contactData.getTargetCourseId(), contactData.getComment());
            try {
                contactRepository.save(newEntity);
                logService.add(newEntity.getContactId(), "New contact created ");
            } catch (Exception e) {
                throw new DatabaseAddingException(e.getMessage());
            }
        } else
            throw new ContactAlreadyExistsException(contactData.getPhone(), contactData.getEmail());
    }

    @Transactional
    public void deleteById(UUID id) {
        if (!contactRepository.existsById(id)) throw new ContactNotFoundException(id.toString());
        logService.deleteById(id);
        try {
            contactRepository.deleteById(id);
        } catch (Exception e) {
            throw new DatabaseDeletingException(e.getMessage());
        }
    }

    @Transactional
    public void updateById(UUID id, ContactsDataDto contactData) {
        checkStatusCourseBranch(contactData.getBranchId(), contactData.getTargetCourseId(), contactData.getStatusId(), branchRepository, courseRepository,statusRepository);
        AbstractContacts entity = contactRepository.getByContactId(id).or(() -> contactArchiveRepository.findById(id)).orElseThrow(() -> new ContactNotFoundException(id.toString()));
        StatusEntity status = statusRepository.findById(contactData.getStatusId()).orElse(null);
        updateEntity(contactData, entity);
        if (entity instanceof ContactArchiveEntity && status != null && !status.getStatusName().equals("Archive")) {
            contactArchiveRepository.deleteById(id);
            contactRepository.save(new ContactsEntity(entity));
        }
        logService.add(id, "New contact created ");
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

    @Transactional
    public void moveToArchiveById(UUID id, String reason) {
        ContactsEntity contact = contactRepository.findById(id).orElseThrow(() -> new ContactNotFoundException(id.toString()));
        int statusId = statusRepository.findStatusEntityByStatusName("Archive").getStatusId();
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

    @Transactional
    public void promoteContactToStudentById(UUID id, StudentsFromContactDataDto studentData) {
        ContactsEntity contact = contactRepository.findById(id).orElseThrow(() -> new ContactNotFoundException(id.toString()));
        if (!studentRepository.existsById(id)) {
            int statusId = statusRepository.findStatusEntityByStatusName("Student").getStatusId();
            contact.setStatusId(statusId);
            try {
                studentRepository.save(new StudentEntity(contact, studentData));
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
}
