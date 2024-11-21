package com.telran.lecturerservice.persistence.share;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LecturersByGroupArchiveRepository extends ILecturerByGroupRepository<LecturersByGroupArchiveEntity>{
    @Modifying
    void deleteByGroupId(@Param("id") UUID groupId);
    List<LecturersByGroupArchiveEntity> getByGroupId(@Param("id") UUID uuid);
}
