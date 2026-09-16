# TASK-005: MCP Transport & Discoverability API

## Goal
Implement the Model Context Protocol (MCP) HTTP/SSE transport layer in Spring Boot, allowing clients to establish connections, discover registered tools/resources/prompts, and execute tool calls using standard MCP JSON-RPC 2.0.

## Existing Architecture
- Spring Boot Web / Jersey is available (as per `build.gradle.kts` dependencies).
- Deep Linking whitelisted `/mcp` in `SpaWebFilter` (from `TASK-003`).

## Requirements
1. **Implement Server-Sent Events (SSE) Transport Endpoint:**
   - Create a Spring MVC Controller at `/mcp` or `/services/mcp`.
   - **GET Endpoint `/mcp/sse`:**
     - Establish an SSE connection using Spring's `SseEmitter`.
     - Generate a unique `sessionId` (e.g., UUID) for each client connection.
     - Register the active connection emitter mapped to the `sessionId` in an in-memory session registry.
     - Send an initial `endpoint` event containing the HTTP POST URI for client messages:
       - Format: `event: endpoint\ndata: /mcp/message?sessionId={sessionId}`
       - Handle timeout and completion events by removing the emitter from the session registry.

2. **Implement Client Message Endpoint:**
   - **POST Endpoint `/mcp/message`:**
     - Accept JSON-RPC 2.0 request bodies with query parameter `sessionId`.
     - Validate that the session is active.
     - Parse the request payload using Jackson into an `McpRequest` object.

3. **Handle Discoverability & Execution Methods:**
   - Route requests based on the `method` parameter in the JSON-RPC payload:
     - **`tools/list`:** Retrieve registered tools from `McpRegistryService` and format them per MCP specification (names, descriptions, parameter schemas).
     - **`resources/list`:** Retrieve registered resources (if any) with their URI templates and descriptions.
     - **`prompts/list`:** Retrieve registered prompt templates.
     - **`tools/call`:** Parse params, extract the tool `name` and `arguments`, execute the tool via `McpRegistryService.executeTool`, and return the tool output (text or JSON).
   - Return standard JSON-RPC 2.0 response format directly in the HTTP POST response body.

## Acceptance Criteria
- SSE endpoint `/mcp/sse` works and sends the `endpoint` event with the correct session URI.
- POST `/mcp/message` processes JSON-RPC requests correctly and yields appropriate responses.
- `tools/list` returns a full, valid list of scanned tools, complete with descriptions and JSON schemas.
- `tools/call` executes the target method and returns the formatted response.
- Server handles connection termination and keeps session registry clean.
- Integration test suite mocking the SSE and POST requests to verify end-to-end handshake, discovery, and execution.

## Files Likely Involved
- Create `com.geodevai.mcp.controller.McpController.java`
- Create `com.geodevai.mcp.service.McpSessionRegistry.java`
- Create integration test `src/test/java/com/geodevai/mcp/McpIntegrationTest.java`

## Constraints & Dependencies
- Depends on: `TASK-003` (to bypass SPA redirect filter) and `TASK-004` (requires core scanning & execution engine).
- Required by: `TASK-006` (securing the newly created endpoints).
