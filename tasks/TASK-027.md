# TASK-027: Create IntegrationDispatcher and BuildiumIntegrationRunner

## Goal
Create the `IntegrationDispatcher` service that discovers all `IntegrationRunner` beans and dispatches by integration type. Also create the `BuildiumIntegrationRunner` stub implementation.

## Requirements

### 1. Create `IntegrationDispatcher` Service
**Location:** `src/main/java/com/geodevai/integration/IntegrationDispatcher.java`

```java
@Service
public class IntegrationDispatcher {
    private final Map<String, IntegrationRunner> runners = new HashMap<>();

    // Constructor injection: Spring injects all IntegrationRunner beans keyed by type
    public IntegrationDispatcher(List<IntegrationRunner> allRunners) {
        for (IntegrationRunner runner : allRunners) {
            runners.put(runner.getClass().getAnnotation(IntegrationType.class).name(), runner);
        }
    }

    public void dispatch(UUID integrationId, IntegrationRepository integrationRepository) {
        Integration integration = integrationRepository.findById(integrationId)
                .orElseThrow(() new RuntimeException("Integration not found"));
        String type = integration.getType();
        IntegrationRunner runner = runners.get(type);
        if (runner == null) {
            throw new RuntimeException("No runner registered for type: " + type);
        }
        runner.run(integration);
    }
}
```

Wait — a better approach: each `IntegrationRunner` implementation should provide a `getType()` method. Let me revise:

**Updated `IntegrationRunner` interface** (add to TASK-025):
```java
public interface IntegrationRunner {
    String getType();
    void run(Integration integration);
}
```

**Updated `IntegrationDispatcher`**:
```java
@Service
public class IntegrationDispatcher {
    private final Map<String, IntegrationRunner> runners = new HashMap<>();

    public IntegrationDispatcher(List<IntegrationRunner> allRunners) {
        for (IntegrationRunner runner : allRunners) {
            runners.put(runner.getType(), runner);
        }
    }

    public void dispatch(UUID integrationId, IntegrationRepository integrationRepository) {
        Integration integration = integrationRepository.findById(integrationId)
                .orElseThrow(() -> new RuntimeException("Integration not found: " + integrationId));
        IntegrationRunner runner = runners.get(integration.getType());
        if (runner == null) {
            throw new RuntimeException("No runner registered for type: " + integration.getType());
        }
        runner.run(integration);
    }
}
```

### 2. Create `BuildiumIntegrationRunner` Stub
**Location:** `src/main/java/com/geodevai/integration/BuildiumIntegrationRunner.java`

```java
@Component
public class BuildiumIntegrationRunner implements IntegrationRunner {
    @Override
    public String getType() { return IntegrationType.BUILDIUM.getTypeName(); }

    @Override
    public void run(Integration integration) {
        // TODO: Implement Buildium full-pull logic
        // For now, just log the integration details
    }
}
```

Note: The `@Component` annotation ensures Spring discovers this bean for constructor injection into `IntegrationDispatcher`.

### 3. Update `IntegrationRunner` Interface
The `IntegrationRunner` interface should have two methods:
- `String getType()` — returns the integration type name (e.g., "BUILDIUM")
- `void run(Integration integration)` — performs the full-pull

## Acceptance Criteria
- `IntegrationDispatcher` service compiles with constructor injection of all `IntegrationRunner` beans
- `dispatch(UUID, IntegrationRepository)` method works correctly
- `BuildiumIntegrationRunner` implements `IntegrationRunner`, has `@Component` annotation
- `getType()` returns "BUILDIUM"
- `run(Integration)` method exists (stub implementation acceptable)
- If `runners.get(type)` returns null, throws `RuntimeException` with descriptive message
- All files compile without errors

## Files Likely Involved
- `src/main/java/com/geodevai/integration/IntegrationRunner.java` (modify — add `getType()`)
- `src/main/java/com/geodevai/integration/IntegrationDispatcher.java` (new)
- `src/main/java/com/geodevai/integration/BuildiumIntegrationRunner.java` (new)

## Dependencies
- TASK-025 (IntegrationRunner interface, IntegrationType enum)

## Constraints
- `IntegrationDispatcher` must use constructor injection (not field injection)
- `IntegrationRunner` implementations must use `@Component` for auto-discovery
- `getType()` must return a String matching `Integration` entity's `type` field
- Do NOT implement actual Buildium API calls — that's out of scope
- The stub `run()` method should log the integration name and type
