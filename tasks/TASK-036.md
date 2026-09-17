# TASK-036: Add REST Search Endpoints for Person, Property, Unit, and WorkOrder

## Goal
Create REST controller endpoints for searching and viewing Person, Property, Unit, and WorkOrder entities. All results are scoped to the authenticated user's organization.

## Requirements

### 1. Create PropertyController
**File:** `src/main/java/com/geodevai/controller/PropertyController.java` (new)

```java
@RestController
@RequestMapping("/services/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getProperties(
            @RequestParam(required = false, defaultValue = "") String search) {
        // Get authenticated user, resolve organization
        // If search is blank: return all properties for org
        // If search is provided: return filtered properties
        // Return list with count
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<Map<String, Object>> getProperty(@PathVariable UUID propertyId) {
        // Get authenticated user, resolve organization
        // Verify property belongs to user's organization
        // Return property details with nested data (address, units)
    }
}
```

### 2. Create UnitController
**File:** `src/main/java/com/geodevai/controller/UnitController.java` (new)

```java
@RestController
@RequestMapping("/services/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitRepository unitRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUnits(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false) UUID propertyId) {
        // If propertyId provided: filter by property (still within org)
        // If search provided: filter by query
        // Else: return all units for org
    }

    @GetMapping("/{unitId}")
    public ResponseEntity<Map<String, Object>> getUnit(@PathVariable UUID unitId) {
        // Get authenticated user, resolve organization
        // Verify unit belongs to user's organization (via property)
        // Return unit details
    }
}
```

### 3. Create PersonController
**File:** `src/main/java/com/geodevai/controller/PersonController.java` (new)

```java
@RestController
@RequestMapping("/services/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonRepository personRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPersons(
            @RequestParam(required = false, defaultValue = "") String search) {
        // Scope by user's organization (via integration)
        // If search: filter by name/email
    }

    @GetMapping("/{personId}")
    public ResponseEntity<Map<String, Object>> getPerson(@PathVariable UUID personId) {
        // Verify person belongs to user's organization
        // Return person details with unit info
    }
}
```

### 4. Create WorkOrderController
**File:** `src/main/java/com/geodevai/controller/WorkOrderController.java` (new)

```java
@RestController
@RequestMapping("/services/work-orders")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderRepository workOrderRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getWorkOrders(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID propertyId) {
        // Scope by user's organization
        // If status: filter by status
        // If propertyId: filter by property
        // If search: filter by title/external ID
    }

    @GetMapping("/{workOrderId}")
    public ResponseEntity<Map<String, Object>> getWorkOrder(@PathVariable UUID workOrderId) {
        // Verify work order belongs to user's organization
        // Return work order details with property, unit, tenant info
    }
}
```

### 5. Common Authentication Pattern

Each controller follows the same pattern as `IntegrationController`:

```java
private UUID resolveUserOrganizationId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userId = auth.getName();
    User user = userRepository.findById(UUID.fromString(userId))
            .orElseThrow();
    if (user.getOrganization() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has no organization");
    }
    return user.getOrganization().getOrganizationId();
}
```

### 6. Response Format

All list endpoints return:
```json
{
  "items": [...],
  "count": 5,
  "query": "search term"
}
```

All detail endpoints return the entity with nested relationships (address, property, unit, etc.).

## Scope
This is read-only — search and view only. No create, update, or delete endpoints. The integration is unidirectional from PM systems.

## Acceptance Criteria
- `GET /services/properties` returns all properties for user's organization
- `GET /services/properties?search=foo` filters properties by name or external ID
- `GET /services/properties/{id}` returns single property with address and units
- `GET /services/units` returns all units for user's organization
- `GET /services/units?propertyId=...` filters units by property
- `GET /services/persons` returns all persons for user's organization
- `GET /services/work-orders` returns all work orders for user's organization
- `GET /services/work-orders?status=OPEN` filters by status
- All endpoints return 403 if user has no organization or entity doesn't belong to org
- Search is case-insensitive and matches partial strings
- No POST, PUT, PATCH, or DELETE endpoints
- All files compile without errors

## Files Likely Involved
- `src/main/java/com/geodevai/controller/PropertyController.java` (new)
- `src/main/java/com/geodevai/controller/UnitController.java` (new)
- `src/main/java/com/geodevai/controller/PersonController.java` (new)
- `src/main/java/com/geodevai/controller/WorkOrderController.java` (new)

## Dependencies
- TASK-035 (repository methods)
- TASK-029 (controller patterns from `IntegrationController`)

## Constraints
- Must follow existing controller patterns from `IntegrationController`
- Use `@RequiredArgsConstructor` for dependency injection
- Use `SecurityContextHolder.getContext().getAuthentication()` for auth
- Validate organization ownership on every request
- Do NOT expose raw Spring Data REST endpoints for these entities
- Use `@RestController` (not `@RepositoryRestResource` for these)
