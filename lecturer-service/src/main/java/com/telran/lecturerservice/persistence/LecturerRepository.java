package com.telran.lecturerservice.persistence;

import feign.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LecturerRepository extends ILecturerRepository<LecturerEntity> {
    Optional<BaseLecturer> getLecturerByLecturerId(@Param("id") UUID id);
}
