package com.telran.contactservice.service;

import com.telran.contactservice.dto.RabbitMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class ContactRabbitListener {

    private final ContactsService service;

    @RabbitListener(queues = "contacts_q")
    RabbitMessageDto receiveMessage(RabbitMessageDto message) {
        UUID lecturerId = UUID.fromString((String) message.getPayload());
        String type = message.getType();
        String correlationId = message.getCorrelationId();
        log.info("RequestId: {} - Method {} from RabbitMQ. Payload: {}", correlationId, type, lecturerId);
        try {
            message.setPayload(service.existsById(lecturerId));
        } catch (Exception e) {
            message.setType("error");
            message.setPayload(e.getMessage());
        }
        return message;
    }
}
