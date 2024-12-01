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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.telran.studentservice.persistence.StudentsFilterSpecifications.getStudentSpecifications;

@Service
@RequiredArgsConstructor

public class StudentsService {

    private static final boolean IS_CURRENT_REPOSITORY = true;
    private final StudentsRepository repository;
    private final ContactFeignClient contactFeignClient;
    private final StudentsArchiveRepository archiveRepository;
    private final LogFeignClient logFeignClient;
    private final StatusFeignClient statusFeignClient;
    private final BranchFeignClient branchFeignClient;
    private final CourseFeignClient courseFeignClient;
    private final PaymentsFeignClient paymentsFeignClient;
    private final GroupFeignClient groupFeignClient;

    @Loggable
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public StudentSearchDto getAll(int page, int pageSize, String search, Integer statusId, UUID groupId, UUID courseId) {
        String statusName = statusId == null ? null : statusFeignClient.getStatusById(statusId).getStatusName();
        Pageable pageable = PageRequest.of(page, pageSize);
        List<AbstractStudent> foundStudents;
        if (statusName != null && statusName.equalsIgnoreCase("Archive")) {
            foundStudents = findStudents(pageable, search, statusId, groupId, courseId, false);
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
        StatusCourseBranchNamesDto properties = checkStatusCourseBranch(studentData.getBranchId(), studentData.getTargetCourseId(), studentData.getStatusId());
        if (!repository.existsByPhoneOrEmail(studentData.getPhone(), studentData.getEmail())) {
            int statusId = statusFeignClient.findStatusEntityByStatusName("Student").getStatusId();
            try {
                AbstractStudent saved = repository.save(new StudentEntity(
                        studentData, statusId));
                String log = studentData.getLogText();
                logFeignClient.add(
                        saved.getStudentId(),
                        log != null ? log : " - New contact added. Status: " + properties.getStatusName() + ", course: " + properties.getCourseName() + ", branch: " + properties.getBranchName());
            } catch (Exception e) {
                throw new DatabaseAddingException(e.getMessage());
            }

            contactFeignClient.findByPhoneOrEmailAndDelete(studentData.getPhone(), studentData.getEmail());
        }
    }

    @Loggable
    @Transactional
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public void deleteById(UUID id) {
        if (!repository.existsById(id)) {
            if (!archiveRepository.existsById(id)) {
                throw new StudentNotFoundException(id.toString());
            } else {
                try {
                    groupFeignClient.deleteByStudentId(id, !IS_CURRENT_REPOSITORY);
                    paymentsFeignClient.deletePaymentByStudentId(id);
                    archiveRepository.deleteById(id);
                } catch (Exception e) {
                    throw new DatabaseDeletingException(e.getMessage());
                }
            }
        } else {
            try {
                groupFeignClient.deleteByStudentId(id, IS_CURRENT_REPOSITORY);
                paymentsFeignClient.deletePaymentByStudentId(id);
                repository.deleteById(id);
            } catch (Exception e) {
                throw new DatabaseDeletingException(e.getMessage());
            }
        }
        logFeignClient.deleteById(id);
    }

    @Loggable
    @Transactional
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public void updateById(UUID id, StudentsDataDto studentData) {
        checkStatusCourseBranch(studentData.getBranchId(), studentData.getTargetCourseId(), studentData.getStatusId());
        AbstractStudent entity = repository.getByStudentId(id).or(() -> archiveRepository.findById(id)).orElseThrow(() -> new StudentNotFoundException(id.toString()));
        List<String> updates = updateEntity(studentData, entity);
        String log = studentData.getLogText();
        logFeignClient.add(
                id,
                log != null ? log : " - Contact updated. Updated info: " + updates);
    }

    private <T extends AbstractStudent> List<String> updateEntity(StudentsDataDto studentData, T entity) {
        int statusId = statusFeignClient.findStatusEntityByStatusName("Student").getStatusId();
        List<String> updates = new ArrayList<>();

        String name = studentData.getContactName();
        if (!entity.getContactName().equals(name)) {
            entity.setContactName(name);
            updates.add("name");
        }

        String phone = studentData.getPhone();
        if (!entity.getPhone().equals(phone)) {
            entity.setPhone(phone);
            updates.add("phone");
        }

        String email = studentData.getEmail();
        if (!entity.getPhone().equals(email)) {
            entity.setPhone(email);
            updates.add("phone");
        }

        String comment = studentData.getComment();
        if (!entity.getComment().equals(comment)) {
            entity.setPhone(comment);
            updates.add("phone");
        }

        if (entity.getStatusId() != statusId) {
            entity.setStatusId(statusId);
            updates.add("status");
        }


        int branch = studentData.getBranchId();
        if (entity.getBranchId() != branch) {
            entity.setBranchId(branch);
            updates.add("branch");
        }

        UUID course = studentData.getTargetCourseId();
        if (!entity.getTargetCourseId().equals(course)) {
            entity.setTargetCourseId(course);
            updates.add("branch");
        }

        int fullPayment = studentData.getFullPayment();
        if (entity.getFullPayment() != fullPayment) {
            entity.setFullPayment(fullPayment);
            updates.add("payment amount");
        }

        boolean documentsDone = studentData.isDocumentsDone();
        if (entity.isDocumentsDone() != documentsDone) {
            entity.setDocumentsDone(documentsDone);
            updates.add("documents status");
        }
        return updates;
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
            archiveRepository.save(studentArchEntity);
            paymentsFeignClient.moveToArchiveById(id);
            groupFeignClient.archiveStudents(id);
        } catch (Exception e) {
            throw new DatabaseAddingException(e.getMessage());
        }
        try {
            repository.deleteById(id);
        } catch (Exception e) {
            throw new DatabaseDeletingException(e.getMessage());
        }
        logFeignClient.add(
                id,
                "Student archived. Reason: " + reason);
    }

    @Loggable
    @Transactional
    public void graduateById(UUID id) {
        moveToArchiveById(id, "Finished course and graduated");
    }

    @Loggable
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    private StatusCourseBranchNamesDto checkStatusCourseBranch(int branchId, UUID targetCourseId, int statusId) {
        String branch = branchFeignClient.getNameById(branchId);
        if (branch == null) throw new BranchNotFoundException(String.valueOf(branchId));
        String course = courseFeignClient.getNameById(targetCourseId);
        if (course == null) throw new CourseNotFoundException(String.valueOf(branchId));
        String status = statusFeignClient.getNameById(statusId);
        if (status == null) throw new StatusNotFoundException(statusId);
        return new StatusCourseBranchNamesDto(branch, course, status);
    }

    @Loggable
    @SuppressWarnings("unchecked")
    public <S extends AbstractStudent> List<AbstractStudent> findStudents(Pageable pageable, String search, Integer statusId, UUID group_id, UUID courseId, boolean isCurrentRepository) {
        Specification<S> studentSpecs = getStudentSpecifications(search, statusId, group_id, courseId);
        Page<? extends AbstractStudent> pageContactEntity = isCurrentRepository ? repository.findAll((Specification<StudentEntity>) studentSpecs, pageable) : archiveRepository.findAll((Specification<StudentsArchiveEntity>) studentSpecs, pageable);
        return (List<AbstractStudent>) pageContactEntity.getContent();
    }

    public AbstractStudent findByPhoneOrEmailAndDelete(String phone, String email) {
        try {
            return repository.deleteByPhoneOrEmail(phone, email);
        } catch (Exception e) {
            throw new DatabaseDeletingException(e.getMessage());
        }
    }
}
