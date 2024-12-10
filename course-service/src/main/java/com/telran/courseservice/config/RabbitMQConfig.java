package com.telran.courseservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telran.courseservice.dto.RabbitMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {
    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    @Bean
    public MessageConverter messageConverter() {
        return new CustomMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new CustomMessageConverter());
        return rabbitTemplate;
    }

    private static class CustomMessageConverter implements MessageConverter {

        private final ObjectMapper objectMapper;

        public CustomMessageConverter() {
            this.objectMapper = new ObjectMapper();
        }

        @Override
        public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
            try {
                byte[] body = objectMapper.writeValueAsBytes(object);
                log.info("Sending message to RabbitMQ: {}", new String(body));
                return new Message(body, messageProperties);
            } catch (Exception e) {
                throw new MessageConversionException("Error converting object to message", e);
            }
        }

        @Override
        public RabbitMessageDto fromMessage(Message message) throws MessageConversionException {
            try {
                log.info("Received message from RabbitMQ: {}", message);
                byte[] body = message.getBody();
                log.info("Message body: {}", new String(body));
                return objectMapper.readValue(body, RabbitMessageDto.class);
            } catch (Exception e) {
                log.error("Error deserializing message", e);
                throw new MessageConversionException("Error deserializing message", e);
            }
        }
    }
}