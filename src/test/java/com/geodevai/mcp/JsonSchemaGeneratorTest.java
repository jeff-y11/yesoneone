package com.geodevai.mcp;

import com.geodevai.mcp.service.JsonSchemaGenerator;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonSchemaGeneratorTest {

    @Test
    void testMapTypeForPrimitives() {
        assertEquals("string", JsonSchemaGenerator.mapType(String.class));
        assertEquals("integer", JsonSchemaGenerator.mapType(int.class));
        assertEquals("integer", JsonSchemaGenerator.mapType(Integer.class));
        assertEquals("number", JsonSchemaGenerator.mapType(double.class));
        assertEquals("number", JsonSchemaGenerator.mapType(Double.class));
        assertEquals("boolean", JsonSchemaGenerator.mapType(boolean.class));
        assertEquals("boolean", JsonSchemaGenerator.mapType(Boolean.class));
    }

    @Test
    void testMapTypeForSpecialTypes() {
        assertEquals("string", JsonSchemaGenerator.mapType(java.util.UUID.class));
        assertEquals("string", JsonSchemaGenerator.mapType(java.time.Instant.class));
        assertEquals("array", JsonSchemaGenerator.mapType(java.util.List.class));
        assertEquals("object", JsonSchemaGenerator.mapType(java.util.Map.class));
    }

    @Test
    void testGenerateSchema() throws NoSuchMethodException {
        Method method = TestBean.class.getMethod("exampleTool", String.class, Integer.class, Boolean.class);
        Map<String, Object> schema = JsonSchemaGenerator.generateSchema(method.getParameters());

        assertEquals(3, schema.size());

        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map<String, Object>) schema.get("properties");
        assertNotNull(properties);
        assertTrue(properties.containsKey("arg0"));
        assertTrue(properties.containsKey("arg1"));
        assertTrue(properties.containsKey("arg2"));
    }

    @Test
    void testGenerateSchemaWithMcpParameter() throws NoSuchMethodException {
        Method method = TestBean.class.getMethod("annotatedTool", String.class);
        Map<String, Object> schema = JsonSchemaGenerator.generateSchema(method.getParameters());

        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map<String, Object>) schema.get("properties");
        @SuppressWarnings("unchecked")
        Map<String, Object> propSchema = (Map<String, Object>) properties.get("arg0");
        assertEquals("string", propSchema.get("type"));
        assertEquals("test description", propSchema.get("description"));
    }

    static class TestBean {
        public void exampleTool(String arg0, Integer arg1, Boolean arg2) {}
        public void annotatedTool(@com.geodevai.mcp.annotation.McpParameter(name = "name", description = "test description") String arg0) {}
    }
}
