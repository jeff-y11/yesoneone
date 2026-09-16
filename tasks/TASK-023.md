# TASK-023: Create Integration Documentation

## Goal
Create `docs/pm-integration-mapping.md` that maps Buildium and Skywalk (AppFolio) API entity fields to our internal domain entities, identifies fields that should NOT be represented internally, and identifies internal fields with no external equivalent.

## Requirements

Create `docs/pm-integration-mapping.md` with the following sections:

### 1. Overview
Brief explanation of the purpose: establishing a provider-neutral internal domain model for future PM integration (Buildium, AppFolio/Skywalk).

### 2. Entity Field Mappings

#### Address
| Internal Field | Buildium Field | Skywalk Field | Notes |
|---|---|---|---|
| `addressId` | N/A | N/A | Internal surrogate key |
| `addressLine1` | `AddressMessage.AddressLine1` | N/A | Direct mapping |
| `addressLine2` | `AddressMessage.AddressLine2` | N/A | Direct mapping |
| `addressLine3` | `AddressMessage.AddressLine3` | N/A | Direct mapping |
| `city` | `AddressMessage.City` | N/A | Direct mapping |
| `state` | `AddressMessage.State` | N/A | Direct mapping |
| `postalCode` | `AddressMessage.PostalCode` | N/A | Direct mapping |
| `country` | `AddressMessage.Country` | N/A | Direct mapping |

#### Property
| Internal Field | Buildium Field | Skywalk Field | Notes |
|---|---|---|---|
| `propertyId` | N/A | N/A | Internal surrogate key |
| `name` | `PropertyMessage` → `Name` (from `RentalPropertyPostMessage.Name`) | `/v1/properties` response | Property name |
| `externalPropertyId` | `PropertyMessage.Id` (integer) | `/v1/properties` `Id` | Stored as String for provider-neutrality |
| `propertyType` | `RentalPropertyPostMessage.RentalSubType` | N/A | Mapped from Buildium subtype |
| `numberOfUnits` | `RentalPropertyPostMessage.Units` (array size) | N/A | Count of associated units |
| `managementCompany` | N/A | N/A | Internal domain field — no direct external equivalent |
| `address` | `RentalPropertyPostMessage.Address` (embedded Address) | N/A | Embedded in Create/Update messages |
| `organization` | N/A | N/A | Internal domain field — maps to client/landlord |

#### Unit
| Internal Field | Buildium Field | Skywalk Field | Notes |
|---|---|---|---|
| `unitId` | N/A | N/A | Internal surrogate key |
| `unitNumber` | `RentalUnitMessage.UnitNumber` | `/v1/units` response | Direct mapping |
| `externalUnitId` | `RentalUnitMessage.Id` (integer) | `/v1/units` `Id` | Stored as String for provider-neutrality |
| `unitType` | N/A | N/A | Internal domain field |
| `squareFootage` | `RentalUnitMessage.UnitSize` | N/A | Direct mapping |
| `bedrooms` | `RentalUnitMessage.UnitBedrooms` | N/A | Direct mapping |
| `bathrooms` | `RentalUnitMessage.UnitBathrooms` | N/A | Direct mapping |
| `rentAmount` | `RentalUnitMessage.MarketRent` | N/A | Direct mapping |
| `isOccupied` | `RentalUnitMessage.IsUnitOccupied` | `/v1/unit-vacancies` | Direct mapping |

#### Person (Tenant)
| Internal Field | Buildium Field | Skywalk Field | Notes |
|---|---|---|---|
| `personId` | N/A | N/A | Internal surrogate key |
| `firstName` | `TenantMessage.FirstName` | `/v1/tenants` response | Direct mapping |
| `lastName` | `TenantMessage.LastName` | `/v1/tenants` response | Direct mapping |
| `phoneNumber` | `TenantMessage.PhoneNumbers` (array → first) | N/A | Buildium has multiple phone numbers; we store primary |
| `email` | `TenantMessage.Email` | N/A | Direct mapping |
| `externalTenantId` | `TenantMessage.Id` (integer) | `/v1/tenants` `Id` | Stored as String for provider-neutrality |
| `leaseStartDate` | `TenantMessage.Leases` → start date | N/A | Extracted from lease data |
| `leaseEndDate` | `TenantMessage.Leases` → end date | N/A | Extracted from lease data |
| `unit` | N/A | N/A | Internal domain field — links tenant to unit |

#### WorkOrder
| Internal Field | Buildium Field | Skywalk Field | Notes |
|---|---|---|---|
| `workOrderId` | N/A | N/A | Internal surrogate key |
| `externalWorkOrderId` | `WorkOrderMessage.Id` (integer) | `/v1/work-orders` `Id` | Stored as String for provider-neutrality |
| `title` | `WorkOrderMessage.Title` | `/v1/work-orders` response | Direct mapping |
| `summary` | `WorkOrderMessage.Title` (shortened) | N/A | Derived from title or description |
| `description` | `WorkOrderMessage.WorkDetails` | N/A | Direct mapping |
| `workDetails` | `WorkOrderMessage.WorkDetails` | N/A | Same as description but from PM system |
| `status` | `WorkOrderMessage.Status` | `/v1/work-orders` response | Direct mapping |
| `priority` | `WorkOrderMessage.Priority` | N/A | Direct mapping |
| `amount` | `WorkOrderMessage.Amount` | N/A | Direct mapping |
| `dueDate` | `WorkOrderMessage.DueDate` | N/A | Direct mapping |
| `completionDate` | N/A | N/A | Internal domain field — derived from status update |
| `entryNotes` | `WorkOrderMessage.EntryNotes` | N/A | Direct mapping |
| `vendorNotes` | `WorkOrderMessage.VendorNotes` | N/A | Direct mapping |
| `invoiceNumber` | `WorkOrderMessage.InvoiceNumber` | N/A | Direct mapping |
| `chargeableTo` | `WorkOrderMessage.ChargeableTo` | N/A | Direct mapping |
| `callSource` | N/A | N/A | Internal domain field — identifies Voice Agent as source |
| `callerName` | N/A | N/A | Internal domain field — from Voice Agent input, not PM system |
| `callerContactInfo` | N/A | N/A | Internal domain field — from Voice Agent input |
| `property` | `WorkOrderMessage` → property link | `/v1/work-orders` → property | Relationship |
| `unit` | `WorkOrderMessage` → unit link | `/v1/work-orders` → unit | Relationship |
| `tenant` | `WorkOrderMessage` → tenant link | `/v1/work-orders` → tenant | Optional — caller may not be listed tenant |

### 3. Fields That Externally Exist But Should NOT Be Represented Internally

The following fields exist in Buildium/Skywalk but should NOT be represented in our internal domain model:

| External Field | Source | Reason for Exclusion |
|---|---|---|
| `TaxId` | Buildium `TenantMessage.TaxId` | Compliance/tax-specific data not relevant to our domain |
| `SMSOptInStatus` | Buildium `TenantMessage.SMSOptInStatus` | Marketing/notification preference, not core domain data |
| `MailingPreference` | Buildium `TenantMessage.MailingPreference` | Communication preference, not core domain data |
| `DateOfBirth` | Buildium `TenantMessage.DateOfBirth` | Personal identifying information; GDPR/CCPA compliance risk |
| `AlternateEmail` | Buildium `TenantMessage.AlternateEmail` | Redundant; `email` field is sufficient |
| `AlternateAddress` | Buildium `TenantMessage.AlternateAddress` | Not needed for maintenance workflow |
| `EmergencyContact` | Buildium `TenantMessage.EmergencyContact` | Not relevant to maintenance/work order use case |
| `Comment` | Buildium `TenantMessage.Comment` | Not relevant to maintenance workflow |
| `OperatingBankAccountId` | Buildium `RentalPropertyPostMessage.OperatingBankAccountId` | Financial/accounting data, not domain entity data |
| `Reserve` | Buildium `RentalPropertyPostMessage.Reserve` | Accounting/financial data, not domain entity data |
| `GLAccountName` | Buildium `WorkOrderMessage` → line items | Accounting data, not domain entity data |
| `BillTransactionId` | Buildium `WorkOrderMessage.BillTransactionId` | Billing/accounting data, not domain entity data |
| `VendorId` (integer) | Buildium `WorkOrderMessage.VendorId` | Not needed in internal model — `vendorId` string field captures this |
| `LineItems` (array) | Buildium `WorkOrderMessage.LineItems` | Too detailed for domain model; handled in integration layer |
| `EntryContacts` (array) | Buildium `WorkOrderMessage.EntryContacts` | Too detailed for domain model |
| `Task` (embedded) | Buildium `WorkOrderMessage.Task` | Separate entity concept, not part of work order domain |
| `PhoneNumbers` (array) | Buildium `TenantMessage.PhoneNumbers` | We store primary phone number only |
| `Leases` (array) | Buildium `TenantMessage.Leases` | Lease details are managed separately; we store start/end dates |
| `Permissions` | Skywalk `users` response | Authorization data, not domain entity data |
| `Role` | Skywalk `users` response | Authorization data, not domain entity data |
| `MFAEnabled` | Skywalk `users` response | Security data, not domain entity data |
| `ProfilePhotoUrl` | Skywalk `users` response | Media data, not domain entity data |
| `AccountNumber` | Buildium `RentalPropertyPostMessage` | Accounting data, not domain entity data |
| `PropertyManagerId` | Buildium `RentalPropertyPostMessage` | Internal organizational data, not domain entity data |

### 4. Internal Fields with No Direct External Equivalent

| Internal Field | Entity | Reason |
|---|---|---|
| `propertyId` | Property | Internal surrogate UUID key |
| `unitId` | Unit | Internal surrogate UUID key |
| `personId` | Person | Internal surrogate UUID key |
| `workOrderId` | WorkOrder | Internal surrogate UUID key |
| `addressId` | Address | Internal surrogate UUID key |
| `managementCompany` | Property | Internal domain field — our organization manages properties |
| `unitType` | Unit | Internal domain classification not present in external APIs |
| `completionDate` | WorkOrder | Derived from status transitions |
| `callSource` | WorkOrder | Internal tracking for Voice Agent source |
| `callerName` | WorkOrder | Voice Agent input — not from PM system |
| `callerContactInfo` | WorkOrder | Voice Agent input — not from PM system |
| `assignedTo` | WorkOrder | Internal assignment tracking |
| `isOccupied` | Unit | Internal derived field — not directly from PM system |
| `leaseStartDate` | Person | Derived from lease data — not directly exposed in PM APIs |
| `leaseEndDate` | Person | Derived from lease data — not directly exposed in PM APIs |
| `numberOfUnits` | Property | Derived — count of associated units |

### 5. Provider-Neutrality Notes
- All external IDs stored as `String` to accommodate both Buildium (integer IDs) and Skywalk/AppFolio (string UUIDs)
- `Externalable` interface provides a consistent contract for integration regardless of provider
- No Buildium-specific or Skywalk-specific annotations or fields in domain entities
- Field names in internal entities follow `camelCase` Java conventions, not the mixed-case conventions used by Buildium (`AddressLine1`, `PostalCode`)
- Audit fields (`createdBy`, `created`, `modifiedBy`, `modified`, `updatedSource`, `createdByName`, `modifiedByName`) are handled by `AuditableEntity` and are not mapped to external APIs

## Acceptance Criteria
- Documentation file exists at `docs/pm-integration-mapping.md`
- All four entity mappings (Address, Property, Unit, Person, WorkOrder) are documented
- At least 10 external fields identified as NOT represented internally with reasons
- At least 10 internal fields identified with no direct external equivalent
- Provider-neutrality notes are clear
- Markdown renders correctly

## Files Likely Involved
- `docs/pm-integration-mapping.md` (new)

## Dependencies
- TASK-017 through TASK-021 (all entity tasks)

## Constraints
- Follow `EntityStandards.md` naming conventions in documentation
- Do not reference provider-specific fields as if they should be in the internal model
- Explain why external fields are excluded — do not just list them
