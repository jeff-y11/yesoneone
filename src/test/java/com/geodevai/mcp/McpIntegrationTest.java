package com.geodevai.mcp;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.geodevai.mcp.controller.McpController;
import com.geodevai.mcp.model.McpRequest;
import com.geodevai.mcp.model.McpResponse;
import com.geodevai.mcp.service.McpRegistryService;
import com.geodevai.mcp.service.McpSessionRegistry;
import com.geodevai.mcp.service.McpKeyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class McpIntegrationTest {

    @Mock
    private McpRegistryService registryService;

    @Mock
    private McpSessionRegistry sessionRegistry;

    @Mock
    private McpKeyService keyService;

    @InjectMocks
    private McpController controller;

    @Test
    void testHandleToolsList() throws Exception {
        when(sessionRegistry.isActive(any(String.class))).thenReturn(true);
        when(keyService.validateKey(any(String.class))).thenReturn(true);
        when(registryService.listToolsAsJson()).thenReturn(JsonNodeFactory.instance.objectNode());

        var request = new McpRequest();
        request.setMethod("tools/list");
        request.setId(1);
        request.setParams(JsonNodeFactory.instance.objectNode());

        var httpRequest = new MockHttpServletRequest();
        httpRequest.addHeader("Authorization", "Bearer test-key-1234567890");

        var result = controller.handleMessage(request, httpRequest);
        assertEquals("2.0", result.get("jsonrpc").asText());
        assertEquals(1, result.get("id").asInt());
        assertTrue(result.has("result"));
    }

    @Test
    void testHandleToolsCall() throws Exception {
        when(sessionRegistry.isActive(any(String.class))).thenReturn(true);
        when(keyService.validateKey(any(String.class))).thenReturn(true);
        when(registryService.executeTool(any(String.class), any(Map.class)))
                .thenAnswer(invocation -> {
                    var response = new McpResponse();
                    response.setResult(JsonNodeFactory.instance.textNode("result"));
                    return response;
                });

        var params = JsonNodeFactory.instance.objectNode();
        params.put("name", "testTool");
        params.set("arguments", JsonNodeFactory.instance.objectNode());

        var request = new McpRequest();
        request.setMethod("tools/call");
        request.setId(1);
        request.setParams(params);

        var httpRequest = new MockHttpServletRequest();
        httpRequest.addHeader("Authorization", "Bearer test-key-1234567890");

        var result = controller.handleMessage(request, httpRequest);
        assertEquals("2.0", result.get("jsonrpc").asText());
        assertTrue(result.has("result"));
    }

    @Test
    void testInvalidSession() throws Exception {
        when(sessionRegistry.isActive(any(String.class))).thenReturn(false);

        var request = new McpRequest();
        request.setMethod("tools/list");
        request.setId(1);
        request.setParams(JsonNodeFactory.instance.objectNode());

        var httpRequest = new MockHttpServletRequest();
        httpRequest.addHeader("Authorization", "Bearer test-key-1234567890");

        var result = controller.handleMessage(request, httpRequest);
        assertEquals(-32600, result.get("error").get("code").asInt());
    }

    @Test
    void testMethodNotFound() throws Exception {
        when(sessionRegistry.isActive(any(String.class))).thenReturn(true);
        when(keyService.validateKey(any(String.class))).thenReturn(true);

        var params = JsonNodeFactory.instance.objectNode();
        var request = new McpRequest();
        request.setMethod("invalid/method");
        request.setId(1);
        request.setParams(params);

        var httpRequest = new MockHttpServletRequest();
        httpRequest.addHeader("Authorization", "Bearer test-key-1234567890");

        var result = controller.handleMessage(request, httpRequest);
        assertEquals(-32601, result.get("error").get("code").asInt());
    }

    @Test
    void testUnauthorizedInvalidKey() throws Exception {
        when(sessionRegistry.isActive(any(String.class))).thenReturn(true);
        when(keyService.validateKey(any(String.class))).thenReturn(false);

        var params = JsonNodeFactory.instance.objectNode();
        var request = new McpRequest();
        request.setMethod("tools/list");
        request.setId(1);
        request.setParams(params);

        var httpRequest = new MockHttpServletRequest();
        httpRequest.addHeader("Authorization", "Bearer invalid-key");

        var result = controller.handleMessage(request, httpRequest);
        assertEquals(-32601, result.get("error").get("code").asInt());
        assertEquals("Unauthorized: invalid API key", result.get("error").get("message").asText());
    }
}
