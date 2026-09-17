package com.geodevai.integration.mcp;

import com.geodevai.data.repository.IntegrationRepository;
import com.geodevai.integration.IntegrationDispatcher;
import com.geodevai.mcp.annotation.McpParameter;
import com.geodevai.mcp.annotation.McpService;
import com.geodevai.mcp.annotation.McpTool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@McpService
@RequiredArgsConstructor
public class IntegrationMcpService {

    private final IntegrationDispatcher dispatcher;
    private final IntegrationRepository integrationRepository;

    @McpTool(name = "runIntegration", description = "Triggers a full-pull of a PM integration by integrationId")
    public Map<String, Object> runIntegration(
            @McpParameter(name = "integrationId", description = "UUID of the integration to run", required = true) String integrationId) {
        try {
            dispatcher.dispatch(UUID.fromString(integrationId), integrationRepository);
            return Map.of("status", "RUNNING", "integrationId", integrationId);
        } catch (Exception e) {
            return Map.of("status", "FAILED", "error", e.getMessage());
        }
    }
}
