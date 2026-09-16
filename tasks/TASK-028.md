# TASK-028: Create IntegrationJobService with @Scheduled Cron Execution

## Goal
Create the Spring job service that polls `IntegrationSchedule` entities and triggers integration runs based on their cron expressions. This enables automatic scheduled execution of PM integrations.

## Requirements

### 1. Create `IntegrationJobService`
**Location:** `src/main/java/com/geodevai/integration/IntegrationJobService.java`

```java
@Service
public class IntegrationJobService {
    private final IntegrationScheduleRepository scheduleRepository;
    private final IntegrationDispatcher dispatcher;
    private final IntegrationRepository integrationRepository;

    public IntegrationJobService(IntegrationScheduleRepository scheduleRepository,
                                  IntegrationDispatcher dispatcher,
                                  IntegrationRepository integrationRepository) {
        this.scheduleRepository = scheduleRepository;
        this.dispatcher = dispatcher;
        this.integrationRepository = integrationRepository;
    }

    @Scheduled(cron = "0 */5 * * * *") // Run every 5 minutes
    public void runScheduledIntegrations() {
        List<IntegrationSchedule> activeSchedules = scheduleRepository.findByActiveIsTrue();
        for (IntegrationSchedule schedule : activeSchedules) {
            try {
                if (isDue(schedule)) {
                    dispatcher.dispatch(schedule.getIntegration().getIntegrationId(), integrationRepository);
                    schedule.setLastRunTime(LocalDateTime.now());
                    schedule.setCurrentRetryCount(0);
                    // Calculate nextRunTime from cron expression
                    schedule.setNextRunTime(calculateNextRunTime(schedule.getCronExpression()));
                    scheduleRepository.save(schedule);
                }
            } catch (Exception e) {
                schedule.setCurrentRetryCount(schedule.getCurrentRetryCount() + 1);
                if (schedule.getCurrentRetryCount() >= schedule.getMaxRetries()) {
                    schedule.setActive(false); // Disable schedule after max retries
                }
                scheduleRepository.save(schedule);
            }
        }
    }

    private boolean isDue(IntegrationSchedule schedule) {
        if (schedule.getNextRunTime() == null) return true;
        return LocalDateTime.now().isAfter(schedule.getNextRunTime());
    }

    private LocalDateTime calculateNextRunTime(String cronExpression) {
        // Use Spring's CronExpression or a simple parser
        // For simplicity, return LocalDateTime.now().plusMinutes(5) as placeholder
        return LocalDateTime.now().plusMinutes(5);
    }
}
```

### 2. Ensure `@EnableScheduling` is Configured
**File:** `src/main/java/com/geodevai/config/DataConfiguration.java` (modify)

Add `@EnableScheduling` to the existing `@Configuration` class:
```java
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableScheduling
@RequiredArgsConstructor
public class DataConfiguration implements RepositoryRestConfigurer { ... }
```

### 3. Add `@Scheduled` Dependency Verification
Verify `spring-boot-starter-scheduling` is in `build.gradle.kts` (added in TASK-025).

## Acceptance Criteria
- `IntegrationJobService` compiles without errors
- `@Scheduled(cron = "0 */5 * * * *")` method exists and runs every 5 minutes
- `runScheduledIntegrations()` iterates over all active `IntegrationSchedule` entities
- `isDue()` correctly determines if a schedule is ready to run
- Error handling: retry count increments, schedule disables after max retries
- `@EnableScheduling` is present in `DataConfiguration`
- `runScheduledIntegrations()` calls `dispatcher.dispatch()` with correct integration ID
- All files compile without errors

## Files Likely Involved
- `src/main/java/com/geodevai/integration/IntegrationJobService.java` (new)
- `src/main/java/com/geodevai/config/DataConfiguration.java` (modify — add `@EnableScheduling`)

## Dependencies
- TASK-025 (scheduling dependency, `@EnableScheduling`)
- TASK-026 (`IntegrationSchedule` entity, `IntegrationScheduleRepository`)
- TASK-027 (`IntegrationDispatcher`, `IntegrationRunner`)

## Constraints
- Must use `@Scheduled` annotation, not manual thread scheduling
- The 5-minute cron interval can be adjusted but should be the default
- `calculateNextRunTime` can be a simple placeholder — the actual cron parsing can be refined later
- Do NOT implement actual integration logic (Buildium, AppFolio API calls)
- Error handling should not crash the job — catch exceptions per-schedule
