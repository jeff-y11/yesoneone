# TASK-003: Fix Deep Linking and SPA filtering route issues

## Goal
Fix SPA deep linking routing exceptions in the backend to ensure backend-only API services (like `/services` and `/mcp`) are not intercepted and redirected to `/index.html` by the SPA routing filter.

## Existing Architecture
- **SPA Routing Filter:** `SpaWebFilter` redirects standard routes (deep links) to `/` (index.html) so the React router can handle them on the client side.
- **Whitelist Arrays:**
  - `spaDEV = { "/service", "/data", "/actuator", "/oauth2", "/login", "/h2-console", "/callback", "/home" }`
  - `spaPROD = { "/service", "/data", "/actuator", "/login", "/oauth2", "/callback", "/home" }`
- **Security Configuration:** `WebSecurityConfig` controls authorization for endpoints.

## Requirements
1. **Add MCP Route to Whitelist:**
   - In `SpaWebFilter.java`, add `/mcp` to both `spaDEV` and `spaPROD` whitelists so requests starting with `/mcp` bypass the SPA routing filter and reach the Spring Boot MCP controllers.
2. **Correct Services Whitelist Entry:**
   - Ensure `/services` (as defined in `README.md`) is accurately covered. While `startsWith("/service")` matches `/services`, explicitly replacing `/service` with `/services` or including both avoids confusion.
3. **Configure MCP Security Permits:**
   - In `WebSecurityConfig.java`, add routing and authorization rules for the `/mcp/**` endpoints. 
   - Ensure the MCP endpoints are authorized/permitted according to the desired security model (e.g., permit `/mcp` for initial discoverability/handshakes, or secure it with token authentication).

## Acceptance Criteria
- Requests made to `/mcp` or `/mcp/` are NOT forwarded to `/` (index.html).
- Requests to backend REST services under `/services/**` or `/data/**` continue to function and bypass SPA forwarding.
- Frontend deep links (e.g., `/home`, `/callback`, etc.) continue to be handled by `SpaWebFilter` and load the React SPA correctly.

## Files Likely Involved
- `D:\workspace\geodevai\src\main\java\com\geodevai\security\SpaWebFilter.java`
- `D:\workspace\geodevai\src\main\java\com\geodevai\config\WebSecurityConfig.java`

## Constraints & Dependencies
- Dependent on: `TASK-002`
- Required by: `TASK-005` (since the MCP transport endpoints depend on this routing fix).
