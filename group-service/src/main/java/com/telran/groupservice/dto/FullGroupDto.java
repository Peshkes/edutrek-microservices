package com.telran.groupservice.dto;

import com.telran.groupservice.persistence.lecturers_by_group.BaseLecturerByGroup;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class FullGroupDto {
    private UUID groupId;
    private String groupName;
    private LocalDate startDate;
    private LocalDate finishDate;
    private Boolean isActive;
    private UUID courseId;
    private String slackLink;
    private String whatsAppLink;
    private String skypeLink;
    private Boolean deactivateAfter;
    private List<Integer> lessons;
    private List<Integer> webinars;
    private List<? extends BaseLecturerByGroup> lecturers;

    public FullGroupDto(UUID groupId, String groupName, LocalDate startDate, LocalDate finishDate, UUID courseId, String slackLink, String whatsAppLink, String skypeLink, Boolean deactivateAfter, List<Integer> list, List<Integer> list1, List<? extends BaseLecturerByGroup> lecturers) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.courseId = courseId;
        this.slackLink = slackLink;
        this.whatsAppLink = whatsAppLink;
        this.skypeLink = skypeLink;
        this.deactivateAfter = deactivateAfter;
        this.lessons = list;
        this.webinars = list1;
        this.lecturers = lecturers;
    }
}
