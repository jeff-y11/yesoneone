# TASK-013: Frontend - Organization Summary Page

## Goal
Create an Organization summary page accessible at `/home/organization` where users can view and edit their organization name.

## Requirements

### 1. Backend API Endpoint
**Location:** `src/main/java/com/geodevai/controller/OrganizationController.java` (new)

Endpoints:
- `GET /data/organization` — Returns current user's organization (via auth principal)
- `PUT /data/organization` — Updates organization name
  - Request: `{ "name": "New Name" }`
  - Response: Updated organization DTO

### 2. Frontend Route & Page
**Location:** `src/main/react/src/pages/OrganizationPage.tsx` (new)

Features:
- Display organization name in editable field
- Save button to persist changes
- Loading and error states
- Toast/notification on success/error

### 3. Update Routing
**Location:** `src/main/react/src/App.tsx`

Add route under `/home`:
```tsx
{
  path: "organization",
  element: <OrganizationPage />,
  loader: organizationLoader,  // fetches org data
}
```

### 4. Update Sidebar Navigation
**Location:** `src/main/react/src/components/Sidebar.tsx`

Add link:
```tsx
<NavLink to="/home/organization" ...>
  Organization
</NavLink>
```

### 5. Data Types & API Client
**Location:** `src/main/react/src/lib/api.ts` (new or extend existing)

Add:
- `Organization` TypeScript interface
- `fetchOrganization()`, `updateOrganization(name)` functions

## Acceptance Criteria
- Authenticated user with organization sees page at `/home/organization`
- Organization name displays and is editable
- Changes persist via PUT `/data/organization`
- Sidebar includes "Organization" link
- Proper error handling (no org, unauthorized, etc.)

## Files Likely Involved
- `src/main/java/com/geodevai/controller/OrganizationController.java` (new)
- `src/main/java/com/geodevai/service/OrganizationService.java` (new, optional)
- `src/main/react/src/pages/OrganizationPage.tsx` (new)
- `src/main/react/src/App.tsx` (modified)
- `src/main/react/src/components/Sidebar.tsx` (modified)
- `src/main/react/src/lib/api.ts` (new/modified)

## Dependencies
- TASK-011 (Organization entity exists)

## Constraints
- Follow existing React patterns (React Router loaders, Tailwind styling)
- Reuse auth pattern from `McpKeyPage` (loader + action)
- Only show page if user has an organization (redirect otherwise)