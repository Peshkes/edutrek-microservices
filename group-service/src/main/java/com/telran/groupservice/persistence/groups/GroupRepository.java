package com.telran.groupservice.persistence.groups;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupRepository extends IGroupRepository<GroupEntity>{
    @Query(value = "SELECT * FROM current.groups WHERE is_active = true AND finish_date <= :finishDate AND deactivate_after = true", nativeQuery = true)
    List<GroupEntity> findActiveGroupsToGraduate(@Param("finishDate") LocalDate finishDate);
    Optional<BaseGroup> getGroupByGroupId(@Param("id") UUID id);
    Page<GroupEntity> findAll(Specification<GroupEntity> spec, Pageable pageable);
    @Modifying
    void deleteGroupByGroupId(@Param("id") UUID id);
}