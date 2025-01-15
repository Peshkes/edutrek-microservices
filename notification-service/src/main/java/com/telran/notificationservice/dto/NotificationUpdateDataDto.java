package com.telran.notificationservice.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
public class NotificationUpdateDataDto {
    private int notificationId;
    private LocalDateTime scheduledTime;
    private String notificationText;
}