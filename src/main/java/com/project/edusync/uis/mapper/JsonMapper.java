package com.project.edusync.uis.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JsonMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Converts JSON String (DB) -> List (DTO)
    public List<String> mapFromJson(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            // Handle error or return empty list
            return Collections.emptyList();
        }
    }

    // Converts List (DTO) -> JSON String (DB)
    public String mapToJson(List<String> list) {
        if (list == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
}