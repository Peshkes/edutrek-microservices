package com.telran.groupservice.persistence.lecturers_by_group;

import com.telran.groupservice.key.ComposeLecturerKey;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ComposeLecturerKey.class)
@MappedSuperclass
public abstract class BaseLecturerByGroup {
    @Id
    @Column(name = "group_id")
    private UUID groupId;
    @Id
    @Column(name = "lecturer_id")
    private UUID lecturerId;
    @Id
    @Column(name = "is_webinarist")
    private boolean isWebinarist;
    @Column(name = "is_active")
    private boolean isActive;

    public BaseLecturerByGroup(BaseLecturerByGroup baseLecturerByGroup) {
        this.groupId = baseLecturerByGroup.getGroupId();
        this.lecturerId = baseLecturerByGroup.getLecturerId();
        this.isWebinarist = baseLecturerByGroup.isWebinarist();
        this.isActive = baseLecturerByGroup.isActive();
    }

    public BaseLecturerByGroup(UUID groupId, UUID lecturerId, boolean isWebinarist) {
        this.groupId = groupId;
        this.lecturerId = lecturerId;
        this.isWebinarist = isWebinarist;
        this.isActive = true;
    }
}
