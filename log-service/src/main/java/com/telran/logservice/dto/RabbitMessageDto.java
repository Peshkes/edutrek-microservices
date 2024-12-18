package com.telran.logservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.telran.logservice.config.RabbitMessageDtoDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonDeserialize(using = RabbitMessageDtoDeserializer.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RabbitMessageDto {
    private String type;
    private Object payload;
    private String correlationId;
}
