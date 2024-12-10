package com.telran.groupservice.service;

import com.telran.groupservice.dto.RabbitMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class GroupsRabbitListener {

    private final GroupService service;

    @RabbitListener(queues = "groups_q")
    RabbitMessageDto receiveMessage(RabbitMessageDto message) {
        UUID groupId = UUID.fromString((String) message.getPayload());
        String type = message.getType();
        String correlationId = message.getCorrelationId();
        log.info("RequestId: {} - Method {} from RabbitMQ. Payload: {}", correlationId, type, groupId);
        message.setPayload(switch (message.getType()) {
            case "getEntityById" -> service.getById(groupId);
            case "existsById" -> service.existsById(groupId);
            default -> null;
        });
        return message;
    }
}
