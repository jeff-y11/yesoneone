package com.geodevai.mcp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.geodevai.data.model.McpKey;
import com.geodevai.data.model.User;
import com.geodevai.mcp.model.McpRequest;
import com.geodevai.mcp.model.McpResponse;
import com.geodevai.mcp.service.McpRegistryService;
import com.geodevai.mcp.service.McpSessionRegistry;
import com.geodevai.mcp.service.McpKeyService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/mcp")
@RequiredArgsConstructor
public class McpController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final McpRegistryService registryService;
    private final McpSessionRegistry sessionRegistry;
    private final McpKeyService keyService;

    @PostMapping(value = "/stream", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ObjectNode handleMessage(@RequestBody McpRequest request, HttpServletRequest httpRequest) throws IOException {
        String sessionId = httpRequest.getHeader("X-Session-Id");
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
        }

        if (!sessionRegistry.isActive(sessionId)) {
            return buildErrorResponse(request.getId(), -32600, "Invalid session");
        }

        String key = extractKeyFromHeader(httpRequest);
        if (key == null || !keyService.validateKey(key)) {
            return buildErrorResponse(request.getId(), -32601, "Unauthorized: invalid API key");
        }

        Optional<McpKey> keyRecord = keyService.getKeyByHash(keyService.hashKey(key));
        if (keyRecord.isPresent()) {
            User user = keyRecord.get().getUser();
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user, null, List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
            );
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        String method = request.getMethod();
        return switch (method) {
            case "tools/list" -> {
                ObjectNode data = registryService.listToolsAsJson();
                ObjectNode response = buildJsonRpcResponse(request.getId(), data);
                yield response;
            }
            case "resources/list" -> {
                ObjectNode data = registryService.listResourcesAsJson();
                ObjectNode response = buildJsonRpcResponse(request.getId(), data);
                yield response;
            }
            case "prompts/list" -> {
                ObjectNode data = registryService.listPromptsAsJson();
                ObjectNode response = buildJsonRpcResponse(request.getId(), data);
                yield response;
            }
            case "tools/call" -> {
                JsonNode params = request.getParams();
                if (params == null || !params.has("name")) {
                    yield buildErrorResponse(request.getId(), -32602, "Invalid params: missing tool name");
                }
                String toolName = params.get("name").asText();
                Map<String, Object> arguments = null;
                if (params.has("arguments") && params.get("arguments") != null && !params.get("arguments").isNull()) {
                    arguments = objectMapper.convertValue(params.get("arguments"), new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
                }
                try {
                    McpResponse result = registryService.executeTool(toolName, arguments);
                    ObjectNode response = buildJsonRpcResponse(request.getId(), result.getResult() != null ? result.getResult() : objectMapper.createObjectNode());
                    if (result.hasError()) {
                        response.set("error", objectMapper.valueToTree(result.getError()));
                    }
                    yield response;
                } catch (Exception e) {
                    yield buildErrorResponse(request.getId(), -32603, e.getMessage());
                }
            }
            default -> buildErrorResponse(request.getId(), -32601, "Method not found: " + method);
        };
    }

    private String extractKeyFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private ObjectNode buildErrorResponse(Object id, int code, String message) throws IOException {
        ObjectNode response = buildJsonRpcResponse(id, objectMapper.createObjectNode());
        ObjectNode errorNode = objectMapper.createObjectNode();
        errorNode.put("code", code);
        errorNode.put("message", message);
        response.set("error", errorNode);
        return response;
    }

    private ObjectNode buildJsonRpcResponse(Object id, JsonNode data) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        if (id instanceof Number) {
            response.put("id", ((Number) id).intValue());
        } else {
            response.put("id", id.toString());
        }
        response.set("result", data);
        return response;
    }
}
