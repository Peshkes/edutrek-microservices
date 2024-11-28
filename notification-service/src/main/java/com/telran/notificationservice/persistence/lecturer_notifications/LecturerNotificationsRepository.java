package com.telran.notificationservice.persistence.lecturer_notifications;


import com.telran.notificationservice.persistence.INotificationsRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturerNotificationsRepository extends INotificationsRepository<LecturerNotificationDocument> {



}