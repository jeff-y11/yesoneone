import { useNavigate, useSearchParams } from "react-router-dom";
import { useEffect } from "react";
import { setToken } from "./lib/auth";

export default function Callback() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    const token = searchParams.get("token");
    if (token) {
      setToken(token);
      navigate("/home", { replace: true });
    }
  }, [searchParams, navigate]);

  return <div>Loading...</div>;
}
