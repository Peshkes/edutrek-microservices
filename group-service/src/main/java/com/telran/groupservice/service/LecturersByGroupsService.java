package com.telran.groupservice.service;

import com.telran.groupservice.persistence.lecturers_by_group.LecturersByGroupArchiveEntity;
import com.telran.groupservice.persistence.lecturers_by_group.LecturersByGroupArchiveRepository;
import com.telran.groupservice.persistence.lecturers_by_group.LecturersByGroupEntity;
import com.telran.groupservice.persistence.lecturers_by_group.LecturersByGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LecturersByGroupsService {

    private final LecturersByGroupRepository repository;
    private final LecturersByGroupArchiveRepository archiveRepository;

    public void deleteArchiveLecturersByLecturerId(UUID lecturerId) {
        repository.deleteByLecturerId(lecturerId);
    }

    public void deleteCurrentLecturersByLecturerId(UUID lecturerId) {
        archiveRepository.deleteByLecturerId(lecturerId);
    }

    public void archiveLecturersByLecturerId(UUID lecturerId) {
        List<LecturersByGroupEntity> entities = repository.deleteByLecturerId(lecturerId);
        entities.forEach(entity -> archiveRepository.save(new LecturersByGroupArchiveEntity(entity)));
    }
}
