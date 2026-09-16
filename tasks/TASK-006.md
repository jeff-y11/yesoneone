# TASK-006: MCP Security, Authentication, and RBAC

## Goal
Secure the Model Context Protocol (MCP) endpoints using the existing JWT authentication mechanism. Implement support for token authentication in Server-Sent Events (SSE) connections (via query parameters), and add Role-Based Access Control (RBAC) for individual MCP tools.

## Existing Architecture
- **JWT Authentication:** `TokenAuthenticationFilter` extracts tokens from the `Authorization` header.
- **Spring Security Configuration:** `WebSecurityConfig` defines security chains and roles.
- **MCP Core:** Built in `TASK-004` and `TASK-005`.

## Requirements
### 1. **Key Management (New - TASK-008)**
    - Implement backend key generation and validation system for MCP access.
    - Users can create named API keys (visible as first 5 + last 5 characters).
    - Keys are hashed (SHA-256) and associated with the creating user.
    - Keys can be deactivated/revoked.
    - Backend endpoints for key creation, validation, and listing.

### 2. **Support Query Parameter Authentication for SSE:**
    - Native browser `EventSource` (used for SSE) does not support setting custom headers (such as `Authorization: Bearer ...`).
    - Update `TokenAuthenticationFilter.java` (or create an MCP-specific security configuration) to inspect and extract JWTs from a `token` query parameter if the request is for `/mcp/sse` and the header is missing:
      - Example: `/mcp/sse?token=eyJ...`
    - Set the authenticated principal in the Spring `SecurityContext` accordingly.

### 2. **Secure `/mcp` Routing in WebSecurityConfig:**
    - In `WebSecurityConfig.java`, declare that all `/mcp/**` endpoints require authentication:
      - `authorize.requestMatchers("/mcp/**").authenticated();`

### 3. **Add Role-Based Access Control (RBAC) to `@McpTool` Annotation:**
    - Expand the `@McpTool` annotation to include a `requiredRole` field (e.g., `String requiredRole() default;`).
    - Supported values could match existing roles like `ADMIN`, `USER`, or `ADVISOR`.

### 4. **Enforce RBAC during Tool Execution:**
    - In `McpRegistryService.executeTool`:
      - Inspect the `@McpTool` annotation metadata for the requested tool.
      - If `requiredRole` is specified (not empty), fetch the current user's authenticated roles from Spring's `SecurityContextHolder.getContext().getAuthentication()`.
      - Verify that the authenticated user possesses the role (e.g. `ROLE_ADMIN` if the required role is `ADMIN`).
      - If the role check fails, reject the execution and return a standard JSON-RPC error:
        - Code: `-32003` (Standard JSON-RPC for Unauthorized / Forbidden) or similar.
        - Message: `"Unauthorized: current user role does not permit executing tool: {toolName}"`.

## Acceptance Criteria
- Backend key management system allows key creation, validation, and listing.
- Keys are displayed as first 5 + last 5 characters (e.g., `abcd1...efg12`).
- `/mcp/sse` and `/mcp/message` are blocked with HTTP `401 Unauthorized` if no credentials are provided.
- Connecting to `/mcp/sse?token=<valid_token>` successfully authenticates and establishes the SSE channel.
- Calling `/mcp/message` with a valid JWT in the `Authorization` header executes successfully.
- Calling a tool with a specified `requiredRole` fails with an appropriate JSON-RPC error if the authenticated user doesn't have the necessary role.
- Calling a tool with a specified `requiredRole` succeeds when invoked by an authorized user.

## Files Likely Involved
- `D:\workspace\geodevai\src\main\java\com\geodevai\mcp\entity\McpKey.java` (new)
- `D:\workspace\geodevai\src\main\java\com\geodevai\mcp\repository\McpKeyRepository.java` (new)
- `D:\workspace\geodevai\src\main\java\com\geodevai\mcp\service\McpKeyService.java` (new)
- `D:\workspace\geodevai\src\main\java\com\geodevai\mcp\util\KeyMaskUtil.java` (new)
- `D:\workspace\geodevai\src\main\java\com\geodevai\mcp\controller\McpController.java` (updated)
- `D:\workspace\geodevai\src\main\java\com\geodevai\data\model\User.java` (updated)
- `D:\workspace\geodevai\tasks\TASK-008.md` (new - Key Management Page)

## Constraints & Dependencies
- Depends on: `TASK-004` & `TASK-005` (core MCP infrastructure).
- New dependency: `spring-boot-starter-web` for MVC and SSE support.
- Ensure role comparison correctly accounts for standard Spring Security prefixes (e.g. `ROLE_` prefixing).