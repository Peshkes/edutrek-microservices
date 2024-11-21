package com.telran.lecturerservice.persistence.share;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.UUID;

@NoRepositoryBean
public interface ILecturerByGroupRepository<T extends BaseLecturerByGroup> extends JpaRepository<T, ComposeLecturerKey> {
    void deleteByGroupId(UUID groupId);
    List<T> getByGroupId(UUID uuid);
}