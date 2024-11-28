package com.telran.notificationservice.persistence.group_notifications;


import com.telran.notificationservice.persistence.AbstractNotificationDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("notifications")
@EqualsAndHashCode(callSuper = true)
public class GroupNotificationDocument extends AbstractNotificationDocument {
}