# GeodeVAI MCP and Code Quality Tasks

This directory contains a series of structured tasks designed to upgrade the application, resolve outstanding bugs/technical debt, and implement a robust, secure Model Context Protocol (MCP) server inside the Spring Boot backend.

## Task Overview

| Task ID | Title | Description | Dependencies | Status |
|---------|-------|-------------|--------------|--------|
| [TASK-001](./TASK-001.md) | Package Version Upgrades | Upgrade backend (Spring Boot 4.1.1, JJWT, Kotlin) and frontend (React 19.3.0, React Router, Vite, Tailwind, TS) packages. | None | Completed |
| [TASK-002](./TASK-002.md) | Code Quality, Bugs, & Technical Debt | Fix potential NullPointerExceptions, remove redundant configurations, externalize secrets, and resolve open TODOs. | TASK-001 | Completed |
| [TASK-003](./TASK-003.md) | Deep Linking & SPA Filtering Route Issues | Fix SpaWebFilter to bypass backend-only services (like `/mcp` and `/services`) from SPA routing redirects. | TASK-002 | Completed |
| [TASK-004](./TASK-004.md) | MCP Core Services & Custom Annotations | Create custom `@McpService` annotations, scan the Spring context, and implement dynamic reflection-based execution. | TASK-001, TASK-002 | Completed |
| [TASK-005](./TASK-005.md) | MCP Transport & Discoverability API | Implement HTTP/SSE handshake and message processing (JSON-RPC 2.0) endpoints. | TASK-003, TASK-004 | Completed |
| [TASK-006](./TASK-006.md) | MCP Security, Authentication, & RBAC | Secure `/mcp` routes, add token extraction from query parameters, and enforce RBAC rules on tools. | TASK-004, TASK-005 | Completed |
| [TASK-007](./TASK-007.md) | Register Sample MCP Services & Verify | Create a sample service using the MCP platform and write integration tests to verify the end-to-end setup. | TASK-004, TASK-005, TASK-006 | Completed |
| [TASK-008](./TASK-008.md) | MCP Key Management Page | Create a frontend page for generating, displaying, and deleting MCP API keys with navigation layout (top nav with logout, left sidebar). | TASK-006 | Completed |
| [TASK-009](./TASK-009.md) | MCP Streamable HTTP Transport | Move away from SSE for the MCP server, use streamable HTTP instead. Use key-based authentication from TASK-008. | TASK-006 | Completed |
| [TASK-010](./TASK-010.md) | Externalable Interface & Enhanced Auditing | Create `Externalable` interface (integrationId, integrationRemoteId, lastSyncTime) and add `updatedSource` to `AuditFields` with custom auditing listener. | TASK-009 | Completed |
| [TASK-011](./TASK-011.md) | Organization & Integration Entities | Create `Organization` and `Integration` entities with bidirectional relationships, repositories, and DB migration. | TASK-010 | Completed |
| [TASK-012](./TASK-012.md) | ~~Custom Principal with Channel Tracking~~ | ~~Create `GeodeUserPrincipal` carrying auth channel (WEB, MCP, INTEGRATION), update `TokenAuthenticationFilter` for both auth paths, and custom auditing listener to populate `updatedSource`.~~ | TASK-010 | Cancelled |
| [TASK-013](./TASK-013.md) | Frontend - Organization Summary Page | Create `/home/organization` page with name edit, backend API, routing, and sidebar navigation. | TASK-011 | Completed |
| [TASK-014](./TASK-014.md) | Frontend - Integrations Management Page | Create `/home/integrations` page with full CRUD (list, create, edit, delete), dynamic parameter grid, type selector, backend API, routing, sidebar. | TASK-011 | Completed |
| [TASK-015](./TASK-015.md) | Audit & Fix Existing Entities | Review all entities (User, Person, Login, McpKey, Organization, Integration) against EntityStandards.md; fix fetch types, collection types, Lombok, cascade. | TASK-011 | Completed |
| [TASK-016](./TASK-016.md) | Add createdByName/modifiedByName to AuditFields | Add `createdByName` and `modifiedByName` to AuditFields for historical auditability after hard-delete of users (GDPR/CCPA compliance). | TASK-010 | Completed |
| [TASK-017](./TASK-017.md) | Create Address Entity | Create `Address` entity with address fields (line1, line2, line3, city, state, postalCode, country) for Property, Unit, and Person addresses. | None | Open |
| [TASK-018](./TASK-018.md) | Create Property Entity | Create `Property` entity implementing `Externalable` with name, externalPropertyId, propertyType, numberOfUnits, managementCompany, and Address/Organization relationships. | TASK-017 | Open |
| [TASK-019](./TASK-019.md) | Create Unit Entity | Create `Unit` entity implementing `Externalable` with unitNumber, externalUnitId, unitType, squareFootage, bedrooms, bathrooms, rentAmount, isOccupied, and Property/Address relationships. | TASK-017, TASK-018 | Open |
| [TASK-020](./TASK-020.md) | Enhance Person Entity for PM Integration | Add phoneNumber, email, externalTenantId, leaseStartDate, leaseEndDate, User, Integration, and Unit relationships to existing Person entity. Person implements Externalable. Tenant represented by Person, not separate entity. Organization resolved via person.getUser().getOrganization() or person.getIntegration().getOrganization(). | TASK-019 | Open |
| [TASK-021](./TASK-021.md) | Create WorkOrder Entity | Create `WorkOrder` entity implementing `Externalable` with title, summary, description, status, priority, amount, callSource, callerName, callerContactInfo, and Property/Unit/Person relationships. | TASK-018, TASK-019, TASK-020 | Open |
| [TASK-022](./TASK-022.md) | Create Database Migration Scripts | Create Flyway migration scripts (V1-V5) for address, property, unit, person (new columns), and work_order tables with foreign key constraints. | TASK-017 through TASK-021 | Open |
| [TASK-023](./TASK-023.md) | Create Integration Documentation | Create `docs/pm-integration-mapping.md` mapping Buildium and Skywalk API fields to internal entities, identifying excluded external fields and internal-only fields. | TASK-017 through TASK-021 | Open |
| [TASK-024](./TASK-024.md) | Add Repository Methods and Integration Tests | Add custom query methods to all repository interfaces and create integration tests verifying entity relationships and repository operations. | TASK-017 through TASK-022 | Open |
| [TASK-025](./TASK-025.md) | Define IntegrationRunner Interface and IntegrationType Enum | Create `IntegrationRunner` interface with `getType()` and `run(Integration)`, `IntegrationType` enum (BUILDIUM, APPOFOLIO), add `spring-boot-starter-scheduling` dependency to `build.gradle.kts`, add `@EnableScheduling` to `DataConfiguration`. | None | Open |
| [TASK-026](./TASK-026.md) | Create IntegrationSchedule Entity and Repository | Create `IntegrationSchedule` entity with cronExpression, isActive, lastRunTime, nextRunTime, maxRetries, currentRetryCount, and `@ManyToOne` to `Integration`. Create `IntegrationScheduleRepository` with `findByActiveIsTrue`, `findByIntegration`, `findByIntegrationAndActiveIsTrue`. | TASK-025 | Open |
| [TASK-027](./TASK-027.md) | Create IntegrationDispatcher and BuildiumIntegrationRunner | Create `IntegrationDispatcher` service with constructor injection of all `IntegrationRunner` beans and `dispatch(UUID, IntegrationRepository)` method. Create `BuildiumIntegrationRunner` stub `@Component` implementing `IntegrationRunner`. | TASK-025 | Open |
| [TASK-028](./TASK-028.md) | Create IntegrationJobService with @Scheduled Cron Execution | Create `IntegrationJobService` with `@Scheduled(cron = "0 */5 * * * *")` method polling `IntegrationScheduleRepository.findByActiveIsTrue()` and triggering runs via dispatcher. Error handling with retry logic and auto-disable after max retries. | TASK-025, TASK-026, TASK-027 | Open |
| [TASK-029](./TASK-029.md) | Add Manual Trigger Endpoints for Web Client and MCP | Add `POST /services/integrations/{integrationId}/run` REST endpoint to `IntegrationController` with auth/org validation. Create `IntegrationMcpService` `@McpService` with `@McpTool(name = "runIntegration")` for MCP trigger. | TASK-025, TASK-027 | Open |
| [TASK-030](./TASK-030.md) | Add Integration Scaffolding Tests | Create 5 test files: `IntegrationDispatcherTest`, `IntegrationJobServiceTest`, `IntegrationControllerTest`, `IntegrationMcpServiceTest`, `IntegrationScheduleRepositoryTest`. Cover valid dispatch, invalid type, scheduled execution, retry logic, endpoint auth, and MCP trigger. | TASK-025 through TASK-029 | Open |
| [TASK-031](./TASK-031.md) | Create Integration Scaffolding Documentation | Create `docs/integration-scaffolding.md` with architecture diagram, component relationships, adding-new-type guide, trigger mechanism docs (cron, web client, MCP), entity field references, and external field exclusions. | TASK-025 through TASK-030 | Open |

### Status Explanation

- **Completed**: The task has been fully implemented, tested, and verified. All acceptance criteria have been met.
- **Open**: The task is in progress or has not yet been started. Work is ongoing or needed.
- **Blocked**: The task cannot progress due to external dependencies, blocked resources, or unresolved issues. Progress will resume once the blocking issue is resolved.
- **Cancelled**: The task is no longer necessary. The goal was either already achieved by another task, deemed unnecessary, or superseded by a different approach.

### Next Agent Instructions

When you complete a task, please update its status from `Open` to `Completed` in the table above. The next agent should follow this pattern: If not givent a task by the prompt, find the first `Open` task in sequential order and work to complete it, then update its status to `Completed` if successful.
