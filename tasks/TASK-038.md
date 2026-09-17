# TASK-038: Search and View Tests for Person, Property, Unit, and WorkOrder

## Goal
Create integration tests for the new repository methods, REST controllers, and frontend components.

## Requirements

### Test Files

#### Repository Tests

##### PropertyRepositorySearchTest
**File:** `src/test/java/com/geodevai/data/repository/PropertyRepositorySearchTest.java` (new)
- Test `findByOrganization()` — verify returns only properties for given org
- Test `searchByOrganizationAndQuery()` — verify search filters by name
- Test `searchByOrganizationAndQuery()` with empty query — verify returns all for org
- Use `@ExtendWith(MockitoExtension.class)` with mocked repository

##### UnitRepositorySearchTest
**File:** `src/test/java/com/geodevai/data/repository/UnitRepositorySearchTest.java` (new)
- Test `findByOrganization()` — verify returns units through property join
- Test `searchByOrganizationAndQuery()` — verify search by unit number
- Use `@ExtendWith(MockitoExtension.class)` with mocked repository

##### PersonRepositorySearchTest
**File:** `src/test/java/com/geodevai/data/repository/PersonRepositorySearchTest.java` (new)
- Test `findByOrganization()` — verify returns persons through integration join
- Test `searchByOrganizationAndQuery()` — verify search by name/email
- Use `@ExtendWith(MockitoExtension.class)` with mocked repository

##### WorkOrderRepositorySearchTest
**File:** `src/test/java/com/geodevai/data/repository/WorkOrderRepositorySearchTest.java` (new)
- Test `findByOrganization()` — verify returns work orders through property join
- Test `searchByOrganizationAndQuery()` — verify search by title
- Use `@ExtendWith(MockitoExtension.class)` with mocked repository

#### Controller Tests

##### PropertyControllerTest
**File:** `src/test/java/com/geodevai/controller/PropertyControllerTest.java` (new)
- Test `testGetProperties_ValidUser_ReturnsList()` — verify 200 with list
- Test `testGetProperties_SearchQuery_ReturnsFiltered()` — verify search works
- Test `testGetProperty_ValidId_ReturnsProperty()` — verify single item
- Test `testGetProperty_NotInOrg_ReturnsForbidden()` — verify 403
- Use `@ExtendWith(MockitoExtension.class)` with `MockMvc`

##### UnitControllerTest
**File:** `src/test/java/com/geodevai/controller/UnitControllerTest.java` (new)
- Test `testGetUnits_ValidUser_ReturnsList()` — verify 200
- Test `testGetUnits_WithPropertyId_ReturnsFiltered()` — verify property filter
- Test `testGetUnit_NotInOrg_ReturnsForbidden()` — verify 403

##### PersonControllerTest
**File:** `src/test/java/com/geodevai/controller/PersonControllerTest.java` (new)
- Test `testGetPersons_ValidUser_ReturnsList()` — verify 200
- Test `testGetPersons_SearchQuery_ReturnsFiltered()` — verify search
- Test `testGetPerson_NotInOrg_ReturnsForbidden()` — verify 403

##### WorkOrderControllerTest
**File:** `src/test/java/com/geodevai/controller/WorkOrderControllerTest.java` (new)
- Test `testGetWorkOrders_ValidUser_ReturnsList()` — verify 200
- Test `testGetWorkOrders_WithStatus_ReturnsFiltered()` — verify status filter
- Test `testGetWorkOrder_NotInOrg_ReturnsForbidden()` — verify 403

### Test Configuration
- All tests use JUnit 5
- Use `@ExtendWith(MockitoExtension.class)` with `@Mock` and `@InjectMocks`
- Follow existing test patterns from `IntegrationControllerTest.java`
- Tests should be deterministic and fast

## Acceptance Criteria
- All test files exist and compile without errors
- All tests pass when run with `./gradlew test`
- Repository tests verify org-scoped queries
- Controller tests verify auth, org validation, search, and filtering
- No test failures

## Files Likely Involved
- `src/test/java/com/geodevai/data/repository/PropertyRepositorySearchTest.java` (new)
- `src/test/java/com/geodevai/data/repository/UnitRepositorySearchTest.java` (new)
- `src/test/java/com/geodevai/data/repository/PersonRepositorySearchTest.java` (new)
- `src/test/java/com/geodevai/data/repository/WorkOrderRepositorySearchTest.java` (new)
- `src/test/java/com/geodevai/controller/PropertyControllerTest.java` (new)
- `src/test/java/com/geodevai/controller/UnitControllerTest.java` (new)
- `src/test/java/com/geodevai/controller/PersonControllerTest.java` (new)
- `src/test/java/com/geodevai/controller/WorkOrderControllerTest.java` (new)

## Dependencies
- TASK-035 (repository methods)
- TASK-036 (REST controllers)
- TASK-030 (existing test patterns)

## Constraints
- Follow existing test patterns from the project
- Do NOT test external API integration
- All mocks should be for repository and service layer components only
- Tests should be deterministic and fast
