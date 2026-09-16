# TASK-010: Create Externalable Interface and Enhanced Auditing Fields

## Goal
Create the `Externalable` interface for entities that integrate with external APIs, and add the `updatedSource` field to `AuditFields` to track the channel/source of updates.

## Requirements

### 1. Create `Externalable` Interface
**Location:** `src/main/java/com/geodevai/data/model/Externalable.java`

Interface with the following methods:
- `UUID getIntegrationId()` / `void setIntegrationId(UUID integrationId)` — References the `Integration` entity that owns this record
- `String getIntegrationRemoteId()` / `void setIntegrationRemoteId(String remoteId)` — The partner/external system's assigned ID
- `LocalDateTime getLastSyncTime()` / `void setLastSyncTime(LocalDateTime lastSyncTime)` — Timestamp of last successful sync

### 2. Add `updatedSource` to `AuditFields`
**Location:** `src/main/java/com/geodevai/data/model/AuditFields.java`

Add new field:
```java
@Column(name = "updated_source", length = 255)
private String updatedSource;
```

Include getter/setter via Lombok (already covered by `@Getter`/`@Setter`).

### 3. Update `AuditorAware` to Populate `updatedSource`
**Location:** `src/main/java/com/geodevai/config/WebSecurityConfig.java` (or new audit configuration)

The `AuditorAware<UUID>` currently only returns the user ID. We need a mechanism to also populate `updatedSource` on the entity being saved. Options:
- **Option A:** Custom `AuditorAware` that also sets a thread-local/contextual `updatedSource` that an `EntityListener` picks up
- **Option B:** Spring Data JPA `@EntityListener` / `@PrePersist` / `@PreUpdate` that reads from `SecurityContext`
- **Option C:** Custom `AuditingEntityListener` extension

**Recommended:** Create a custom `AuditingEntityListener` that:
1. Gets the current `Authentication` from `SecurityContextHolder`
2. Extracts the "channel" from the principal (see TASK-013 for principal changes)
3. Sets `auditFields.setUpdatedSource(channel)` before save

## Acceptance Criteria
- `Externalable` interface exists and compiles
- `AuditFields` has `updatedSource` field with proper JPA column mapping
- Entities implementing `Externalable` can be persisted with integration tracking
- `updatedSource` is automatically populated on create/update

## Files Likely Involved
- `src/main/java/com/geodevai/data/model/Externalable.java` (new)
- `src/main/java/com/geodevai/data/model/AuditFields.java` (modified)
- `src/main/java/com/geodevai/config/WebSecurityConfig.java` (modified - auditorProvider)
- New: `src/main/java/com/geodevai/config/AuditingConfig.java` (custom listener)

## Dependencies
- None (foundational task)

## Constraints
- Must follow EntityStandards.md (use `@Getter`/`@Setter`/`@NoArgsConstructor`, no `@Data`)
- Minimal code changes preferred