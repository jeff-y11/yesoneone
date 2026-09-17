import { useState } from "react";
import { searchWorkOrders, WorkOrderData } from "../lib/api";

export default function WorkOrderSearchPage() {
  const [query, setQuery] = useState("");
  const [status, setStatus] = useState("");
  const [items, setItems] = useState<WorkOrderData[]>([]);
  const [loading, setLoading] = useState(false);
  const [searched, setSearched] = useState(false);

  const handleSearch = async () => {
    setLoading(true);
    setSearched(true);
    const results = await searchWorkOrders(query, status || undefined);
    setItems(results);
    setLoading(false);
  };

  return (
    <div className="flex-1 p-6 overflow-auto">
      <h1 className="text-2xl font-bold mb-6">Work Orders</h1>
      <div className="flex gap-2 mb-6">
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search by title or external ID..."
          className="flex-1 px-4 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          onKeyDown={(e) => e.key === "Enter" && handleSearch()}
        />
        <select
          value={status}
          onChange={(e) => setStatus(e.target.value)}
          className="px-4 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          <option value="">All Statuses</option>
          <option value="OPEN">Open</option>
          <option value="IN_PROGRESS">In Progress</option>
          <option value="COMPLETED">Completed</option>
          <option value="CANCELLED">Cancelled</option>
        </select>
        <button
          onClick={handleSearch}
          disabled={loading}
          className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
        >
          {loading ? "Searching..." : "Search"}
        </button>
      </div>

      {loading ? (
        <p className="text-slate-500">Loading...</p>
      ) : items.length === 0 ? (
        <p className="text-slate-500">
          {searched ? "No results found." : "No work orders yet."}
        </p>
      ) : (
        <div className="bg-white rounded-lg shadow overflow-hidden">
          <table className="w-full">
            <thead className="bg-slate-100">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Title</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Status</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Priority</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Property</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Unit</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Amount</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Due Date</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {items.map((item) => (
                <tr key={item.workOrderId} className="hover:bg-slate-50">
                  <td className="px-6 py-4 text-sm font-medium">{item.title}</td>
                  <td className="px-6 py-4 text-sm">
                    <span className={`px-2 py-1 rounded-full text-xs ${
                      item.status === "OPEN" ? "bg-blue-100 text-blue-700" :
                      item.status === "IN_PROGRESS" ? "bg-yellow-100 text-yellow-700" :
                      item.status === "COMPLETED" ? "bg-green-100 text-green-700" :
                      "bg-slate-100 text-slate-700"
                    }`}>
                      {item.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-500">{item.priority || "N/A"}</td>
                  <td className="px-6 py-4 text-sm text-slate-500">{item.propertyName || "N/A"}</td>
                  <td className="px-6 py-4 text-sm text-slate-500">{item.unitNumber || "N/A"}</td>
                  <td className="px-6 py-4 text-sm text-slate-500">
                    {item.amount ? `$${item.amount.toLocaleString()}` : "N/A"}
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-500">
                    {item.dueDate ? new Date(item.dueDate).toLocaleDateString() : "N/A"}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
