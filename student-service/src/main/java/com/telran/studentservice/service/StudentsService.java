package com.telran.studentservice.service;

import com.telran.studentservice.dto.*;
import com.telran.studentservice.error.DatabaseException.DatabaseAddingException;
import com.telran.studentservice.error.DatabaseException.DatabaseDeletingException;
import com.telran.studentservice.error.Exceptions.*;
import com.telran.studentservice.feign.ContactFeignClient;
import com.telran.studentservice.feign.GroupFeignClient;
import com.telran.studentservice.feign.PaymentsFeignClient;
import com.telran.studentservice.logging.Loggable;
import com.telran.studentservice.persistence.AbstractStudent;
import com.telran.studentservice.persistence.archive.StudentsArchiveEntity;
import com.telran.studentservice.persistence.archive.StudentsArchiveRepository;
import com.telran.studentservice.persistence.current.StudentEntity;
import com.telran.studentservice.persistence.current.StudentsRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.telran.studentservice.persistence.StudentsFilterSpecifications.getStudentSpecifications;

@Service
@RequiredArgsConstructor

public class StudentsService {

    private static final boolean IS_CURRENT_REPOSITORY = true;
    private final StudentsRepository repository;
    private final StudentsArchiveRepository archiveRepository;
    private final StudentRabbitProducer rabbitProducer;
    private final ContactFeignClient contactFeignClient;
    private final PaymentsFeignClient paymentsFeignClient;
    private final GroupFeignClient groupFeignClient;

    @Loggable
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public StudentSearchDto getAll(int page, int pageSize, String search, Integer statusId, UUID groupId, UUID courseId) {
        String statusName = statusId == null ? null : rabbitProducer.sendGetStatusNameById(statusId);
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
        Map<UUID, StudentWithGroupDto> foundStudentsMap = foundStudents.stream().collect(Collectors.toMap(AbstractStudent::getStudentId, StudentWithGroupDto::new));
        System.out.println("Map<UUID, StudentWithGroupDto>: " + foundStudentsMap);
        List<GetStudentsByGroupDto> studentsByGroup = groupFeignClient.getStudentsByGroup(foundStudentsMap.keySet());
        System.out.println("List<GetStudentsByGroupDto>: " + studentsByGroup);
        studentsByGroup.forEach(s -> foundStudentsMap.get(s.getStudentId()).getGroups().add(new GroupsDto(s.getGroupId(), s.getIsActive(), s.getGroupName())));
        return new StudentSearchDto(foundStudentsMap.values(), page, pageSize, foundStudents.size());
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
        List<String> properties = checkStatusCourseBranch(studentData.getBranchId(), studentData.getTargetCourseId(), studentData.getStatusId());
        if (!properties.get(2).equals("Student")) throw new NotAStudentException();
        if (!repository.existsByPhoneOrEmail(studentData.getPhone(), studentData.getEmail()) && !contactFeignClient.existsByPhoneOrEmail(studentData.getPhone(), studentData.getEmail())) {
            try {
                AbstractStudent saved = repository.save(new StudentEntity(studentData));
                String log = studentData.getLogText();
                rabbitProducer.sendAddLog(
                        saved.getStudentId(),
                        log != null ? log : " - New contact added. Status: " + properties.get(0) + ", course: " + properties.get(1) + ", branch: " + properties.get(2));
            } catch (Exception e) {
                throw new DatabaseAddingException(e.getMessage());
            }
        } else
            throw new StudentOrContactAlreadyExistsException();
    }

    @Loggable
    @Transactional
    public void promoteEntity(StudentsDataDto studentsDataDto) {
        try {
            repository.save(new StudentEntity(studentsDataDto));
            String log = studentsDataDto.getLogText();
            rabbitProducer.sendAddLog(
                    studentsDataDto.getContactId(),
                    log != null ? log : " - New student promoted from contact. Status: " + studentsDataDto.getStatusId() + ", course: " + studentsDataDto.getTargetCourseId() + ", branch: " + studentsDataDto.getBranchId());
        } catch (Exception e) {
            throw new DatabaseAddingException(e.getMessage());
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
        rabbitProducer.sendDeleteLogById(id);
    }

    @Loggable
    @Transactional
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public void updateById(StudentsDataDto studentData) {
        UUID id = studentData.getContactId();
        checkStatusCourseBranch(studentData.getBranchId(), studentData.getTargetCourseId());
        AbstractStudent entity = repository.getByStudentId(id).or(() -> archiveRepository.findById(id)).orElseThrow(() -> new StudentNotFoundException(id.toString()));
        List<String> updates = updateEntity(studentData, entity);
        String log = studentData.getLogText();
        rabbitProducer.sendAddLog(
                id,
                log != null ? log : " - Contact updated. Updated info: " + updates);
    }

    private <T extends AbstractStudent> List<String> updateEntity(StudentsDataDto studentData, T entity) {
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
            entity.setEmail(email);
            updates.add("email");
        }

        String comment = studentData.getComment();
        if (!entity.getComment().equals(comment)) {
            entity.setComment(comment);
            updates.add("comment");
        }

        int branch = studentData.getBranchId();
        if (entity.getBranchId() != branch) {
            entity.setBranchId(branch);
            updates.add("branch");
        }

        UUID course = studentData.getTargetCourseId();
        if (!entity.getTargetCourseId().equals(course)) {
            entity.setTargetCourseId(course);
            updates.add("course");
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
        int statusId = rabbitProducer.sendGetStatusIdByName("Archive");
        System.out.println("Status received: " + statusId);
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
        rabbitProducer.sendAddLog(
                id,
                "Student archived. Reason: " + reason);
    }

    @Loggable
    @Transactional
    public void graduateById(UUID id) {
        moveToArchiveById(id, "Finished course and graduated");
    }

    private void checkStatusCourseBranch(Integer branchId, UUID targetCourseId) {
        //CompletableFuture<Void> resultFuture = new CompletableFuture<>();
        List<CompletableFuture<Boolean>> list = new ArrayList<>();
        if (branchId != null) {
            list.add(CompletableFuture.supplyAsync(() -> rabbitProducer.sendCourseExists(targetCourseId)));
        }
        if (branchId != null) {
            list.add(CompletableFuture.supplyAsync(() -> rabbitProducer.sendBranchExists(branchId)));
        }
        try {
            CompletableFuture.allOf(list.toArray(new CompletableFuture[0]))
                    .thenApply(v -> list.stream().map(CompletableFuture::join).toList()).get().forEach(item -> {
                        if (!item) throw new EntityNotFoundException();
                    });
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error extracting data", e);
        }
    }

//    public class CancelableFuture {
//
//        public static CompletableFuture<Void> allTrueOrCancel(List<CompletableFuture<Boolean>> futures) {
//            CompletableFuture<Void> resultFuture = new CompletableFuture<>();
//
//            futures.forEach(future ->
//                    future.thenAccept(result -> {
//                        if (!result) {
//                            // Отмена всех фьючеров и завершение с ошибкой
//                            futures.forEach(f -> f.cancel(true));
//                            resultFuture.completeExceptionally(new RuntimeException("One of the tasks returned false."));
//                        }
//                    }).exceptionally(e -> {
//                        // Завершаем результат с ошибкой, если один из фьючеров упал
//                        resultFuture.completeExceptionally(e);
//                        return null;
//                    })
//            );
//
//            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
//                    .thenAccept(v -> {
//                        // Завершаем результат, если все фьючеры вернули true
//                        if (!resultFuture.isDone()) {
//                            resultFuture.complete(null);
//                        }
//                    });
//
//            return resultFuture;
//        }
//    }

    @Loggable
    private List<String> checkStatusCourseBranch(Integer branchId, UUID targetCourseId, Integer statusId) {
        List<CompletableFuture<String>> list = new ArrayList<>();
        createFuture(branchId, list, rabbitProducer::sendGetBranchNameById);
        createFuture(targetCourseId, list, rabbitProducer::sendGetCourseNameById);
        createFuture(statusId, list, rabbitProducer::sendGetStatusNameById);

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
    @SuppressWarnings("unchecked")
    public <S extends AbstractStudent> List<AbstractStudent> findStudents(Pageable pageable, String search, Integer statusId, UUID group_id, UUID courseId, boolean isCurrentRepository) {
        Specification<S> studentSpecs = getStudentSpecifications(search, statusId, group_id, courseId);
        Page<? extends AbstractStudent> pageContactEntity = isCurrentRepository ? repository.findAll((Specification<StudentEntity>) studentSpecs, pageable) : archiveRepository.findAll((Specification<StudentsArchiveEntity>) studentSpecs, pageable);
        return new ArrayList<>(pageContactEntity.getContent());
    }

    @Loggable
    @SuppressWarnings("unchecked")
    public <S extends AbstractStudent> List<AbstractStudent> findStudentForContacts(int quantity, String search, Integer statusId, UUID group_id, UUID courseId, boolean isCurrentRepository) {
        Specification<S> studentSpecs = getStudentSpecifications(search, statusId, group_id, courseId);
        List<? extends AbstractStudent> pageContactEntity = isCurrentRepository ? repository.findAll((Specification<StudentEntity>) studentSpecs) : archiveRepository.findAll((Specification<StudentsArchiveEntity>) studentSpecs);
        System.out.println("Found students " + pageContactEntity);
        if (pageContactEntity.size() <= quantity)
            return (List<AbstractStudent>) pageContactEntity;
        return (List<AbstractStudent>) pageContactEntity.subList(0, quantity);
    }

    public AbstractStudent findByPhoneOrEmailAndDelete(String phone, String email) {
        try {
            return repository.deleteByPhoneAndEmail(phone, email);
        } catch (Exception e) {
            throw new DatabaseDeletingException(e.getMessage());
        }
    }
}
