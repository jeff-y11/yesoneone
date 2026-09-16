# TASK-008: MCP Key Management Page (Frontend)

## Goal
Create a frontend page for generating, displaying, and deleting MCP API keys. Keys are named, hashed, and associated with the creating user. The page is served entirely on the frontend (React) with a backend API for data operations. The page should be accessible to authenticated users only. The page should also include a top nav with logout and a left-side navigation sidebar.

## Use Case
Thinkerr.ai voice agents need persistent MCP authentication without requiring login each time. API keys provide a simple alternative to JWTs for partner integrations.

## Requirements
1. **Navigation Layout:**
   - Top nav bar with a logout button on the right corner
   - Logout should delete/invalidate the JWT from sessionStorage and redirect to the login page
   - Left-side navigation sidebar with a link leading to the API Key Management page
   - Both nav components should be present on all authenticated pages

2. **Key Generation Page:**
   - Form to create a new key with a descriptive name
   - Upon creation, key is hashed (SHA-256) and stored with the creating user
   - Key is displayed as masked: first 5 characters + "..." + last 5 characters (e.g., `abcd1...efg12`)
   - Full key value is shown only once at creation time and stored encrypted

3. **Key Listing Page:**
   - Display all keys belonging to the authenticated user
   - Each key shows: name, masked key value, creation date, status (active/inactive)
   - Action to deactivate/revoke a key
   - Users can only see and manage their own keys (no admin-wide key listing)

4. **Access Control:**
   - Authenticated users can manage their own keys
   - Unauthorized access returns `401 Unauthorized`

5. **API Endpoints (Backend):**
   - `GET /services/keys` - List keys for the authenticated user only
   - `POST /services/keys` - Create a new key
   - `DELETE /services/keys/{keyId}` - Deactivate/revoke a key
   - All endpoints require authentication (JWT header)

6. **Frontend Implementation:**
   - Key Management page served at `/home/mcp/keys`
   - Form for key creation with name field
   - Table displaying keys with masked values
   - Delete button per key with confirmation dialog
   - Full page layout with top nav and left sidebar

## Files Likely Involved
### Frontend (React):
- `D:\workspace\geodevai\src\main\react\src\App.tsx` (updated with layout and new routes)
- `D:\workspace\geodevai\src\main\react\src\Home.tsx` (updated with nav layout)
- `D:\workspace\geodevai\src\main\react\src/components/Navbar.tsx` (new)
- `D:\workspace\geodevai\src\main\react\src/components/Sidebar.tsx` (new)
- `D:\workspace\geodevai\src\main\react/src/components/Layout.tsx` (new)
- `D:\workspace\geodevai\src\main\react/src/pages/McpKeyPage.tsx` (new)
- `D:\workspace\geodevai\src\main\react/src/index.css` (updated with layout styling)

### Backend (Spring Boot):
- `D:\workspace\geodevai/src/main/java/com/geodevai/controller/KeyController.java` (new)
- `D:\workspace\geodevai/src/main/java/com/geodevai/mcp/service/McpKeyService.java` (existing)
- `D:\workspace\geodevai/src/main/java/com/geodevai/mcp/util/KeyMaskUtil.java` (existing)
- `D:\workspace\geodevai/src/main/java/com/geodevai/config/WebSecurityConfig.java` (updated)

## Constraints & Dependencies
- Depends on: `TASK-006` (key management backend).
- Frontend-only page focus with backend API support.
- Users can only manage their own keys; no admin-wide key listing.
- Keys masked: first 5 + last 5 characters displayed; full value stored hashed.
- API keys are exposed under `/services/keys`, NOT under `/mcp`.
