# TASK-002: Code Quality, Bugs, and Technical Debt

## Goal
Resolve existing bugs, complete pending `TODO` items, and clean up technical debt/security issues across the backend codebase.

## Existing Architecture
- **JWT Filtering:** `TokenAuthenticationFilter` extracts authorization header and validates JWT.
- **Redundant Annotations:** `GeodeVAIApplication` has redundant annotations on top of `@SpringBootApplication`.
- **Database Entity Relationships:** `User` references `Person` via `@OneToOne` and `Login` references `User` via `@ManyToOne` without cascade rules.
- **Audit Logging:** `AuditFields` uses `String` fields for `createdBy` and `modifiedBy` instead of `UUID`.
- **Security Secrets:** Hardcoded JWT secret and Google OAuth2 credentials in `application.properties`.

## Requirements
1. **Fix NullPointerException in `TokenAuthenticationFilter`:**
   - In `TokenAuthenticationFilter.java` line 53, check if the `Authorization` header is null or empty *before* invoking `.trim()`. 
   - Ensure a request without an `Authorization` header is handled gracefully, returning `Optional.empty()` without generating an exception.

2. **Clean up Main Application Class Annotations:**
   - In `GeodeVAIApplication.java`, remove `@Configuration` and `@AutoConfiguration`. `@SpringBootApplication` already encapsulates `@SpringBootConfiguration` and `@EnableAutoConfiguration`.

3. **Externalize Secrets in Configuration:**
   - Modify `application.properties` to load sensitive properties from environment variables with safe fallbacks for local development
   
4. **Address Entity Cascade & Foreign Key Rules:**
   - Specify cascading behavior (e.g. `CascadeType.ALL` or `CascadeType.REMOVE` where appropriate) on JPA relationships such as:
     - `@OneToOne` from `User` to `Person`.
     - `@ManyToOne` from `Login` to `User`.
   - Prevent database inconsistency or `ConstraintViolationException` during user/person deletion.

5. **Address Codebase `TODO` items:**
   - **AuthService.java (Line 30):** Implement verification that the user linking a Google account has the matching identity/privileges to prevent arbitrary mapping.
   - **AuditFields.java (Line 24) / WebSecurityConfig.java:** Refactor audit fields `createdBy` and `modifiedBy` to use `UUID` instead of raw String usernames, and update the Spring Security context's `AuditorAware` bean to return the logged-in user's `UUID` (retrieved from JWT payload).
   - **WebSecurityConfig.java (Line 115):** Provide fallback auditing authentication for non-web operations (e.g., background queues or migrations) to avoid falling back to `"Anonymous"` blindly.

## Acceptance Criteria
- No `NullPointerException` thrown when hitting endpoints with a missing `Authorization` header.
- Main class has only `@SpringBootApplication`.
- Sensitive fields in `application.properties` use env placeholders with default values.
- User/Person database records can be safely deleted or managed without relationship constraint violations.
- Code compiles, and all tests pass with no regressions.

## Files Likely Involved
- `D:\workspace\geodevai\src\main\java\com\geodevai\security\TokenAuthenticationFilter.java`
- `D:\workspace\geodevai\src\main\java\com\geodevai\GeodeVAIApplication.java`
- `D:\workspace\geodevai\src\main\resources\application.properties`
- `D:\workspace\geodevai\src\main\java\com\geodevai\data\model\User.java`
- `D:\workspace\geodevai\src\main\java\com\geodevai\data\model\Login.java`
- `D:\workspace\geodevai\src\main\java\com\geodevai\data\model\AuditFields.java`
- `D:\workspace\geodevai\src\main\java\com\geodevai\service\AuthService.java`
- `D:\workspace\geodevai\src\main\java\com\geodevai\config\WebSecurityConfig.java`

## Constraints & Dependencies
- Dependent on: `TASK-001` (Upgrades should preferably be done first).
