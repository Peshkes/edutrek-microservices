package com.telran.branchservice.service;

import com.telran.branchservice.dto.RabbitMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class BranchRabbitListener {

    private final BranchService service;

    @RabbitListener(queues = "branches_q")
    RabbitMessageDto receiveMessage(RabbitMessageDto message) {
        int branchId = (int) message.getPayload();
        String type = message.getType();
        String correlationId = message.getCorrelationId();
        log.info("RequestId: {} - Method {} from RabbitMQ. Payload: {}", correlationId, type, branchId);
        try {
            message.setPayload(switch (message.getType()) {
                case "getNameById" -> service.getById(branchId).getBranchName();
                case "existsById" -> service.existsById(branchId);
                default -> null;
            });
        } catch (Exception e) {
            message.setType("error");
            message.setPayload(e.getMessage());
        }
        return message;
    }
}
