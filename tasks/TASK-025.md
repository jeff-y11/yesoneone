# TASK-025: Define IntegrationRunner Interface and IntegrationType Enum

## Goal
Define the contract that all PM integration types must implement, and add the Spring scheduling dependency to the build configuration.

## Requirements

### 1. Add Spring Scheduling Dependency
**File:** `build.gradle.kts`

Add to `dependencies`:
```kotlin
implementation("org.springframework.boot:spring-boot-starter-scheduling")
```

### 2. Create `IntegrationRunner` Interface
**Location:** `src/main/java/com/geodevai/integration/IntegrationRunner.java`

Interface with a single method:
```java
public interface IntegrationRunner {
    void run(Integration integration);
}
```

Each PM integration type (Buildium, AppFolio/Skywalk) will implement this interface as a Spring `@Component`.

### 3. Create `IntegrationType` Enum
**Location:** `src/main/java/com/geodevai/integration/IntegrationType.java`

Enum with values:
```java
public enum IntegrationType {
    BUILDIUM,
    APPOFOLIO
}
```

Each enum value provides:
- `String getTypeName()` — returns the string representation (e.g., "BUILDIUM")
- `static IntegrationType fromString(String name)` — parses from string, returns null if not found

### 4. Update `Integration` Entity
**File:** `src/main/java/com/geodevai/data/model/Integration.java`

No structural changes needed — `type` field already exists as `String`. However, add a comment documenting that valid values are `IntegrationType` enum names.

### 5. Add `@EnableScheduling` Configuration
**File:** `src/main/java/com/geodevai/config/DataConfiguration.java` (modify) or new config class

Add `@EnableScheduling` annotation to enable Spring's task scheduling.

## Acceptance Criteria
- `build.gradle.kts` includes `spring-boot-starter-scheduling` dependency
- `IntegrationRunner` interface exists with `void run(Integration integration)` method
- `IntegrationType` enum exists with `BUILDIUM` and `APPOFOLIO` values
- `fromString` method works correctly
- `@EnableScheduling` is configured
- `Integration.type` field is documented to reference `IntegrationType` enum names
- All files compile without errors
- Entity standards followed (`@Getter/@Setter/@NoArgsConstructor`)

## Files Likely Involved
- `build.gradle.kts` (modify — add scheduling dependency)
- `src/main/java/com/geodevai/integration/IntegrationRunner.java` (new)
- `src/main/java/com/geodevai/integration/IntegrationType.java` (new)
- `src/main/java/com/geodevai/data/model/Integration.java` (modify — add comment)
- `src/main/java/com/geodevai/config/DataConfiguration.java` (modify — add `@EnableScheduling`)

## Dependencies
None (foundational scaffolding task)

## Constraints
- Must follow `EntityStandards.md`
- `IntegrationRunner` is a simple interface — no annotations needed
- `IntegrationType` enum values must match the `type` String values stored in `Integration`
- `@EnableScheduling` should not conflict with existing configuration
- The `Integration.type` field remains `String` — no migration needed
