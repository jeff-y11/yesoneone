# TASK-011: Create Organization and Integration Entities

## Goal
Create `Organization` and `Integration` JPA entities with proper bidirectional relationships, following EntityStandards.md.

## Requirements

### 1. Organization Entity
**Location:** `src/main/java/com/geodevai/data/model/Organization.java`

Fields:
- `organizationId` (UUID, `@Id`, `@GeneratedValue(strategy = GenerationType.UUID)`)
- `name` (String, `@Column(nullable = false, unique = true)`)
- `users` — `@OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)` → `Set<User>`
- `integrations` — `@OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)` → `Set<Integration>`

Extends `AuditableEntity`, uses `@Getter`, `@Setter`, `@NoArgsConstructor`.

### 2. Integration Entity
**Location:** `src/main/java/com/geodevai/data/model/Integration.java`

Fields:
- `integrationId` (UUID, `@Id`, `@GeneratedValue(strategy = GenerationType.UUID)`)
- `name` (String, `@Column(nullable = false)`)
- `endpoint` (String, `@Column(nullable = false)` — base URL for the external API)
- `type` (String, `@Column(nullable = false)` — e.g., "WEB_QUERY", "WEB_HEADER")
- `parameters` — `@ElementCollection` + `@CollectionTable(name = "integration_parameter")` + `@MapKeyColumn(name = "param_name")` + `@Column(name = "param_value")` → `Map<String, String>` for query/header params
- `organization` — `@ManyToOne(fetch = FetchType.LAZY)` `@JoinColumn(name = "organization_id", nullable = false)` → `Organization`

Extends `AuditableEntity`, uses `@Getter`, `@Setter`, `@NoArgsConstructor`.

### 3. Update User Entity
**Location:** `src/main/java/com/geodevai/data/model/User.java`

Add:
- `organization` — `@ManyToOne(fetch = FetchType.LAZY)` `@JoinColumn(name = "organization_id")` → `Organization`
- Update `UserRepository` if needed for org-based queries

### 4. Repository Interfaces
**Locations:**
- `src/main/java/com/geodevai/data/repository/OrganizationRepository.java` — extends `JpaRepository<Organization, UUID>`
- `src/main/java/com/geodevai/data/repository/IntegrationRepository.java` — extends `JpaRepository<Integration, UUID>` with helper methods:
  - `Optional<Integration> findByIntegrationRemoteIdAndOrganization(String remoteId, Organization org)`
  - `List<Integration> findByOrganization(Organization org)`

### 5. Database Migration (if using Flyway/Liquibase)
Create migration script for:
- `organization` table
- `integration` table
- `integration_parameter` table (for `@ElementCollection` map)
- Add `organization_id` column to `app_user` table

## Acceptance Criteria
- Entities compile and follow EntityStandards.md
- Bidirectional relationships work (Organization ↔ Integration, Organization ↔ User)
- Parameters stored as map in separate table
- Repositories provide basic CRUD + org-scoped queries

## Files Likely Involved
- `src/main/java/com/geodevai/data/model/Organization.java` (new)
- `src/main/java/com/geodevai/data/model/Integration.java` (new)
- `src/main/java/com/geodevai/data/model/User.java` (modified)
- `src/main/java/com/geodevai/data/repository/OrganizationRepository.java` (new)
- `src/main/java/com/geodevai/data/repository/IntegrationRepository.java` (new)
- Database migration script (if applicable)

## Dependencies
- TASK-010 (Externalable interface, AuditFields.updatedSource)

## Constraints
- Follow EntityStandards.md strictly (ID naming, no `@Data`, lazy fetching, Set for collections)
- Use UUID surrogate keys
- `@ElementCollection` for parameters map