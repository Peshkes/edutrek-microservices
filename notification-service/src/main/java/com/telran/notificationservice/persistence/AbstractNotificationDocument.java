package com.telran.notificationservice.persistence;


import com.telran.notificationservice.dto.NotificationDataDto;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbstractNotificationDocument {
    @Id
    private UUID entityId;
    private List<NotificationDataDto> notificationData;
}