package com.telran.contactservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.telran.contactservice.dto.AbstractStudentDto;
import com.telran.contactservice.dto.FindStudentsDto;
import com.telran.contactservice.dto.RabbitMessageDto;
import com.telran.contactservice.dto.StudentsDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class ContactRabbitProducer {

    private final RabbitTemplate rabbitTemplate;

    //Statuses
    public String sendGetStatusNameById(int statusId) {
        return sendMessage("getNameById", statusId, "statuses_key", String.class);
    }

    public String sendGetStatusIdByName(String name) {
        return sendMessage("getNameById", name, "statuses_key", String.class);
    }

    //Branches
    public String sendGetBranchNameById(int branchId) {
        return sendMessage("getNameById", branchId, "branches_key", String.class);
    }

    //Courses
    public String sendGetCourseNameById(UUID courseId) {
        return sendMessage("getNameById", courseId, "courses_key", String.class);
    }

    //Logs
    public void sendAddLog(UUID logId, String text) {
        return sendMessage("existsById", statusId, "statuses_key", Boolean.class);
    }

    public void sendDeleteLogById(UUID logId) {
        sendMessage("deleteById", logId, "logs_key", Boolean.class);
    }

    //Students
    public boolean sendStudentExists(UUID studentId) {
        return sendMessage("existsById", studentId, "students_key", Boolean.class);
    }

    //Students
    public List<AbstractStudentDto> sendFindStudents(FindStudentsDto dto) {
        return sendMessage("existsById", dto, "students_key", Boolean.class);
    }

    public AbstractStudentDto sendSaveStudent(StudentsDataDto studentsDataDto) {
        return sendMessage("save", studentsDataDto, "students_key", Boolean.class);
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