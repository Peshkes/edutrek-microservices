package com.telran.notificationservice.persistence.student_notifications;


import com.telran.notificationservice.persistence.INotificationsRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentNotificationsRepository extends INotificationsRepository<StudentNotificationDocument> {
}