package com.telran.studentservice.persistence;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface IStudentRepository<T extends AbstractStudent> extends JpaRepository<T, UUID>, JpaSpecificationExecutor<T> {
    Optional<AbstractStudent> getByStudentId(UUID id);
    boolean existsByPhoneOrEmail(String phone, String email);
    AbstractStudent deleteByPhoneAndEmail(String phone, String email);
}
