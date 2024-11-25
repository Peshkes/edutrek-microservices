package com.telran.notificationservice.controller;

import com.telran.notificationservice.dto.DeleteNotificationDto;
import com.telran.notificationservice.dto.NotificationDataDto;
import com.telran.notificationservice.dto.NotificationDto;
import com.telran.notificationservice.persistence.NotificationDocument;
import com.telran.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;


@Validated
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

//    @GetMapping("/all/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public List<NotificationDocument> getAllEntityNotifications(@PathVariable UUID id) {
//        return service.getAllEntityNotifications(id);
//    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NotificationDocument getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping("/{entityId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> addNotificationToId(@PathVariable UUID entityId, @RequestBody @Valid NotificationDto notificationDto) {
       service.addNotificationToId(entityId, notificationDto);
       return new ResponseEntity<>("Notification created", HttpStatus.CREATED);
    }

    @DeleteMapping("")
    public ResponseEntity<String> deleteById( @RequestBody @Valid DeleteNotificationDto deleteNotificationDto) {
        service.deleteNotificationById(deleteNotificationDto.getEntityId(), deleteNotificationDto.getNotificationId());
        return new ResponseEntity<>("Notification deleted", HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateById(@PathVariable UUID id, @RequestBody @Valid NotificationDataDto notificationDataDto) {
        service.updateById(id, notificationDataDto);
        return new ResponseEntity<>("Contact updated", HttpStatus.OK);
    }

}
