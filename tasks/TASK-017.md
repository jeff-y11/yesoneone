# TASK-017: Create Address Entity

## Goal
Create a provider-neutral `Address` entity as a supporting entity for Property, Unit, and Person addresses. This entity is needed for PM integration (Buildium, Skywalk/AppFolio).

## Requirements

Create `Address` JPA entity at `src/main/java/com/geodevai/data/model/Address.java`:

### Fields
- `addressId` (UUID, `@Id`, `@GeneratedValue(strategy = GenerationType.UUID)`) — primary key
- `addressLine1` (String, `@Column(nullable = false)`) — street, PO Box, or company name
- `addressLine2` (String) — apartment, suite, unit, or building
- `addressLine3` (String) — additional address line
- `city` (String) — city, district, suburb, town, or village
- `state` (String) — state, county, province, or region
- `postalCode` (String) — ZIP or postal code
- `country` (String) — country

### Entity Configuration
- Extends `AuditableEntity`
- Uses `@Getter`, `@Setter`, `@NoArgsConstructor` (no `@Data`)
- `@Entity`, `@Table(name = "address")`
- Follows `EntityStandards.md` strictly (UUID surrogate key, `@Id` named `addressId`, lazy fetching for relationships — none here)

### Repository
Create `src/main/java/com/geodevai/data/repository/AddressRepository.java`:
- Extends `JpaRepository<Address, UUID>`
- `@RepositoryRestResource(path = "address")`
- No custom query methods needed initially

### Database Migration
Since the project uses `spring.jpa.hibernate.ddl-auto=update` (no Flyway/Liquibase found), no migration script is required for development. For production, a migration script should be added later if needed.

## Acceptance Criteria
- `Address` entity compiles without errors
- Entity follows all `EntityStandards.md` rules (UUID PK named `addressId`, `@Getter/@Setter/@NoArgsConstructor`, extends `AuditableEntity`)
- `AddressRepository` compiles and works
- `@Table(name = "address")` matches snake_case convention
- No provider-specific fields (Buildium/Skywalk fields mapped separately in documentation)

## Files Likely Involved
- `src/main/java/com/geodevai/data/model/Address.java` (new)
- `src/main/java/com/geodevai/data/repository/AddressRepository.java` (new)

## Dependencies
None (foundational entity task)

## Constraints
- Must follow `EntityStandards.md` strictly
- Do NOT add `@Data` annotation
- Do NOT add any provider-specific fields (Buildium `AddressMessage` has `Country`, `State`, etc. — these map to our fields but the entity must remain provider-neutral)
- Use `String` for all address fields (no integer types)
- `@Column(nullable = false)` on `addressLine1`