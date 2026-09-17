# MCP (Model Context Protocol) Server

## Overview

The MCP server allows AI assistants to interact with the GeodeVAI platform through a standardized JSON-RPC 2.0 protocol. The server exposes tools, resources, and prompts that an AI client can call to perform operations.

## Architecture

```
AI Client (e.g., Claude)
        │
        │  JSON-RPC 2.0 over HTTP POST
        ▼
┌─────────────────────┐
│  McpController      │  ← @RestController at /mcp/stream
│  (handles messages) │
└────────┬────────────┘
         │
         ▼
┌─────────────────────┐
│  McpRegistryService │  ← Scans @McpService beans on ContextRefreshedEvent
│  (tool discovery)   │     Stores tools, resources, prompts
└────────┬────────────┘
         │
         ▼
┌─────────────────────┐
│  @McpService beans  │  ← Business logic services
│  (actual execution) │
└─────────────────────┘
```

## How It Works

### 1. Authentication

All MCP requests must include an API key in the `Authorization: Bearer <key>` header. The `McpKeyService` validates the key, looks up the associated user, and sets the `SecurityContext` authentication. Invalid or missing keys return JSON-RPC error code `-32601`.

### 2. Session Management

Sessions are tracked via `X-Session-Id` header. If no session ID is provided, a random UUID is generated. The `McpSessionRegistry` tracks active sessions. Inactive sessions return error code `-32600`.

### 3. JSON-RPC Message Flow

All requests follow the JSON-RPC 2.0 format:

```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "method": "tools/call",
  "params": {
    "name": "runIntegration",
    "arguments": { "integrationId": "550e8400-e29b-41d4-a716-446655440000" }
  }
}
```

The `McpController.handleMessage()` processes requests:

1. Validates session → returns `-32600` if invalid
2. Validates API key → returns `-32601` if invalid
3. Routes by `method`:
   - `tools/list` → returns all registered tools
   - `resources/list` → returns all registered resources
   - `prompts/list` → returns all registered prompts
   - `tools/call` → executes the named tool via `McpRegistryService.executeTool()`

### 4. Tool Discovery

On `ContextRefreshedEvent`, `McpRegistryService.scanBean()` scans all Spring beans annotated with `@McpService`. For each bean, it finds methods annotated with `@McpTool`, `@McpResource`, or `@McpPrompt` and registers them.

## How to Create a New MCP Service

### Step 1: Create the Service Class

Create a new class annotated with `@Service` and `@McpService`:

```java
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
public class MyNewService {

    private final IntegrationRepository integrationRepository;

    @McpTool(name = "myTool", description = "Description of what this tool does")
    public Map<String, Object> myTool(
            @McpParameter(name = "paramName", description = "Parameter description", required = true) String paramName) {
        // Implementation here
        return Map.of("status", "OK", "result", paramName);
    }
}
```

### Step 2: Define Methods

Each method that should be exposed as an MCP tool must be annotated with `@McpTool`:

```java
@McpTool(name = "toolName", description = "Human-readable description")
public ReturnType methodName(@McpParameter(name = "paramName", description = "Param desc") ParamType param) {
    // ...
}
```

### Step 3: Define Parameters

Use `@McpParameter` on method parameters to specify metadata:

```java
@McpParameter(name = "paramName", description = "What this parameter does", required = true)
String paramName
```

### Step 4: Auto-Registration

The `McpRegistryService` automatically discovers your service on application startup via `@McpService`. No manual registration is needed.

### Step 5: Add Resources (Optional)

Use `@McpResource` to expose data as a resource:

```java
@McpResource(uriPattern = "system://config", description = "System configuration")
public Map<String, Object> getSystemConfig() {
    return Map.of("active", true);
}
```

### Step 6: Add Prompts (Optional)

Use `@McpPrompt` to expose prompts:

```java
@McpPrompt(name = "greeting", description = "Welcome prompt")
public String getGreeting() {
    return "Welcome to GeodeVAI!";
}
```

## Annotations Reference

| Annotation | Target | Purpose |
|------------|--------|---------|
| `@McpService` | Class | Marks a Spring bean as an MCP service for tool/resource/prompt discovery |
| `@McpTool` | Method | Exposes a method as a callable MCP tool |
| `@McpResource` | Method | Exposes a method as a queryable MCP resource |
| `@McpPrompt` | Method | Exposes a method as a prompt template |
| `@McpParameter` | Method parameter | Provides metadata for a tool parameter (name, description, required) |

## Error Codes

| Code | Meaning |
|------|---------|
| `-32600` | Invalid session |
| `-32601` | Unauthorized / Method not found |
| `-32602` | Invalid parameters |
| `-32603` | Internal error during execution |

## Key Files

| File | Purpose |
|------|---------|
| `mcp/controller/McpController.java` | HTTP endpoint handling JSON-RPC requests |
| `mcp/service/McpRegistryService.java` | Scans `@McpService` beans and manages tool/resource/prompt registry |
| `mcp/service/McpKeyService.java` | API key generation, validation, and hashing |
| `mcp/service/McpSessionRegistry.java` | Session management |
| `mcp/annotation/McpService.java` | `@McpService` annotation |
| `mcp/annotation/McpTool.java` | `@McpTool` annotation |
| `mcp/annotation/McpResource.java` | `@McpResource` annotation |
| `mcp/annotation/McpPrompt.java` | `@McpPrompt` annotation |
| `mcp/annotation/McpParameter.java` | `@McpParameter` annotation |
