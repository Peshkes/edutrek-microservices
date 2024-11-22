package com.telran.groupservice.persistence.groups;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupArchiveRepository extends IGroupRepository<GroupArchiveEntity> {
    @Modifying
    void deleteGroupByGroupId(UUID id);
    Optional<BaseGroup> getGroupByGroupId(@Param("id") UUID id);
    Page<GroupArchiveEntity> findAll(Specification<GroupArchiveEntity> spec, Pageable pageable);

}
