package com.telran.groupservice.persistence.students_by_group;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@Table(schema = "archive", name = "students_by_group")
public class StudentsByGroupArchiveEntity extends BaseStudentsByGroup {
    public StudentsByGroupArchiveEntity(BaseStudentsByGroup studentsByGroupEntity) {
        super(studentsByGroupEntity);
    }

    public StudentsByGroupArchiveEntity(UUID groupId, UUID studentId, boolean isActive) {
        super(groupId, studentId, isActive);
    }
}
