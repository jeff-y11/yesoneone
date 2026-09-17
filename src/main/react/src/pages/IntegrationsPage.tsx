import { useState } from "react";
import { useLoaderData, useFetcher, redirect } from "react-router-dom";
import { fetchIntegrations, createIntegration, deleteIntegration, toggleActiveIntegration, Integration } from "../lib/api";

export async function loader() {
  const integrations = await fetchIntegrations("");
  return integrations;
}

export async function action({ request }: { request: Request }) {
  const formData = await request.formData();
  const intent = formData.get("intent") as string;

  if (intent === "create") {
    const name = formData.get("name") as string;
    const endpoint = formData.get("endpoint") as string;
    const type = formData.get("type") as string;

    const parameters: Record<string, string> = {};
    const paramNames = formData.getAll("paramName") as string[];
    const paramValues = formData.getAll("paramValue") as string[];
    for (let i = 0; i < paramNames.length; i++) {
      if (paramNames[i] && paramValues[i]) {
        parameters[paramNames[i]] = paramValues[i];
      }
    }

    return createIntegration({ name, endpoint, type, parameters });
  }

  if (intent === "delete") {
    const integrationId = formData.get("integrationId") as string;
    return deleteIntegration(integrationId);
  }

  if (intent === "toggle") {
    const integrationId = formData.get("integrationId") as string;
    return toggleActiveIntegration(integrationId);
  }

  return null;
}

interface IntegrationFormProps {
  initial?: Integration;
  onSubmit: () => void;
  onCancel: () => void;
}

function IntegrationForm({ initial, onSubmit, onCancel }: IntegrationFormProps) {
  const fetcher = useFetcher();
  const [parameters, setParameters] = useState<{ name: string; value: string }[]>(
    initial?.parameters
      ? Object.entries(initial.parameters).map(([name, value]) => ({ name, value }))
      : [{ name: "", value: "" }]
  );

  const isSubmitting = fetcher.state !== "idle";
  const error = fetcher.data?.error;

  return (
    <fetcher.Form
      method="post"
      onSubmit={() => setTimeout(onSubmit, 100)}
      className="flex flex-col gap-4"
    >
      <input type="hidden" name="intent" value={initial ? "update" : "create"} />
      {initial && <input type="hidden" name="integrationId" value={initial.integrationId} />}

      <div>
        <label className="block text-sm font-medium text-slate-700 mb-1">Name</label>
        <input
          type="text"
          name="name"
          defaultValue={initial?.name}
          className="w-full px-4 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>

      <div>
        <label className="block text-sm font-medium text-slate-700 mb-1">Endpoint</label>
        <input
          type="text"
          name="endpoint"
          defaultValue={initial?.endpoint}
          placeholder="https://api.example.com/v1"
          className="w-full px-4 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>

      <div>
        <label className="block text-sm font-medium text-slate-700 mb-1">Type</label>
        <select
          name="type"
          defaultValue={initial?.type || "WEB_HEADER"}
          className="w-full px-4 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          <option value="WEB_HEADER">Web Header</option>
          <option value="WEB_QUERY">Web Query</option>
        </select>
      </div>

      <div>
        <label className="block text-sm font-medium text-slate-700 mb-1">Parameters</label>
        <div className="flex flex-col gap-2">
          {parameters.map((param, idx) => (
            <div key={idx} className="flex gap-2">
              <input
                type="text"
                name="paramName"
                value={param.name}
                onChange={(e) => {
                  const newParams = [...parameters];
                  newParams[idx].name = e.target.value;
                  setParameters(newParams);
                }}
                placeholder="Name"
                className="flex-1 px-3 py-1 border rounded text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <input
                type="text"
                name="paramValue"
                value={param.value}
                onChange={(e) => {
                  const newParams = [...parameters];
                  newParams[idx].value = e.target.value;
                  setParameters(newParams);
                }}
                placeholder="Value"
                className="flex-1 px-3 py-1 border rounded text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <button
                type="button"
                onClick={() => setParameters(parameters.filter((_, i) => i !== idx))}
                className="px-2 text-red-500 hover:text-red-700"
              >
                X
              </button>
            </div>
          ))}
        </div>
        <button
          type="button"
          onClick={() => setParameters([...parameters, { name: "", value: "" }])}
          className="mt-2 text-sm text-blue-600 hover:text-blue-800"
        >
          + Add Parameter
        </button>
      </div>

      {error && <p className="text-red-600 text-sm">{error}</p>}

      <div className="flex gap-2 justify-end">
        <button
          type="button"
          onClick={onCancel}
          className="px-4 py-2 border rounded hover:bg-slate-50"
        >
          Cancel
        </button>
        <button
          type="submit"
          disabled={isSubmitting}
          className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
        >
          {isSubmitting ? "Saving..." : initial ? "Save" : "Create"}
        </button>
      </div>
    </fetcher.Form>
  );
}

function Modal({ children, onClose }: { children: React.ReactNode; onClose: () => void }) {
  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl p-6 w-full max-w-lg">
        {children}
      </div>
    </div>
  );
}

export default function IntegrationsPage() {
  const integrations = useLoaderData() as Integration[] | undefined;
  const fetcher = useFetcher();
  const [showCreate, setShowCreate] = useState(false);
  const [editItem, setEditItem] = useState<Integration | null>(null);
  const [deleteItem, setDeleteItem] = useState<Integration | null>(null);

  const items = integrations ?? [];

  return (
    <div className="flex-1 p-6 overflow-auto">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Integrations</h1>
        <button
          onClick={() => setShowCreate(true)}
          className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
        >
          New Integration
        </button>
      </div>

      {items.length === 0 ? (
        <p className="text-slate-500">No integrations yet. Create one to get started.</p>
      ) : (
        <div className="bg-white rounded-lg shadow overflow-hidden">
          <table className="w-full">
            <thead className="bg-slate-100">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Name</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Type</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Endpoint</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {items.map((item) => (
                <tr key={item.integrationId} className={`hover:bg-slate-50 ${!item.active ? 'bg-slate-50 opacity-75' : ''}`}>
                  <td className="px-6 py-4 text-sm font-medium">
                    <div className="flex items-center gap-2">
                      {item.name}
                      {!item.active && (
                        <span className="px-2 py-0.5 rounded-full text-xs bg-amber-100 text-amber-700">
                          Paused
                        </span>
                      )}
                    </div>
                  </td>
                  <td className="px-6 py-4 text-sm">
                    <span className="px-2 py-1 rounded-full text-xs bg-slate-100 text-slate-700">
                      {item.type === "WEB_HEADER" ? "Header" : "Query"}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-500 truncate max-w-xs">{item.endpoint}</td>
                  <td className="px-6 py-4 text-sm space-x-3">
                    <button
                      onClick={() => {
                        fetcher.submit(
                          { intent: "toggle", integrationId: item.integrationId },
                          { method: "post" }
                        );
                      }}
                      className={item.active ? "text-amber-600 hover:text-amber-800" : "text-green-600 hover:text-green-800"}
                    >
                      {item.active ? "Pause" : "Resume"}
                    </button>
                    <button
                      onClick={() => setEditItem(item)}
                      className="text-blue-600 hover:text-blue-800"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => setDeleteItem(item)}
                      className="text-red-600 hover:text-red-800"
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {showCreate && (
        <Modal onClose={() => setShowCreate(false)}>
          <h2 className="text-lg font-semibold mb-4">Create Integration</h2>
          <IntegrationForm
            onSubmit={() => setShowCreate(false)}
            onCancel={() => setShowCreate(false)}
          />
        </Modal>
      )}

      {editItem && (
        <Modal onClose={() => setEditItem(null)}>
          <h2 className="text-lg font-semibold mb-4">Edit Integration</h2>
          <IntegrationForm
            initial={editItem}
            onSubmit={() => setEditItem(null)}
            onCancel={() => setEditItem(null)}
          />
        </Modal>
      )}

      {deleteItem && (
        <Modal onClose={() => setDeleteItem(null)}>
          <h2 className="text-lg font-semibold mb-4">Delete Integration</h2>
          <p className="mb-4">
            Are you sure you want to delete <strong>{deleteItem.name}</strong>?
          </p>
          <div className="flex gap-2 justify-end">
            <button
              onClick={() => setDeleteItem(null)}
              className="px-4 py-2 border rounded hover:bg-slate-50"
            >
              Cancel
            </button>
            <button
              onClick={() => {
                fetcher.submit(
                  { intent: "delete", integrationId: deleteItem.integrationId },
                  { method: "post" }
                );
                setDeleteItem(null);
              }}
              className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700"
            >
              Delete
            </button>
          </div>
        </Modal>
      )}
    </div>
  );
}
