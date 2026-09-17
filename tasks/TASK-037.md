# TASK-037: Frontend - Person, Property, Unit, and WorkOrder Search and View Pages

## Goal
Create frontend pages for searching and viewing Person, Property, Unit, and WorkOrder entities. All data is scoped to the user's organization.

## Requirements

### 1. Create PropertySearchPage
**File:** `src/main/react/src/pages/PropertySearchPage.tsx` (new)

Features:
- Search input field (searches by property name or external ID)
- Table listing properties: Name, Type, External ID, Units Count, Address
- Click row to navigate to detail view
- Empty state message when no results
- Loading state during fetch

Route: `/home/properties`

### 2. Create UnitSearchPage
**File:** `src/main/react/src/pages/UnitSearchPage.tsx` (new)

Features:
- Search input field (searches by unit number or external ID)
- Optional property filter (dropdown or link from property detail)
- Table listing units: Unit Number, Property, Bedrooms, Bathrooms, Sq Ft, Rent, Occupied
- Click row to navigate to detail view
- Empty state message when no results

Route: `/home/units`

### 3. Create PersonSearchPage
**File:** `src/main/react/src/pages/PersonSearchPage.tsx` (new)

Features:
- Search input field (searches by name or email)
- Table listing persons: First Name, Last Name, Email, Phone, Unit, Lease Dates
- Click row to navigate to detail view
- Empty state message when no results

Route: `/home/persons`

### 4. Create WorkOrderSearchPage
**File:** `src/main/react/src/pages/WorkOrderSearchPage.tsx` (new)

Features:
- Search input field (searches by title or external ID)
- Status filter dropdown (All, Open, In Progress, Completed, etc.)
- Optional property filter
- Table listing work orders: Title, Status, Priority, Property, Unit, Amount, Due Date
- Click row to navigate to detail view
- Empty state message when no results

Route: `/home/work-orders`

### 5. Create API Client Functions
**File:** `src/main/react/src/lib/api.ts` (modify)

Add fetch functions for each entity:

```typescript
export interface PropertyData {
  propertyId: string;
  name: string;
  externalPropertyId: string;
  propertyType?: string;
  numberOfUnits?: number;
  managementCompany?: string;
  address?: Address;
  organizationId: string;
  lastSyncTime?: string;
}

export interface UnitData {
  unitId: string;
  unitNumber: string;
  externalUnitId: string;
  unitType?: string;
  squareFootage?: number;
  bedrooms?: number;
  bathrooms?: number;
  rentAmount?: number;
  isOccupied?: boolean;
  propertyId: string;
  propertyName?: string;
  lastSyncTime?: string;
}

export interface PersonData {
  personId: string;
  firstName?: string;
  lastName?: string;
  externalTenantId: string;
  phoneNumber?: string;
  email?: string;
  leaseStartDate?: string;
  leaseEndDate?: string;
  unitId?: string;
  unitNumber?: string;
  integrationId?: string;
  lastSyncTime?: string;
}

export interface WorkOrderData {
  workOrderId: string;
  externalWorkOrderId: string;
  title: string;
  summary?: string;
  status: string;
  priority?: string;
  amount?: number;
  dueDate?: string;
  completionDate?: string;
  propertyId?: string;
  propertyName?: string;
  unitId?: string;
  unitNumber?: string;
  tenantId?: string;
  tenantName?: string;
  callSource?: string;
  callerName?: string;
  callerContactInfo?: string;
  lastSyncTime?: string;
}

export async function searchProperties(query: string): Promise<PropertyData[]> { ... }
export async function getProperty(propertyId: string): Promise<PropertyData | null> { ... }
export async function searchUnits(query: string, propertyId?: string): Promise<UnitData[]> { ... }
export async function getUnit(unitId: string): Promise<UnitData | null> { ... }
export async function searchPersons(query: string): Promise<PersonData[]> { ... }
export async function getPerson(personId: string): Promise<PersonData | null> { ... }
export async function searchWorkOrders(query: string, status?: string): Promise<WorkOrderData[]> { ... }
export async function getWorkOrder(workOrderId: string): Promise<WorkOrderData | null> { ... }
```

### 6. Update Router Configuration
**File:** `src/main/react/src/App.tsx` (modify)

Add routes:
```typescript
{
  path: "properties",
  element: <PropertySearchPage />,
  loader: propertiesLoader,
},
{
  path: "units",
  element: <UnitSearchPage />,
  loader: unitsLoader,
},
{
  path: "persons",
  element: <PersonSearchPage />,
  loader: personsLoader,
},
{
  path: "work-orders",
  element: <WorkOrderSearchPage />,
  loader: workOrdersLoader,
},
```

### 7. Update Sidebar Navigation
**File:** `src/main/react/src/components/Layout.tsx` (modify)

Add navigation items to the left sidebar:
- Properties
- Units
- Persons
- Work Orders

### 8. Common UI Pattern

Each search page follows this pattern:
```tsx
export default function EntitySearchPage() {
  const [query, setQuery] = useState("");
  const [items, setItems] = useState<Entity[]>([]);
  const [loading, setLoading] = useState(false);

  const handleSearch = async () => {
    setLoading(true);
    const results = await searchEntities(query);
    setItems(results);
    setLoading(false);
  };

  return (
    <div className="flex-1 p-6 overflow-auto">
      <h1 className="text-2xl font-bold mb-6">Entities</h1>
      <div className="flex gap-2 mb-6">
        <input ... />
        <button onClick={handleSearch}>Search</button>
      </div>
      <table>...</table>
    </div>
  );
}
```

## Scope
This is read-only — search and view only. No create, edit, or delete UI. The integration is unidirectional from PM systems.

## Acceptance Criteria
- PropertySearchPage renders with search input and table
- UnitSearchPage renders with search input, optional property filter, and table
- PersonSearchPage renders with search input and table
- WorkOrderSearchPage renders with search input, status filter, and table
- Each page calls the corresponding API function
- Search input triggers API call with query parameter
- Empty state shows "No results found" message
- Loading state shows spinner or "Loading..." text
- Clicking a row shows detail view (no edit capability)
- Sidebar navigation includes all 4 entity types
- No create/edit buttons or forms
- All files compile without TypeScript errors

## Files Likely Involved
- `src/main/react/src/pages/PropertySearchPage.tsx` (new)
- `src/main/react/src/pages/UnitSearchPage.tsx` (new)
- `src/main/react/src/pages/PersonSearchPage.tsx` (new)
- `src/main/react/src/pages/WorkOrderSearchPage.tsx` (new)
- `src/main/react/src/lib/api.ts` (modify)
- `src/main/react/src/App.tsx` (modify)
- `src/main/react/src/components/Layout.tsx` (modify)

## Dependencies
- TASK-035 (repository methods)
- TASK-036 (REST endpoints)
- TASK-032 (existing page patterns from IntegrationsPage)

## Constraints
- All API calls use the authenticated token from `lib/auth.ts`
- Follow existing page patterns from `IntegrationsPage.tsx`
- Use Tailwind CSS for styling (consistent with existing pages)
- Use React Router's `useLoaderData` and `useFetcher` patterns
- No external UI component libraries — use plain Tailwind
