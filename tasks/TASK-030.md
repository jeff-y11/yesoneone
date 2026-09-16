# TASK-030: Add Integration Scaffolding Tests

## Goal
Create integration tests for all new components: `IntegrationDispatcher`, `IntegrationJobService`, REST endpoint, and MCP tool.

## Requirements

### Test Files

#### IntegrationDispatcherTest
**File:** `src/test/java/com/geodevai/integration/IntegrationDispatcherTest.java`
- Test: `testDispatch_ValidType_CallsRunner()` — verify runner is called for valid integration type
- Test: `testDispatch_InvalidType_ThrowsRuntimeException()` — verify exception thrown for unregistered type
- Test: `testConstructor_RegisterAllRunners()` — verify constructor injection registers all runners
- Use `@ExtendWith(MockitoExtension.class)` 
- Use `@Mock IntegrationRepository` and `@Mock IntegrationRunner` (BuildiumIntegrationRunner)
- Use `@InjectMocks IntegrationDispatcher`

#### IntegrationJobServiceTest
**File:** `src/test/java/com/geodevai/integration/IntegrationJobServiceTest.java`
- Test: `testRunScheduledIntegrations_ActiveSchedule_CallsDispatch()` — verify scheduled jobs trigger dispatch
- Test: `testRunScheduledIntegrations_InactiveSchedule_Skips()` — verify inactive schedules are skipped
- Test: `testRunScheduledIntegrations_MaxRetries_DisablesSchedule()` — verify schedule disables after max retries
- Use `@DataJpaTest` or `@ExtendWith(MockitoExtension.class)`
- Use `@Mock IntegrationScheduleRepository`, `@Mock IntegrationDispatcher`, `@Mock IntegrationRepository`
- Use `@InjectMocks IntegrationJobService`

#### IntegrationControllerTest
**File:** `src/test/java/com/geodevai/controller/IntegrationControllerTest.java`
- Test: `testRunIntegration_ValidUserAndIntegration_ReturnsRunning()` — verify successful trigger returns 200
- Test: `testRunIntegration_UserNotInOrg_ReturnsForbidden()` — verify 403 for unauthorized user
- Test: `testRunIntegration_InactiveIntegration_ReturnsBadRequest()` — verify 400 for inactive integration
- Test: `testCreateIntegration_WithType()` — verify `type` field works correctly in POST
- Use `@WebMvcTest(IntegrationController.class)` or `@ExtendWith(MockitoExtension.class)` with MockMvc
- Use `@Mock IntegrationRepository`, `@Mock UserRepository`, `@Mock OrganizationRepository`, `@Mock IntegrationDispatcher`

#### IntegrationMcpServiceTest
**File:** `src/test/java/com/geodevai/integration/mcp/IntegrationMcpServiceTest.java`
- Test: `testRunIntegration_ValidId_ReturnsRunning()` — verify MCP tool triggers dispatch
- Test: `testRunIntegration_InvalidId_ReturnsFailed()` — verify MCP tool returns failure status
- Use `@ExtendWith(MockitoExtension.class)`
- Use `@Mock IntegrationDispatcher`, `@Mock IntegrationRepository`
- Use `@InjectMocks IntegrationMcpService`

#### IntegrationScheduleRepositoryTest
**File:** `src/test/java/com/geodevai/data/repository/IntegrationScheduleRepositoryTest.java`
- Test: `testFindByActiveIsTrue()` — verify active schedule retrieval
- Test: `testFindByIntegration()` — verify schedule lookup by integration
- Use `@DataJpaTest`

### Test Configuration Notes
- All tests use JUnit 5
- Mockito for service/repository tests
- `MockMvc` for controller tests
- Follow existing test patterns from `McpIntegrationTest.java` and `McpSampleServiceTest.java`
- Use `@MockBean` instead of `@Mock` when using `@DataJpaTest` or `@WebMvcTest`

## Acceptance Criteria
- All test files exist and compile without errors
- All tests pass when run with `./gradlew test`
- `IntegrationDispatcherTest` covers valid dispatch, invalid type, and constructor registration
- `IntegrationJobServiceTest` covers active/inactive schedules and retry logic
- `IntegrationControllerTest` covers all three endpoint scenarios
- `IntegrationMcpServiceTest` covers MCP tool success and failure paths
- `IntegrationScheduleRepositoryTest` covers repository query methods
- No test failures

## Files Likely Involved
- `src/test/java/com/geodevai/integration/IntegrationDispatcherTest.java` (new)
- `src/test/java/com/geodevai/integration/IntegrationJobServiceTest.java` (new)
- `src/test/java/com/geodevai/controller/IntegrationControllerTest.java` (new)
- `src/test/java/com/geodevai/integration/mcp/IntegrationMcpServiceTest.java` (new)
- `src/test/java/com/geodevai/data/repository/IntegrationScheduleRepositoryTest.java` (new)

## Dependencies
- TASK-025 through TASK-029

## Constraints
- Follow existing test patterns from the project
- Do NOT test actual PM API calls
- All mocks should be for integration layer components only
- Tests should be deterministic and fast
