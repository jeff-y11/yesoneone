import { useFetcher, useLoaderData, redirect } from "react-router-dom";
import { fetchOrganization, updateOrganization, Organization } from "../lib/api";

export async function loader() {
  const org = await fetchOrganization();
  return org;
}

export async function action({ request }: { request: Request }) {
  const formData = await request.formData();
  const name = formData.get("name") as string;
  const orgId = formData.get("organizationId") as string;

  if (!name || !orgId) {
    return { error: "Name is required" };
  }

  return updateOrganization(orgId, name);
}

export default function OrganizationPage() {
  const org = useLoaderData() as Organization | null;
  const fetcher = useFetcher();

  const isSubmitting = fetcher.state !== "idle";
  const error = fetcher.data?.error;
  const success = fetcher.data?.organization;

  if (!org) {
    return (
      <div className="flex-1 p-6">
        <p className="text-slate-500">No organization found. Join or create an organization to get started.</p>
      </div>
    );
  }

  return (
    <div className="flex-1 p-6">
      <h1 className="text-2xl font-bold mb-6">Organization</h1>

      <div className="bg-white rounded-lg shadow p-6 max-w-md">
        <h2 className="text-lg font-semibold mb-4">Organization Details</h2>

        <fetcher.Form method="post" className="flex flex-col gap-4">
          <input type="hidden" name="organizationId" value={org.organizationId} />
          <div>
            <label htmlFor="name" className="block text-sm font-medium text-slate-700 mb-1">
              Name
            </label>
            <input
              type="text"
              id="name"
              name="name"
              defaultValue={org.name}
              className="w-full px-4 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>
          <button
            type="submit"
            disabled={isSubmitting}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
          >
            {isSubmitting ? "Saving..." : "Save"}
          </button>

          {error && <p className="text-red-600 text-sm">{error}</p>}
          {success && <p className="text-green-600 text-sm">Organization updated successfully.</p>}
        </fetcher.Form>
      </div>
    </div>
  );
}
