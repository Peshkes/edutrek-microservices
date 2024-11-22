package com.telran.groupservice.persistence.students_by_group;

import com.telran.groupservice.key.ComposeStudentsKey;
import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@Table(schema = "current", name = "students_by_group")
@IdClass(ComposeStudentsKey.class)
public class StudentsByGroupEntity extends BaseStudentsByGroup {
    public StudentsByGroupEntity(StudentsByGroupEntity studentsByGroupEntity) {
        super(studentsByGroupEntity);
    }

    public StudentsByGroupEntity(UUID groupId, UUID studentId, boolean isActive) {
        super(groupId, studentId, isActive);
    }
}
