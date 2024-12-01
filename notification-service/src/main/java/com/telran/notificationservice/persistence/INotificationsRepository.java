package com.telran.notificationservice.persistence;


import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface INotificationsRepository<T extends AbstractNotificationDocument> extends MongoRepository<T, UUID> {


    @Query("{ '_id': ?0}")
    @Update("{ '$pull' : { 'notificationData': {'notificationId': {$in: ?1}}  } } ")
    void deleteNotificationDocumentsById(UUID entityId, Integer[] elementNumbers);

    @Query(value = "{ '_id': ?0, 'notificationData.notificationId': ?1 }")
    @Update(value = "{ '$set': { 'notificationData.$.notificationDate': ?2, 'notificationData.$.notificationText': ?3 } }")
    void updateNotificationDocumentsByNotificationId(UUID entityId, int notificationId, LocalDateTime notificationDate, String notificationText);

    @Query("{ 'notificationData.scheduledTime': { '$lt': ?0 } }")
    List<AbstractNotificationDocument> findByScheduledTimeBefore(LocalDateTime time);

    @DeleteQuery("{ 'notificationData': { '$size': 0 } }")
    void deleteByIdIfNotificationDataIsEmpty();

}