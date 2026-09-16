# TASK-016: Add createdByName/modifiedByName to AuditFields

## Goal
Add `createdByName` and `modifiedByName` fields to `AuditFields` so that audit history survives hard deletion of users (e.g., for GDPR/CCPA compliance). The UUID audit fields become meaningless if the user is deleted, but the display name provides a historical record.

## Requirements

### 1. Add fields to AuditFields
**Location:** `src/main/java/com/geodevai/data/model/AuditFields.java`

Add two fields:
```java
@Column(name = "created_by_name", length = 255, updatable = false)
private String createdByName;

@Column(name = "modified_by_name", length = 255)
private String modifiedByName;
```

`@CreatedBy` / `@LastModifiedBy` will continue to populate the UUID fields. The name fields will be populated by our custom listener.

### 2. Update CustomAuditingEntityListener
**Location:** `src/main/java/com/geodevai/config/CustomAuditingEntityListener.java`

Modify to read the `displayName` from the principal and set both name fields:
- For create: set `createdByName`
- For update: set `modifiedByName`

The display name can come from the `UserDetails` principal (if it carries it) or from a `User` entity lookup. Since we already have `AuthChannelHolder`, the simplest approach is to also store the display name in a thread-local (or add it to the existing holder).

**Option A:** Add a `displayName` thread-local to `AuthChannelHolder` alongside channel.
**Option B:** Read from a custom `UserDetails` implementation.

**Recommended:** Option A — extend `AuthChannelHolder` to also hold `displayName`, set it in `TokenAuthenticationFilter`.

### 3. Update TokenAuthenticationFilter
**Location:** `src/main/java/com/geodevai/security/TokenAuthenticationFilter.java`

Set the display name in `AuthChannelHolder` when authenticating:
- **JWT path:** extract from JWT payload (`displayName` claim)
- **MCP Key path:** look up from the `User` entity (`user.getDisplayName()`)

## Acceptance Criteria
- `AuditFields` has `createdByName` and `modifiedByName` fields
- `CustomAuditingEntityListener` populates both fields on create/update
- `TokenAuthenticationFilter` sets display name in `AuthChannelHolder` for both auth paths
- Existing entities continue to work (UUID fields still populated by Spring Data auditing)

## Files Likely Involved
- `src/main/java/com/geodevai/data/model/AuditFields.java` (modify)
- `src/main/java/com/geodevai/config/CustomAuditingEntityListener.java` (modify)
- `src/main/java/com/geodevai/security/AuthChannelHolder.java` (modify)
- `src/main/java/com/geodevai/security/TokenAuthenticationFilter.java` (modify)

## Dependencies
- TASK-010 (AuditFields.updatedSource, CustomAuditingEntityListener, AuthChannelHolder)

## Constraints
- Do not remove or change existing UUID audit fields
- Name fields are supplemental
- Display name should be null-safe (use "System" or similar fallback for system operations)
