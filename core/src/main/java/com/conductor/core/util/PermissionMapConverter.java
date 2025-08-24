package com.conductor.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * JPA converter for converting Map<String, String> permissions to/from JSON for database storage
 */
@Converter
@Slf4j
public class PermissionMapConverter implements AttributeConverter<Map<String, String>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(permissions);
        } catch (JsonProcessingException e) {
            log.error("Error converting permissions map to JSON", e);
            return "{}";
        }
    }

    @Override
    public Map<String, String> convertToEntityAttribute(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to permissions map", e);
            return new HashMap<>();
        }
    }
}
