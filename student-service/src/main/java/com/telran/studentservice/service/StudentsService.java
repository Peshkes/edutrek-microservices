package com.telran.studentservice.service;

import com.telran.studentservice.dto.*;
import com.telran.studentservice.error.DatabaseException.DatabaseAddingException;
import com.telran.studentservice.error.DatabaseException.DatabaseDeletingException;
import com.telran.studentservice.error.Exceptions.BranchNotFoundException;
import com.telran.studentservice.error.Exceptions.CourseNotFoundException;
import com.telran.studentservice.error.Exceptions.StatusNotFoundException;
import com.telran.studentservice.error.Exceptions.StudentNotFoundException;
import com.telran.studentservice.feign.*;
import com.telran.studentservice.logging.Loggable;
import com.telran.studentservice.persistence.AbstractStudent;
import com.telran.studentservice.persistence.archive.StudentsArchiveEntity;
import com.telran.studentservice.persistence.archive.StudentsArchiveRepository;
import com.telran.studentservice.persistence.current.StudentEntity;
import com.telran.studentservice.persistence.current.StudentsRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.telran.studentservice.persistence.StudentsFilterSpecifications.getStudentSpecifications;

@Service
@RequiredArgsConstructor

public class StudentsService {

    private final StudentsRepository repository;
    private final ContactFeignClient contactFeignClient;
    private final StudentsArchiveRepository archiveRepository;
    private final LogFeignClient logFeignClient;
    private final StatusFeignClient statusFeignClient;
    private final BranchFeignClient branchFeignClient;
    private final CourseFeignClient courseFeignClient;
    private final PaymentsFeignClient paymentsFeignClient;

    @Loggable
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public StudentSearchDto getAll(int page, int pageSize, String search, Integer statusId, UUID groupId, UUID courseId) {
        String statusName = statusId == null ? null : statusFeignClient.getStatusById(statusId).getStatusName();
        Pageable pageable = PageRequest.of(page, pageSize);
        List<AbstractStudent> foundStudents;
        if (statusName != null && statusName.equalsIgnoreCase("Archive")) {
            foundStudents = findStudents( pageable, search, statusId, groupId, courseId, false);
        } else {
            foundStudents = findStudents(pageable, search, statusId, groupId, courseId, true);
            if (foundStudents.size() < pageSize) {
                List<AbstractStudent> foundStudentsArchive = findStudents(PageRequest.of(page, pageSize - foundStudents.size()), search, statusId, groupId, courseId, false);
                if (!foundStudentsArchive.isEmpty())
                    foundStudents.addAll(foundStudentsArchive);
            }
        }
        return new StudentSearchDto(foundStudents, page, pageSize, foundStudents.size());
    }

    @Loggable
    public AbstractStudent getById(UUID id) {
        return repository.getByStudentId(id).or(() -> archiveRepository.findById(id)).orElseThrow(() -> new StudentNotFoundException(id.toString()));
    }

    @Loggable
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Loggable
    @Transactional
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public void addEntity(StudentsDataDto studentData) {
        checkStatusCourseBranch(studentData.getBranchId(), studentData.getTargetCourseId(), studentData.getStatusId());
        if (!repository.existsByPhoneOrEmail(studentData.getPhone(), studentData.getEmail())) {
            int statusId = statusFeignClient.findStatusEntityByStatusName("Student").getStatusId();
            AbstractContactsDto contact = contactFeignClient.findByPhoneOrEmail(studentData.getPhone(), studentData.getEmail());
            if (contact == null) {
                repository.save(new StudentEntity(
                        studentData, statusId));
            } else {
                contactFeignClient.promoteContactToStudentById(contact.getContactId(), new StudentsFromContactDataDto(studentData.getFullPayment(), studentData.isDocumentsDone()));
            }
        }
    }

    @Loggable
    @Transactional
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public void deleteById(UUID id) {
        if (!repository.existsById(id)) throw new StudentNotFoundException(id.toString());
        logFeignClient.deleteById(id);
        try {
            repository.deleteById(id);
        } catch (Exception e) {
            throw new DatabaseDeletingException(e.getMessage());
        }
    }

    @Loggable
    @Transactional
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public void updateById(UUID id, StudentsDataDto studentData) {
        checkStatusCourseBranch(studentData.getBranchId(), studentData.getTargetCourseId(), studentData.getStatusId());
        AbstractStudent entity = repository.getByStudentId(id).or(() -> archiveRepository.findById(id)).orElseThrow(() -> new StudentNotFoundException(id.toString()));
        updateEntity(studentData, entity);
    }

    private <T extends AbstractStudent> void updateEntity(StudentsDataDto studentData, T entity) {
        int statusId = statusFeignClient.findStatusEntityByStatusName("Student").getStatusId();
        entity.setContactName(studentData.getContactName());
        entity.setPhone(studentData.getPhone());
        entity.setEmail(studentData.getEmail());
        entity.setComment(studentData.getComment());
        entity.setStatusId(statusId);
        entity.setBranchId(studentData.getBranchId());
        entity.setTargetCourseId(studentData.getTargetCourseId());
        entity.setFullPayment(studentData.getFullPayment());
        entity.setDocumentsDone(studentData.isDocumentsDone());
    }

    @Loggable
    @Transactional
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public void moveToArchiveById(UUID id, String reason) {
        StudentEntity student = repository.findById(id).orElseThrow(() -> new StudentNotFoundException(id.toString()));
        int statusId = statusFeignClient.findStatusEntityByStatusName("Archive").getStatusId();
        student.setStatusId(statusId);
        StudentsArchiveEntity studentArchEntity = new StudentsArchiveEntity(student, reason);
        try {
            List<AbstractPaymentInformationDto> payments = paymentsFeignClient.getPaymentByStudentId(id).paymentsInfo();
            archiveRepository.save(studentArchEntity);
            payments.forEach(p -> paymentsFeignClient.moveToArchiveById(p.getPaymentId()));
        } catch (Exception e) {
            throw new DatabaseAddingException(e.getMessage());
        }
        try {
            repository.deleteById(id);
        } catch (Exception e) {
            throw new DatabaseDeletingException(e.getMessage());
        }

    }

    @Loggable
    @Transactional
    public void graduateById(UUID id) {
        moveToArchiveById(id, "Finished course and graduated");
    }

    @Loggable
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
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
    public <S extends AbstractStudent> List<AbstractStudent> findStudents(Pageable pageable, String search, Integer statusId, UUID group_id, UUID courseId, boolean isCurrentRepository) {
        Specification<S> studentSpecs = getStudentSpecifications(search, statusId, group_id, courseId);
        Page<? extends AbstractStudent> pageContactEntity = isCurrentRepository? repository.findAll((Specification<StudentEntity>) studentSpecs, pageable): archiveRepository.findAll((Specification<StudentsArchiveEntity>) studentSpecs, pageable);
        return (List<AbstractStudent>) pageContactEntity.getContent();
    }
}
