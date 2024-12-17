package com.telran.notificationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.telran.notificationservice.dto.RabbitMessageDto;
import com.telran.notificationservice.error.Exceptions.*;
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
public class NotificationsRabbitProducer {

    private final RabbitTemplate rabbitTemplate;


    public boolean sendContactExists(UUID contactId) {
        return sendMessage("existsById", contactId, "contacts_key", Boolean.class);
    }


    public boolean sendStudentExists(UUID studentId) {
        return sendMessage("existsById", studentId, "students_key", Boolean.class);
    }


    public boolean sendLecturerExists(UUID lecturerId) {
        return sendMessage("existsById", lecturerId, "lecturers_key", Boolean.class);
    }

    public boolean sendGroupExists(UUID groupId) {
        return sendMessage("existsById", groupId, "groups_key", Boolean.class);
    }

    @Retryable(retryFor = {JsonProcessingException.class}, backoff = @Backoff(delay = 50))
    private <R> R sendMessage(String type, Object payload, String routingKey, Class<R> responseType) {
        RabbitMessageDto messageDto = sendAndReceiveRabbitMessage(type, payload, routingKey);
        Object result = messageDto.getPayload();
        if (responseType.isInstance(result)) {
            return responseType.cast(result);
        }
        throw new IllegalArgumentException("Unexpected response type: " + result.getClass().getName());
    }

    private RabbitMessageDto sendAndReceiveRabbitMessage(String type, Object payload, String routingKey) {
        RabbitMessageDto messageDto = prepareRabbitMessageDto(type, payload);
        messageDto = (RabbitMessageDto) rabbitTemplate.convertSendAndReceive("edutreck_direct_e", routingKey, messageDto);
        if (messageDto == null) throw new IllegalArgumentException("Response is null");
        if (messageDto.getType().equals("error")) {
            log.info("Response from RabbitMQ. RequestId: {} - Method: {} - Payload: {}", messageDto.getCorrelationId(), type, messageDto.getPayload());
            throw new UnsuccessfulRequest(messageDto.getPayload().toString());
        }
        log.info("Response from RabbitMQ. RequestId: {} - Method: {} - Payload: {}", messageDto.getCorrelationId(), type, messageDto.getPayload());
        return messageDto;
    }

    //Utility methods
    private String getRequestId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String customId = null;
        if (attributes != null)
            customId = attributes.getRequest().getHeader("X-Request-ID");
        return customId;
    }

    private RabbitMessageDto prepareRabbitMessageDto(String type, Object payload) {
        String requestId = getRequestId();
        RabbitMessageDto messageDto = new RabbitMessageDto(type, payload, requestId);
        log.info("Request to RabbitMQ. RequestId: {} - Method: {} - Payload: {}", requestId, type, payload);
        return messageDto;
    }

}
