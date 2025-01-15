package com.telran.notificationservice.controller;

import com.telran.notificationservice.dto.DeleteNotificationDto;
import com.telran.notificationservice.dto.NotificationDto;
import com.telran.notificationservice.dto.NotificationUpdateDataDto;
import com.telran.notificationservice.persistence.AbstractNotificationDocument;
import com.telran.notificationservice.persistence.EntityTypes;
import com.telran.notificationservice.service.NotificationService;
import com.telran.notificationservice.service.SseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;


@Validated
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;
    private final SseService sseService;

//    @GetMapping("/all/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public List<NotificationDocument> getAllEntityNotifications(@PathVariable UUID id) {
//        return service.getAllEntityNotifications(id);
//    }

    @GetMapping("/{entityType}/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AbstractNotificationDocument getById(@PathVariable UUID id, @PathVariable EntityTypes entityType) {
        return service.getById(id, entityType);
    }

    @GetMapping("/entityTypes")
    @ResponseStatus(HttpStatus.OK)
    public EntityTypes[] getEntityTypes() {
        return service.getEntityTypes();
    }

    @PostMapping("/{entityType}/{entityId}/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> addNotificationToEntity(@PathVariable EntityTypes entityType, @PathVariable UUID entityId, @RequestBody @Valid NotificationDto notificationDto,@PathVariable UUID id ) {
       service.addNotificationToId(entityId, notificationDto, entityType, id);
       return new ResponseEntity<>("Notification for " + entityType + ": " + entityId + " created", HttpStatus.CREATED);
    }

    @DeleteMapping("/{entityType}")
    public ResponseEntity<String> deleteById(@PathVariable EntityTypes entityType, @RequestBody @Valid DeleteNotificationDto deleteNotificationDto ) {
        service.deleteNotificationById(deleteNotificationDto.getEntityId(), deleteNotificationDto.getNotificationId(), entityType);
        return new ResponseEntity<>("Notification deleted", HttpStatus.OK);
    }

    @PutMapping("/{entityType}/{entityId}")
    public ResponseEntity<String> updateById(@PathVariable EntityTypes entityType, @PathVariable UUID entityId, @RequestBody @Valid NotificationUpdateDataDto notificationDataDto) {
        service.updateById(entityId, notificationDataDto, entityType);
        return new ResponseEntity<>("Notification updated", HttpStatus.OK);
    }

    @GetMapping("/subscribe/{clientId}")
    public SseEmitter subscribe(@PathVariable java.util.UUID clientId) {
        return sseService.subscribe(clientId);
    }
}
