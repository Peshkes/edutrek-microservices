package com.telran.lecturerservice.service;

import com.telran.lecturerservice.dto.LecturerDataDto;
import com.telran.lecturerservice.dto.LecturerPaginationResponseDto;
import com.telran.lecturerservice.error.DatabaseException.*;
import com.telran.lecturerservice.error.LecturerNotFoundException;
import com.telran.lecturerservice.feign.GroupClient;
import com.telran.lecturerservice.logging.Loggable;
import com.telran.lecturerservice.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Loggable
    public BaseLecturer getById(UUID id) {
        return repository.getLecturerByLecturerId(id).or(() -> archiveRepository.getLecturerByLecturerId(id)).orElseThrow(() -> new LecturerNotFoundException(id.toString()));
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
    public void addEntity(LecturerDataDto data) {
        try {
            repository.save(new LecturerEntity(data.getLecturerName(), data.getPhone(), data.getEmail(), data.getBranchId(), data.getComment()));
        } catch (Exception e) {
            throw new DatabaseAddingException(e.getMessage());
        }
    }

    @Loggable
    @Transactional
    public BaseLecturer deleteById(UUID id) {
        BaseLecturer lecturer = repository.findById(id).orElse(null);
        if (lecturer != null) {
            deleteLecturer(id, groupClient::deleteCurrentLecturersByLecturerId, repository::deleteById);
        } else {
            lecturer = archiveRepository.findById(id).orElse(null);
            if (lecturer != null)
                deleteLecturer(id, groupClient::deleteArchiveLecturersByLecturerId, archiveRepository::deleteById);
            else
                throw new LecturerNotFoundException(id.toString());
        }
        return lecturer;
    }

    private void deleteLecturer(UUID id, Consumer<UUID> groupClientMethod, Consumer<UUID> repositoryMethod) {
        try {
            groupClientMethod.accept(id);
            repositoryMethod.accept(id);
        } catch (Exception e) {
            throw new DatabaseDeletingException(e.getMessage());
        }
    }

    @Loggable
    @Transactional
    public void updateById(UUID id, LecturerDataDto data) {
        BaseLecturer entity = repository.getLecturerByLecturerId(id).or(() -> archiveRepository.getLecturerByLecturerId(id)).orElseThrow(() -> new LecturerNotFoundException(id.toString()));
        entity.setLecturerName(data.getLecturerName());
        entity.setPhone(data.getPhone());
        entity.setEmail(data.getEmail());
        entity.setBranchId(data.getBranchId());
        entity.setComment(data.getComment());
    }

    @Loggable
    @Transactional
    public void archiveById(UUID uuid, String reason) {
        if (repository.existsById(uuid)) {
            BaseLecturer lecturer = deleteById(uuid);
            try {
                archiveRepository.save(new LecturerArchiveEntity(lecturer, reason));
            } catch (Exception e) {
                throw new DatabaseAddingException(e.getMessage());
            }
        }
    }

    @Loggable
    public boolean existsById(UUID id) {
        return repository.existsById(id) || archiveRepository.existsById(id);
    }
}
