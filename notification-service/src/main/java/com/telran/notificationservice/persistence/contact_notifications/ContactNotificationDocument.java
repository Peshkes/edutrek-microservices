package com.telran.notificationservice.persistence.contact_notifications;


import com.telran.notificationservice.dto.NotificationDataDto;
import com.telran.notificationservice.persistence.AbstractNotificationDocument;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
@Document("notifications")
@EqualsAndHashCode(callSuper = true)
public class ContactNotificationDocument extends AbstractNotificationDocument {

    public ContactNotificationDocument(UUID id, List<NotificationDataDto> notificationData) {
        super(id, notificationData);
    }
}