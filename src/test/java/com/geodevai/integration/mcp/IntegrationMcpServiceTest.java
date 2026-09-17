package com.geodevai.integration.mcp;

import com.geodevai.data.repository.IntegrationRepository;
import com.geodevai.integration.IntegrationDispatcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class IntegrationMcpServiceTest {

    @Mock
    private IntegrationDispatcher dispatcher;

    @Mock
    private IntegrationRepository integrationRepository;

    @InjectMocks
    private IntegrationMcpService mcpService;

    @Test
    void testRunIntegration_ValidId_ReturnsRunning() {
        doNothing().when(dispatcher).dispatch(any(UUID.class), any());

        Map<String, Object> result = mcpService.runIntegration(UUID.randomUUID().toString());

        assertEquals("RUNNING", result.get("status"));
        verify(dispatcher, times(1)).dispatch(any(UUID.class), eq(integrationRepository));
    }

    @Test
    void testRunIntegration_InvalidId_ReturnsFailed() {
        doThrow(new RuntimeException("Integration not found"))
                .when(dispatcher).dispatch(any(UUID.class), any());

        Map<String, Object> result = mcpService.runIntegration("invalid-uuid");

        assertEquals("FAILED", result.get("status"));
        assertTrue(result.get("error").toString().contains("Integration not found"));
    }
}
