# TASK-024: Add Repository Methods and Integration Tests

## Goal
Add custom query methods to repository interfaces for the new entities and create integration tests to verify entity relationships and repository functionality.

## Requirements

### Repository Updates

#### PersonRepository (modify)
File: `src/main/java/com/geodevai/data/repository/PersonRepository.java`

Add the following methods:
```java
Optional<Person> findByPhoneNumber(String phoneNumber);
Optional<Person> findByEmail(String email);
Optional<Person> findByExternalTenantId(String externalTenantId);
List<Person> findByUnit(Unit unit);
```

Ensure `@RepositoryRestResource(path = "person")` annotation is present.

#### PropertyRepository (modify)
File: `src/main/java/com/geodevai/data/repository/PropertyRepository.java`

Ensure `@RepositoryRestResource(path = "property")` annotation is present. The repository was created in TASK-018.

#### UnitRepository (modify)
File: `src/main/java/com/geodevai/data/repository/UnitRepository.java`

Ensure `@RepositoryRestResource(path = "unit")` annotation is present. The repository was created in TASK-019.

#### WorkOrderRepository (modify)
File: `src/main/java/com/geodevai/data/repository/WorkOrderRepository.java`

Ensure `@RepositoryRestResource(path = "work-order")` annotation is present. The repository was created in TASK-021.

### Test Files

Create the following test files:

#### AddressRepositoryTest
File: `src/test/java/com/geodevai/data/repository/AddressRepositoryTest.java`
- Test: `testFindByExternalPropertyId()` — verify address retrieval
- Test: `testSaveAndFindById()` — basic CRUD
- Uses `@DataJpaTest` annotation
- Uses `@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)` for H2 test database

#### PropertyRepositoryTest
File: `src/test/java/com/geodevai/data/repository/PropertyRepositoryTest.java`
- Test: `testFindByOrganization()` — verify property retrieval by organization
- Test: `testFindByExternalPropertyId()` — verify external ID lookup
- Test: `testSaveWithAddress()` — verify Property-Address relationship
- Uses `@DataJpaTest` annotation
- Create test Property and Address entities, verify bidirectional relationship works

#### UnitRepositoryTest
File: `src/test/java/com/geodevai/data/repository/UnitRepositoryTest.java`
- Test: `testFindByProperty()` — verify unit retrieval by property
- Test: `testFindByUnitNumberAndProperty()` — verify unit number + property lookup
- Test: `testSaveWithPropertyAndAddress()` — verify Unit-Property-Address relationship
- Uses `@DataJpaTest` annotation

#### WorkOrderRepositoryTest
File: `src/test/java/com/geodevai/data/repository/WorkOrderRepositoryTest.java`
- Test: `testFindByProperty()` — verify work order retrieval by property
- Test: `testFindByUnit()` — verify work order retrieval by unit
- Test: `testFindByStatus()` — verify work order retrieval by status
- Test: `testFindByExternalWorkOrderId()` — verify external ID lookup
- Test: `testSaveWithPropertyUnitAndTenant()` — verify WorkOrder-Property-Unit-Person relationships
- Uses `@DataJpaTest` annotation

#### PersonRepositoryTest
File: `src/test/java/com/geodevai/data/repository/PersonRepositoryTest.java`
- Test: `testFindByPhoneNumber()` — verify phone lookup
- Test: `testFindByEmail()` — verify email lookup
- Test: `testFindByExternalTenantId()` — verify external tenant ID lookup
- Test: `testFindByUnit()` — verify tenant retrieval by unit
- Uses `@DataJpaTest` annotation
- Verify that enhanced Person fields (phoneNumber, email, unit relationship) work correctly

### Test Configuration
- All tests use `@DataJpaTest` for JPA-focused tests
- H2 in-memory database for testing (matching `application-DEV.properties`)
- Use `@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)` to use the H2 test database
- Create helper methods to build test entities
- Verify that all entity relationships work correctly

## Acceptance Criteria
- All repository methods compile and have correct signatures
- All test files exist and compile without errors
- All tests pass when run with `./gradlew test`
- Entity relationships work correctly (Property → Unit → WorkOrder, Property → Address, Person → Unit)
- `@RepositoryRestResource` annotations are present on all repositories
- Test coverage includes CRUD operations and custom query methods
- No test failures related to the new entities

## Files Likely Involved
- `src/main/java/com/geodevai/data/repository/PersonRepository.java` (modify)
- `src/main/java/com/geodevai/data/repository/PropertyRepository.java` (modify)
- `src/main/java/com/geodevai/data/repository/UnitRepository.java` (modify)
- `src/main/java/com/geodevai/data/repository/WorkOrderRepository.java` (modify)
- `src/test/java/com/geodevai/data/repository/AddressRepositoryTest.java` (new)
- `src/test/java/com/geodevai/data/repository/PropertyRepositoryTest.java` (new)
- `src/test/java/com/geodevai/data/repository/UnitRepositoryTest.java` (new)
- `src/test/java/com/geodevai/data/repository/WorkOrderRepositoryTest.java` (new)
- `src/test/java/com/geodevai/data/repository/PersonRepositoryTest.java` (new)

## Dependencies
- TASK-017 through TASK-022 (all entity tasks, migration scripts)

## Constraints
- Follow existing test patterns (e.g., `McpSampleServiceTest.java` uses `@ExtendWith(MockitoExtension.class)` and JUnit 5)
- Use `@DataJpaTest` for repository tests (not `@ExtendWith(MockitoExtension.class)`)
- Do NOT test integration with external APIs (Buildium, Skywalk) — that's out of scope
- All test data should be in-memory H2
- Use `@Rollback` on write operations to avoid polluting the test database
