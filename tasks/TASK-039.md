# TASK-039: Integration capabilities

## Goal

Add integration capabilities model/entity, update the Integration entity to store selected entity capabilities, and add a capability retrieval endpoint for the frontend to query supported capabilities by integration type.

## Requirements

### Capabilities Model

Create a new capabilities entity/model that defines:

| Field | Type | Description |
|---|---|---|
| `capabilityId` | UUID | Primary key, auto-generated |
| `integration` | Integration | Parent integration (many-to-one, lazy) |
| `entityType` | String | Entity type (e.g., "Property", "Unit", "Tenant", "WorkOrder") |
| `direction` | String | Integration direction: "PULL", "PUSH", or "PUSH+PULL" |
| `required` | Boolean | Whether the entity is REQUIRED or OPTIONAL |

### Integration Entity Update

Update the `Integration` entity to store the selected/configured capabilities. The selected capabilities should represent which entities the user has chosen to synchronize for this integration instance.

Add a mechanism to persist the entity selection as part of the integration instance. This could be:

- A separate `IntegrationCapabilities` entity with a one-to-many relationship to `Integration`, OR
- A JSON/Map field on the `Integration` entity itself

**Constraint:** Do not hard-code Buildium's capabilities into the frontend. The capabilities should be provided by the integration implementation.

### Capability Retrieval Endpoint

Add a backend endpoint that the frontend can query after a user selects an integration type:

- **Endpoint**: `GET /services/integrations/{integrationId}/capabilities`
- **Response**: Returns the capabilities supported by that integration type, consisting of entity type, direction, and required/optional status
- **Purpose**: The frontend uses these capabilities to allow the user to select/deselect optional entities

### Integration Runner Update

Update the `IntegrationRunner` interface or add a method to retrieve capabilities:

- Add `getCapabilities()` method returning the capabilities for that integration type
- The `BuildiumIntegrationRunner` should return the capabilities as specified:
  - Property: PULL, OPTIONAL
  - Unit: PULL, OPTIONAL
  - Tenant: PULL, OPTIONAL
  - WorkOrder: PUSH+PULL, OPTIONAL

### API Changes

- Add new repository method/interface for capabilities if using a separate entity
- Add new controller endpoint for capability retrieval
- Ensure the generic dispatcher or runner exposes capabilities to the frontend

## Files Likely Involved

- `src/main/java/com/geodevai/data/model/Integration.java` - Add capabilities relationship/field
- `src/main/java/com/geodevai/data/model/IntegrationCapabilities.java` (new) - Capabilities entity
- `src/main/java/com/geodevai/data/repository/IntegrationRepository.java` - Add capabilities query methods
- `src/main/java/com/geodevai/integration/IntegrationRunner.java` - Add `getCapabilities()` method
- `src/main/java/com/geodevai/integration/BuildiumIntegrationRunner.java` - Implement `getCapabilities()`
- `src/main/java/com/geodevai/integration/mcp/IntegrationMcpService.java` - If MCP support needed
- `src/main/java/com/geodevai/controller/IntegrationController.java` - Add capability endpoint

## Acceptance Criteria

- Capabilities model/entity created with entityType, direction, and required fields
- Integration entity updated to store selected entity capabilities (without hard-coding Buildium specifics)
- Backend endpoint `GET /services/integrations/{integrationId}/capabilities` returns capabilities for the integration type
- `BuildiumIntegrationRunner.getCapabilities()` returns the specified Buildium capabilities
- Frontend can query capabilities after selecting an integration type
- Selected entity capabilities are persisted as part of the user's integration instance