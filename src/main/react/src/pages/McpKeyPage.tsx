import { useFetcher, useLoaderData, redirect } from "react-router-dom";
import { authHeaders, requireToken } from "../lib/auth";

export async function loader() {
  const token = requireToken();
  if (!token) return redirect("/");
  const res = await fetch("/data/mcpkey", { headers: authHeaders() });
  if (!res.ok) return redirect("/");
  const data = await res.json();
  const keys = data._embedded?.mcpKeys ?? data._embedded?.mcpkey ?? [];
  return keys as McpKey[];
}

export async function action({ request }: { request: Request }) {
  const token = requireToken();
  if (!token) return redirect("/");
  const formData = await request.formData();
  const intent = formData.get("intent") as string;

  if (intent === "create") {
    const name = formData.get("name") as string;
    const res = await fetch("/services/keys", {
      method: "POST",
      headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
      body: JSON.stringify({ name }),
    });
    if (!res.ok) return { error: "Failed to create key" };
    return res.json();
  }

  if (intent === "delete") {
    const keyId = formData.get("keyId") as string;
    await fetch(`/services/keys/${keyId}`, {
      method: "DELETE",
      headers: { Authorization: `Bearer ${token}` },
    });
  }

  return null;
}

interface McpKey {
  mcpKeyId: string;
  name: string;
  keyPrefix: string;
  keyHash: string;
  active: boolean;
  createdAt: string;
}

interface CreateActionResponse {
  rawKey?: string;
  error?: string;
}

function maskKey(keyPrefix: string): string {
  if (!keyPrefix) return keyPrefix;
  return keyPrefix + "...";
}

export default function McpKeyPage() {
  const keys = useLoaderData() as McpKey[] | undefined;
  const fetcher = useFetcher();

  if (!keys) return null;

  const createdKey = (fetcher.data as CreateActionResponse | undefined)?.rawKey;
  const error = (fetcher.data as CreateActionResponse | undefined)?.error;
  const isSubmitting = fetcher.state !== "idle";

  return (
    <div className="flex-1 p-6 overflow-auto">
      <h1 className="text-2xl font-bold mb-6">MCP API Keys</h1>

      <div className="mb-8 bg-white rounded-lg shadow p-6">
        <h2 className="text-lg font-semibold mb-4">Create New Key</h2>
        <fetcher.Form method="post" className="flex flex-col gap-4 max-w-md">
          <input type="hidden" name="intent" value="create" />
          <input
            type="text"
            name="name"
            placeholder="Key name"
            className="px-4 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
          <button
            type="submit"
            disabled={isSubmitting}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
          >
            {isSubmitting ? "Creating..." : "Create Key"}
          </button>
          {error && <p className="text-red-600 text-sm">{error}</p>}
        </fetcher.Form>

        {createdKey && (
          <div className="mt-4 p-4 bg-green-50 border border-green-200 rounded">
            <p className="text-sm font-semibold text-green-800">
              Key created! Save it now — it will not be shown again.
            </p>
            <code className="block mt-2 p-2 bg-white rounded text-sm break-all">
              {createdKey}
            </code>
          </div>
        )}
      </div>

      <div className="bg-white rounded-lg shadow overflow-hidden">
        <table className="w-full">
          <thead className="bg-slate-100">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Name</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Key</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Created</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Status</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-200">
            {keys.map((key: McpKey) => (
              <tr key={key.mcpKeyId} className="hover:bg-slate-50">
                <td className="px-6 py-4 text-sm font-medium">{key.name}</td>
                <td className="px-6 py-4 text-sm font-mono">{maskKey(key.keyPrefix)}</td>
                <td className="px-6 py-4 text-sm text-slate-500">
                  {new Date(key.createdAt).toLocaleDateString()}
                </td>
                <td className="px-6 py-4 text-sm">
                  <span
                    className={`px-2 py-1 rounded-full text-xs ${
                      key.active
                        ? "bg-green-100 text-green-800"
                        : "bg-red-100 text-red-800"
                    }`}
                  >
                    {key.active ? "Active" : "Inactive"}
                  </span>
                </td>
                <td className="px-6 py-4 text-sm">
                  {key.active && (
                    <button
                      onClick={() => {
                        if (window.confirm("Are you sure you want to deactivate this key?")) {
                          fetcher.submit(
                            { intent: "delete", keyId: key.mcpKeyId },
                            { method: "post" }
                          );
                        }
                      }}
                      className="text-red-600 hover:text-red-800 text-sm"
                    >
                      Deactivate
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
