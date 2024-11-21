package com.telran.groupservice.persistence.lecturers_by_group;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(schema = "archive", name = "lecturers_by_group")
@NoArgsConstructor
public class LecturersByGroupArchiveEntity extends BaseLecturerByGroup {
    public LecturersByGroupArchiveEntity(BaseLecturerByGroup lecturersByGroupEntity) {
        super(lecturersByGroupEntity);
    }

    public LecturersByGroupArchiveEntity(UUID groupId, UUID lecturerId, boolean isWebinarist) {
        super(groupId, lecturerId, isWebinarist);
    }
}
