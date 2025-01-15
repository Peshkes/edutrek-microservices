package com.telran.notificationservice.persistence;


import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface INotificationsRepository<T extends AbstractNotificationDocument> extends MongoRepository<T, UUID> {


    @Query("{ 'entityId': ?0}")
    @Update("{ '$pull' : { 'notificationData': {'notificationId': {$in: ?1}}  } } ")
    void deleteNotificationDocumentsById(UUID entityId, Integer[] elementNumbers);

    @Query(value = "{ 'entityId': ?0, 'notificationData.notificationId': ?1 }")
    @Update(value = "{ '$set': { 'notificationData.$.scheduledTime': ?2, 'notificationData.$.notificationText': ?3 } }")
    void updateNotificationDocumentsByEntityId(UUID entityId, int notificationId, LocalDateTime notificationDate, String notificationText);

    @Query("{ 'notificationData.scheduledTime': { '$lt': ?0 } }")
    List<AbstractNotificationDocument> findByScheduledTimeBefore(LocalDateTime time);

    @DeleteQuery("{ '_id': ?0, 'notificationData': { '$size': 0 } }")
    void deleteByIdIfNotificationDataIsEmpty(UUID id);

    Optional<AbstractNotificationDocument> findByEntityId(UUID entityId);

    boolean existsByEntityId(UUID entityId);

    void deleteByEntityId(UUID entityId);
}