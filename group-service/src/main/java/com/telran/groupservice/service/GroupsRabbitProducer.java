package com.telran.groupservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.telran.groupservice.dto.CourseDto;
import com.telran.groupservice.dto.RabbitMessageDto;
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
public class GroupsRabbitProducer {

    private final RabbitTemplate rabbitTemplate;

    public Boolean sendCourseExists(UUID courseId) {
        return sendMessage("existsById", courseId, "courses_key", Boolean.class);
    }

    public CourseDto sendGetCourseById(UUID courseId) {
        return sendMessage("getEntityById", courseId, "courses_key", CourseDto.class);
    }

    public String sendGetCourseNameById(UUID courseId) {
        return sendMessage("getNameById", courseId, "courses_key", String.class);
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