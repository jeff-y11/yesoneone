package com.geodevai.mcp.sample;

import com.geodevai.data.model.User;
import com.geodevai.data.repository.UserRepository;
import com.geodevai.mcp.annotation.McpParameter;
import com.geodevai.mcp.annotation.McpResource;
import com.geodevai.mcp.annotation.McpService;
import com.geodevai.mcp.annotation.McpTool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.Map;

@Service
@McpService
@RequiredArgsConstructor
public class McpSampleService {

    private final UserRepository userRepository;

    @McpTool(name = "getSystemStatus", description = "Returns basic server status information including uptime, CPU load, and memory usage")
    public Map<String, Object> getSystemStatus() {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        Runtime runtime = Runtime.getRuntime();
        long uptime = runtimeBean.getUptime();
        double cpuLoad = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        return Map.of(
            "uptime_ms", uptime,
            "cpu_load", cpuLoad,
            "memory_used_bytes", usedMemory,
            "memory_total_bytes", totalMemory,
            "memory_free_bytes", freeMemory
        );
    }

    @McpTool(name = "searchUsers", description = "Searches for users matching the given query string")
    public List<User> searchUsers(@McpParameter(name = "query", description = "Search query string for user display names") String query) {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream()
                .filter(u -> u.getDisplayName() != null && u.getDisplayName().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }

    @McpTool(name = "getCalendarEvents", description = "Retrieves calendar events from the authenticated user's Google Calendar")
    public Map<String, Object> getCalendarEvents(
            @McpParameter(name = "maxResults", description = "Maximum number of events to retrieve") int maxResults,
            @McpParameter(name = "timeMin", description = "Minimum time for events in ISO 8601 format") String timeMin) {
        return Map.of(
            "maxResults", maxResults,
            "timeMin", timeMin,
            "events", List.of()
        );
    }

    @McpResource(uriPattern = "system://configuration", description = "Returns public system configuration metadata including active profiles and enabled services")
    public Map<String, Object> getSystemConfiguration() {
        return Map.of(
            "activeProfiles", List.of("default"),
            "baseUrl", "http://localhost:8080",
            "servicesEnabled", List.of("mcp", "auth", "data"),
            "database", "postgresql",
            "oauth2Provider", "google"
        );
    }
}
