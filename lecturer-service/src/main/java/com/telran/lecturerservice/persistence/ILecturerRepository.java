package com.telran.lecturerservice.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface ILecturerRepository<T extends BaseLecturer> extends JpaRepository<T, UUID>, JpaSpecificationExecutor<T> {
    Optional<BaseLecturer> getLecturerByLecturerId(UUID id);
}
