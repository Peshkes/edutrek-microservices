package com.telran.notificationservice.service;

import com.telran.notificationservice.dto.NotificationDataDto;
import com.telran.notificationservice.dto.NotificationDto;
import com.telran.notificationservice.error.DatabaseException.*;
import com.telran.notificationservice.error.Exceptions.*;
import com.telran.notificationservice.logging.Loggable;
import com.telran.notificationservice.persistence.AbstractNotificationDocument;
import com.telran.notificationservice.persistence.EntityTypes;
import com.telran.notificationservice.persistence.INotificationsRepository;
import com.telran.notificationservice.persistence.contact_notifications.ContactNotificationsRepository;
import com.telran.notificationservice.persistence.group_notifications.GroupNotificationsRepository;
import com.telran.notificationservice.persistence.lecturer_notifications.LecturerNotificationsRepository;
import com.telran.notificationservice.persistence.student_notifications.StudentNotificationsRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final ContactNotificationsRepository contactNotificationsRepository;
    private final StudentNotificationsRepository studentNotificationsRepository;
    private final GroupNotificationsRepository groupNotificationsRepository;
    private final LecturerNotificationsRepository lecturerNotificationsRepository;
    private final SseService sseService;


    @Loggable
    public AbstractNotificationDocument getById(UUID id, String entityType) {
        INotificationsRepository<? extends AbstractNotificationDocument> repository = chooseRepository(entityType);
        if (repository == null) throw new WrongEntityTypeException(entityType);
        return repository.findById(id).orElseThrow(() -> new NotificationNotFoundException(id.toString()));
    }

    @Loggable
    public EntityTypes[] getEntityTypes() {
        return EntityTypes.values();
    }

    @SuppressWarnings("unchecked")
    private <T extends AbstractNotificationDocument> INotificationsRepository<T> chooseRepository(String entityType) {
        return switch (entityType) {
            case "contact" -> (INotificationsRepository<T>) contactNotificationsRepository;
            case "student" -> (INotificationsRepository<T>) studentNotificationsRepository;
            case "group" -> (INotificationsRepository<T>) groupNotificationsRepository;
            case "lecturer" -> (INotificationsRepository<T>) lecturerNotificationsRepository;
            default -> null;
        };
    }

    @Loggable
    @Transactional
    @SuppressWarnings("unchecked")
    public <T extends AbstractNotificationDocument> void addNotificationToId(UUID entityId, NotificationDto notificationDto, String entityType) {
        INotificationsRepository<T> repository = chooseRepository(entityType);
        if (repository == null) throw new WrongEntityTypeException(entityType);
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
            notification = (T) new AbstractNotificationDocument(entityId, newList);
        }
        try {
            repository.save(notification);
        } catch (Exception e) {
            throw new DatabaseAddingException(e.getMessage());
        }
    }

    @Loggable
    @Transactional
    public void deleteNotificationById(UUID entityId, Integer[] elementNumbers, String entityType) {
        INotificationsRepository<? extends AbstractNotificationDocument> repository = chooseRepository(entityType);
        if (repository == null) throw new WrongEntityTypeException(entityType);
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
    public void updateById(UUID id, @Valid NotificationDataDto notificationDataDto, String entityType) {
        INotificationsRepository<? extends AbstractNotificationDocument> repository = chooseRepository(entityType);
        if (repository == null) throw new WrongEntityTypeException(entityType);
        if (!repository.existsById(id))
            throw new NotificationNotFoundException(id.toString());
        try {
            repository.updateNotificationDocumentsByNotificationId(id, notificationDataDto.getNotificationId(), notificationDataDto.getScheduledTime(), notificationDataDto.getNotificationText());
        } catch (Exception e) {
            throw new DatabaseDeletingException(e.getMessage());
        }
    }

    @Loggable
    @Scheduled(fixedRate = 60000)
    public void sendScheduledNotifications() {
        List<INotificationsRepository<? extends AbstractNotificationDocument>> collections = List.of(contactNotificationsRepository, studentNotificationsRepository, groupNotificationsRepository, lecturerNotificationsRepository);
        collections.forEach(repository -> {
            Map<UUID, List<NotificationDataDto>> mapOfDocs = repository.findAll().stream().collect(Collectors.toMap(AbstractNotificationDocument::getId, AbstractNotificationDocument::getNotificationData));
            mapOfDocs.entrySet().removeIf(entry -> {
                entry.getValue().removeIf(value -> value.getScheduledTime().isAfter(LocalDateTime.now()));
                return entry.getValue().isEmpty();
            });
            sseService.sendMessages(mapOfDocs);
        });


    }


}
