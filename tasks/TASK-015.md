# TASK-015: Audit and Fix Existing Entities per EntityStandards.md

## Goal
Review all existing JPA entities (`User`, `Person`, `Login`, `McpKey`) and the new `Organization`/`Integration` entities against `tasks/EntityStandards.md`, fixing any violations.

## Requirements

### 1. Checklist per Entity

For each entity (`User`, `Person`, `Login`, `McpKey`, `Organization`, `Integration`):

| Standard | Check |
|----------|-------|
| ID named `<EntityName>Id` (not `id`) | ✅/❌ |
| Extends `AuditableEntity` (or has comment explaining why not) | ✅/❌ |
| Uses `@Getter` `@Setter` `@NoArgsConstructor` (NOT `@Data`) | ✅/❌ |
| Uses surrogate UUID key (not natural/composite) | ✅/❌ |
| To-one relationships use `FetchType.LAZY` explicitly | ✅/❌ |
| Collections use `Set<>` not `List<>` | ✅/❌ |
| `@Table` name is snake_case | ✅/❌ |
| Cascade types are specific (not `ALL` unless justified) | ✅/❌ |

### 2. Known Issues to Fix

**User.java:**
- ✅ `userId` correct
- ✅ Extends `AuditableEntity`
- ✅ `@Getter` `@Setter` `@NoArgsConstructor`
- ❌ `person` relationship: `@OneToOne` defaults to `EAGER` → add `fetch = FetchType.LAZY`
- ❌ `person` cascade: `CascadeType.ALL` → consider if `PERSIST, MERGE` sufficient

**Person.java:**
- ✅ `personId` correct
- ✅ Extends `AuditableEntity`
- ✅ Lombok annotations correct
- ❌ No relationships to check

**Login.java:**
- ✅ `loginId` correct
- ✅ Extends `AuditableEntity`
- ✅ Lombok annotations correct
- ❌ `user` relationship: `@ManyToOne` defaults to `EAGER` → add `fetch = FetchType.LAZY`
- ❌ Cascade: `{PERSIST, MERGE}` is good, but check if `REMOVE` needed

**McpKey.java:**
- ✅ `mcpKeyId` correct
- ✅ Extends `AuditableEntity`
- ✅ Lombok annotations correct
- ❌ `user` relationship: `@ManyToOne(fetch = FetchType.LAZY)` ✅ already correct!

**Organization.java (from TASK-011):**
- Verify all standards during creation

**Integration.java (from TASK-011):**
- Verify all standards during creation
- `parameters` map: `@ElementCollection` defaults to eager? → Add `@ElementCollection(fetch = FetchType.LAZY)`

### 3. Fix Application
Apply fixes directly to entity files. Run tests to verify no regressions.

## Acceptance Criteria
- All 6 entities pass the checklist above
- No `@Data` annotations on any entity
- All to-one relationships explicitly `LAZY`
- All collections are `Set<>`
- Tests pass

## Files Likely Involved
- `src/main/java/com/geodevai/data/model/User.java` (modify)
- `src/main/java/com/geodevai/data/model/Person.java` (verify)
- `src/main/java/com/geodevai/data/model/Login.java` (modify)
- `src/main/java/com/geodevai/data/model/McpKey.java` (verify)
- `src/main/java/com/geodevai/data/model/Organization.java` (verify during TASK-011)
- `src/main/java/com/geodevai/data/model/Integration.java` (verify during TASK-011)

## Dependencies
- TASK-011 (entities created)
- EntityStandards.md exists

## Constraints
- Do not change public API of entities (field names, types)
- Only fix annotations, fetch types, collection types, Lombok
- Run full test suite after changes