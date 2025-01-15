package com.telran.notificationservice.service;

import com.telran.notificationservice.blocking.Lockable;
import com.telran.notificationservice.dto.*;
import com.telran.notificationservice.error.DatabaseException.DatabaseAddingException;
import com.telran.notificationservice.error.DatabaseException.DatabaseDeletingException;
import com.telran.notificationservice.error.Exceptions.NotificationNotFoundException;
import com.telran.notificationservice.error.Exceptions.TargetEntityNotFoundException;
import com.telran.notificationservice.logging.Loggable;
import com.telran.notificationservice.persistence.AbstractNotificationDocument;
import com.telran.notificationservice.persistence.EntityTypes;
import com.telran.notificationservice.persistence.INotificationsRepository;
import com.telran.notificationservice.persistence.contact_notifications.ContactNotificationDocument;
import com.telran.notificationservice.persistence.contact_notifications.ContactNotificationsRepository;
import com.telran.notificationservice.persistence.group_notifications.GroupNotificationDocument;
import com.telran.notificationservice.persistence.group_notifications.GroupNotificationsRepository;
import com.telran.notificationservice.persistence.lecturer_notifications.LecturerNotificationDocument;
import com.telran.notificationservice.persistence.lecturer_notifications.LecturerNotificationsRepository;
import com.telran.notificationservice.persistence.student_notifications.StudentNotificationDocument;
import com.telran.notificationservice.persistence.student_notifications.StudentNotificationsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final ContactNotificationsRepository contactNotificationsRepository;
    private final StudentNotificationsRepository studentNotificationsRepository;
    private final GroupNotificationsRepository groupNotificationsRepository;
    private final LecturerNotificationsRepository lecturerNotificationsRepository;
    private final SseService sseService;
    private final NotificationsRabbitProducer rabbitProducer;

    @Loggable
    public AbstractNotificationDocument getById(UUID id, EntityTypes entityType) {
        INotificationsRepository<? extends AbstractNotificationDocument> repository = chooseRepository(entityType).getRepository();
        return repository.findByEntityId(id).orElseThrow(() -> new NotificationNotFoundException(id.toString()));
    }

    @Loggable
    public EntityTypes[] getEntityTypes() {
        return EntityTypes.values();
    }

    private TargetEntityDataDto<? extends AbstractNotificationDocument> chooseRepository(EntityTypes entityType) {
        return switch (entityType) {
            case CONTACT, contact ->
                    new TargetEntityDataDto<>(contactNotificationsRepository, ContactNotificationDocument::new);
            case STUDENT, student ->
                    new TargetEntityDataDto<>(studentNotificationsRepository, StudentNotificationDocument::new);
            case GROUP, group ->
                    new TargetEntityDataDto<>(groupNotificationsRepository, GroupNotificationDocument::new);
            case LECTURER, lecturer ->
                    new TargetEntityDataDto<>(lecturerNotificationsRepository, LecturerNotificationDocument::new);
        };
    }

    @Loggable
    @Lockable
    @Transactional
    @SuppressWarnings("unchecked")
    public <T extends AbstractNotificationDocument> void addNotificationToId(UUID entityId, NotificationDto notificationDto, EntityTypes entityType, UUID recipientId) {
        String entityName = entityType.toString();
        if (!checkEntity(entityId, entityType)) throw new TargetEntityNotFoundException(entityType, entityId);
        TargetEntityDataDto<? extends AbstractNotificationDocument> targetEntityDataDto = chooseRepository(entityType);
        INotificationsRepository<T> repository = (INotificationsRepository<T>) targetEntityDataDto.getRepository();
        T notification = (T) repository.findByEntityId(entityId).orElse(null);

        NotificationDataDto notificationDataDto;
        if(notification == null){
            List<NotificationDataDto> newList = new ArrayList<>();
            notificationDataDto = new NotificationDataDto(0, notificationDto, recipientId, entityName);
            newList.add(notificationDataDto);
            notification = (T) targetEntityDataDto.getConstructor().apply(entityId, newList);
        }else {
            System.out.println(notification.getNotificationData());
            List<NotificationDataDto> notificationsList = notification.getNotificationData();
            notificationDataDto = new NotificationDataDto(notificationsList.isEmpty()? 0: notificationsList.get(notificationsList.size() - 1).getNotificationId() + 1, notificationDto, recipientId, entityName);
            notificationsList.add(notificationDataDto);
        }
        System.out.println(notification.getNotificationData());
        try {
            repository.save(notification);
        } catch (Exception e) {
            throw new DatabaseAddingException(e.getMessage());
        }
    }

    @Loggable
    @Lockable
    @Transactional
    public void deleteNotificationById(UUID entityId, Integer[] elementNumbers, EntityTypes entityType) {
        INotificationsRepository<? extends AbstractNotificationDocument> repository = chooseRepository(entityType).getRepository();
        if (!repository.existsByEntityId(entityId))
            throw new NotificationNotFoundException(entityId.toString());
        if (elementNumbers != null) {
            repository.deleteNotificationDocumentsById(entityId, elementNumbers);//TODO как быть с ошибками отсутствия такого элемента
        } else {
            try {
                repository.deleteByEntityId(entityId);
            } catch (Exception e) {
                throw new DatabaseDeletingException(e.getMessage());
            }
        }
    }

    @Loggable
    @Lockable
    @Transactional
    public void updateById(UUID entityId, NotificationUpdateDataDto notificationDataDto, EntityTypes entityType) {
        INotificationsRepository<? extends AbstractNotificationDocument> repository = chooseRepository(entityType).getRepository();
        if (!repository.existsByEntityId(entityId))
            throw new NotificationNotFoundException(entityId.toString());
        try {
            repository.updateNotificationDocumentsByEntityId(entityId, notificationDataDto.getNotificationId(), notificationDataDto.getScheduledTime(), notificationDataDto.getNotificationText());
        } catch (Exception e) {
            throw new DatabaseDeletingException(e.getMessage());
        }
    }

    @Loggable
    @Scheduled(fixedRate = 60000)
    public void sendScheduledNotifications() {
        List.of(contactNotificationsRepository, studentNotificationsRepository, groupNotificationsRepository, lecturerNotificationsRepository)
                .forEach(repository -> repository.findByScheduledTimeBefore(LocalDateTime.now())
                        .forEach(n -> {
                            List<Integer> list = new LinkedList<>();
                            UUID entityId = n.getEntityId();
                            n.getNotificationData()
                                    .forEach(nd -> {
                                        if (sseService.sendMessages(new NotificationSendDataDto(entityId, nd.getScheduledTime(), nd.getEntityName(), nd.getNotificationText()), nd.getRecipientId()))
                                            list.add(nd.getNotificationId());
                                    });
                            if (!list.isEmpty()) {
                                repository.deleteNotificationDocumentsById(entityId, list.toArray(new Integer[0]));
                                repository.deleteByIdIfNotificationDataIsEmpty(entityId);
                            }

                        })
                );

    }


    private boolean checkEntity(UUID entityId, EntityTypes entityType) {
        return switch (entityType) {
            case CONTACT, contact -> rabbitProducer.sendContactExists(entityId);
            case STUDENT, student -> rabbitProducer.sendStudentExists(entityId);
            case LECTURER, lecturer -> rabbitProducer.sendLecturerExists(entityId);
            case GROUP, group -> rabbitProducer.sendGroupExists(entityId);
        };
    }

}
