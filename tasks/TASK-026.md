# TASK-026: Create IntegrationSchedule Entity and Repository

## Goal
Create a separate `IntegrationSchedule` entity for cron-based scheduling of integrations. A separate entity provides flexibility for multiple schedules per integration and keeps scheduling concerns decoupled from the `Integration` domain entity.

## Requirements

Create `IntegrationSchedule` JPA entity at `src/main/java/com/geodevai/data/model/IntegrationSchedule.java`:

### Fields
- `scheduleId` (UUID, `@Id`, `@GeneratedValue(strategy = GenerationType.UUID)`) — primary key
- `integration` (`@ManyToOne(fetch = FetchType.LAZY)`) → `Integration` — the integration this schedule belongs to
- `cronExpression` (String, `@Column(nullable = false)`) — cron expression (e.g., `0 0 * * * *` for hourly)
- `isActive` (Boolean, default `true`) — whether this schedule is active
- `lastRunTime` (LocalDateTime) — timestamp of last successful run
- `nextRunTime` (LocalDateTime) — calculated next run time
- `maxRetries` (Integer, default `3`) — maximum retry attempts on failure
- `currentRetryCount` (Integer, default `0`) — current retry count

### Entity Configuration
- `@Entity`, `@Table(name = "integration_schedule")`
- Extends `AuditableEntity`
- `@Getter`, `@Setter`, `@NoArgsConstructor`
- `@ManyToOne(fetch = FetchType.LAZY)` for `integration` relationship
- `@JoinColumn(name = "integration_id", nullable = false)`

### Repository
Create `src/main/java/com/geodevai/data/repository/IntegrationScheduleRepository.java`:
- Extends `JpaRepository<IntegrationSchedule, UUID>`
- `@RepositoryRestResource(path = "integration-schedule")`
- Custom methods:
  - `List<IntegrationSchedule> findByActiveIsTrue()` — get all active schedules
  - `Optional<IntegrationSchedule> findByIntegration(Integration integration)` — get schedule for a specific integration
  - `List<IntegrationSchedule> findByIntegrationAndActiveIsTrue(Integration integration)` — active schedule for integration

### Database Migration
Since `spring.jpa.hibernate.ddl-auto=update`, new tables will auto-create in dev. For production, migration scripts should be added to `TASK-022` or a follow-up.

## Acceptance Criteria
- `IntegrationSchedule` entity compiles without errors
- Has `Integration` relationship with `FetchType.LAZY` and `@JoinColumn(name = "integration_id", nullable = false)`
- `cronExpression` is `@Column(nullable = false)`
- `isActive`, `lastRunTime`, `nextRunTime`, `maxRetries`, `currentRetryCount` all present
- `IntegrationScheduleRepository` has `findByActiveIsTrue`, `findByIntegration`, `findByIntegrationAndActiveIsTrue` methods
- Entity follows all `EntityStandards.md` rules

## Files Likely Involved
- `src/main/java/com/geodevai/data/model/IntegrationSchedule.java` (new)
- `src/main/java/com/geodevai/data/repository/IntegrationScheduleRepository.java` (new)

## Dependencies
- TASK-025 (IntegrationRunner interface, IntegrationType enum, scheduling config)

## Constraints
- Must follow `EntityStandards.md` strictly
- `integration_id` foreign key must not be nullable
- `cronExpression` stored as String (Spring `@Scheduled` supports String cron expressions)
- `nextRunTime` and `lastRunTime` use `LocalDateTime`
- Do NOT add `@Data` annotation
