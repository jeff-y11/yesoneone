# TASK-004: MCP Core Services & Custom Annotations

## Goal
Implement the core Java annotations and the service registry backend for the Model Context Protocol (MCP). This allows Spring services to declare themselves as MCP-enabled and dynamically register their methods as tools, resources, or prompts. It will handle the discovery, JSON Schema generation, and method execution via reflection.

## Existing Architecture
- Standard Spring Boot Dependency Injection.
- Jackson for JSON serialization/deserialization.
- No existing MCP infrastructure.

## Requirements
1. **Define Custom Annotations:**
   - `@McpService`: Class-level annotation to mark a Spring Bean as containing MCP capabilities.
   - `@McpTool`: Method-level annotation to declare a method as an MCP Tool.
     - Fields: `name` (String, name of tool), `description` (String, description of tool).
   - `@McpParameter`: Parameter-level annotation to describe tool arguments.
     - Fields: `name` (String), `description` (String), `required` (boolean, default true).
   - `@McpResource`: Method-level annotation to declare a method that serves an MCP resource.
     - Fields: `uriPattern` (String, e.g., `db://users/{id}`), `description` (String).
   - `@McpPrompt`: Method-level annotation to declare a method serving pre-defined prompts.
     - Fields: `name` (String), `description` (String).

2. **Implement MCP Core Models (JSON-RPC 2.0 Compliance):**
   - Create standard JSON-RPC 2.0 request and response wrappers:
     - `McpRequest`: contains `jsonrpc` (String, "2.0"), `id` (Object), `method` (String), and `params` (JsonNode).
     - `McpResponse`: contains `jsonrpc`, `id`, and either `result` (JsonNode) or `error` (McpError).
   - Implement MCP specific schemas for Tools, Resources, and Prompts list responses.
   - Implement dynamic JSON Schema generator from java method parameters (converting types like `String`, `Integer`, `Double`, `Boolean`, and custom objects to standard JSON Schema properties).

3. **Implement `McpRegistryService`:**
   - Scan the Spring Application Context at startup (implement `ApplicationListener<ContextRefreshedEvent>`).
   - Find all beans annotated with `@McpService`.
   - Extract and parse all methods annotated with `@McpTool`, `@McpResource`, or `@McpPrompt`.
   - Store metadata (parameters, JSON schemas, types) in an in-memory registry.
   - Implement execution engine:
     - Method: `executeTool(String toolName, Map<String, Object> arguments)`
     - Locate target bean and method.
     - Parse input arguments into Java objects matching the method parameter signature using Jackson `ObjectMapper`.
     - Invoke the method using reflection (`Method.invoke(bean, ...)`).
     - Handle standard validation (missing parameters, incorrect types) and target invocation exceptions, wrapping them in JSON-RPC standard error responses (e.g., `-32602` for Invalid Params, `-32601` for Method Not Found).

## Acceptance Criteria
- Custom annotations `@McpService`, `@McpTool`, `@McpParameter`, `@McpResource`, and `@McpPrompt` are defined.
- `McpRegistryService` successfully scans and registers annotated services on startup.
- JSON Schema is correctly generated for annotated method parameters.
- Tools can be invoked dynamically via `McpRegistryService.executeTool` with arguments mapped automatically via reflection, returning standard JSON-RPC compliant outputs.
- Comprehensive unit tests exist for parameter mapping, JSON Schema generation, error handling, and method execution.

## Files Likely Involved
- Create package `com.geodevai.mcp` containing:
  - `com.geodevai.mcp.annotation`
  - `com.geodevai.mcp.model`
  - `com.geodevai.mcp.service`
  - `com.geodevai.mcp.exception`
- Create test files in `src/test/java/com/geodevai/mcp` to verify registry scanning and execution.

## Constraints & Dependencies
- Depends on: `TASK-001` & `TASK-002` (requires Jackson mapping configurations).
- Required by: `TASK-005` (endpoint handler) and `TASK-006` (securing endpoints).
