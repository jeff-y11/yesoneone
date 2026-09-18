# TASK-044: Synchronization ownership and ordering

## Goal

Implement deterministic sync ordering (Property → Unit → Tenant → WorkOrder → true-up) derived from declared capabilities; the generic dispatcher merely calls integrationRunner.run(integrationContext).

## Requirements

### Sync Ordering Logic

Define a deterministic sync order based on declared capabilities:

1. **Property** - Sync first to establish the base entity
2. **Unit** - Sync second, linked to Property
3. **Tenant** - Sync third, linked to Unit/Property
4. **WorkOrder** - Sync fourth, linked to Property/Unit/Tenant
5. **True-up** - Final synchronization of any remaining changes

### Capability-Derived Ordering

- The sync order is derived from the integration's selected capabilities
- Each capability has an entity type and direction (PULL/PUSH/PUSH+PULL)
- Ordering follows the dependency chain: Property → Unit → Tenant → WorkOrder
- If capabilities are missing, entities are simply skipped

### Generic Dispatcher Role

- The generic `IntegrationDispatcher` continues to call `integrationRunner.run(integrationContext)`
- The `BuildiumIntegrationRunner` (or specific runner) decides which selected entities to sync and in what order
- No ordering logic in the dispatcher; it remains generic

### IntegrationRunner Responsibility

- Each runner implements `run(Integration integration)` with its own ordering logic
- The runner iterates through its selected capabilities in the defined order
- Entity selection is based on the integration's configured capabilities

## Files Likely Involved

- `src/main/java/com/geodevai/integration/BuildiumIntegrationRunner.java` - Implement ordered sync logic
- `src/main/java/com/geodevai/integration/IntegrationRunner.java` - May add ordering-related methods
- `src/main/java/com/geodevai/data/model/Integration.java` - Selected capabilities influence ordering

## Acceptance Criteria

- Sync order is deterministic: Property → Unit → Tenant → WorkOrder → true-up
- Ordering is derived from declared capabilities, not hard-coded in dispatcher
- Generic dispatcher calls `run(integrationContext)` without knowledge of specific ordering
- Runner iterates through selected capabilities in the defined order
- Entities not configured in capabilities are skipped gracefully