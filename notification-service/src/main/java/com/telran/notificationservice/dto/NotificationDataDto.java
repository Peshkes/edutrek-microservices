package com.telran.notificationservice.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
public class NotificationDataDto {
    private int notificationId;
    private UUID recipientId;
    private LocalDateTime scheduledTime;
    private String entityName;
    private String notificationText;


    public NotificationDataDto(int notificationId, NotificationDto notificationDto, UUID recipientId, String entityName) {
        this.notificationId = notificationId;
        this.recipientId = recipientId;
        this.scheduledTime = notificationDto.getScheduledTime();
        this.entityName = entityName;
        this.notificationText = notificationDto.getNotificationText();
    }
}