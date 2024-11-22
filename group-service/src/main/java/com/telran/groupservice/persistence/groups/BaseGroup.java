package com.telran.groupservice.persistence.groups;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@NoArgsConstructor
@MappedSuperclass
public abstract class BaseGroup {
    @Id
    @Column(name = "group_id")
    private UUID groupId;
    @Setter
    @Column(name = "group_name")
    private String groupName;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Setter
    @Column(name = "finish_date")
    private LocalDate finishDate;
    @Setter
    @Column(name = "is_active")
    private Boolean isActive;
    @Setter
    @Column(name = "course_id")
    private UUID courseId;
    @Setter
    @Column(name = "slack_link")
    private String slackLink;
    @Setter
    @Column(name = "whats_app_link")
    private String whatsAppLink;
    @Setter
    @Column(name = "skype_link")
    private String skypeLink;
    @Setter
    @Column(name = "deactivate_after")
    private Boolean deactivateAfter;

    public BaseGroup(String groupName, LocalDate startDate, LocalDate finishDate, Boolean isActive, UUID courseId, String slackLink, String whatsAppLink, String skypeLink, Boolean deactivateAfter) {
        this.groupId = UUID.randomUUID();
        this.groupName = groupName;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.isActive = isActive;
        this.courseId = courseId;
        this.slackLink = slackLink;
        this.whatsAppLink = whatsAppLink;
        this.skypeLink = skypeLink;
        this.deactivateAfter = deactivateAfter;
    }

    public BaseGroup(BaseGroup baseGroup) {
        this.groupId = baseGroup.getGroupId();
        this.groupName = baseGroup.getGroupName();
        this.startDate = baseGroup.getStartDate();
        this.finishDate = baseGroup.getFinishDate();
        this.isActive = baseGroup.getIsActive();
        this.courseId = baseGroup.getCourseId();
        this.slackLink = baseGroup.getSlackLink();
        this.whatsAppLink = baseGroup.getWhatsAppLink();
        this.skypeLink = baseGroup.getSkypeLink();
        this.deactivateAfter = baseGroup.getDeactivateAfter();
    }
}
