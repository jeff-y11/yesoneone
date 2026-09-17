# Integration Scaffolding Documentation

## 1. Overview

The integration execution architecture follows a clean pipeline from domain entities to scheduled execution and external triggers:

```
Integration → IntegrationSchedule → IntegrationJobService → IntegrationDispatcher → IntegrationRunner implementation
```

- **Integration**: The core domain entity representing a PM integration (e.g., Buildium, AppFolio)
- **IntegrationSchedule**: Stores cron-based scheduling configuration for each integration
- **IntegrationJobService**: Polls active schedules every 5 minutes and triggers dispatch
- **IntegrationDispatcher**: Routes integrations to the correct `IntegrationRunner` based on type
- **IntegrationRunner**: Interface that each PM integration type implements

---

## 2. Component Relationships

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

### Key Relationships

| Component | Type | Role |
|-----------|------|------|
| `Integration` | JPA Entity | Domain model for PM integration |
| `IntegrationSchedule` | JPA Entity + Repository | Cron-based scheduling per integration |
| `IntegrationJobService` | `@Service` | Scheduled polling and dispatch trigger |
| `IntegrationDispatcher` | `@Service` | Routes integrations to correct `IntegrationRunner` |
| `BuildiumIntegrationRunner` | `@Component` | Stub implementation of `IntegrationRunner` |
| `IntegrationMcpService` | `@McpService` | MCP tool for triggering integrations |
| `IntegrationRunner` | Interface | Contract all integration types must implement |
| `IntegrationType` | Enum | `BUILDIUM`, `APPOFOLIO` |

---

## 3. Adding a New Integration Type

To add a new PM integration type (e.g., AppFolio):

1. **Add enum value**: Add `APPOFOLIO` to `IntegrationType` enum in `src/main/java/com/geodevai/integration/IntegrationType.java`
2. **Create runner**: Create `AppFolioIntegrationRunner implements IntegrationRunner` with `@Component` annotation
3. **Implement methods**: Implement `getType()` returning `"APPOFOLIO"` and `run(Integration)` method with actual API logic
4. **Auto-discovery**: `IntegrationDispatcher` automatically discovers it via constructor injection of all `IntegrationRunner` beans
5. **Scheduled execution**: `IntegrationJobService` will execute it on schedule when `IntegrationSchedule` matches the type
6. **MCP trigger**: `IntegrationMcpService.runIntegration()` can trigger it via MCP using `integrationId`

No additional configuration is required. Spring's component scanning and constructor injection handle the rest.

---

## 4. Trigger Mechanisms

### Cron Schedule

- `IntegrationSchedule` entity stores `cronExpression`, `isActive`, `lastRunTime`, `nextRunTime`
- `IntegrationJobService.runScheduledIntegrations()` runs every 5 minutes via `@Scheduled(cron = "0 */5 * * * *")`
- Polls `IntegrationScheduleRepository.findByActiveIsTrue()`
- Checks `isDue()` before dispatching — if `nextRunTime` is null or in the past, the schedule is due
- Handles retry logic: increments `currentRetryCount` on failure, auto-disables schedule when `currentRetryCount >= maxRetries`
- Resets `currentRetryCount` to 0 on successful run

### Web Client Trigger

- **Endpoint**: `POST /services/integrations/{integrationId}/run`
- **Authentication**: Validates user has access to the integration's organization via `SecurityContextHolder`
- **Authorization**: Returns 403 if user's organization doesn't match the integration's organization
- **Active check**: Returns 400 if integration is not active
- **Dispatch**: Calls `IntegrationDispatcher.dispatch(integrationId, integrationRepository)` on success
- **Response**: Returns `{"integrationId": "...", "status": "RUNNING", "message": "Integration run initiated"}`

### MCP Trigger

- **Tool**: `runIntegration` with `integrationId` parameter (String type for JSON-RPC compatibility)
- **Registration**: `IntegrationMcpService` annotated with `@McpService` and `@McpTool(name = "runIntegration")`
- **Discovery**: `McpRegistryService` discovers tools automatically via `ContextRefreshedEvent`
- **Response**: Returns `{"status": "RUNNING", "integrationId": "..."}` on success, `{"status": "FAILED", "error": "..."}` on failure
- **Other tools**: `listIntegrations` is available for future use

---

## 5. Entity Field Reference

### Integration (existing)

| Field | Type | Notes |
|---|---|---|
| `integrationId` | UUID | Primary key, auto-generated |
| `name` | String | Integration name |
| `endpoint` | String | PM API base URL |
| `type` | String | Must match `IntegrationType` enum name (e.g., `"BUILDIUM"`, `"APPOFOLIO"`) |
| `active` | boolean | Whether integration is active |
| `parameters` | Map\<String,String\> | Configuration parameters |
| `organization` | Organization | Owning organization (many-to-one) |

### IntegrationSchedule (new)

| Field | Type | Notes |
|---|---|---|
| `scheduleId` | UUID | Primary key, auto-generated |
| `integration` | Integration | Parent integration (many-to-one, lazy) |
| `cronExpression` | String | Cron expression for scheduling (e.g., `"0 */5 * * * *"`) |
| `isActive` | Boolean | Whether schedule is active (default: `true`) |
| `lastRunTime` | LocalDateTime | Timestamp of last successful run |
| `nextRunTime` | LocalDateTime | Calculated next scheduled run time |
| `maxRetries` | Integer | Maximum retry attempts on failure (default: `3`) |
| `currentRetryCount` | Integer | Current retry count (default: `0`) |

---

## 6. External API Fields Not Represented Internally

The following Buildium/Skywalk fields are NOT represented in our internal domain model because they are PM-specific operational details:

- `VendorId`, `VendorName`, `VendorNotes` — vendor management is PM-specific
- `LineItems`, `Task`, `EntryContacts` — sub-entity collections are too detailed
- `BillTransactionId`, `GLAccountName`, `InvoiceNumber` — accounting data
- `Permissions`, `Role`, `MFAEnabled` — authorization data
- `PropertyGroup`, `StructureDescription`, `YearBuilt` — property metadata not needed for maintenance workflow

---

## 7. Internal Fields with No External Equivalent

- `scheduleId`, `nextRunTime`, `maxRetries`, `currentRetryCount` — scheduling infrastructure
- `callSource`, `callerName`, `callerContactInfo` — Voice Agent input
- `managementCompany` — internal organizational concept
- `integrationRemoteId` mapping via `externalTenantId` — provider-neutral ID mapping
