# TASK-014: Frontend - Integrations Management Page

## Goal
Create an Integrations page at `/home/integrations` with full CRUD: list, create, edit, delete integrations.

## Requirements

### 1. Backend API Endpoints
**Location:** `src/main/java/com/geodevai/controller/IntegrationController.java` (new)

Endpoints:
- `GET /data/integrations` — List all integrations for current user's organization
- `POST /data/integrations` — Create new integration
- `GET /data/integrations/{id}` — Get single integration
- `PUT /data/integrations/{id}` — Update integration
- `DELETE /data/integrations/{id}` — Delete integration

Request/Response DTOs for Integration:
```json
{
  "integrationId": "uuid",
  "name": "Stripe API",
  "endpoint": "https://api.stripe.com/v1",
  "type": "WEB_HEADER",
  "parameters": {
    "Authorization": "Bearer sk_xxx",
    "Stripe-Version": "2023-10-16"
  }
}
```
Types: `WEB_QUERY` (params as query string), `WEB_HEADER` (params as headers)

### 2. Frontend Page Component
**Location:** `src/main/react/src/pages/IntegrationsPage.tsx` (new)

Features:
- **List View**: Table/cards showing integrations (name, type, endpoint, actions)
- **Create Modal**: Form with:
  - Name (text, required)
  - Endpoint (URL, required)
  - Type (select: WEB_QUERY, WEB_HEADER)
  - Parameters: Dynamic key-value pair grid (add/remove rows)
- **Edit Modal**: Pre-filled form, same fields
- **Delete**: Confirmation dialog
- Loading, empty, error states

### 3. Update Routing
**Location:** `src/main/react/src/App.tsx`

Add route:
```tsx
{
  path: "integrations",
  element: <IntegrationsPage />,
  loader: integrationsLoader,
}
```

### 4. Update Sidebar
**Location:** `src/main/react/src/components/Sidebar.tsx`

Add "Integrations" link under Organization or as sibling.

### 5. API Client & Types
**Location:** `src/main/react/src/lib/api.ts`

Add:
- `Integration` interface with `parameters: Record<string, string>`
- `IntegrationType` enum: `WEB_QUERY` | `WEB_HEADER`
- CRUD functions: `fetchIntegrations()`, `createIntegration()`, `updateIntegration()`, `deleteIntegration()`

## Acceptance Criteria
- Page loads at `/home/integrations`
- List shows all org integrations
- Create modal works, validates required fields
- Dynamic parameter grid (add/remove key-value rows)
- Edit pre-fills data, saves changes
- Delete removes with confirmation
- Type selector (WEB_QUERY / WEB_HEADER) functional
- Sidebar navigation works

## Files Likely Involved
- `src/main/java/com/geodevai/controller/IntegrationController.java` (new)
- `src/main/java/com/geodevai/service/IntegrationService.java` (new, optional)
- `src/main/react/src/pages/IntegrationsPage.tsx` (new)
- `src/main/react/src/components/IntegrationForm.tsx` (new - shared form component)
- `src/main/react/src/App.tsx` (modified)
- `src/main/react/src/components/Sidebar.tsx` (modified)
- `src/main/react/src/lib/api.ts` (modified)

## Dependencies
- TASK-011 (Integration entity + repository)

## Constraints
- Follow existing patterns (McpKeyPage for loader/action/modal patterns)
- Reuse UI components where possible (Button, Input, Modal, Table)
- Parameter grid: simple two-column input rows with "Add Parameter" / remove button
- Type stored as enum string in DB