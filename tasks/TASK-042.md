# TASK-042: External IDs and relationships

## Goal

Persist Buildium remote IDs (as strings) to resolve relationships later. If a referenced entity is absent, preserve the remote ID and continue; support a later reconciliation pass.

## Requirements

### Remote ID Persistence Model

Add remote ID fields to the relevant domain objects to store Buildium IDs as strings:

- **Property**: `externalPropertyId` - stored as String, maps to `PropertyMessage.Id`
- **Unit**: `externalUnitId` - stored as String, maps to `RentalUnitMessage.Id`
- **Tenant/Person**: `externalTenantId` - stored as String, maps to `TenantMessage.Id`; also serves as `Externalable.integrationRemoteId`
- **WorkOrder**: `externalWorkOrderId` - stored as String, maps to `WorkOrderMessage.Id`

### Relationship Handling

- Design a clean persistence model that keeps remote IDs without polluting the core domain model:
  - Option: Separate relationship table, OR
  - Option: Extensions/add-on fields on existing entities
- If a referenced entity is absent during sync, preserve the remote ID and continue
- Support a later reconciliation pass to resolve missing relationships

### Integration with Existing Model

- Ensure `externalTenantId` on `Person` entity integrates with the `Externalable` interface
- Maintain consistency with the `integrationRemoteId` concept where applicable
- Keep the core domain model clean while providing access to remote IDs

## Files Likely Involved

- `src/main/java/com/geodevai/data/model/Property.java` - Add `externalPropertyId` field
- `src/main/java/com/geodevai/data/model/Unit.java` - Add `externalUnitId` field
- `src/main/java/com/geodevai/data/model/Person.java` - Add/verify `externalTenantId` field
- `src/main/java/com/geodevai/data/model/WorkOrder.java` - Add `externalWorkOrderId` field
- `src/main/java/com/geodevai/model/Externalable.java` - Verify remote ID contract
- `src/main/java/com/geodevai/data/repository/PropertyRepository.java` - If custom queries needed
- `src/main/java/com/geodevai/data/repository/UnitRepository.java` - If custom queries needed
- `src/main/java/com/geodevai/data/repository/PersonRepository.java` - If custom queries needed
- `src/main/java/com/geodevai/data/repository/WorkOrderRepository.java` - If custom queries needed

## Acceptance Criteria

- Remote IDs (`externalPropertyId`, `externalUnitId`, `externalTenantId`, `externalWorkOrderId`) are stored as String fields on their respective entities
- Design supports preserving remote IDs when referenced entities are absent
- Model enables a later reconciliation pass to resolve missing relationships
- Core domain model remains clean; remote IDs are added via extensions or dedicated fields