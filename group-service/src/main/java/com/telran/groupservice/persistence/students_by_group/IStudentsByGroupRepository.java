package com.telran.groupservice.persistence.students_by_group;

import com.telran.groupservice.key.ComposeStudentsKey;
import com.telran.groupservice.persistence.IJunctionTableRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@NoRepositoryBean
public interface IStudentsByGroupRepository<T extends BaseStudentsByGroup> extends IJunctionTableRepository, JpaRepository<T, ComposeStudentsKey> {
   boolean existsByGroupIdAndStudentId(UUID groupId, UUID studentId);
   void deleteByGroupId(UUID groupId);
   List<T> getByGroupId(@Param("id") UUID uuid);
   Optional<BaseStudentsByGroup> getByGroupIdAndStudentId(UUID groupId, UUID studentId);
}