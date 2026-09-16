package com.geodevai.mcp;

import com.geodevai.mcp.annotation.McpService;
import com.geodevai.mcp.annotation.McpTool;
import com.geodevai.mcp.annotation.McpParameter;
import com.geodevai.mcp.annotation.McpPrompt;
import com.geodevai.mcp.annotation.McpResource;
import com.geodevai.mcp.exception.McpInvalidParamsException;
import com.geodevai.mcp.exception.McpMethodNotFoundException;
import com.geodevai.mcp.model.McpPromptInfo;
import com.geodevai.mcp.model.McpResourceInfo;
import com.geodevai.mcp.model.McpResponse;
import com.geodevai.mcp.service.McpRegistryService;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class McpRegistryServiceTest {

    @Test
    void testScanAndListTools() {
        McpRegistryService registry = new McpRegistryService();
        registry.registerBean(new TestBean());

        List<com.geodevai.mcp.model.McpToolInfo> tools = registry.listTools();
        assertFalse(tools.isEmpty());
        assertTrue(tools.stream().anyMatch(t -> t.getName().equals("testTool")));
    }

    @Test
    void testScanAndListResources() {
        McpRegistryService registry = new McpRegistryService();
        registry.registerBean(new TestBean());

        List<McpResourceInfo> resources = registry.listResources();
        assertFalse(resources.isEmpty());
        assertTrue(resources.stream().anyMatch(r -> r.getUriPattern().equals("db://users/{id}")));
    }

    @Test
    void testScanAndListPrompts() {
        McpRegistryService registry = new McpRegistryService();
        registry.registerBean(new TestBean());

        List<McpPromptInfo> prompts = registry.listPrompts();
        assertFalse(prompts.isEmpty());
        assertTrue(prompts.stream().anyMatch(p -> p.getName().equals("greeting")));
    }

    @Test
    void testExecuteTool() {
        McpRegistryService registry = new McpRegistryService();
        registry.registerBean(new TestBean());

        Map<String, Object> args = new HashMap<>();
        args.put("name", "testUser");
        McpResponse response = registry.executeTool("testTool", args);
        assertNotNull(response);
        assertNotNull(response.getResult());
        assertEquals("Hello testUser", response.getResult().asText());
    }

    @Test
    void testExecuteToolNotFound() {
        McpRegistryService registry = new McpRegistryService();
        assertThrows(McpMethodNotFoundException.class, () -> {
            registry.executeTool("nonExistentTool", new HashMap<>());
        });
    }

    @Test
    void testExecuteToolMissingParams() {
        McpRegistryService registry = new McpRegistryService();
        registry.registerBean(new TestBean());
        assertThrows(McpInvalidParamsException.class, () -> {
            registry.executeTool("testTool", null);
        });
    }

    @McpService
    static class TestBean {
        @McpTool(name = "testTool", description = "Test tool")
        public String testTool(@McpParameter(name = "name", description = "User name") String name) {
            return "Hello " + name;
        }

        @McpResource(uriPattern = "db://users/{id}", description = "Fetch user by ID")
        public String getUser(String id) {
            return "User " + id;
        }

        @McpPrompt(name = "greeting", description = "Welcome prompt")
        public String greet() {
            return "Welcome!";
        }
    }
}
