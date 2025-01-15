package com.telran.notificationservice.persistence;


import com.telran.notificationservice.dto.NotificationDataDto;
import jakarta.persistence.*;
import org.springframework.data.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;



import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor

public class AbstractNotificationDocument {
    @Version
    private int version;

    @Id
    private UUID entityId;
    private List<NotificationDataDto> notificationData;

    public AbstractNotificationDocument(UUID entityId, List<NotificationDataDto> notificationData) {
        this.entityId = entityId;
        this.notificationData = notificationData;
    }
}