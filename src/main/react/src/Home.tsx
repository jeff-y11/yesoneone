import { useLoaderData } from "react-router-dom";

interface UserData {
  displayName: string;
  role: string;
}

export default function Home() {
  const user = useLoaderData() as UserData | undefined;

  if (!user) return null;

  return (
    <div className="flex items-center justify-center h-full">
      <div className="text-center">
        <h1 className="text-2xl font-bold text-slate-800">
          Hello {user.displayName}
        </h1>
        <p className="mt-2 text-slate-600">Your role: {user.role}</p>
      </div>
    </div>
  );
}
