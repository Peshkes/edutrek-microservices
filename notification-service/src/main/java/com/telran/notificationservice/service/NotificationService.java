package com.telran.notificationservice.service;

import com.telran.notificationservice.SseService;
import com.telran.notificationservice.dto.NotificationDataDto;
import com.telran.notificationservice.dto.NotificationDto;
import com.telran.notificationservice.error.DatabaseException.*;
import com.telran.notificationservice.error.ShareException.*;
import com.telran.notificationservice.logging.Loggable;
import com.telran.notificationservice.persistence.NotificationDocument;
import com.telran.notificationservice.persistence.NotificationsRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;




@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationsRepository repository;
    private final SseService sseService;


    @Loggable
    public NotificationDocument getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotificationNotFoundException(id.toString()));
    }

    @Loggable
    @Transactional
    public void addNotificationToId(UUID entityId, NotificationDto notificationDto) {

        NotificationDocument notification = repository.findById(entityId).orElse(null);
        NotificationDataDto notificationDataDto;
        if (notification != null) {
            List<NotificationDataDto> notificationsList = notification.getNotificationData();
            if(!notificationsList.isEmpty()) {
                notificationDataDto = new NotificationDataDto(notificationsList.get(notificationsList.size()-1).getNotificationId() + 1, notificationDto);
                notificationsList.add(notificationDataDto);
            }else{
                throw new NotificationListIsEmptyException(entityId);
            }
        } else {
            List<NotificationDataDto> newList = new ArrayList<>();
            System.out.println(notificationDto);
            notificationDataDto = new NotificationDataDto(0, notificationDto);
            newList.add(notificationDataDto);
            notification = new NotificationDocument(entityId, newList);
        }
        try {
            repository.save(notification);
        } catch (Exception e) {
            throw new DatabaseAddingException(e.getMessage());
        }
    }

    @Loggable
    @Transactional
    public void deleteNotificationById(UUID entityId, Integer[] elementNumbers) {
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
    public void updateById(UUID id, @Valid NotificationDataDto notificationDataDto) {
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
        Map<UUID, List<NotificationDataDto>> mapOfDocs = repository.findAll().stream().collect(Collectors.toMap(NotificationDocument::getId,
                NotificationDocument::getNotificationData));
        mapOfDocs.entrySet().removeIf(entry -> {
            entry.getValue().removeIf(value -> value.getScheduledTime().isAfter(LocalDateTime.now()));
            return entry.getValue().isEmpty();
        });
        sseService.sendMessages(mapOfDocs);
    }
}
