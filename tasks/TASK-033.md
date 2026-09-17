# TASK-033: Add Run Integration Button to Frontend

## Goal
Add a "Run" button to each integration row in the frontend table so users can manually trigger an integration run from the UI.

## Requirements

### 1. Add `runIntegration` API Function
**File:** `src/main/react/src/lib/api.ts` (modify)

Add:
```typescript
export async function runIntegration(integrationId: string): Promise<{ status: string; integrationId: string; error?: string }> {
    const token = requireToken();
    if (!token) return { error: "Not authenticated" };

    const res = await fetch(`/services/integrations/${integrationId}/run`, {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` },
    });

    if (!res.ok) {
        const data = await res.json();
        return { error: data.error || "Failed to run integration" };
    }

    return await res.json();
}
```

### 2. Add Run Button to IntegrationTable
**File:** `src/main/react/src/pages/IntegrationsPage.tsx` (modify)

- Import `runIntegration` from `../lib/api`
- Add a "Run" button to each row in the table's Actions column
- Only show "Run" button if the integration is active
- On click, call `runIntegration(integrationId)` and show a success/error toast or alert
- Use `useFetcher` or `useState` for handling the run response
- Disable button while run is in progress

### 3. Handle Run Response
- On success: Show a success notification/message
- On failure (403): Show "You don't have permission to run this integration"
- On failure (400): Show "Integration is not active"
- On other failures: Show the error message

### 4. Update `fetchIntegrations`
- Ensure `fetchIntegrations` returns all integrations (including paused) so users can see which are runnable
- Filter display to show active integrations by default, with option to show paused

## Acceptance Criteria
- Frontend has a "Run" button for each active integration
- Run button calls `POST /services/integrations/{id}/run`
- Success shows status "RUNNING" message
- Unauthorized runs show appropriate error
- Paused integrations do not show the Run button (or show disabled)
- Run button is disabled during execution
- All files compile without errors

## Files Likely Involved
- `src/main/react/src/lib/api.ts` (modify)
- `src/main/react/src/pages/IntegrationsPage.tsx` (modify)

## Dependencies
- TASK-029 (REST endpoint pattern, `IntegrationController`)
- TASK-032 (pause/resume toggle)

## Constraints
- Do NOT implement actual integration execution logic
- The Run button calls the existing backend endpoint
- Use the same `POST /services/integrations/{id}/run` endpoint from TASK-029
- Toast/notification should use the existing UI pattern (alert/banner)
