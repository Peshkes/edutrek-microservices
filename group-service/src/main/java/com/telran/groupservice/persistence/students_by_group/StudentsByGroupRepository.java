package com.telran.groupservice.persistence.students_by_group;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentsByGroupRepository extends IStudentsByGroupRepository<StudentsByGroupEntity> {
    boolean existsByGroupIdAndStudentId(UUID groupId, UUID studentId);
    @Modifying
    void deleteByGroupId(@Param("id") UUID groupId);
    List<StudentsByGroupEntity> getByGroupId(@Param("id") UUID uuid);
    Optional<BaseStudentsByGroup> getByGroupIdAndStudentId(@Param("groupId") UUID groupId, @Param("studentId") UUID studentId);
}
