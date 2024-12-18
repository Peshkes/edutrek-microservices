package com.telran.logservice.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.telran.logservice.dto.AddLogDto;
import com.telran.logservice.dto.RabbitMessageDto;

import java.io.IOException;
import java.util.UUID;


public class RabbitMessageDtoDeserializer extends JsonDeserializer<RabbitMessageDto> {

    @Override
    public RabbitMessageDto deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode rootNode = p.getCodec().readTree(p);

        String type = rootNode.get("type").asText();
        String correlationId = rootNode.get("correlationId").asText();
        JsonNode payloadNode = rootNode.get("payload");

        Object payload;
        if ("add".equals(type)) {
            payload = p.getCodec().treeToValue(payloadNode, AddLogDto.class);
        } else if ("deleteById".equals(type)) {
            payload = UUID.fromString(payloadNode.asText());
        } else {
            throw new IllegalArgumentException("Unknown type: " + type);
        }

        return new RabbitMessageDto(type, payload, correlationId);
    }
}