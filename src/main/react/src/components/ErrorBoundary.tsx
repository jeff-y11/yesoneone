import { useRouteError, isRouteErrorResponse, Link } from "react-router-dom";

export default function ErrorBoundary() {
  const error = useRouteError();

  if (isRouteErrorResponse(error)) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-slate-800">
            {error.status} {error.statusText}
          </h1>
          <p className="mt-2 text-slate-600">{error.data?.message || "Something went wrong."}</p>
          <Link to="/" className="mt-4 inline-block text-blue-600 hover:underline">
            Sign in
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="flex items-center justify-center h-full">
      <div className="text-center">
        <h1 className="text-2xl font-bold text-slate-800">Something went wrong</h1>
        <p className="mt-2 text-slate-600">
          {error instanceof Error ? error.message : "An unexpected error occurred."}
        </p>
        <Link to="/" className="mt-4 inline-block text-blue-600 hover:underline">
          Sign in
        </Link>
      </div>
    </div>
  );
}
