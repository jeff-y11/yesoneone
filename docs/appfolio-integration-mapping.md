# AppFolio (Skywalk) Integration Mapping

This document maps AppFolio/Skywalk API fields to our internal domain entities. AppFolio uses string UUIDs throughout, which maps naturally to our `String` external ID fields.

## Overview

AppFolio (accessed via Skywalk API) is a property management platform. Its REST API uses JSON with string-based IDs and supports async polling for cached data.

**Base URL**: `https://api.skywalkapi.com`

**Authentication**: `X-API-Key` header with every request.

**Async Pattern**: GET requests return cached data. If stale, `meta.status: "updating"` — poll every 10 seconds until `meta.status: "ok"`.

**Rate Limiting**: Per-project limits on reads and writes. Exceeding returns `429` with `Retry-After` header.

## Address Mapping

| Internal Field | AppFolio/Skywalk Field | Notes |
|---|---|---|
| `addressId` | N/A | Internal surrogate UUID key |
| `addressLine1` | N/A | Address line 1 |
| `addressLine2` | N/A | Address line 2 |
| `addressLine3` | N/A | Address line 3 |
| `city` | N/A | City |
| `state` | N/A | State |
| `postalCode` | N/A | Postal/ZIP code |
| `country` | N/A | Country |

*Note: AppFolio may not have a direct address API endpoint identical to Buildium. Address fields may need to be derived from property/unit data.*

## Property Mapping

| Internal Field | AppFolio/Skywalk Field | Notes |
|---|---|---|
| `propertyId` | N/A | Internal surrogate UUID key |
| `name` | `/v1/properties` response | Property name |
| `externalPropertyId` | `/v1/properties` `Id` (string UUID) | Stored as String; matches AppFolio format |
| `propertyType` | N/A | Internal domain field |
| `numberOfUnits` | N/A | Internal domain field — count of associated units |
| `managementCompany` | N/A | Internal domain field |
| `address` | Derived from property data | Embedded |
| `organization` | N/A | Internal domain field — maps to client/landlord |

## Unit Mapping

| Internal Field | AppFolio/Skywalk Field | Notes |
|---|---|---|
| `unitId` | N/A | Internal surrogate UUID key |
| `unitNumber` | `/v1/units` response | Unit identifier |
| `externalUnitId` | `/v1/units` `Id` (string UUID) | Stored as String; matches AppFolio format |
| `unitType` | N/A | Internal domain field |
| `squareFootage` | N/A | Direct mapping |
| `bedrooms` | N/A | Direct mapping |
| `bathrooms` | N/A | Direct mapping |
| `rentAmount` | N/A | Market rent |
| `isOccupied` | `/v1/unit-vacancies` | Occupancy status |

## Person (Tenant) Mapping

| Internal Field | AppFolio/Skywalk Field | Notes |
|---|---|---|
| `personId` | N/A | Internal surrogate UUID key |
| `firstName` | `/v1/tenants` response | Direct mapping |
| `lastName` | `/v1/tenants` response | Direct mapping |
| `externalTenantId` | `/v1/tenants` `Id` (string UUID) | `@Column(nullable = false)`. Also serves as `Externalable.integrationRemoteId` |
| `phoneNumber` | N/A | Primary phone only |
| `email` | `/v1/tenants` response | Direct mapping |
| `leaseStartDate` | Derived from lease data | Lease start |
| `leaseEndDate` | Derived from lease data | Lease end |
| `unit` | N/A | `@ManyToOne LAZY` — optional, nullable |
| `user` | N/A | `@ManyToOne LAZY` — optional, present if registered user |
| `integration` | N/A | `@ManyToOne LAZY` — optional, present if pulled from PM integration |

**Person-Organization resolution:**
- If `person.getUser()` is not null → `person.getUser().getOrganization()` gives the client/landlord
- If `person.getUser()` is null but `person.getIntegration()` is not null → `person.getIntegration().getOrganization()` gives the client/landlord

## WorkOrder Mapping

| Internal Field | AppFolio/Skywalk Field | Notes |
|---|---|---|
| `workOrderId` | N/A | Internal surrogate UUID key |
| `externalWorkOrderId` | `/v1/work-orders` `Id` (string UUID) | Stored as String |
| `title` | `/v1/work-orders` response | Direct mapping |
| `summary` | Derived from `Title` | Short description |
| `description` | `/v1/work-orders` response | Detailed description |
| `status` | `/v1/work-orders` response | Direct mapping |
| `priority` | N/A | Direct mapping |
| `amount` | N/A | Direct mapping |
| `dueDate` | N/A | Direct mapping |
| `completionDate` | N/A | Derived from status transitions |
| `entryNotes` | N/A | Direct mapping |
| `vendorNotes` | N/A | Direct mapping |
| `invoiceNumber` | N/A | Direct mapping |
| `chargeableTo` | N/A | Direct mapping |
| `property` | WorkOrder → property link | Relationship |
| `unit` | WorkOrder → unit link | Relationship |
| `tenant` | WorkOrder → tenant link | Optional |

## External Fields Not Represented Internally (AppFolio/Skywalk)

| External Field | Source | Reason |
|---|---|---|
| `Permissions` | `users` response | Authorization data; not domain entity data |
| `Role` | `users` response | Authorization data; not domain entity data |
| `MFAEnabled` | `users` response | Security data; not domain entity data |
| `ProfilePhotoUrl` | `users` response | Media data; not domain entity data |
| `PropertyGroup` | Property groups | Organizational grouping; not needed |
| `StructureDescription` | Property metadata | Not needed for maintenance workflow |
| `YearBuilt` | Property metadata | Not needed for maintenance workflow |
| `BuildingName` | Unit metadata | Not needed for maintenance workflow |
| `IsUnitListed` | Unit metadata | Listing status; not relevant |
| `VendorId`, `VendorName`, `VendorNotes` | Vendor data | Vendor management is PM-specific |
| `LineItems` (array) | Work orders | Too detailed for domain model |
| `Task` (embedded) | Work orders | Separate entity concept |
| `EntryContacts` (array) | Work orders | Too detailed for domain model |
| `GLAccountName` | Financial data | Accounting data |
| `BillTransactionId` | Financial data | Billing data |
| `OperatingBankAccountId` | Financial data | Accounting data |
| `Reserve` | Financial data | Accounting data |
| `TaxId` | Tenant data | Compliance/tax-specific |
| `SMSOptInStatus` | Tenant data | Marketing preference |
| `MailingPreference` | Tenant data | Communication preference |
| `DateOfBirth` | Tenant data | PII; GDPR/CCPA compliance risk |
| `AlternateEmail` | Tenant data | Redundant |
| `AlternateAddress` | Tenant data | Not needed |
| `EmergencyContact` | Tenant data | Not relevant |
| `Comment` | Tenant data | Not relevant |

## Internal Fields with No External Equivalent (AppFolio/Skywalk)

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
