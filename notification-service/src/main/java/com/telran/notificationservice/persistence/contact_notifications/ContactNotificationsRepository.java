package com.telran.notificationservice.persistence.contact_notifications;


import com.telran.notificationservice.persistence.INotificationsRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactNotificationsRepository extends INotificationsRepository<ContactNotificationDocument> {
}