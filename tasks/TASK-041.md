# TASK-041: Buildium implementation

## Goal

Implement the BuildiumIntegrationRunner with initial synchronization for Property, Unit, Tenant, and WorkOrder using the Buildium OpenAPI specification. The implementation should use the generic integration scaffolding and define field mappings from Buildium objects into domain entities.

## Requirements

### BuildiumIntegrationRunner Implementation

Implement the `run(Integration integration)` method in `BuildiumIntegrationRunner` to:

- Synchronize the four entity types: Property, Unit, Tenant, WorkOrder
- Use the Buildium OpenAPI specification as the source of API endpoints
- Use the API key credential for authentication
- Determine which entities to synchronize based on the integration's selected capabilities

### Field Mappings from Buildium to Domain Entities

Define the field mappings based on `docs/buildium-integration-mapping.md`:

**Property Mapping:**
- `name` → `RentalPropertyPostMessage.Name`
- `externalPropertyId` → `PropertyMessage.Id` (integer → String)
- `propertyType` → `RentalPropertyPostMessage.RentalSubType`
- `numberOfUnits` → count of units (array size)
- `address` → `RentalPropertyPostMessage.Address` (embedded)

**Unit Mapping:**
- `unitNumber` → `RentalUnitMessage.UnitNumber`
- `externalUnitId` → `RentalUnitMessage.Id` (integer → String)
- `squareFootage` → `RentalUnitMessage.UnitSize`
- `bedrooms` → `RentalUnitMessage.UnitBedrooms`
- `bathrooms` → `RentalUnitMessage.UnitBathrooms`
- `rentAmount` → `RentalUnitMessage.MarketRent`
- `isOccupied` → `RentalUnitMessage.IsUnitOccupied`

**Person (Tenant) Mapping:**
- `firstName` → `TenantMessage.FirstName`
- `lastName` → `TenantMessage.LastName`
- `externalTenantId` → `TenantMessage.Id` (integer → String)
- `phoneNumber` → primary phone from `TenantMessage.PhoneNumbers` (array → first)
- `email` → `TenantMessage.Email`
- `leaseStartDate` → derived from lease data
- `leaseEndDate` → derived from lease data

**WorkOrder Mapping:**
- `title` → `WorkOrderMessage.Title`
- `summary` → derived from `Title`
- `description` → `WorkOrderMessage.WorkDetails`
- `status` → `WorkOrderMessage.Status`
- `priority` → `WorkOrderMessage.Priority`
- `amount` → `WorkOrderMessage.Amount`
- `dueDate` → `WorkOrderMessage.DueDate`
- `entryNotes` → `WorkOrderMessage.EntryNotes`
- `vendorNotes` → `WorkOrderMessage.VendorNotes`
- `invoiceNumber` → `WorkOrderMessage.InvoiceNumber`
- `chargeableTo` → `WorkOrderMessage.ChargeableTo`

### Idempotency

- Synchronization must remain idempotent: running the same remote state repeatedly should not create duplicate local entities
- Use external IDs (`externalPropertyId`, `externalUnitId`, `externalTenantId`, `externalWorkOrderId`) to detect existing entities
- Update existing entities rather than creating duplicates when remote IDs match

### Bulk/List Endpoint Preference

- Prefer bulk/list endpoints over fetching entities individually
- Use pagination appropriately
- If the API supports filtering by modification/update date, use it to minimize API calls
- Do not perform unnecessary detail requests when the list endpoint already provides the fields required by the mapping

## Files Likely Involved

- `src/main/java/com/geodevai/integration/BuildiumIntegrationRunner.java` - Main implementation with sync logic
- `docs/buildium-integration-mapping.md` - Reference for field mappings
- Integration repository methods for save/update operations

## Acceptance Criteria

- `BuildiumIntegrationRunner.run(Integration)` implements sync for Property, Unit, Tenant, WorkOrder
- Field mappings from Buildium OpenAPI spec are correctly applied
- Only explicitly mapped fields are modified during synchronization
- Fields not mapped by the integration remain unchanged on local entities
- Synchronization is idempotent - running multiple times does not create duplicates
- Uses bulk/list endpoints preferred over individual detail requests
- API key authentication used for Buildium API calls