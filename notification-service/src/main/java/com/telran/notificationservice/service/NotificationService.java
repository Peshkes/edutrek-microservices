package com.telran.notificationservice.service;

import com.telran.notificationservice.dto.NotificationDataDto;
import com.telran.notificationservice.dto.NotificationDto;
import com.telran.notificationservice.dto.NotificationSendDataDto;
import com.telran.notificationservice.dto.TargetEntityDataDto;
import com.telran.notificationservice.error.DatabaseException.*;
import com.telran.notificationservice.error.Exceptions.*;
import com.telran.notificationservice.logging.Loggable;
import com.telran.notificationservice.persistence.AbstractNotificationDocument;
import com.telran.notificationservice.persistence.EntityTypes;
import com.telran.notificationservice.persistence.INotificationsRepository;
import com.telran.notificationservice.persistence.contact_notifications.ContactNotificationDocument;
import com.telran.notificationservice.persistence.contact_notifications.ContactNotificationsRepository;
import com.telran.notificationservice.persistence.group_notifications.GroupNotificationsRepository;
import com.telran.notificationservice.persistence.group_notifications.GroupNotificationDocument;
import com.telran.notificationservice.persistence.lecturer_notifications.LecturerNotificationsRepository;
import com.telran.notificationservice.persistence.lecturer_notifications.LecturerNotificationDocument;
import com.telran.notificationservice.persistence.student_notifications.StudentNotificationsRepository;
import com.telran.notificationservice.persistence.student_notifications.StudentNotificationDocument;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;


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
        TargetEntityDataDto<? extends AbstractNotificationDocument> targetEntityDataDto = chooseRepository(entityType);
        INotificationsRepository<? extends AbstractNotificationDocument> repository = targetEntityDataDto.getRepository();
        return repository.findById(id).orElseThrow(() -> new NotificationNotFoundException(id.toString()));
    }

    @Loggable
    public EntityTypes[] getEntityTypes() {
        return EntityTypes.values();
    }

    private TargetEntityDataDto<? extends AbstractNotificationDocument> chooseRepository(EntityTypes entityType) {
        return switch (entityType) {
            case CONTACT -> new TargetEntityDataDto<>(contactNotificationsRepository, ContactNotificationDocument::new);
            case STUDENT -> new TargetEntityDataDto<>(studentNotificationsRepository, StudentNotificationDocument::new);
            case GROUP -> new TargetEntityDataDto<>(groupNotificationsRepository, GroupNotificationDocument::new);
            case LECTURER ->
                    new TargetEntityDataDto<>(lecturerNotificationsRepository, LecturerNotificationDocument::new);
        };
    }

    @Loggable
    @Transactional
    @SuppressWarnings("unchecked")
    public <T extends AbstractNotificationDocument> void addNotificationToId(UUID entityId, NotificationDto notificationDto, EntityTypes entityType) {
        if (!checkEntity(entityId, entityType)) throw new TargetEntityNotFoundException(entityType, entityId);
        TargetEntityDataDto<? extends AbstractNotificationDocument> targetEntityDataDto = chooseRepository(entityType);
        INotificationsRepository<T> repository = (INotificationsRepository<T>) targetEntityDataDto.getRepository();
        T notification = repository.findById(entityId).orElse(null);
        NotificationDataDto notificationDataDto;
        if (notification != null) {
            List<NotificationDataDto> notificationsList = notification.getNotificationData();
            if (!notificationsList.isEmpty()) {
                notificationDataDto = new NotificationDataDto(notificationsList.get(notificationsList.size() - 1).getNotificationId() + 1, notificationDto);
                notificationsList.add(notificationDataDto);
            } else {
                throw new NotificationListIsEmptyException(entityId);
            }
        } else {
            List<NotificationDataDto> newList = new ArrayList<>();
            notificationDataDto = new NotificationDataDto(0, notificationDto);
            newList.add(notificationDataDto);
            notification = (T) targetEntityDataDto.getConstructor().apply(entityId, newList);
        }
        try {
            repository.save(notification);
        } catch (Exception e) {
            throw new DatabaseAddingException(e.getMessage());
        }
    }

    @Loggable
    @Transactional
    public void deleteNotificationById(UUID entityId, Integer[] elementNumbers, EntityTypes entityType) {
        TargetEntityDataDto<? extends AbstractNotificationDocument> targetEntityDataDto = chooseRepository(entityType);
        INotificationsRepository<? extends AbstractNotificationDocument> repository = targetEntityDataDto.getRepository();
        if (!repository.existsById(entityId))
            throw new NotificationNotFoundException(entityId.toString());
        if (elementNumbers != null) {
            repository.deleteNotificationDocumentsById(entityId, elementNumbers);//TODO как быть с ошибками отсутствия такого элемента
        } else {
            try {
                repository.deleteById(entityId);
            } catch (Exception e) {
                throw new DatabaseDeletingException(e.getMessage());
            }
        }
    }

    @Loggable
    @Transactional
    public void updateById(UUID entityId, @Valid NotificationDataDto notificationDataDto, EntityTypes entityType) {
        TargetEntityDataDto<? extends AbstractNotificationDocument> targetEntityDataDto = chooseRepository(entityType);
        INotificationsRepository<? extends AbstractNotificationDocument> repository = targetEntityDataDto.getRepository();
        if (!repository.existsById(entityId))
            throw new NotificationNotFoundException(entityId.toString());
        try {
            repository.updateNotificationDocumentsByNotificationId(entityId, notificationDataDto.getNotificationId(), notificationDataDto.getScheduledTime(), notificationDataDto.getNotificationText());
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
            case CONTACT -> rabbitProducer.sendContactExists(entityId);
            case STUDENT -> rabbitProducer.sendStudentExists(entityId);
            case LECTURER -> rabbitProducer.sendLecturerExists(entityId);
            case GROUP -> rabbitProducer.sendGroupExists(entityId);
        };
    }

}
