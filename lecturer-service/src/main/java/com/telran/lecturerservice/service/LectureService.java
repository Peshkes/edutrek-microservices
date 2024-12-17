package com.telran.lecturerservice.service;

import com.telran.lecturerservice.dto.AddLogDto;
import com.telran.lecturerservice.dto.LecturerDataDto;
import com.telran.lecturerservice.dto.LecturerPaginationResponseDto;
import com.telran.lecturerservice.error.BranchNotFoundException;
import com.telran.lecturerservice.error.DatabaseException.DatabaseAddingException;
import com.telran.lecturerservice.error.DatabaseException.DatabaseDeletingException;
import com.telran.lecturerservice.error.Exception;
import com.telran.lecturerservice.feign.GroupClient;
import com.telran.lecturerservice.logging.Loggable;
import com.telran.lecturerservice.persistence.*;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final LecturerRepository repository;
    private final LecturerArchiveRepository archiveRepository;
    private final GroupClient groupClient;
    private final LecturersRabbitProducer rabbitProducer;

    @Loggable
    public BaseLecturer getById(UUID id) {
        return repository.getLecturerByLecturerId(id).or(() -> archiveRepository.getLecturerByLecturerId(id)).orElseThrow(() -> new Exception(id.toString()));
    }

    @Loggable
    public LecturerPaginationResponseDto getAllPaginated(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<LecturerEntity> mainPage = repository.findAll(pageable);
        List<BaseLecturer> results = new ArrayList<>(mainPage.getContent());

        if (mainPage.getTotalElements() < pageable.getPageSize()) {
            int remainingElements = pageable.getPageSize() - results.size();
            Pageable archivePageable = PageRequest.of(0, remainingElements);
            Page<LecturerArchiveEntity> archivePage = archiveRepository.findAll(archivePageable);
            results.addAll(archivePage.getContent());
        }

        long totalElements = repository.count() + archiveRepository.count();

        return new LecturerPaginationResponseDto(results, totalElements, pageable.getPageNumber(), pageable.getPageSize());
    }

    @Loggable
    @Transactional
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public void addEntity(LecturerDataDto data) {
        try {
            String branchName = getBranchName(data.getBranchId());
            UUID lecturerId = repository.save(new LecturerEntity(data.getLecturerName(), data.getPhone(), data.getEmail(), data.getBranchId(), data.getComment())).getLecturerId();
            String log = data.getLogText();
            addLog(lecturerId, log != null ? log : "New lecturer added. Branch: " + branchName);
        } catch (java.lang.Exception e) {
            throw new DatabaseAddingException(e.getMessage());
        }
    }

    @Loggable
    @Transactional
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public BaseLecturer deleteById(UUID id) {
        try {
            BaseLecturer lecturer = repository.findById(id).orElse(null);
            if (lecturer != null) {
                deleteLecturer(id, groupClient::deleteCurrentLecturersByLecturerId, repository::deleteById);
            } else {
                lecturer = archiveRepository.findById(id).orElse(null);
                if (lecturer != null)
                    deleteLecturer(id, groupClient::deleteArchiveLecturersByLecturerId, archiveRepository::deleteById);
                else
                    throw new Exception(id.toString());
            }
            rabbitProducer.deleteLog(id);
            return lecturer;
        } catch (java.lang.Exception e) {
            throw new DatabaseDeletingException(e.getMessage());
        }
    }

    private void deleteLecturer(UUID id, Consumer<UUID> groupClientMethod, Consumer<UUID> repositoryMethod) {
        try {
            groupClientMethod.accept(id);
            repositoryMethod.accept(id);
        } catch (java.lang.Exception e) {
            throw new DatabaseDeletingException(e.getMessage());
        }
    }

    @Loggable
    @Transactional
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public void updateById(UUID id, LecturerDataDto data) {
        checkBranchExists(data.getBranchId());
        BaseLecturer entity = repository.getLecturerByLecturerId(id).or(() -> archiveRepository.getLecturerByLecturerId(id)).orElseThrow(() -> new Exception(id.toString()));
        List<String> updates = updateEntity(data, entity);
        String log = data.getLogText();
        if (!updates.isEmpty())
            addLog(id, log != null ? log : "Lecturer updated. Updated info: " + updates);
    }

    private <T extends BaseLecturer> List<String> updateEntity(LecturerDataDto data, T entity) {
        List<String> updates = new ArrayList<>();

        String name = data.getLecturerName();
        if (!entity.getLecturerName().equals(name)) {
            entity.setLecturerName(name);
            updates.add("name");
        }

        String phone = data.getPhone();
        if (!entity.getPhone().equals(phone)) {
            entity.setPhone(phone);
            updates.add("phone");
        }

        String email = data.getEmail();
        if (!entity.getPhone().equals(email)) {
            entity.setPhone(email);
            updates.add("email");
        }

        String comment = data.getComment();
        if (!entity.getComment().equals(comment)) {
            entity.setPhone(comment);
            updates.add("comment");
        }

        int branch = data.getBranchId();
        if (entity.getBranchId() != branch) {
            entity.setBranchId(branch);
            updates.add("branch");
        }

        return updates;
    }

    @Loggable
    @Transactional
    public void archiveById(UUID uuid, String reason) {
        if (repository.existsById(uuid)) {
            BaseLecturer lecturer = deleteById(uuid);
            try {
                archiveRepository.save(new LecturerArchiveEntity(lecturer, reason));
                addLog(uuid, "Lecturer archived. Reason: " + reason);
            } catch (java.lang.Exception e) {
                throw new DatabaseAddingException(e.getMessage());
            }
        }
    }

    @Loggable
    public boolean existsById(UUID id) {
        return repository.existsById(id) || archiveRepository.existsById(id);
    }

    private String getBranchName(int branchId) {
        String branchName = rabbitProducer.sendGetBranchNameById(branchId);
        if (branchName == null) throw new BranchNotFoundException(String.valueOf(branchId));
        return branchName;
    }

    private void checkBranchExists(int branchId) {
        boolean isExists = rabbitProducer.sendBranchExists(branchId);
        if (!isExists) throw new BranchNotFoundException(String.valueOf(branchId));
    }

    private void addLog(UUID id, String log) {
        AddLogDto logDto = new AddLogDto(id, log);
        rabbitProducer.addLog(logDto);
    }
}