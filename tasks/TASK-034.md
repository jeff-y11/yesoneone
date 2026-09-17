# TASK-034: Integration Management Tests

## Goal
Create integration tests for the new pause/resume endpoint and the integration management UI components.

## Requirements

### Test Files

#### IntegrationControllerTest (update)
**File:** `src/test/java/com/geodevai/controller/IntegrationControllerTest.java` (modify)

Add tests:
- `testToggleActive_ValidUser_TogglesActive()` — verify toggle changes active status
- `testToggleActive_UserNotInOrg_ReturnsForbidden()` — verify 403 for unauthorized user
- `testToggleActive_InvalidIntegrationId_ReturnsNotFound()` — verify 404 for non-existent integration
- `testToggleActive_TogglesBackAndForth()` — verify toggle works in both directions

#### IntegrationApiTest
**File:** `src/test/java/com/geodevai/integration/IntegrationApiTest.java` (new)
- Test `toggleActiveIntegration` API function
- Test `runIntegration` API function
- Verify error handling for network failures

#### IntegrationsPageTest
**File:** `src/test/java/com/geodevai/integration/IntegrationsPageTest.java` (new)
- Test that Run button appears only for active integrations
- Test that Pause/Resume button toggles correctly
- Test that clicking Run triggers the correct API call

### Test Configuration
- All tests use JUnit 5
- Use `@WebMvcTest` or `@ExtendWith(MockitoExtension.class)` with `MockMvc`
- Follow existing test patterns from `IntegrationControllerTest.java`
- Use `@MockBean` for repository dependencies
- Tests should be deterministic and fast

## Acceptance Criteria
- All test files exist and compile without errors
- All tests pass when run with `./gradlew test`
- Controller tests cover toggle endpoint scenarios
- API tests cover run and toggle client functions
- UI tests verify button visibility and behavior
- No test failures

## Files Likely Involved
- `src/test/java/com/geodevai/controller/IntegrationControllerTest.java` (modify)
- `src/test/java/com/geodevai/integration/IntegrationApiTest.java` (new)
- `src/test/java/com/geodevai/integration/IntegrationsPageTest.java` (new)

## Dependencies
- TASK-032 (Pause/Resume endpoint)
- TASK-033 (Run button)
- TASK-030 (existing test patterns)

## Constraints
- Follow existing test patterns from the project
- Do NOT test actual integration execution
- All mocks should be for integration layer components only
- Tests should be deterministic and fast
