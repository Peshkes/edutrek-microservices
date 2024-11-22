package com.telran.groupservice.service;

import com.telran.groupservice.persistence.lecturers_by_group.LecturersByGroupArchiveRepository;
import com.telran.groupservice.persistence.lecturers_by_group.LecturersByGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
