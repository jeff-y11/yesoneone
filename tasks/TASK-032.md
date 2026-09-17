# TASK-032: Add Pause/Resume Integration Endpoint and Frontend Toggle

## Goal
Add a toggle endpoint to pause/resume an integration, and add a toggle button to the frontend integration management page.

## Requirements

### 1. Add Toggle Endpoint to IntegrationController
**File:** `src/main/java/com/geodevai/controller/IntegrationController.java` (modify)

Add a new endpoint:
```java
@PostMapping("/{integrationId}/toggle-active")
public ResponseEntity<Map<String, Object>> toggleActive(@PathVariable UUID integrationId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userId = auth.getName();
    User user = userRepository.findById(UUID.fromString(userId))
            .orElseThrow();

    Integration integration = integrationRepository.findById(integrationId)
            .orElseThrow();

    if (user.getOrganization() == null ||
        !user.getOrganization().getOrganizationId().equals(integration.getOrganization().getOrganizationId())) {
        return ResponseEntity.status(403).build();
    }

    integration.setActive(!integration.isActive());
    integrationRepository.save(integration);

    return ResponseEntity.ok(Map.of(
        "integrationId", integration.getIntegrationId(),
        "active", integration.isActive(),
        "message", integration.isActive() ? "Integration activated" : "Integration paused"
    ));
}
```

### 2. Add `toggleActive` API Function
**File:** `src/main/react/src/lib/api.ts` (modify)

Add:
```typescript
export async function toggleActiveIntegration(integrationId: string): Promise<{ active: boolean; error?: string }> {
    const token = requireToken();
    if (!token) return { error: "Not authenticated" };

    const res = await fetch(`/services/integrations/${integrationId}/toggle-active`, {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` },
    });

    if (!res.ok) {
        const data = await res.json();
        return { error: data.error || "Failed to toggle integration" };
    }

    return await res.json();
}
```

### 3. Update IntegrationsPage Frontend
**File:** `src/main/react/src/pages/IntegrationsPage.tsx` (modify)

- Import `toggleActiveIntegration` from `../lib/api`
- Add a toggle button to each row in the table
- Handle toggle in the action handler
- Button shows "Pause" when active, "Resume" when paused
- Use `useFetcher` or `useNavigate` for the toggle action

### 4. Update `fetchIntegrations` to Include Inactive
**File:** `src/main/react/src/lib/api.ts` (modify)

Update `fetchIntegrations` to optionally include paused integrations so they can be resumed from the page.

### 5. Add Refresh After Toggle
After toggling active status, refresh the integration list to reflect the change.

## Acceptance Criteria
- `IntegrationController` has `POST /{integrationId}/toggle-active` endpoint
- Endpoint validates user has access to the integration's organization
- Endpoint returns 403 if user doesn't own the integration
- Endpoint toggles `active` field and returns updated status
- Frontend shows toggle button (Pause/Resume) for each integration
- Toggle button triggers the API call and updates the UI
- `fetchIntegrations` includes both active and paused integrations
- Refresh works correctly after toggle
- All files compile without errors

## Files Likely Involved
- `src/main/java/com/geodevai/controller/IntegrationController.java` (modify)
- `src/main/react/src/lib/api.ts` (modify)
- `src/main/react/src/pages/IntegrationsPage.tsx` (modify)

## Dependencies
- TASK-029 (REST endpoint pattern, `IntegrationController`)
- TASK-031 (existing integration scaffolding)

## Constraints
- The toggle endpoint must follow existing `IntegrationController` patterns (auth check, org validation)
- Do NOT implement actual pause logic beyond setting `active = false`
- The pause state persists via the `active` boolean field on `Integration`
- UI should show clear visual state: active (green/blue) vs paused (grey)
