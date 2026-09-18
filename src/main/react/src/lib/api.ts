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
  if (!token || !orgId) return [];

  const res = await fetch(`/data/organization/${orgId}/integrations`, { headers: authHeaders() });
  if (!res.ok) return [];

  const data = await res.json();
  const integrations = data._embedded?.integrations ?? [];
  return integrations;
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

export async function toggleActiveIntegration(integrationId: string): Promise<{ active?: boolean; error?: string }> {
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

export async function runIntegration(integrationId: string): Promise<{ status: string; integrationId: string; error?: string }> {
  const token = requireToken();
  if (!token) return { error: "Not authenticated" } as any;

  const res = await fetch(`/services/integrations/${integrationId}/run`, {
    method: "POST",
    headers: { Authorization: `Bearer ${token}` },
  });

  if (!res.ok) {
    const data = await res.json();
    return { error: data.error || "Failed to run integration" } as any;
  }

  return await res.json();
}

export interface PropertyData {
  propertyId: string;
  name: string;
  externalPropertyId: string;
  propertyType?: string;
  numberOfUnits?: number;
  managementCompany?: string;
  address?: Record<string, unknown>;
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

export async function searchProperties(query: string): Promise<PropertyData[]> {
  const token = requireToken();
  if (!token) return [];

  const params = new URLSearchParams();
  if (query) params.append("search", query);

  const res = await fetch(`/services/properties?${params.toString()}`, { headers: authHeaders() });
  if (!res.ok) return [];

  const data = await res.json();
  return data.items ?? [];
}

export async function getProperty(propertyId: string): Promise<PropertyData | null> {
  const token = requireToken();
  if (!token) return null;

  const res = await fetch(`/services/properties/${propertyId}`, { headers: authHeaders() });
  if (!res.ok) return null;

  return await res.json();
}

export async function searchUnits(query: string, propertyId?: string): Promise<UnitData[]> {
  const token = requireToken();
  if (!token) return [];

  const params = new URLSearchParams();
  if (query) params.append("search", query);
  if (propertyId) params.append("propertyId", propertyId);

  const res = await fetch(`/services/units?${params.toString()}`, { headers: authHeaders() });
  if (!res.ok) return [];

  const data = await res.json();
  return data.items ?? [];
}

export async function getUnit(unitId: string): Promise<UnitData | null> {
  const token = requireToken();
  if (!token) return null;

  const res = await fetch(`/services/units/${unitId}`, { headers: authHeaders() });
  if (!res.ok) return null;

  return await res.json();
}

export async function searchPersons(query: string): Promise<PersonData[]> {
  const token = requireToken();
  if (!token) return [];

  const params = new URLSearchParams();
  if (query) params.append("search", query);

  const res = await fetch(`/services/persons?${params.toString()}`, { headers: authHeaders() });
  if (!res.ok) return [];

  const data = await res.json();
  return data.items ?? [];
}

export async function getPerson(personId: string): Promise<PersonData | null> {
  const token = requireToken();
  if (!token) return null;

  const res = await fetch(`/services/persons/${personId}`, { headers: authHeaders() });
  if (!res.ok) return null;

  return await res.json();
}

export async function searchWorkOrders(query: string, status?: string): Promise<WorkOrderData[]> {
  const token = requireToken();
  if (!token) return [];

  const params = new URLSearchParams();
  if (query) params.append("search", query);
  if (status) params.append("status", status);

  const res = await fetch(`/services/work-orders?${params.toString()}`, { headers: authHeaders() });
  if (!res.ok) return [];

  const data = await res.json();
  return data.items ?? [];
}

export async function getWorkOrder(workOrderId: string): Promise<WorkOrderData | null> {
  const token = requireToken();
  if (!token) return null;

  const res = await fetch(`/services/work-orders/${workOrderId}`, { headers: authHeaders() });
  if (!res.ok) return null;

  return await res.json();
}
