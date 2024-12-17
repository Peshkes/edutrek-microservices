package com.telran.notificationservice.service;


import com.telran.notificationservice.persistence.contact_notifications.ContactNotificationsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


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


    public boolean sendMessages(Object message, UUID recipientId) {
        if (clients.isEmpty()) return false;
        SseEmitter emitter = clients.get(recipientId);
        if (emitter == null) return false;
        try {
            emitter.send(message);
            return true;
        } catch (IOException e) {
            emitter.completeWithError(e);
            return false;
        }
    }

}