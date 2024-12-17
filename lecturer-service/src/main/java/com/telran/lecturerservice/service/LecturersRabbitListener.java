package com.telran.lecturerservice.service;

import com.telran.lecturerservice.dto.RabbitMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class LecturersRabbitListener {

    private final LectureService service;

    @RabbitListener(queues = "lecturers_q")
    RabbitMessageDto receiveMessage(RabbitMessageDto message) {
        UUID lecturerId = UUID.fromString((String) message.getPayload());
        String type = message.getType();
        String correlationId = message.getCorrelationId();
        log.info("RequestId: {} - Method {} from RabbitMQ. Payload: {}", correlationId, type, lecturerId);
        try {
            message.setPayload(switch (message.getType()) {
                case "getEntityById" -> service.getById(lecturerId);
                case "existsById" -> service.existsById(lecturerId);
                default -> null;
            });
        } catch (Exception e) {
            message.setType("error");
            message.setPayload(e.getMessage());
        }
        return message;
    }
}
