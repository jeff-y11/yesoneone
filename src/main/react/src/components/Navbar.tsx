import { clearToken } from "../lib/auth";

export default function Navbar() {
  const handleLogout = () => {
    clearToken();
    window.location.href = "/";
  };

  return (
    <nav className="flex items-center justify-between px-6 py-3 bg-slate-900 text-white shadow-lg">
      <div className="flex items-center gap-2">
        <span className="text-xl font-bold">GeodeVAI</span>
      </div>
      <div className="flex items-center gap-4">
        <button
          onClick={handleLogout}
          className="px-4 py-2 text-sm bg-red-600 rounded hover:bg-red-700 transition-colors"
        >
          Logout
        </button>
      </div>
    </nav>
  );
}
