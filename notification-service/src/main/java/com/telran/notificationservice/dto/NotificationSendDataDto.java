package com.telran.notificationservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSendDataDto {
    private UUID entityId;
    private LocalDateTime scheduledTime;
    private String entityName;
    private String notificationText;
}