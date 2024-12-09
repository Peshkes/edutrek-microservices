package com.telran.courseservice.service;

import com.telran.courseservice.dto.RabbitMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class CoursesRabbitListener {
    private final CourseService service;
    //  ObjectMapper mapper = new ObjectMapper();


    @RabbitListener(queues = "courses_q")
    RabbitMessageDto receiveMessage(RabbitMessageDto message) {
        UUID courseId = UUID.fromString((String) message.getPayload());
        String type = message.getType();
        String correlationId = message.getCorrelationId();
        log.info(
                "RequestId: {} - Method {} from RabbitMQ. Payload: {}",
                correlationId,
                type,
                courseId
        );
        message.setPayload(switch (message.getType()) {
            case "getEntityById" -> service.getById(courseId);
            case "getNameById" -> service.getById(courseId).getCourseName();
            case "existsById" -> service.existsById(courseId);
            default -> null;
        });
        return message;
    }

//    private void modifyContext() {
//        ServletRequestAttributes attributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
//        log.error(String.valueOf(attributes));
//    }
}
