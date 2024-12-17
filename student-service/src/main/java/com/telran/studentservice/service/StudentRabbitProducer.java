package com.telran.studentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.telran.studentservice.dto.AddLogDto;
import com.telran.studentservice.dto.RabbitMessageDto;
import com.telran.studentservice.error.Exceptions.*;
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
public class StudentRabbitProducer {

    private final RabbitTemplate rabbitTemplate;

    //Statuses
    public String sendGetStatusNameById(int statusId) {
        return sendMessage("getNameById", statusId, "statuses_key", String.class);
    }

    public String sendGetStatusIdByName(String name) {
        return sendMessage("getIdByName", name, "statuses_key", String.class);
    }

    //Branches
    public String sendGetBranchNameById(int branchId) {
        return sendMessage("getNameById", branchId, "branches_key", String.class);
    }

    public boolean sendBranchExists(int branchId) {
        return sendMessage("existsById", branchId, "branches_key", Boolean.class);
    }

    //Courses
    public String sendGetCourseNameById(UUID courseId) {
        return sendMessage("getNameById", courseId, "courses_key", String.class);
    }

    public boolean sendCourseExists(UUID courseId) {
        return sendMessage("existsById", courseId, "courses_key", Boolean.class);
    }

    //Logs
    public void sendAddLog(UUID logId, String text) {
        sendMessage("add", new AddLogDto(logId, text), "logs_key");
    }

    public void sendDeleteLogById(UUID logId) {
        sendMessage("deleteById", logId, "logs_key");
    }

    //Send message methods
    @Retryable(retryFor = {JsonProcessingException.class}, backoff = @Backoff(delay = 50))
    private <R> R sendMessage(String type, Object payload, String routingKey, Class<R> responseType) {
        RabbitMessageDto messageDto = sendAndReceiveRabbitMessage(type, payload, routingKey);
        Object result = messageDto.getPayload();
        if (responseType.isInstance(result)) {
            return responseType.cast(result);
        }
        throw new IllegalArgumentException("Unexpected response type: " + result.getClass().getName());
    }

    @Retryable(retryFor = {JsonProcessingException.class}, backoff = @Backoff(delay = 50))
    private void sendMessage(String type, Object payload, String routingKey) {
        RabbitMessageDto messageDto = prepareRabbitMessageDto(type, payload);
        rabbitTemplate.convertAndSend("edutreck_direct_e", routingKey, messageDto);
    }

    //Utility methods
    private String getRequestId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String customId = null;
        if (attributes != null)
            customId = attributes.getRequest().getHeader("X-Request-ID");
        return customId;
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

    private RabbitMessageDto prepareRabbitMessageDto(String type, Object payload) {
        String requestId = getRequestId();
        RabbitMessageDto messageDto = new RabbitMessageDto(type, payload, requestId);
        log.info("Request to RabbitMQ. RequestId: {} - Method: {} - Payload: {}", requestId, type, payload);
        return messageDto;
    }

}