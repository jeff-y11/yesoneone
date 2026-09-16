import { authHeaders, getUserIdFromToken, requireToken } from "./auth";

export interface Organization {
  organizationId: string;
  name: string;
}

export interface Integration {
  integrationId: string;
  name: string;
  endpoint: string;
  type: "WEB_QUERY" | "WEB_HEADER";
  parameters: Record<string, string>;
  active: boolean;
}

export async function fetchOrganization(): Promise<Organization | null> {
  const token = requireToken();
  if (!token) return null;

  const userId = getUserIdFromToken(token);
  const userRes = await fetch(`/data/user/${userId}`, { headers: authHeaders() });
  if (!userRes.ok) return null;

  const userData = await userRes.json();
  const orgId = userData.organization?.id || userData.organizationId;
  if (!orgId) return null;

  const orgRes = await fetch(`/data/organization/${orgId}`, { headers: authHeaders() });
  if (!orgRes.ok) return null;

  return await orgRes.json();
}

export async function updateOrganization(orgId: string, name: string): Promise<{ organization?: Organization; error?: string }> {
  const token = requireToken();
  if (!token) return { error: "Not authenticated" };

  const res = await fetch(`/data/organization/${orgId}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
    body: JSON.stringify({ name }),
  });

  if (!res.ok) {
    const data = await res.json();
    return { error: data.error || "Failed to update organization" };
  }

  return { organization: await res.json() };
}

export async function fetchIntegrations(orgId: string): Promise<Integration[]> {
  const token = requireToken();
  if (!token) return [];

  const res = await fetch(`/data/organization/${orgId}/integrations`, { headers: authHeaders() });
  if (!res.ok) return [];

  const data = await res.json();
  const integrations = data._embedded?.integrations ?? [];
  return integrations.filter((i: Integration) => i.active);
}

export async function createIntegration(integration: {
  name: string;
  endpoint: string;
  type: string;
  parameters?: Record<string, string>;
}): Promise<{ integration?: Integration; error?: string }> {
  const token = requireToken();
  if (!token) return { error: "Not authenticated" };

  const res = await fetch("/services/integrations", {
    method: "POST",
    headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
    body: JSON.stringify(integration),
  });

  if (!res.ok) {
    const data = await res.json();
    return { error: data.error || "Failed to create integration" };
  }

  return { integration: await res.json() };
}

export async function deleteIntegration(integrationId: string): Promise<{ error?: string }> {
  const token = requireToken();
  if (!token) return { error: "Not authenticated" };

  const res = await fetch(`/services/integrations/${integrationId}`, {
    method: "DELETE",
    headers: { Authorization: `Bearer ${token}` },
  });

  if (!res.ok) {
    return { error: "Failed to delete integration" };
  }

  return {};
}
