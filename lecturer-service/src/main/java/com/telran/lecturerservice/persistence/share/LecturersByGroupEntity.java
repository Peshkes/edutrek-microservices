package com.telran.lecturerservice.persistence.share;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(schema = "current", name = "lecturers_by_group")
@NoArgsConstructor
public class LecturersByGroupEntity extends BaseLecturerByGroup {
    public LecturersByGroupEntity(BaseLecturerByGroup lecturersByGroupEntity) {
        super(lecturersByGroupEntity);
    }

    public LecturersByGroupEntity(UUID groupId, UUID lecturerId, boolean isWebinarist) {
        super(groupId, lecturerId, isWebinarist);
    }
}
