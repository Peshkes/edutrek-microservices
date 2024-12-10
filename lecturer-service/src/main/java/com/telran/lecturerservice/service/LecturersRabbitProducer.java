package com.telran.lecturerservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.telran.lecturerservice.dto.AddLogDto;
import com.telran.lecturerservice.dto.RabbitMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class LecturersRabbitProducer {

    private final RabbitTemplate rabbitTemplate;

    public boolean sendBranchExists(int branchId) {
        return sendMessage("existsById", branchId, "branches_key", Boolean.class);
    }

    public String sendGetBranchNameById(int branchId) {
        return sendMessage("getNameById", branchId, "branches_key", String.class);
    }

    public void addLog(AddLogDto dto) {
        sendMessage("add", dto, "logs_key", Void.class);
    }

    public void deleteLog(UUID id) {
        sendMessage("deleteById", id, "logs_key", Void.class);
    }

    private String getRequestId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String customId = null;
        if (attributes != null)
            customId = attributes.getRequest().getHeader("X-Request-ID");
        return customId;
    }

    @Retryable(retryFor = {JsonProcessingException.class}, backoff = @Backoff(delay = 50))
    private <R> R sendMessage(String type, Object payload, String routingKey, Class<R> responseType) {
        String requestId = getRequestId();
        RabbitMessageDto messageDto = new RabbitMessageDto(type, payload, requestId);
        log.info("Request to RabbitMQ. RequestId: {} - Method: {} - Payload: {}", requestId, type, payload);
        messageDto = (RabbitMessageDto) rabbitTemplate.convertSendAndReceive("edutreck_direct_e", routingKey, messageDto);
        if (messageDto == null) throw new IllegalArgumentException("Response is null");
        Object result = messageDto.getPayload();
        log.info("Response from RabbitMQ. RequestId: {} - Method: {} - Payload: {}", requestId, type, messageDto.getPayload());
        if (responseType.isInstance(result))
            return responseType.cast(result);
        throw new IllegalArgumentException("Unexpected response type: " + result.getClass().getName());
    }
}