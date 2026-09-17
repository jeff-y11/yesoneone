import { createBrowserRouter, redirect, RouterProvider } from "react-router-dom";
import { authHeaders, getUserIdFromToken, requireToken } from "./lib/auth";
import Login from "./Login";
import Callback from "./Callback";
import Home from "./Home";
import McpKeyPage, { loader as keysLoader, action as keysAction } from "./pages/McpKeyPage";
import OrganizationPage, { loader as orgLoader, action as orgAction } from "./pages/OrganizationPage";
import IntegrationsPage, { loader as integrationsLoader, action as integrationsAction } from "./pages/IntegrationsPage";
import PropertySearchPage from "./pages/PropertySearchPage";
import UnitSearchPage from "./pages/UnitSearchPage";
import PersonSearchPage from "./pages/PersonSearchPage";
import WorkOrderSearchPage from "./pages/WorkOrderSearchPage";
import Layout from "./components/Layout";
import ErrorBoundary from "./components/ErrorBoundary";

async function authLoader() {
  const token = requireToken();
  if (!token) return redirect("/");
  const userId = getUserIdFromToken(token);
  const res = await fetch(`/data/user/${userId}`, { headers: authHeaders() });
  if (!res.ok) return redirect("/");
  return res.json();
}

const router = createBrowserRouter([
  {
    path: "/",
    element: <Login />,
  },
  {
    path: "/callback",
    element: <Callback />,
  },
  {
    path: "/home",
    element: <Layout />,
    loader: authLoader,
    errorElement: <ErrorBoundary />,
    children: [
      {
        index: true,
        element: <Home />,
      },
      {
        path: "mcp/keys",
        element: <McpKeyPage />,
        loader: keysLoader,
        action: keysAction,
      },
      {
        path: "organization",
        element: <OrganizationPage />,
        loader: orgLoader,
        action: orgAction,
      },
      {
        path: "integrations",
        element: <IntegrationsPage />,
        loader: integrationsLoader,
        action: integrationsAction,
      },
      {
        path: "properties",
        element: <PropertySearchPage />,
      },
      {
        path: "units",
        element: <UnitSearchPage />,
      },
      {
        path: "persons",
        element: <PersonSearchPage />,
      },
      {
        path: "work-orders",
        element: <WorkOrderSearchPage />,
      },
    ],
  },
]);

export default function App() {
  return <RouterProvider router={router} />;
}
