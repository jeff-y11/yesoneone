# TASK-035: Add Organization-Scoped Repository Methods for Search

## Goal
Add custom JPQL query methods to `PropertyRepository`, `UnitRepository`, `PersonRepository`, and `WorkOrderRepository` that scope results to the authenticated user's organization. These methods power the search and view capabilities for externally-pulled entities.

## Organization Resolution Paths

Each entity reaches the organization through a different join path:

| Entity | Join Path | Direct FK |
|--------|-----------|-----------|
| `Property` | `property.organization` | `organization_id` |
| `Unit` | `unit.property.organization` | `property_id → organization_id` |
| `Person` | `person.integration.organization` | `integration_id → organization_id` |
| `WorkOrder` | `workorder.property.organization` | `property_id → organization_id` |

## Requirements

### 1. Add `findByOrganization` to UnitRepository
**File:** `src/main/java/com/geodevai/data/repository/UnitRepository.java` (modify)

Add JPQL query to scope by organization through the property relationship:

```java
@Query("SELECT u FROM Unit u JOIN u.property p WHERE p.organization = :organization")
List<Unit> findByOrganization(@Param("organization") Organization organization);
```

### 2. Add `findByOrganization` to PersonRepository
**File:** `src/main/java/com/geodevai/data/repository/PersonRepository.java` (modify)

Add JPQL query to scope by organization through the integration relationship:

```java
@Query("SELECT p FROM Person p JOIN p.integration i WHERE i.organization = :organization")
List<Person> findByOrganization(@Param("organization") Organization organization);
```

### 3. Add `findByOrganization` to WorkOrderRepository
**File:** `src/main/java/com/geodevai/data/repository/WorkOrderRepository.java` (modify)

Add JPQL query to scope by organization through the property relationship:

```java
@Query("SELECT wo FROM WorkOrder wo JOIN wo.property p WHERE p.organization = :organization")
List<WorkOrder> findByOrganization(@Param("organization") Organization organization);
```

### 4. Add Search/Filter Methods to Repositories

For each entity, add search methods that combine org scoping with keyword filtering:

#### PropertyRepository
```java
@Query("SELECT p FROM Property p WHERE p.organization = :organization AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.externalPropertyId) LIKE LOWER(CONCAT('%', :query, '%')))")
List<Property> searchByOrganizationAndQuery(@Param("organization") Organization organization, @Param("query") String query);
```

#### UnitRepository
```java
@Query("SELECT u FROM Unit u JOIN u.property p WHERE p.organization = :organization AND (LOWER(u.unitNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.externalUnitId) LIKE LOWER(CONCAT('%', :query, '%')))")
List<Unit> searchByOrganizationAndQuery(@Param("organization") Organization organization, @Param("query") String query);
```

#### PersonRepository
```java
@Query("SELECT p FROM Person p JOIN p.integration i WHERE i.organization = :organization AND (LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.email) LIKE LOWER(CONCAT('%', :query, '%')))")
List<Person> searchByOrganizationAndQuery(@Param("organization") Organization organization, @Param("query") String query);
```

#### WorkOrderRepository
```java
@Query("SELECT wo FROM WorkOrder wo JOIN wo.property p WHERE p.organization = :organization AND (LOWER(wo.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(wo.externalWorkOrderId) LIKE LOWER(CONCAT('%', :query, '%')))")
List<WorkOrder> searchByOrganizationAndQuery(@Param("organization") Organization organization, @Param("query") String query);
```

## Scope
This is read-only — search and view only. No create, update, or delete endpoints or UI. The integration is unidirectional from PM systems.

## Acceptance Criteria
- `UnitRepository.findByOrganization(Organization)` compiles and returns `List<Unit>`
- `PersonRepository.findByOrganization(Organization)` compiles and returns `List<Person>`
- `WorkOrderRepository.findByOrganization(Organization)` compiles and returns `List<WorkOrder>`
- All `searchByOrganizationAndQuery` methods compile and accept `Organization` + `String query`
- Search methods use case-insensitive `LIKE` matching on key fields
- `PropertyRepository.findByOrganization` already exists — verify it works
- All files compile without errors

## Files Likely Involved
- `src/main/java/com/geodevai/data/repository/UnitRepository.java` (modify)
- `src/main/java/com/geodevai/data/repository/PersonRepository.java` (modify)
- `src/main/java/com/geodevai/data/repository/WorkOrderRepository.java` (modify)
- `src/main/java/com/geodevai/data/repository/PropertyRepository.java` (modify — add search)

## Dependencies
- TASK-024 (existing repository methods)

## Constraints
- Must follow `EntityStandards.md` naming conventions
- All `@Query` annotations use JPQL (not native SQL)
- Parameters use `@Param` annotations
- Case-insensitive search with `LOWER()` function
- No `@Transactional` needed — read-only queries
