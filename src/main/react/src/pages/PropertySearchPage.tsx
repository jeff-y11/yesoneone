import { useState } from "react";
import { searchProperties, PropertyData } from "../lib/api";

export default function PropertySearchPage() {
  const [query, setQuery] = useState("");
  const [items, setItems] = useState<PropertyData[]>([]);
  const [loading, setLoading] = useState(false);
  const [searched, setSearched] = useState(false);

  const handleSearch = async () => {
    setLoading(true);
    setSearched(true);
    const results = await searchProperties(query);
    setItems(results);
    setLoading(false);
  };

  return (
    <div className="flex-1 p-6 overflow-auto">
      <h1 className="text-2xl font-bold mb-6">Properties</h1>
      <div className="flex gap-2 mb-6">
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search by name or external ID..."
          className="flex-1 px-4 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          onKeyDown={(e) => e.key === "Enter" && handleSearch()}
        />
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
          {searched ? "No results found." : "No properties yet."}
        </p>
      ) : (
        <div className="bg-white rounded-lg shadow overflow-hidden">
          <table className="w-full">
            <thead className="bg-slate-100">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Name</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Type</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">External ID</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Units</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Address</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {items.map((item) => (
                <tr key={item.propertyId} className="hover:bg-slate-50">
                  <td className="px-6 py-4 text-sm font-medium">{item.name}</td>
                  <td className="px-6 py-4 text-sm">
                    <span className="px-2 py-1 rounded-full text-xs bg-slate-100 text-slate-700">
                      {item.propertyType || "N/A"}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-500">{item.externalPropertyId}</td>
                  <td className="px-6 py-4 text-sm text-slate-500">{item.numberOfUnits || 0}</td>
                  <td className="px-6 py-4 text-sm text-slate-500 truncate max-w-xs">
                    {item.address ? `${(item.address as Record<string, string>).line1 || ""}` : "N/A"}
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
