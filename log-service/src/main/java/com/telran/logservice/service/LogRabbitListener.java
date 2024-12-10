package com.telran.logservice.service;

import com.telran.logservice.dto.AddLogDto;
import com.telran.logservice.dto.RabbitMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class LogRabbitListener {

    private final LogService service;

    @RabbitListener(queues = "logs_q")
    void receiveMessage(RabbitMessageDto message) {
        Object payload = message.getPayload();
        String type = message.getType();
        String correlationId = message.getCorrelationId();
        log.info("RequestId: {} - Method {} from RabbitMQ. Payload: {}", correlationId, type, payload);

        if (Objects.equals(type, "deleteById")) {
            UUID id = (UUID) payload;
            service.deleteById(id);
        } else if (Objects.equals(type, "add")) {
            AddLogDto dto = (AddLogDto) payload;
            service.add(dto.getId(), dto.getLog());
        }
    }
}
