package com.telran.groupservice.persistence.groups;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(schema = "current", name = "groups")
@NoArgsConstructor
public class GroupEntity extends BaseGroup {

    public GroupEntity(String groupName, LocalDate startDate, LocalDate finishDate, UUID courseId, String slackLink, String whatsAppLink, String skypeLink, Boolean deactivateAfter) {
        super(groupName, startDate, finishDate, courseId, slackLink, whatsAppLink, skypeLink, deactivateAfter);
    }

    public GroupEntity(BaseGroup groupEntity) {
        super(groupEntity);
    }
}
