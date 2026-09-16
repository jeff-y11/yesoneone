package com.geodevai.mcp.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geodevai.mcp.controller.McpController;
import com.geodevai.mcp.model.McpRequest;
import com.geodevai.mcp.service.McpRegistryService;
import com.geodevai.mcp.service.McpSessionRegistry;
import com.geodevai.mcp.service.McpKeyService;
import com.geodevai.mcp.annotation.McpService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class McpSampleServiceTest {

    @Mock
    private McpKeyService keyService;

    @Mock
    private McpSessionRegistry sessionRegistry;

    @Mock
    private McpRegistryService registryService;

    @InjectMocks
    private McpController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testAnnotatedClassHasMcpServiceAnnotation() {
        assertTrue(McpSampleService.class.isAnnotationPresent(McpService.class));
    }

    @Test
    void testControllerToolsCall() throws Exception {
        when(sessionRegistry.isActive(anyString())).thenReturn(true);
        when(keyService.validateKey(anyString())).thenReturn(true);

        McpRequest request = new McpRequest();
        request.setMethod("tools/call");
        request.setId(1);
        var params = new ObjectMapper().createObjectNode();
        params.put("name", "getSystemStatus");
        params.set("arguments", new ObjectMapper().createObjectNode());
        request.setParams(params);

        var httpRequest = new MockHttpServletRequest();
        httpRequest.addHeader("Authorization", "Bearer test-key-1234567890");

        var result = controller.handleMessage(request, httpRequest);
        assertEquals("2.0", result.get("jsonrpc").asText());
        assertTrue(result.has("result"));
    }

    @Test
    void testControllerToolsList() throws Exception {
        when(sessionRegistry.isActive(anyString())).thenReturn(true);
        when(keyService.validateKey(anyString())).thenReturn(true);
        var toolsNode = objectMapper.createObjectNode();
        toolsNode.set("tools", objectMapper.createArrayNode());
        when(registryService.listToolsAsJson()).thenReturn(toolsNode);

        McpRequest request = new McpRequest();
        request.setMethod("tools/list");
        request.setId(1);
        request.setParams(objectMapper.createObjectNode());

        var httpRequest = new MockHttpServletRequest();
        httpRequest.addHeader("Authorization", "Bearer test-key-1234567890");

        var result = controller.handleMessage(request, httpRequest);
        assertEquals("2.0", result.get("jsonrpc").asText());
        assertTrue(result.has("result"));
        assertTrue(result.get("result").has("tools"));
    }
}
