package com.telran.groupservice.persistence.groups;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface IGroupRepository<T extends BaseGroup> extends JpaRepository<T, UUID>, JpaSpecificationExecutor<T> {

    Optional<BaseGroup> getGroupByGroupId(UUID id);
    Page<T> findAll(Specification<T> spec, Pageable pageable);
    void deleteGroupByGroupId(UUID id);
}
