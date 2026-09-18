# TASK-043: Synchronization efficiency

## Goal

Implement batch sync, pagination, and cursor/state persistence for subsequent syncs of changed entities.

## Requirements

### Bulk/List Endpoint Preference

- Prefer bulk/list endpoints over fetching entities individually
- Use pagination appropriately for large result sets
- Default page size should be reasonable (e.g., 50-100 records per page)

### Modification-Date Filtering

- Use modification-date filtering where available to support incremental syncs
- Persist a last-sync timestamp cursor per integration instance
- On subsequent syncs, only fetch entities modified since the last successful sync

### Incremental Sync Cursor

- Store a `lastSyncTime` timestamp on the Integration entity or as a separate configuration
- The cursor drives which entities are fetched on each sync cycle
- Enable/disable cursor-based filtering based on API support

### API Optimization

- Do not perform unnecessary detail requests when the list endpoint already provides the fields required by the mapping
- Cache or batch entity details when possible
- Minimize API calls while ensuring data completeness

## Files Likely Involved

- `src/main/java/com/geodevai/data/model/Integration.java` - Add `lastSyncTime` field
- `src/main/java/com/geodevai/integration/BuildiumIntegrationRunner.java` - Implement cursor-based sync logic
- `src/main/java/com/geodevai/integration/IntegrationRunner.java` - Add cursor/retrieval method if needed
- Any API client layers that handle pagination and date filtering

## Acceptance Criteria

- Bulk/list endpoints are preferred over individual detail requests
- Pagination is implemented appropriately
- `lastSyncTime` cursor is persisted and used for incremental syncs
- Entities modified since last sync are fetched; unchanged entities are skipped
- Unnecessary detail requests are avoided when list endpoints provide sufficient data