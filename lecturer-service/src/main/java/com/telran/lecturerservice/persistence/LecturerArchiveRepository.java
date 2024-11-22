package com.telran.lecturerservice.persistence;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LecturerArchiveRepository extends ILecturerRepository<LecturerArchiveEntity> {
    Optional<BaseLecturer> getLecturerByLecturerId(@Param("id") UUID id);
}
