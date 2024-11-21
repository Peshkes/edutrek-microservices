package com.telran.groupservice.persistence.students_by_group;

import com.telran.groupservice.key.ComposeStudentsKey;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(ComposeStudentsKey.class)
@MappedSuperclass
public class BaseStudentsByGroup {
    @Id
    @Column(name = "group_id")
    private UUID groupId;
    @Id
    @Column(name = "student_id")
    private UUID studentId;
    @Setter
    @Column(name = "is_active")
    private Boolean isActive;

    public BaseStudentsByGroup(BaseStudentsByGroup studentsByGroupEntity) {
        this.groupId = studentsByGroupEntity.getGroupId();
        this.studentId = studentsByGroupEntity.getStudentId();
        this.isActive = studentsByGroupEntity.getIsActive();
    }
}
