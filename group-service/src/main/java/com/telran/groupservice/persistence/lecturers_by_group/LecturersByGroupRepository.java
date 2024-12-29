package com.telran.groupservice.persistence.lecturers_by_group;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LecturersByGroupRepository extends ILecturerByGroupRepository<LecturersByGroupEntity>{
    @Modifying
    void deleteByGroupId(@Param("id") UUID groupId);
    List<LecturersByGroupEntity> getByGroupId(@Param("id") UUID uuid);
    List<LecturersByGroupEntity> deleteByLecturerId(UUID lecturerId);
}
