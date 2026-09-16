# TASK-007: Register Sample MCP Services and Verify

## Goal
Create a sample Spring service annotated with the new `@McpService` annotation and verify that its methods (annotated with `@McpTool`, `@McpParameter`, and `@McpResource`) are successfully discovered, listed, and executed by the MCP engine.

## Existing Architecture
- **MCP Core and Transport:** Built in `TASK-004`, `TASK-005`, and `TASK-006`.
- **Google OAuth2 scopes:** In `application.properties`, we request scopes including `calendar.readonly`.

## Requirements
1. **Create Sample service `McpSampleService.java`:**
   - Annotate the class with `@Service` and `@McpService`.
   - Implement the following tools:
     - **`getSystemStatus`**: A tool requiring no parameters. Returns basic server status information (uptime, CPU load, memory usage).
     - **`searchUsers`**: A tool taking a required string `query` parameter (annotated with `@McpParameter`) and returning matching users from `UserRepository`.
     - **`getCalendarEvents`**: A tool taking an integer `maxResults` and string `timeMin` (using `@McpParameter`), which retrieves events from the authenticated user's Google Calendar using the token saved in `Login` (via Google APIs).
2. **Implement Sample Resource:**
   - Implement a resource handler using `@McpResource`:
     - Pattern: `system://configuration`
     - Returns public system configuration metadata (e.g. active spring profiles, base URLs, enabled services).
3. **Register and Verify End-to-End Flow:**
   - Ensure the tools/resources are discoverable when hitting `POST /mcp/message` with method `tools/list` and `resources/list`.
   - Verify tool execution by hitting `POST /mcp/message` with method `tools/call`, providing the tool name and arguments.

## Acceptance Criteria
- `McpSampleService` is registered automatically on startup.
- Calling `tools/list` returns descriptions and parameter JSON Schemas for `getSystemStatus`, `searchUsers`, and `getCalendarEvents`.
- Invoking `tools/call` for `getSystemStatus` executes the underlying method and returns correct system metrics.
- Invoking `tools/call` for `searchUsers` executes successfully, queries the DB, and returns results.
- Comprehensive integration tests in `McpSampleServiceTest.java` to assert automatic registration and validation.

## Files Likely Involved
- Create `D:\workspace\geodevai\src\main\java\com\geodevai\mcp\sample\McpSampleService.java`
- Create test file `D:\workspace\geodevai\src\test\java\com\geodevai\mcp\sample\McpSampleServiceTest.java`

## Constraints & Dependencies
- Depends on: `TASK-004`, `TASK-005`, and `TASK-006`.
