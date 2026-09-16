package com.geodevai.mcp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class JsonSchemaGenerator {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, Object> generateSchema(Parameter[] params) {
        Map<String, Object> schema = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        Map<String, Boolean> required = new HashMap<>();

        for (Parameter param : params) {
            String paramName = param.getName();
            Map<String, Object> propSchema = new HashMap<>();
            Class<?> type = param.getType();

            propSchema.put("type", mapType(type));
            propSchema.put("description", param.isAnnotationPresent(com.geodevai.mcp.annotation.McpParameter.class)
                    ? param.getAnnotation(com.geodevai.mcp.annotation.McpParameter.class).description()
                    : "");
            properties.put(paramName, propSchema);
            required.put(paramName, true);
        }

        schema.put("type", "object");
        schema.put("properties", properties);
        schema.put("required", required.keySet());
        return schema;
    }

    public static String mapType(Class<?> type) {
        if (type == String.class) return "string";
        if (type == int.class || type == Integer.class) return "integer";
        if (type == long.class || type == Long.class) return "integer";
        if (type == double.class || type == Double.class) return "number";
        if (type == float.class || type == Float.class) return "number";
        if (type == boolean.class || type == Boolean.class) return "boolean";
        if (type == java.util.UUID.class) return "string";
        if (type == java.time.Instant.class) return "string";
        if (type == java.time.LocalDateTime.class) return "string";
        if (type == java.time.LocalDate.class) return "string";
        if (type == java.time.LocalTime.class) return "string";
        if (type == java.time.ZonedDateTime.class) return "string";
        if (type == java.util.List.class) return "array";
        if (type == java.util.Map.class) return "object";
        if (Number.class.isAssignableFrom(type)) return "number";
        return "string";
    }
}
