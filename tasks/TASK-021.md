# TASK-021: Create WorkOrder Entity

## Goal
Create a provider-neutral `WorkOrder` entity representing a maintenance request, implementing `Externalable` for future PM integration. This is the core entity for the Voice Agent workflow.

## Requirements

Create `WorkOrder` JPA entity at `src/main/java/com/geodevai/data/model/WorkOrder.java`:

### Fields
- `workOrderId` (UUID, `@Id`, `@GeneratedValue(strategy = GenerationType.UUID)`) — primary key
- `externalWorkOrderId` (String, `@Column(nullable = false)`) — the PM system's assigned work order ID
- `title` (String, `@Column(nullable = false)`) — brief summary (e.g., "water leak")
- `summary` (String) — short description of the issue
- `description` (String) — detailed description (from Voice Agent input)
- `workDetails` (String) — detailed work description from PM system
- `status` (String, `@Column(nullable = false)`) — current status (e.g., "OPEN", "IN_PROGRESS", "COMPLETED", "CANCELLED")
- `priority` (String) — priority level (e.g., "LOW", "MEDIUM", "HIGH")
- `workOrderType` (String) — type of work order (e.g., "MAINTENANCE", "REPAIR")
- `amount` (java.math.BigDecimal) — cost associated with the work order
- `dueDate` (java.time.LocalDateTime) — due date for completion
- `completionDate` (java.time.LocalDateTime) — date when work order was completed
- `entryNotes` (String) — notes entered during work order creation (from Voice Agent)
- `vendorNotes` (String) — notes for the vendor/contractor
- `invoiceNumber` (String) — invoice number if applicable
- `chargeableTo` (String) — who the work order is chargeable to
- `callSource` (String) — source of the work order (e.g., "VOICE_AGENT")
- `callerName` (String) — name of the person who called in (may or may not be the listed tenant)
- `callerContactInfo` (String) — phone or email of the caller
- `property` (`@ManyToOne(fetch = FetchType.LAZY)`) → `Property` — the property associated with this work order
- `unit` (`@ManyToOne(fetch = FetchType.LAZY)`) → `Unit` — the unit associated with this work order
- `tenant` (`@ManyToOne(fetch = FetchType.LAZY)`) → `Person` — the tenant associated with this work order (optional, caller may not be listed tenant)
- `assignedTo` (String) — name/ID of person assigned to the work order
- `vendorId` (String) — the PM system's vendor ID if applicable

### Externalable Implementation
The `WorkOrder` entity must implement the `Externalable` interface:
- `getIntegrationId()` / `setIntegrationId(UUID)`
- `getIntegrationRemoteId()` / `setIntegrationRemoteId(String)` — stores `externalWorkOrderId`
- `getLastSyncTime()` / `setLastSyncTime(LocalDateTime)`

### Entity Configuration
- `@Entity`, `@Table(name = "work_order")`
- Extends `AuditableEntity`, implements `Externalable`
- `@Getter`, `@Setter`, `@NoArgsConstructor` (no `@Data`)

### Repository
Create `src/main/java/com/geodevai/data/repository/WorkOrderRepository.java`:
- Extends `JpaRepository<WorkOrder, UUID>`
- `@RepositoryRestResource(path = "work-order")`
- Custom methods:
  - `List<WorkOrder> findByProperty(Property property)`
  - `List<WorkOrder> findByUnit(Unit unit)`
  - `List<WorkOrder> findByStatus(String status)`
  - `Optional<WorkOrder> findByExternalWorkOrderId(String externalWorkOrderId)`
  - `List<WorkOrder> findByTenant(Person tenant)`

## Acceptance Criteria
- `WorkOrder` entity compiles without errors
- Implements `Externalable` interface correctly
- Has `Property`, `Unit`, and `Person` relationships with `FetchType.LAZY`
- `externalWorkOrderId` is `@Column(nullable = false)` — required for integration
- `title` is `@Column(nullable = false)` — required for work order creation
- `status` is `@Column(nullable = false)` — required to track work order state
- `tenant` relationship is optional (nullable) — caller may not be the listed tenant
- Repository has all required custom query methods
- Entity follows all `EntityStandards.md` rules
- Uses `java.math.BigDecimal` for `amount`

## Files Likely Involved
- `src/main/java/com/geodevai/data/model/WorkOrder.java` (new)
- `src/main/java/com/geodevai/data/repository/WorkOrderRepository.java` (new)

## Dependencies
- TASK-018 (Property entity)
- TASK-019 (Unit entity)
- TASK-020 (Person entity enhancement)

## Constraints
- Must follow `EntityStandards.md` strictly
- Store external IDs as `String` to remain provider-neutral (Buildium uses int32, Skywalk uses string)
- `tenant` relationship is `@ManyToOne(fetch = FetchType.LAZY)` — optional, nullable since caller may not be the listed tenant
- `@JoinColumn(name = "property_id")` for property relationship
- `@JoinColumn(name = "unit_id")` for unit relationship
- `@JoinColumn(name = "tenant_id")` for tenant relationship
- `callSource` identifies the source channel (e.g., "VOICE_AGENT") — this maps to `updatedSource` concept in AuditFields but is a separate domain field
- `callerName` and `callerContactInfo` are domain-specific fields with no direct external equivalent (they capture the Voice Agent input)
