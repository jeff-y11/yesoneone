# TASK-012: Custom Security Principal with Channel Tracking

## Goal
Create a custom `UserDetails`/`Principal` implementation that carries the authentication channel (source), so both JWT and MCP Key authentication can populate `updatedSource` correctly.

## Requirements

### 1. Define Channel Constants
**Location:** `src/main/java/com/geodevai/security/AuthChannel.java` (new enum/class)

```java
public enum AuthChannel {
    WEB("Web"),
    MCP("MCP"),
    INTEGRATION("Integration"); // Will be appended with integrationId at runtime
    
    private final String label;
    AuthChannel(String label) { this.label = label; }
    public String getLabel() { return label; }
}
```

### 2. Custom Principal Implementation
**Location:** `src/main/java/com/geodevai/security/GeodeUserPrincipal.java` (new class)

Implements `UserDetails` (or extends `org.springframework.security.core.userdetails.User`) with:
- `UUID userId` (the actual user ID)
- `AuthChannel channel` — the authentication channel
- `UUID integrationId` — only populated for INTEGRATION channel
- Standard `UserDetails` methods (username = userId.toString(), authorities, etc.)

### 3. Update TokenAuthenticationFilter
**Location:** `src/main/java/com/geodevai/security/TokenAuthenticationFilter.java`

Modify both authentication paths:
- **JWT path (`authenticateWithJwt`)**: Create `GeodeUserPrincipal` with `channel = AuthChannel.WEB`
- **MCP Key path (`authenticateWithKey`)**: Create `GeodeUserPrincipal` with `channel = AuthChannel.MCP`

Set this principal in the `UsernamePasswordAuthenticationToken`.

### 4. Update AuditorAware / Auditing Listener
**Location:** `src/main/java/com/geodevai/config/WebSecurityConfig.java` (or new `AuditingConfig.java`)

Modify the auditing mechanism to:
1. Get `Authentication` from `SecurityContextHolder`
2. Cast principal to `GeodeUserPrincipal`
3. Extract channel and integrationId
4. Format `updatedSource`:
   - `WEB` → "Web"
   - `MCP` → "MCP"
   - `INTEGRATION` → "Integration " + integrationId
5. Set on entity's `auditFields.setUpdatedSource(...)`

**Implementation approach:** Create a custom `AuditingEntityListener` bean that extends Spring's `AuditingEntityListener` and overrides `touchForCreate`/`touchForUpdate` to inject `updatedSource` before delegating to super.

## Acceptance Criteria
- `GeodeUserPrincipal` carries channel + integrationId
- JWT auth sets channel = "Web"
- MCP Key auth sets channel = "MCP"
- Custom auditing listener reads principal and populates `updatedSource`
- Integration code (future task) can create authentication with `INTEGRATION` channel + integrationId

## Files Likely Involved
- `src/main/java/com/geodevai/security/AuthChannel.java` (new)
- `src/main/java/com/geodevai/security/GeodeUserPrincipal.java` (new)
- `src/main/java/com/geodevai/security/TokenAuthenticationFilter.java` (modified)
- `src/main/java/com/geodevai/config/AuditingConfig.java` (new - custom listener)
- `src/main/java/com/geodevai/config/WebSecurityConfig.java` (modified - register listener)

## Dependencies
- TASK-010 (AuditFields.updatedSource exists)

## Constraints
- Minimal code changes
- Both auth paths must work
- Future integration auth should be able to reuse this pattern