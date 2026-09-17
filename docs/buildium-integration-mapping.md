# Buildium Integration Mapping

This document maps Buildium API fields to our internal domain entities. Buildium uses integer IDs throughout, but we store all external IDs as `String` for provider-neutrality.

## Overview

Buildium is a property management platform. Its API uses a RESTful JSON structure with embedded objects and arrays. All entity IDs are `int32` internally but are stored as `String` in our domain model.

**Base URL**: `https://api.buildium.com/`

**Authentication**: Buildium Open API requires a Premium Subscription and API keys via `Authorization` header.

## Address Mapping

| Internal Field | Buildium Field | Notes |
|---|---|---|
| `addressId` | N/A | Internal surrogate UUID key |
| `addressLine1` | `AddressMessage.AddressLine1` | Direct mapping |
| `addressLine2` | `AddressMessage.AddressLine2` | Direct mapping |
| `addressLine3` | `AddressMessage.AddressLine3` | Direct mapping |
| `city` | `AddressMessage.City` | Direct mapping |
| `state` | `AddressMessage.State` | Direct mapping |
| `postalCode` | `AddressMessage.PostalCode` | Direct mapping |
| `country` | `AddressMessage.Country` | Direct mapping |

## Property Mapping

| Internal Field | Buildium Field | Notes |
|---|---|---|
| `propertyId` | N/A | Internal surrogate UUID key |
| `name` | `RentalPropertyPostMessage.Name` | Property name |
| `externalPropertyId` | `PropertyMessage.Id` (integer) | Stored as String for provider-neutrality |
| `propertyType` | `RentalPropertyPostMessage.RentalSubType` | Mapped from Buildium subtype |
| `numberOfUnits` | `RentalPropertyPostMessage.Units` (array size) | Count of associated units |
| `managementCompany` | N/A | Internal domain field |
| `address` | `RentalPropertyPostMessage.Address` (embedded) | Embedded in Create/Update messages |
| `organization` | N/A | Internal domain field — maps to client/landlord |

## Unit Mapping

| Internal Field | Buildium Field | Notes |
|---|---|---|
| `unitId` | N/A | Internal surrogate UUID key |
| `unitNumber` | `RentalUnitMessage.UnitNumber` | Direct mapping |
| `externalUnitId` | `RentalUnitMessage.Id` (integer) | Stored as String for provider-neutrality |
| `unitType` | N/A | Internal domain field |
| `squareFootage` | `RentalUnitMessage.UnitSize` | Direct mapping |
| `bedrooms` | `RentalUnitMessage.UnitBedrooms` | Direct mapping |
| `bathrooms` | `RentalUnitMessage.UnitBathrooms` | Direct mapping |
| `rentAmount` | `RentalUnitMessage.MarketRent` | Direct mapping |
| `isOccupied` | `RentalUnitMessage.IsUnitOccupied` | Direct mapping |

## Person (Tenant) Mapping

| Internal Field | Buildium Field | Notes |
|---|---|---|
| `personId` | N/A | Internal surrogate UUID key |
| `firstName` | `TenantMessage.FirstName` | Direct mapping |
| `lastName` | `TenantMessage.LastName` | Direct mapping |
| `externalTenantId` | `TenantMessage.Id` (integer) | `@Column(nullable = false)`. Also serves as `Externalable.integrationRemoteId` |
| `phoneNumber` | `TenantMessage.PhoneNumbers` (array → first) | Primary phone only |
| `email` | `TenantMessage.Email` | Direct mapping |
| `leaseStartDate` | `TenantMessage.Leases` → start date | Derived from lease data |
| `leaseEndDate` | `TenantMessage.Leases` → end date | Derived from lease data |
| `unit` | N/A | `@ManyToOne LAZY` — optional, nullable |
| `user` | N/A | `@ManyToOne LAZY` — optional, present if registered user |
| `integration` | N/A | `@ManyToOne LAZY` — optional, present if pulled from PM integration |

**Person-Organization resolution:**
- If `person.getUser()` is not null → `person.getUser().getOrganization()` gives the client/landlord
- If `person.getUser()` is null but `person.getIntegration()` is not null → `person.getIntegration().getOrganization()` gives the client/landlord

## WorkOrder Mapping

| Internal Field | Buildium Field | Notes |
|---|---|---|
| `workOrderId` | N/A | Internal surrogate UUID key |
| `externalWorkOrderId` | `WorkOrderMessage.Id` (integer) | Stored as String for provider-neutrality |
| `title` | `WorkOrderMessage.Title` | Direct mapping |
| `summary` | Derived from `Title` | Short description |
| `description` | `WorkOrderMessage.WorkDetails` | Detailed description |
| `status` | `WorkOrderMessage.Status` | Direct mapping |
| `priority` | `WorkOrderMessage.Priority` | Direct mapping |
| `amount` | `WorkOrderMessage.Amount` | Direct mapping |
| `dueDate` | `WorkOrderMessage.DueDate` | Direct mapping |
| `completionDate` | N/A | Derived from status transitions |
| `entryNotes` | `WorkOrderMessage.EntryNotes` | Direct mapping |
| `vendorNotes` | `WorkOrderMessage.VendorNotes` | Direct mapping |
| `invoiceNumber` | `WorkOrderMessage.InvoiceNumber` | Direct mapping |
| `chargeableTo` | `WorkOrderMessage.ChargeableTo` | Direct mapping |
| `property` | WorkOrder → property link | Relationship |
| `unit` | WorkOrder → unit link | Relationship |
| `tenant` | WorkOrder → tenant link | Optional |

## External Fields Not Represented Internally (Buildium)

| External Field | Source | Reason |
|---|---|---|
| `TaxId` | `TenantMessage.TaxId` | Compliance/tax-specific; not relevant to maintenance domain |
| `SMSOptInStatus` | `TenantMessage.SMSOptInStatus` | Marketing preference; not core domain data |
| `MailingPreference` | `TenantMessage.MailingPreference` | Communication preference; not core domain data |
| `DateOfBirth` | `TenantMessage.DateOfBirth` | PII; GDPR/CCPA compliance risk |
| `AlternateEmail` | `TenantMessage.AlternateEmail` | Redundant; `email` field is sufficient |
| `AlternateAddress` | `TenantMessage.AlternateAddress` | Not needed for maintenance workflow |
| `EmergencyContact` | `TenantMessage.EmergencyContact` | Not relevant to maintenance/work order use case |
| `Comment` | `TenantMessage.Comment` | Not relevant to maintenance workflow |
| `OperatingBankAccountId` | `RentalPropertyPostMessage` | Financial/accounting data; not domain entity data |
| `Reserve` | `RentalPropertyPostMessage.Reserve` | Accounting data; not domain entity data |
| `GLAccountName` | `WorkOrderMessage` → line items | Accounting data; not domain entity data |
| `BillTransactionId` | `WorkOrderMessage.BillTransactionId` | Billing data; not domain entity data |
| `LineItems` (array) | `WorkOrderMessage.LineItems` | Too detailed for domain model |
| `EntryContacts` (array) | `WorkOrderMessage.EntryContacts` | Too detailed for domain model |
| `Task` (embedded) | `WorkOrderMessage.Task` | Separate entity concept; not part of work order domain |
| `PhoneNumbers` (array) | `TenantMessage.PhoneNumbers` | We store primary phone number only |
| `Leases` (array) | `TenantMessage.Leases` | Lease details managed separately |
| `Permissions` | Skywalk `users` response | Authorization data |
| `Role` | Skywalk `users` response | Authorization data |
| `MFAEnabled` | Skywalk `users` response | Security data |
| `ProfilePhotoUrl` | Skywalk `users` response | Media data |
| `PropertyManagerId` | `RentalPropertyPostMessage` | Internal organizational data |
| `PropertyGroup` | `PropertyGroupMessage` | Organizational grouping; not needed |
| `StructureDescription` | `RentalPropertyPostMessage` | Property detail; not needed for maintenance |
| `YearBuilt` | `RentalPropertyPostMessage.YearBuilt` | Property detail; not needed for maintenance |
| `BuildingName` | `RentalUnitMessage.BuildingName` | Unit detail; not needed for maintenance |
| `IsUnitListed` | `RentalUnitMessage.IsUnitListed` | Listing status; not relevant |

## Internal Fields with No External Equivalent (Buildium)

| Internal Field | Entity | Reason |
|---|---|---|
| `propertyId`, `unitId`, `personId`, `workOrderId`, `addressId` | All | Internal surrogate UUID keys |
| `managementCompany` | Property | Internal domain field |
| `unitType` | Unit | Internal domain classification |
| `completionDate` | WorkOrder | Derived from status transitions |
| `callSource`, `callerName`, `callerContactInfo` | WorkOrder | Voice Agent input |
| `assignedTo` | WorkOrder | Internal assignment tracking |
| `numberOfUnits` | Property | Derived — count of associated units |
| `workOrderType` | WorkOrder | Internal classification |
| `externalTenantId` | Person | Provider-neutral ID mapping |
| `integration` | Person | `@ManyToOne LAZY` — internal JPA relationship |
| `user` | Person | `@ManyToOne LAZY` — optional registered user |
