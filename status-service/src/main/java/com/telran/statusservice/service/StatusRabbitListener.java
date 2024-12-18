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
        String type = message.getType();
        Object statusPayload = message.getPayload();
        String correlationId = message.getCorrelationId();
        log.info("RequestId: {} - Method {} from RabbitMQ. Payload: {}", correlationId, type, statusPayload);
        try {
            message.setPayload(switch (type) {
                case "getIdByName" -> service.getIdByName((String) statusPayload);
                case "getNameById" -> service.getById((int) statusPayload).getStatusName();
                case "existsById" -> service.existsById((int) statusPayload);
                default -> null;
            });
        } catch (Exception e) {
            message.setType("error");
            message.setPayload(e.getMessage());
        }
        return message;
    }
}
