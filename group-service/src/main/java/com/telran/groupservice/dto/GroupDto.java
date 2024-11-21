package com.telran.groupservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDto {
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
}