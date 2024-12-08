package com.telran.groupservice.service;

import com.telran.groupservice.dto.RabbitMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupsRabbitProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(RabbitMessageDto message, String routingKey) {
        rabbitTemplate.convertAndSend(
                "edutreck_direct_e",
                routingKey,
                message
        );
        System.out.println("Message sent: " + message);
    }


}