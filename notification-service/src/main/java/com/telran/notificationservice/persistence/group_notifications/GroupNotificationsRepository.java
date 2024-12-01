package com.telran.notificationservice.persistence.group_notifications;


import com.telran.notificationservice.persistence.INotificationsRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupNotificationsRepository extends INotificationsRepository<GroupNotificationDocument> {



}