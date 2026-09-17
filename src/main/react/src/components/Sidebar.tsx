import { NavLink } from "react-router-dom";

export default function Sidebar() {
  return (
    <aside className="w-56 min-h-[calc(100vh-48px)] bg-slate-800 text-white p-4 flex flex-col gap-2">
      <nav className="flex flex-col gap-1">
        <NavLink
          to="/home"
          end={false}
          className={({ isActive }) =>
            `px-4 py-2 rounded transition-colors ${
              isActive ? "bg-slate-600" : "hover:bg-slate-700"
            }`
          }
        >
          Dashboard
        </NavLink>
        <NavLink
          to="/home/organization"
          className={({ isActive }) =>
            `px-4 py-2 rounded transition-colors ${
              isActive ? "bg-slate-600" : "hover:bg-slate-700"
            }`
          }
        >
          Organization
        </NavLink>
        <NavLink
          to="/home/integrations"
          className={({ isActive }) =>
            `px-4 py-2 rounded transition-colors ${
              isActive ? "bg-slate-600" : "hover:bg-slate-700"
            }`
          }
        >
          Integrations
        </NavLink>
        <NavLink
          to="/home/mcp/keys"
          className={({ isActive }) =>
            `px-4 py-2 rounded transition-colors ${
              isActive ? "bg-slate-600" : "hover:bg-slate-700"
            }`
          }
        >
          MCP API Keys
        </NavLink>
        <NavLink
          to="/home/properties"
          className={({ isActive }) =>
            `px-4 py-2 rounded transition-colors ${
              isActive ? "bg-slate-600" : "hover:bg-slate-700"
            }`
          }
        >
          Properties
        </NavLink>
        <NavLink
          to="/home/units"
          className={({ isActive }) =>
            `px-4 py-2 rounded transition-colors ${
              isActive ? "bg-slate-600" : "hover:bg-slate-700"
            }`
          }
        >
          Units
        </NavLink>
        <NavLink
          to="/home/persons"
          className={({ isActive }) =>
            `px-4 py-2 rounded transition-colors ${
              isActive ? "bg-slate-600" : "hover:bg-slate-700"
            }`
          }
        >
          Persons
        </NavLink>
        <NavLink
          to="/home/work-orders"
          className={({ isActive }) =>
            `px-4 py-2 rounded transition-colors ${
              isActive ? "bg-slate-600" : "hover:bg-slate-700"
            }`
          }
        >
          Work Orders
        </NavLink>
      </nav>
    </aside>
  );
}
