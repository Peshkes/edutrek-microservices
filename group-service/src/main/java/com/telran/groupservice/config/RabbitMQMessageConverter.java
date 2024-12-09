package com.telran.groupservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telran.groupservice.dto.RabbitMessageDto;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;

public class RabbitMQMessageConverter extends Jackson2JsonMessageConverter {

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        try {
            byte[] body = message.getBody();
            // Десериализация только в указанный тип
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(body, RabbitMessageDto.class);
        } catch (Exception e) {
            throw new MessageConversionException("Error deserializing message", e);
        }
    }
}