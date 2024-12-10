package com.telran.branchservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RabbitMessageDto {
    private String type;
    private Object payload;
    private String correlationId;
}
