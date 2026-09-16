# PM Integration Domain Model Mapping

This document maps the external Buildium and Skywalk (AppFolio) API entity fields to our internal domain entities. It identifies fields that should NOT be represented internally and internal fields with no direct external equivalent.

## Overview

The internal domain model is **provider-neutral** — it does not mirror Buildium, AppFolio, or Skywalk schemas. Instead, it captures the domain concepts needed for our Voice Agent maintenance workflow. The `Externalable` interface provides a consistent contract for future integration regardless of provider.

External IDs are stored as `String` to accommodate both Buildium (integer IDs) and Skywalk/AppFolio (string UUIDs).

---

## Entity Field Mappings

### Address

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

### Property

| Internal Field | Buildium Field | Skywalk Field | Notes |
|---|---|---|---|
| `propertyId` | N/A | N/A | Internal surrogate key |
| `name` | `RentalPropertyPostMessage.Name` | `/v1/properties` response | Property name |
| `externalPropertyId` | `PropertyMessage.Id` (integer) | `/v1/properties` `Id` | Stored as String for provider-neutrality |
| `propertyType` | `RentalPropertyPostMessage.RentalSubType` | N/A | Mapped from Buildium subtype |
| `numberOfUnits` | `RentalPropertyPostMessage.Units` (array size) | N/A | Count of associated units |
| `managementCompany` | N/A | N/A | Internal domain field |
| `address` | `RentalPropertyPostMessage.Address` (embedded) | N/A | Embedded in Create/Update messages |
| `organization` | N/A | N/A | Internal domain field — maps to client/landlord |

### Unit

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

### Person (Tenant)

| Internal Field | Buildium Field | Skywalk Field | Notes |
|---|---|---|---|
| `personId` | N/A | N/A | Internal surrogate key |
| `firstName` | `TenantMessage.FirstName` | `/v1/tenants` response | Direct mapping |
| `lastName` | `TenantMessage.LastName` | `/v1/tenants` response | Direct mapping |
| `externalTenantId` | `TenantMessage.Id` (integer) | `/v1/tenants` `Id` | `@Column(nullable = false)`. Also serves as `Externalable.integrationRemoteId` |
| `phoneNumber` | `TenantMessage.PhoneNumbers` (array → first) | N/A | Primary phone only |
| `email` | `TenantMessage.Email` | N/A | Direct mapping |
| `leaseStartDate` | `TenantMessage.Leases` → start date | N/A | Derived from lease data |
| `leaseEndDate` | `TenantMessage.Leases` → end date | N/A | Derived from lease data |
| `unit` | N/A | N/A | `@ManyToOne LAZY` — optional, nullable |
| `user` | N/A | N/A | `@ManyToOne LAZY` — optional, present if registered user |
| `integration` | N/A | N/A | `@ManyToOne LAZY` — optional, present if pulled from PM integration. Gives access to `integration.getOrganization()` |

**Person-Organization resolution:**
- If `person.getUser()` is not null → `person.getUser().getOrganization()` gives the client/landlord
- If `person.getUser()` is null but `person.getIntegration()` is not null → `person.getIntegration().getOrganization()` gives the client/landlord
- Not all persons have a `User`; persons pulled from PM integrations may only have `Integration`
- `Organization` is a local concept — not a remote/PM-system org

### WorkOrder

| Internal Field | Buildium Field | Skywalk Field | Notes |
|---|---|---|---|
| `workOrderId` | N/A | N/A | Internal surrogate key |
| `externalWorkOrderId` | `WorkOrderMessage.Id` (integer) | `/v1/work-orders` `Id` | Stored as String for provider-neutrality |
| `title` | `WorkOrderMessage.Title` | `/v1/work-orders` response | Direct mapping |
| `summary` | Derived from `Title` | N/A | Short description |
| `description` | `WorkOrderMessage.WorkDetails` | N/A | Detailed description |
| `workDetails` | `WorkOrderMessage.WorkDetails` | N/A | Same as description from PM system |
| `status` | `WorkOrderMessage.Status` | `/v1/work-orders` response | Direct mapping |
| `priority` | `WorkOrderMessage.Priority` | N/A | Direct mapping |
| `amount` | `WorkOrderMessage.Amount` | N/A | Direct mapping |
| `dueDate` | `WorkOrderMessage.DueDate` | N/A | Direct mapping |
| `completionDate` | N/A | N/A | Derived from status transitions |
| `entryNotes` | `WorkOrderMessage.EntryNotes` | N/A | Direct mapping |
| `vendorNotes` | `WorkOrderMessage.VendorNotes` | N/A | Direct mapping |
| `invoiceNumber` | `WorkOrderMessage.InvoiceNumber` | N/A | Direct mapping |
| `chargeableTo` | `WorkOrderMessage.ChargeableTo` | N/A | Direct mapping |
| `callSource` | N/A | N/A | Internal domain field |
| `callerName` | N/A | N/A | Internal domain field |
| `callerContactInfo` | N/A | N/A | Internal domain field |
| `property` | WorkOrder → property link | `/v1/work-orders` → property | Relationship |
| `unit` | WorkOrder → unit link | `/v1/work-orders` → unit | Relationship |
| `tenant` | WorkOrder → tenant link | `/v1/work-orders` → tenant | Optional |

---

## Fields That Externally Exist But Should NOT Be Represented Internally

| External Field | Source | Reason for Exclusion |
|---|---|---|
| `TaxId` | Buildium `TenantMessage.TaxId` | Compliance/tax-specific; not relevant to maintenance domain |
| `SMSOptInStatus` | Buildium `TenantMessage.SMSOptInStatus` | Marketing preference; not core domain data |
| `MailingPreference` | Buildium `TenantMessage.MailingPreference` | Communication preference; not core domain data |
| `DateOfBirth` | Buildium `TenantMessage.DateOfBirth` | PII; GDPR/CCPA compliance risk |
| `AlternateEmail` | Buildium `TenantMessage.AlternateEmail` | Redundant; `email` field is sufficient |
| `AlternateAddress` | Buildium `TenantMessage.AlternateAddress` | Not needed for maintenance workflow |
| `EmergencyContact` | Buildium `TenantMessage.EmergencyContact` | Not relevant to maintenance/work order use case |
| `Comment` | Buildium `TenantMessage.Comment` | Not relevant to maintenance workflow |
| `OperatingBankAccountId` | Buildium `RentalPropertyPostMessage` | Financial/accounting data; not domain entity data |
| `Reserve` | Buildium `RentalPropertyPostMessage.Reserve` | Accounting data; not domain entity data |
| `GLAccountName` | Buildium `WorkOrderMessage` → line items | Accounting data; not domain entity data |
| `BillTransactionId` | Buildium `WorkOrderMessage.BillTransactionId` | Billing data; not domain entity data |
| `LineItems` (array) | Buildium `WorkOrderMessage.LineItems` | Too detailed for domain model; handled in integration layer |
| `EntryContacts` (array) | Buildium `WorkOrderMessage.EntryContacts` | Too detailed for domain model |
| `Task` (embedded) | Buildium `WorkOrderMessage.Task` | Separate entity concept; not part of work order domain |
| `PhoneNumbers` (array) | Buildium `TenantMessage.PhoneNumbers` | We store primary phone number only |
| `Leases` (array) | Buildium `TenantMessage.Leases` | Lease details managed separately; we store start/end dates |
| `Permissions` | Skywalk `users` response | Authorization data; not domain entity data |
| `Role` | Skywalk `users` response | Authorization data; not domain entity data |
| `MFAEnabled` | Skywalk `users` response | Security data; not domain entity data |
| `ProfilePhotoUrl` | Skywalk `users` response | Media data; not domain entity data |
| `PropertyManagerId` | Buildium `RentalPropertyPostMessage` | Internal organizational data; not domain entity data |
| `PropertyGroup` | Buildium `PropertyGroupMessage` | Organizational grouping; not needed in domain model |
| `StructureDescription` | Buildium `RentalPropertyPostMessage` | Property detail; not needed for maintenance workflow |
| `YearBuilt` | Buildium `RentalPropertyPostMessage.YearBuilt` | Property detail; not needed for maintenance workflow |
| `BuildingName` | Buildium `RentalUnitMessage.BuildingName` | Unit detail; not needed for maintenance workflow |
| `IsUnitListed` | Buildium `RentalUnitMessage.IsUnitListed` | Listing status; not relevant to maintenance workflow |
| `SMSOptInStatus` | Buildium `TenantMessage.SMSOptInStatus` | Marketing preference; not core domain data |

---

## Internal Fields with No Direct External Equivalent

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
| `propertyType` | Property | Internal classification not present in external APIs |
| `workOrderType` | WorkOrder | Internal classification not present in external APIs |
| `externalTenantId` | Person | `@Column(nullable = false)` — stored as String for provider-neutrality (Buildium uses int32, Skywalk uses string). Maps to `Externalable.integrationRemoteId`. |
| `integration` | Person | `@ManyToOne LAZY` — internal JPA relationship to `Integration`. Not a direct external field; it provides the chain to `Integration.getOrganization()`. |
| `user` | Person | `@ManyToOne LAZY` — internal relationship to `User`. Not all persons have a registered user account; some come from PM integrations. |

---

## Provider-Neutrality Notes

- All external IDs stored as `String` to accommodate both Buildium (integer IDs) and Skywalk/AppFolio (string UUIDs)
- `Externalable` interface provides a consistent contract for integration regardless of provider
- No Buildium-specific or Skywalk-specific annotations or fields in domain entities
- Field names follow `camelCase` Java conventions, not Buildium's mixed-case conventions (`AddressLine1`, `PostalCode`)
- Audit fields (`createdBy`, `created`, `modifiedBy`, `modified`, `updatedSource`, `createdByName`, `modifiedByName`) are handled by `AuditableEntity` and are not mapped to external APIs
- The internal model focuses on the maintenance workflow domain concepts (Property, Unit, Person/Tenant, WorkOrder) and does not replicate the full breadth of PM system entities
