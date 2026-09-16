# TASK-019: Create Unit Entity

## Goal
Create a provider-neutral `Unit` entity representing a unit within a property, implementing `Externalable` for future PM integration. Units are associated with properties and addresses.

## Requirements

Create `Unit` JPA entity at `src/main/java/com/geodevai/data/model/Unit.java`:

### Fields
- `unitId` (UUID, `@Id`, `@GeneratedValue(strategy = GenerationType.UUID)`) — primary key
- `unitNumber` (String, `@Column(nullable = false)`) — unit identifier (e.g., "3C", "3D", "1A")
- `externalUnitId` (String, `@Column(nullable = false)`) — the PM system's assigned unit ID
- `unitType` (String) — type of unit (e.g., "apartment", "studio")
- `squareFootage` (Integer) — unit size in square feet
- `bedrooms` (Integer) — number of bedrooms
- `bathrooms` (Integer) — number of bathrooms
- `rentAmount` (java.math.BigDecimal) — monthly rent
- `isOccupied` (Boolean, default `false`) — occupancy status
- `property` (`@ManyToOne(fetch = FetchType.LAZY)`) → `Property` — the parent property
- `address` (`@ManyToOne(fetch = FetchType.LAZY)`) → `Address` — the unit's address

### Externalable Implementation
The `Unit` entity must implement the `Externalable` interface:
- `getIntegrationId()` / `setIntegrationId(UUID)`
- `getIntegrationRemoteId()` / `setIntegrationRemoteId(String)` — stores `externalUnitId`
- `getLastSyncTime()` / `setLastSyncTime(LocalDateTime)`

### Entity Configuration
- `@Entity`, `@Table(name = "unit")`
- Extends `AuditableEntity`, implements `Externalable`
- `@Getter`, `@Setter`, `@NoArgsConstructor` (no `@Data`)
- `@OneToMany(mappedBy = "unit", fetch = FetchType.LAZY)` → `Set<WorkOrder> workOrders = new HashSet<>()`

### Repository
Create `src/main/java/com/geodevai/data/repository/UnitRepository.java`:
- Extends `JpaRepository<Unit, UUID>`
- `@RepositoryRestResource(path = "unit")`
- Custom methods:
  - `List<Unit> findByProperty(Property property)`
  - `Optional<Unit> findByUnitNumberAndProperty(String unitNumber, Property property)`
  - `Optional<Unit> findByExternalUnitId(String externalUnitId)`

## Acceptance Criteria
- `Unit` entity compiles without errors
- Implements `Externalable` interface correctly
- Has `Property` and `Address` relationships with `FetchType.LAZY`
- Has `Set<WorkOrder>` collection with `FetchType.LAZY`
- `unitNumber` stores values like "3C", "3D" correctly
- `externalUnitId` stores PM-assigned ID as string
- Repository has `findByProperty`, `findByUnitNumberAndProperty`, `findByExternalUnitId` methods
- Entity follows all `EntityStandards.md` rules

## Files Likely Involved
- `src/main/java/com/geodevai/data/model/Unit.java` (new)
- `src/main/java/com/geodevai/data/repository/UnitRepository.java` (new)

## Dependencies
- TASK-017 (Address entity)
- TASK-018 (Property entity)

## Constraints
- Must follow `EntityStandards.md` strictly
- Store external IDs as `String` to remain provider-neutral
- `unitNumber` should be `String` not `Integer` — units may have formats like "3C", "APT-1", etc.
- `@JoinColumn(name = "property_id", nullable = false)` for property relationship
- `@JoinColumn(name = "address_id")` for address relationship (nullable since unit may not always have address)
- Use `java.math.BigDecimal` for `rentAmount` per Java conventions for monetary values
