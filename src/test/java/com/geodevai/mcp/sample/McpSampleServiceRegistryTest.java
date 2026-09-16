package com.geodevai.mcp.sample;

import com.geodevai.data.model.User;
import com.geodevai.data.repository.UserRepository;
import com.geodevai.mcp.service.McpRegistryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class McpSampleServiceRegistryTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void testServiceIsRegisteredAsMcpService() {
        McpRegistryService registry = new McpRegistryService();
        registry.registerBean(new McpSampleService(null));
        var tools = registry.listTools();
        assertTrue(tools.stream().anyMatch(t -> t.getName().equals("getSystemStatus")));
    }

    @Test
    void testToolsListContainsAllExpectedTools() {
        McpRegistryService registry = new McpRegistryService();
        registry.registerBean(new McpSampleService(null));
        var tools = registry.listTools();
        assertTrue(tools.stream().anyMatch(t -> t.getName().equals("getSystemStatus")));
        assertTrue(tools.stream().anyMatch(t -> t.getName().equals("searchUsers")));
        assertTrue(tools.stream().anyMatch(t -> t.getName().equals("getCalendarEvents")));
    }

    @Test
    void testResourcesListContainsConfiguration() {
        McpRegistryService registry = new McpRegistryService();
        registry.registerBean(new McpSampleService(null));
        var resources = registry.listResources();
        assertTrue(resources.stream().anyMatch(r -> r.getUriPattern().equals("system://configuration")));
    }

    @Test
    void testGetSystemStatusReturnsValidResult() {
        McpRegistryService registry = new McpRegistryService();
        registry.registerBean(new McpSampleService(null));
        Map<String, Object> args = new HashMap<>();
        var response = registry.executeTool("getSystemStatus", args);
        assertNotNull(response);
        assertNotNull(response.getResult());
        assertTrue(response.getResult().has("uptime_ms"));
        assertTrue(response.getResult().has("cpu_load"));
        assertTrue(response.getResult().has("memory_used_bytes"));
    }

    @Test
    void testSearchUsersToolWorks() {
        User user = new User();
        user.setDisplayName("Test User");
        user.setContactEmail("test@example.com");
        user.setRole("USER");
        when(userRepository.findAll()).thenReturn(List.of(user));

        McpRegistryService registry = new McpRegistryService();
        registry.registerBean(new McpSampleService(userRepository));
        Map<String, Object> args = Map.of("query", "Test");
        var response = registry.executeTool("searchUsers", args);
        assertNotNull(response);
        assertNotNull(response.getResult());
        assertTrue(response.getResult().isArray());
        assertFalse(response.getResult().isEmpty());
    }

    @Test
    void testGetCalendarEventsToolWorks() {
        McpRegistryService registry = new McpRegistryService();
        registry.registerBean(new McpSampleService(null));
        Map<String, Object> args = Map.of("maxResults", 10, "timeMin", "2026-01-01T00:00:00Z");
        var response = registry.executeTool("getCalendarEvents", args);
        assertNotNull(response);
        assertNotNull(response.getResult());
        assertTrue(response.getResult().has("maxResults"));
        assertTrue(response.getResult().has("timeMin"));
    }
}
