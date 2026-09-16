# TASK-029: Add Manual Trigger Endpoints for Web Client and MCP

## Goal
Add REST and MCP endpoints to manually trigger integration runs. This allows the web client and MCP to initiate a full-pull on any integration on demand.

## Requirements

### 1. Add REST Endpoint to IntegrationController
**File:** `src/main/java/com/geodevai/controller/IntegrationController.java` (modify)

Add a new endpoint:
```java
@PostMapping("/{integrationId}/run")
public ResponseEntity<Map<String, Object>> runIntegration(@PathVariable UUID integrationId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userId = auth.getName();
    User user = userRepository.findById(UUID.fromString(userId))
            .orElseThrow();

    Integration integration = integrationRepository.findById(integrationId)
            .orElseThrow();

    if (user.getOrganization() == null ||
        !user.getOrganization().getOrganizationId().equals(integration.getOrganization().getOrganizationId())) {
        return ResponseEntity.status(403).build();
    }

    if (!integration.isActive()) {
        return ResponseEntity.badRequest().body(Map.of("error", "Integration is not active"));
    }

    // Dispatch the integration run
    integrationDispatcher.dispatch(integrationId, integrationRepository);

    return ResponseEntity.ok(Map.of(
        "integrationId", integration.getIntegrationId(),
        "status", "RUNNING",
        "message", "Integration run initiated"
    ));
}
```

Add `IntegrationDispatcher` to the `@RequiredArgsConstructor` field list.

Add imports:
```java
import com.geodevai.integration.IntegrationDispatcher;
```

### 2. Add MCP Tool for Integration Trigger
**Location:** `src/main/java/com/geodevai/integration/mcp/IntegrationMcpService.java` (new)

Create a new `@McpService` annotated bean:
```java
@Service
@McpService
@RequiredArgsConstructor
public class IntegrationMcpService {
    private final IntegrationDispatcher dispatcher;
    private final IntegrationRepository integrationRepository;

    @McpTool(name = "runIntegration", description = "Triggers a full-pull of a PM integration by integrationId")
    public Map<String, Object> runIntegration(
            @McpParameter(name = "integrationId", description = "UUID of the integration to run", required = true) UUID integrationId) {
        try {
            dispatcher.dispatch(integrationId, integrationRepository);
            return Map.of("status", "RUNNING", "integrationId", integrationId);
        } catch (Exception e) {
            return Map.of("status", "FAILED", "error", e.getMessage());
        }
    }

    @McpTool(name = "listIntegrations", description = "Lists all active integrations")
    public List<Integration> listIntegrations() {
        return integrationRepository.findByOrganizationAndActiveIsTrue(/* need org */);
    }
}
```

Wait — `listIntegrations` needs the organization. The MCP system currently doesn't pass the user's org context into MCP tools. For now, just implement `runIntegration`. The `listIntegrations` can be added later.

Actually, looking at the MCP architecture, `McpRegistryService.executeTool()` doesn't have access to the SecurityContext. The MCP tool might need to look up the integration by ID only. Let me simplify:

```java
@McpTool(name = "runIntegration", description = "Triggers a full-pull of a PM integration by integrationId")
public Map<String, Object> runIntegration(
        @McpParameter(name = "integrationId", description = "UUID of the integration to run", required = true) String integrationId) {
    try {
        dispatcher.dispatch(UUID.fromString(integrationId), integrationRepository);
        return Map.of("status", "RUNNING", "integrationId", integrationId);
    } catch (Exception e) {
        return Map.of("status", "FAILED", "error", e.getMessage());
    }
}
```

### 3. Add `IntegrationDispatcher` to `DataConfiguration` Exposed IDs
**File:** `src/main/java/com/geodevai/config/DataConfiguration.java` (modify)

Ensure `IntegrationDispatcher` is properly registered as a Spring bean (it will be via `@Service`).

## Acceptance Criteria
- `IntegrationController` has `POST /services/integrations/{integrationId}/run` endpoint
- Endpoint validates user has access to the integration's organization
- Endpoint returns 403 if user doesn't own the integration
- Endpoint returns 400 if integration is not active
- Endpoint calls `dispatcher.dispatch(integrationId, integrationRepository)` on success
- Returns 200 with status "RUNNING" and integrationId
- `IntegrationMcpService` exists with `@McpService` and `@McpTool(name = "runIntegration")` annotation
- `runIntegration` MCP tool accepts `integrationId` as String parameter
- MCP tool calls `dispatcher.dispatch()` and returns status map
- All files compile without errors

## Files Likely Involved
- `src/main/java/com/geodevai/controller/IntegrationController.java` (modify)
- `src/main/java/com/geodevai/integration/mcp/IntegrationMcpService.java` (new)

## Dependencies
- TASK-025 (IntegrationRunner interface)
- TASK-027 (IntegrationDispatcher)

## Constraints
- The REST endpoint must follow existing `IntegrationController` patterns (auth check, org validation)
- The MCP tool must use `@McpService` and `@McpTool` annotations matching the existing MCP system
- Do NOT implement actual integration API calls — `run()` is a stub
- The `listIntegrations` MCP tool is optional and can be added later
- MCP tool parameter for `integrationId` should be `String` (not `UUID`) for easier JSON-RPC serialization
