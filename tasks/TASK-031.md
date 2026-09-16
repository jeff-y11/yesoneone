# TASK-031: Create Integration Scaffolding Documentation

## Goal
Create `docs/integration-scaffolding.md` documenting the integration execution architecture, entity relationships, trigger mechanisms, and how to add new integration types.

## Requirements

Create `docs/integration-scaffolding.md` with the following sections:

### 1. Overview
Architecture diagram explanation: `Integration` → `IntegrationSchedule` → `IntegrationJobService` → `IntegrationDispatcher` → `IntegrationRunner` implementation.

### 2. Component Relationships
```
┌─────────────────┐     ┌──────────────────────┐     ┌─────────────────────┐
│  Integration    │────▶│ IntegrationSchedule  │────▶│ IntegrationJob      │
│  (entity)       │     │  (entity + repo)     │     │  Service (@Scheduled) │
└────────┬────────┘     └──────────────────────┘     └──────────┬──────────┘
         │                                                       │
         │                    ┌──────────────────────┐            │
         │                    │ IntegrationDispatcher │◀───────────┘
         │                    │  (@Service)          │
         │                    └──────────┬───────────┘
         │                               │ dispatch(integrationId)
         │                    ┌──────────┴───────────┐
         │                    │  BuildiumIntegration  │
         │                    │  Runner (@Component)  │
         │                    │  implements IntegrationRunner
         │                    └──────────────────────┘
         │
         │    REST: POST /services/integrations/{id}/run
         │    MCP:  tools/call "runIntegration" {integrationId}
         │    Cron: @Scheduled every 5 min
         ▼
┌──────────────────────┐
│  IntegrationMcpService│
│  (@McpService)       │
└──────────────────────┘
```

### 3. Adding a New Integration Type
Step-by-step guide for adding a new PM integration (e.g., AppFolio):
1. Add `APPOFOLIO` to `IntegrationType` enum
2. Create `AppFolioIntegrationRunner implements IntegrationRunner` with `@Component`
3. Implement `getType()` returning `"APPOFOLIO"` and `run(Integration)` method
4. `IntegrationDispatcher` automatically discovers it via constructor injection
5. `IntegrationJobService` will execute it on schedule
6. `IntegrationMcpService.runIntegration()` can trigger it via MCP

### 4. Trigger Mechanisms

#### Cron Schedule
- `IntegrationSchedule` entity stores `cronExpression`, `isActive`, `lastRunTime`, `nextRunTime`
- `IntegrationJobService.runScheduledIntegrations()` runs every 5 minutes
- Polls `IntegrationScheduleRepository.findByActiveIsTrue()`
- Checks `isDue()` before dispatching
- Handles retry logic and auto-disables after max retries

#### Web Client Trigger
- Endpoint: `POST /services/integrations/{integrationId}/run`
- Validates user has access to the integration's organization
- Returns 403 if unauthorized, 400 if integration not active
- Calls `IntegrationDispatcher.dispatch()` immediately

#### MCP Trigger
- Tool: `runIntegration` with `integrationId` parameter
- Registered via `@McpService` and `@McpTool` annotations
- `McpRegistryService` discovers it automatically via `ContextRefreshedEvent`
- Returns `{"status": "RUNNING", "integrationId": "..."}` on success

### 5. Entity Field Reference

#### Integration (existing)
| Field | Type | Notes |
|---|---|---|
| `integrationId` | UUID | PK |
| `name` | String | Integration name |
| `endpoint` | String | PM API base URL |
| `type` | String | Must match `IntegrationType` enum name (e.g., "BUILDIUM") |
| `active` | boolean | Whether integration is active |
| `parameters` | Map<String,String> | Configuration parameters |
| `organization` | Organization | Owning organization |

#### IntegrationSchedule (new)
| Field | Type | Notes |
|---|---|---|
| `scheduleId` | UUID | PK |
| `integration` | Integration | Parent integration |
| `cronExpression` | String | Cron expression for scheduling |
| `isActive` | boolean | Whether schedule is active |
| `lastRunTime` | LocalDateTime | Last successful run |
| `nextRunTime` | LocalDateTime | Next scheduled run |
| `maxRetries` | Integer | Max retry attempts on failure |
| `currentRetryCount` | Integer | Current retry count |

### 6. External API Fields Not Represented Internally
The following Buildium/Skywalk fields are NOT represented in our internal domain model because they are PM-specific operational details:
- `VendorId`, `VendorName`, `VendorNotes` — vendor management is PM-specific
- `LineItems`, `Task`, `EntryContacts` — sub-entity collections are too detailed
- `BillTransactionId`, `GLAccountName`, `InvoiceNumber` — accounting data
- `Permissions`, `Role`, `MFAEnabled` — authorization data
- `PropertyGroup`, `StructureDescription`, `YearBuilt` — property metadata not needed for maintenance workflow

### 7. Internal Fields with No External Equivalent
- `scheduleId`, `nextRunTime`, `maxRetries`, `currentRetryCount` — scheduling infrastructure
- `callSource`, `callerName`, `callerContactInfo` — Voice Agent input
- `managementCompany` — internal organizational concept
- `integrationRemoteId` mapping via `externalTenantId` — provider-neutral ID mapping

## Acceptance Criteria
- Documentation file exists at `docs/integration-scaffolding.md`
- Architecture diagram is clear and readable
- Adding new integration type steps are complete and actionable
- All three trigger mechanisms (cron, web client, MCP) are documented
- Entity field references are complete
- External field exclusions are documented
- Internal-only fields are identified
- Markdown renders correctly

## Files Likely Involved
- `docs/integration-scaffolding.md` (new)

## Dependencies
- TASK-025 through TASK-030

## Constraints
- Follow `EntityStandards.md` naming conventions
- Document only the scaffolding — not the actual integration implementations
- Do not reference Buildium/Skywalk implementation details as requirements for internal entities
