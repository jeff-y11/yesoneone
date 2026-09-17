import { useState } from "react";
import { searchPersons, PersonData } from "../lib/api";

export default function PersonSearchPage() {
  const [query, setQuery] = useState("");
  const [items, setItems] = useState<PersonData[]>([]);
  const [loading, setLoading] = useState(false);
  const [searched, setSearched] = useState(false);

  const handleSearch = async () => {
    setLoading(true);
    setSearched(true);
    const results = await searchPersons(query);
    setItems(results);
    setLoading(false);
  };

  return (
    <div className="flex-1 p-6 overflow-auto">
      <h1 className="text-2xl font-bold mb-6">Persons</h1>
      <div className="flex gap-2 mb-6">
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search by name or email..."
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
          {searched ? "No results found." : "No persons yet."}
        </p>
      ) : (
        <div className="bg-white rounded-lg shadow overflow-hidden">
          <table className="w-full">
            <thead className="bg-slate-100">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">First Name</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Last Name</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Email</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Phone</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Unit</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-700 uppercase">Lease Dates</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {items.map((item) => (
                <tr key={item.personId} className="hover:bg-slate-50">
                  <td className="px-6 py-4 text-sm font-medium">{item.firstName || "N/A"}</td>
                  <td className="px-6 py-4 text-sm text-slate-500">{item.lastName || "N/A"}</td>
                  <td className="px-6 py-4 text-sm text-slate-500">{item.email || "N/A"}</td>
                  <td className="px-6 py-4 text-sm text-slate-500">{item.phoneNumber || "N/A"}</td>
                  <td className="px-6 py-4 text-sm text-slate-500">{item.unitNumber || "N/A"}</td>
                  <td className="px-6 py-4 text-sm text-slate-500">
                    {item.leaseStartDate && item.leaseEndDate
                      ? `${new Date(item.leaseStartDate).toLocaleDateString()} - ${new Date(item.leaseEndDate).toLocaleDateString()}`
                      : "N/A"}
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
