package com.telran.notificationservice.service;


import com.telran.notificationservice.dto.NotificationDataDto;
import com.telran.notificationservice.persistence.contact_notifications.ContactNotificationsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;


@Service
@RequiredArgsConstructor
public class SseService {
    private final ContactNotificationsRepository notificationsRepository;
    private final Map<UUID, SseEmitter> clients = new HashMap<>();


    public SseEmitter subscribe(UUID clientId) {

        SseEmitter emitter = new SseEmitter(0L);
        clients.put(clientId, emitter);
        emitter.onCompletion(() -> clients.remove(clientId));
        emitter.onTimeout(() -> clients.remove(clientId));
        emitter.onError(e -> clients.remove(clientId));
        System.err.println("ClientId: " + clientId);
        System.err.println("Emmiter: " + emitter);
        return emitter;
    }


    public void sendMessages(Map<UUID, List<NotificationDataDto>> mapOfDocs) {
        if (clients.isEmpty()) return;
        mapOfDocs.forEach((k, v) -> {
            SseEmitter emitter = clients.get(k);
            List<Integer> list = new LinkedList<>();
            v.forEach(n -> {
                try {
                    emitter.send(n);
                    list.add(n.getNotificationId());
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
                notificationsRepository.deleteNotificationDocumentsById(k, list.toArray(new Integer[0]));
            });
        });
    }
}