export default function Login() {
  return (
    <main className="flex min-h-screen items-center justify-center">
      <div className="text-center">
        <button
          onClick={() => {
            window.location.href = "/oauth2/authorization/google";
          }}
          className="px-6 py-3 text-white bg-blue-600 rounded hover:bg-blue-700"
        >
          Sign in with Google
        </button>
      </div>
    </main>
  );
}
