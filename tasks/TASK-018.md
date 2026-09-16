# TASK-018: Create Property Entity

## Goal
Create a provider-neutral `Property` entity representing a rental property, implementing `Externalable` for future PM integration. This supports the workflow where a Voice Agent provides a property address and we create/retrieve work orders in the client's PM software.

## Requirements

Create `Property` JPA entity at `src/main/java/com/geodevai/data/model/Property.java`:

### Fields
- `propertyId` (UUID, `@Id`, `@GeneratedValue(strategy = GenerationType.UUID)`) — primary key
- `name` (String, `@Column(nullable = false)`) — property name
- `externalPropertyId` (String, `@Column(nullable = false)`) — the PM system's assigned property ID (Buildium uses integer, Skywalk/AppFolio uses string — store as string for provider-neutrality)
- `propertyType` (String) — type of property (e.g., "apartment", "house", "condo")
- `numberOfUnits` (Integer) — total number of units in the property
- `managementCompany` (String) — name of the management company
- `address` (`@ManyToOne(fetch = FetchType.LAZY)`) → `Address` — the property's address
- `organization` (`@ManyToOne(fetch = FetchType.LAZY)`) → `Organization` — the organization that owns this property
- `externalPropertyId` (String) — stores the PM-assigned ID for integration tracking

### Externalable Implementation
The `Property` entity must implement the `Externalable` interface:
- `getIntegrationId()` / `setIntegrationId(UUID)` — from `Externalable`
- `getIntegrationRemoteId()` / `setIntegrationRemoteId(String)` — stores `externalPropertyId`
- `getLastSyncTime()` / `setLastSyncTime(LocalDateTime)` — from `Externalable`

Since `Externalable` is an interface, implement it directly:
```java
public class Property extends AuditableEntity implements Externalable { ... }
```

### Entity Configuration
- `@Entity`, `@Table(name = "property")`
- Extends `AuditableEntity`, implements `Externalable`
- `@Getter`, `@Setter`, `@NoArgsConstructor` (no `@Data`)
- `@OneToMany(mappedBy = "property", fetch = FetchType.LAZY)` → `Set<Unit> units = new HashSet<>()`

### Repository
Create `src/main/java/com/geodevai/data/repository/PropertyRepository.java`:
- Extends `JpaRepository<Property, UUID>`
- `@RepositoryRestResource(path = "property")`
- Custom methods:
  - `Optional<Property> findByExternalPropertyId(String externalPropertyId)`
  - `List<Property> findByOrganization(Organization organization)`

## Acceptance Criteria
- `Property` entity compiles without errors
- Implements `Externalable` interface correctly
- Has `Address` and `Organization` relationships with `FetchType.LAZY`
- Has `Set<Unit>` collection with `FetchType.LAZY`
- `externalPropertyId` stores PM-assigned ID as string for provider-neutrality
- Repository has `findByExternalPropertyId` and `findByOrganization` methods
- Entity follows all `EntityStandards.md` rules

## Files Likely Involved
- `src/main/java/com/geodevai/data/model/Property.java` (new)
- `src/main/java/com/geodevai/data/repository/PropertyRepository.java` (new)

## Dependencies
- TASK-017 (Address entity)

## Constraints
- Must follow `EntityStandards.md` strictly
- Store external IDs as `String` to remain provider-neutral (Buildium uses int32, Skywalk uses string UUID)
- `externalPropertyId` must be `@Column(nullable = false)` since it's required for integration
- Do NOT add any Buildium-specific or Skywalk-specific fields directly to the entity
- `@JoinColumn(name = "organization_id", nullable = false)` for organization relationship
