# TASK-020: Enhance Person Entity for PM Integration

## Goal
Add PM-integration-relevant fields to the existing `Person` entity and add a relationship to `Unit`. Per feature spec, the existing `Person` entity represents the Tenant — do NOT create a separate Tenant entity.

## Requirements

Modify `src/main/java/com/geodevai/data/model/Person.java`:

### New Fields
- `externalTenantId` (String, `@Column(nullable = false)`) — the PM system's assigned tenant ID. Also serves as `Externalable.integrationRemoteId`.
- `phoneNumber` (String) — primary phone number for the tenant (from Buildium `PhoneNumbers` array / Skywalk tenant data)
- `email` (String) — email address (from Buildium `Email` / Skywalk tenant data)
- `leaseStartDate` (java.time.LocalDateTime) — lease start date
- `leaseEndDate` (java.time.LocalDateTime) — lease end date
- `unit` (`@ManyToOne(fetch = FetchType.LAZY)`) → `Unit` — the unit the tenant currently occupies
- `user` (`@ManyToOne(fetch = FetchType.LAZY)`) → `User` — optional; present if the tenant is a registered user in our system
- `integration` (`@ManyToOne(fetch = FetchType.LAZY)`) → `Integration` — optional; present if the tenant was pulled from a PM system integration. This gives access to `integration.getOrganization()` for the client/landlord org.
- `integrationId` (`@Transient`) — delegates to `integration.getId()` per `Externalable` contract
- `lastSyncTime` (LocalDateTime) — per `Externalable` contract

### Updated Person Entity
```java
package com.geodevai.data.model;

import com.geodevai.data.model.Externalable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Person extends AuditableEntity implements Externalable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID personId;
    private String firstName;
    private String lastName;

    @Column(nullable = false)
    private String externalTenantId;
    private String phoneNumber;
    private String email;
    private LocalDateTime leaseStartDate;
    private LocalDateTime leaseEndDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "integration_id")
    private Integration integration;

    @Transient
    private UUID integrationId;
    private LocalDateTime lastSyncTime;

    @Override
    public UUID getIntegrationId() { return integration != null ? integration.getId() : null; }
    @Override
    public void setIntegrationId(UUID integrationId) { this.integrationId = integrationId; }
    @Override
    public String getIntegrationRemoteId() { return externalTenantId; }
    @Override
    public void setIntegrationRemoteId(String integrationRemoteId) { this.externalTenantId = integrationRemoteId; }
    @Override
    public LocalDateTime getLastSyncTime() { return lastSyncTime; }
    @Override
    public void setLastSyncTime(LocalDateTime lastSyncTime) { this.lastSyncTime = lastSyncTime; }
}
```

**Key design decisions:**
- `externalTenantId` is `@Column(nullable = false)` and serves as the `Externalable.integrationRemoteId` field (via getter/setter delegation)
- `integrationId` is `@Transient` — it delegates to `integration.getId()` to avoid a separate column
- `integration` is the JPA `@ManyToOne` relationship — this is what provides access to `integration.getOrganization()`
- `integrationRemoteId` and `externalTenantId` refer to the same underlying value

### Repository
Update `src/main/java/com/geodevai/data/repository/PersonRepository.java`:
- Add `Optional<Person> findByPhoneNumber(String phoneNumber)`
- Add `Optional<Person> findByEmail(String email)`
- Add `Optional<Person> findByExternalTenantId(String externalTenantId)`
- Add `List<Person> findByUnit(Unit unit)`
- Add `List<Person> findByIntegration(Integration integration)`
- Add `List<Person> findByUser(User user)`

### Database Migration
Since `spring.jpa.hibernate.ddl-auto=update`, new columns will be auto-created. No migration script needed for dev.

## Acceptance Criteria
- `Person` entity compiles without errors
- `Person` implements `Externalable` interface correctly
- `integrationId` is `@Transient` and delegates to `integration.getId()`
- `integrationRemoteId` delegates to `externalTenantId` field
- `lastSyncTime` field exists with getter/setter
- `externalTenantId` is `@Column(nullable = false)` — required for integration
- New fields `phoneNumber`, `email`, `leaseStartDate`, `leaseEndDate` added
- `unit` relationship exists with `FetchType.LAZY`
- `user` relationship exists with `FetchType.LAZY` — nullable (not all persons are registered users)
- `integration` relationship exists with `FetchType.LAZY` — nullable (not all persons come from integrations)
- `@JoinColumn(name = "integration_id")` on `Integration integration` — no separate `integration_id` column from `Externalable`
- Repository has `findByPhoneNumber`, `findByEmail`, `findByExternalTenantId`, `findByUnit`, `findByIntegration`, `findByUser` methods
- Existing `firstName`, `lastName` fields remain unchanged
- Entity follows all `EntityStandards.md` rules
- Person-Organization resolution documented: `person.getUser().getOrganization()` or `person.getIntegration().getOrganization()`

## Files Likely Involved
- `src/main/java/com/geodevai/data/model/Person.java` (modify)
- `src/main/java/com/geodevai/data/repository/PersonRepository.java` (modify)
- Database migration script (if applicable)

## Dependencies
- TASK-019 (Unit entity)

## Constraints
- Must follow `EntityStandards.md` strictly
- `Person` **must implement `Externalable`** — tenants are represented by Person, not a separate Tenant entity
- `integrationId`, `integrationRemoteId`, `lastSyncTime` must be declared as entity fields (not inherited from a separate class)
- Do NOT create a separate `Tenant` entity — use existing `Person`
- `externalTenantId` must be `@Column(nullable = false)` since it's required for integration — this also serves as `Externalable.integrationRemoteId`
- `integrationId` is `@Transient` — it delegates to `integration.getId()` rather than having its own column
- `integrationRemoteId` delegates to `externalTenantId` — same underlying value
- `integration` column is `integration_id` in DB — only one column, no conflict with `Externalable.integrationId`
- `unit` relationship is nullable (tenant may not always have a unit)
- `phoneNumber` and `email` are nullable (not all tenants may have these)
- `externalTenantId` stored as String for provider-neutrality (Buildium uses int32, Skywalk uses string)
- `user` is nullable — not all persons are registered users in our system
- `integration` is nullable — not all persons come from PM system integrations
- Organization is resolved via `person.getUser().getOrganization()` or `person.getIntegration().getOrganization()` — NOT a direct foreign key on Person
