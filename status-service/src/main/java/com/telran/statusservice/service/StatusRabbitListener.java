package com.telran.statusservice.service;

import com.telran.statusservice.dto.RabbitMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class StatusRabbitListener {

    private final StatusService service;

    @RabbitListener(queues = "statuses_q")
    RabbitMessageDto receiveMessage(RabbitMessageDto message) {
        int statusId = (int) message.getPayload();
        String type = message.getType();
        String correlationId = message.getCorrelationId();
        log.info("RequestId: {} - Method {} from RabbitMQ. Payload: {}", correlationId, type, statusId);
        message.setPayload(switch (message.getType()) {
            case "getEntityById" -> service.getById(statusId);
            case "getNameById" -> service.getById(statusId).getStatusName();
            case "existsById" -> service.existsById(statusId);
            default -> null;
        });
        return message;
    }
}
