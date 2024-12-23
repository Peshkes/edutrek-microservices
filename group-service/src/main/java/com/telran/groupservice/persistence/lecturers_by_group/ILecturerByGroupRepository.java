package com.telran.groupservice.persistence.lecturers_by_group;

import com.telran.groupservice.key.ComposeLecturerKey;
import com.telran.groupservice.persistence.IJunctionTableRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.UUID;

@NoRepositoryBean
public interface ILecturerByGroupRepository<T extends BaseLecturerByGroup> extends IJunctionTableRepository, JpaRepository<T, ComposeLecturerKey> {
    void deleteByGroupId(UUID groupId);
    List<T> getByGroupId(UUID uuid);
    List<T> deleteByLecturerId(UUID lecturerId);
}