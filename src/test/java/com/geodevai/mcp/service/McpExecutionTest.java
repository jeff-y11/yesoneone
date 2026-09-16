package com.geodevai.mcp;

import com.geodevai.mcp.annotation.McpService;
import com.geodevai.mcp.annotation.McpParameter;
import com.geodevai.mcp.annotation.McpTool;
import com.geodevai.mcp.service.JsonSchemaGenerator;
import com.geodevai.mcp.service.McpRegistryService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class McpExecutionTest {

    @Test
    void testMapTypeForVariousTypes() {
        assertEquals("string", JsonSchemaGenerator.mapType(String.class));
        assertEquals("integer", JsonSchemaGenerator.mapType(Integer.class));
        assertEquals("number", JsonSchemaGenerator.mapType(Double.class));
        assertEquals("boolean", JsonSchemaGenerator.mapType(Boolean.class));
    }

    @Test
    void testSchemaGenerationForComplexMethod() throws NoSuchMethodException {
        Method method = SampleService.class.getMethod("complexMethod", String.class, Integer.class, Double.class, Boolean.class);
        Map<String, Object> schema = JsonSchemaGenerator.generateSchema(method.getParameters());

        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map<String, Object>) schema.get("properties");
        assertEquals(4, properties.size());
        assertEquals("string", ((Map<String, Object>) properties.get("arg0")).get("type"));
        assertEquals("integer", ((Map<String, Object>) properties.get("arg1")).get("type"));
        assertEquals("number", ((Map<String, Object>) properties.get("arg2")).get("type"));
        assertEquals("boolean", ((Map<String, Object>) properties.get("arg3")).get("type"));
    }

    @Test
    void testErrorHandlingForMissingTool() {
        McpRegistryService registry = new McpRegistryService();
        assertThrows(com.geodevai.mcp.exception.McpMethodNotFoundException.class, () -> {
            registry.executeTool("missingTool", new HashMap<>());
        });
    }

    @Test
    void testErrorHandlingForMissingParams() {
        McpRegistryService registry = new McpRegistryService();
        registry.registerBean(new TestBean());
        assertThrows(com.geodevai.mcp.exception.McpInvalidParamsException.class, () -> {
            registry.executeTool("testTool", null);
        });
    }

    @McpService
    static class TestBean {
        @McpTool(name = "testTool", description = "Test tool")
        public String testTool(@McpParameter(name = "name", description = "Name") String name) {
            return "Hello " + name;
        }
    }

    static class SampleService {
        public void complexMethod(String arg0, Integer arg1, Double arg2, Boolean arg3) {}
    }
}
