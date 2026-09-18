# TASK-045: Manual entity synchronization

## Goal

Allow an individual entity type to be manually triggered from the frontend/MCP using the same batch implementation; filter to the requested entity type. Defer full UI/MCP exposure if scope expands.

## Requirements

### Manual Sync Endpoint/Tool

- Add a mechanism to manually trigger sync for a specific entity type
- Can be a frontend button, MCP tool, or REST endpoint
- Re-uses the same batch runner implementation
- Filters sync to the requested entity type only

### Entity-Type Filtering

- Manual sync accepts an entity type parameter (e.g., "WorkOrder", "Tenant")
- Runner filters capabilities/selects to only the requested entity type
- Other entities in the integration are left unchanged
- Uses the same authentication and batch implementation as scheduled sync

### MCP Integration (Optional)

- MCP tool could accept `integrationId` and `entityType` parameters
- Tool name could be `syncIntegrationEntity` or similar
- Returns sync results for the filtered entity type only
- Full UI exposure deferred if scope expands

### Frontend Consideration (Optional)

- "Sync [Entity]" button on integration details page
- Shows last sync status and option to trigger manual sync
- Currently deferred; implementation should be ready for future UI

## Files Likely Involved

- `src/main/java/com/geodevai/controller/IntegrationController.java` - Add manual sync endpoint if needed
- `src/main/java/com/geodevai/integration/BuildiumIntegrationRunner.java` - Accept entity type filter parameter
- `src/main/java/com/geodevai/integration/IntegrationMcpService.java` - Add MCP tool for manual entity sync if needed
- `src/main/java/com/geodevai/integration/IntegrationRunner.java` - Add entity type parameter to run method if needed

## Acceptance Criteria

- Manual sync can be triggered for a specific entity type
- Re-uses the same batch sync implementation
- Only the requested entity type is synced; others are unaffected
- MCP tool (optional) accepts integrationId and entityType parameters
- Frontend action (optional) allows per-entity-type manual sync
- Deferred UI exposure does not break existing functionality