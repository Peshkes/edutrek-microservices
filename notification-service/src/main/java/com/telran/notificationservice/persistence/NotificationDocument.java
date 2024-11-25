package com.telran.notificationservice.persistence;



import com.telran.notificationservice.dto.NotificationDataDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("notifications")
public class NotificationDocument {

    @Id
    private UUID id;
    List<NotificationDataDto> notificationData;
}