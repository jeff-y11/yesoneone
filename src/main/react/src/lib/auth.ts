const TOKEN_KEY = "token";

export function getToken(): string | null {
  return sessionStorage.getItem(TOKEN_KEY);
}

export function setToken(token: string): void {
  sessionStorage.setItem(TOKEN_KEY, token);
}

export function clearToken(): void {
  sessionStorage.removeItem(TOKEN_KEY);
}

export function authHeaders(): Record<string, string> {
  const token = getToken();
  return token ? { Authorization: `Bearer ${token}` } : {};
}

export function requireToken(): string | null {
  return getToken();
}

export function getUserIdFromToken(token: string): string {
  const payload = JSON.parse(atob(token.split(".")[1]));
  return payload.sub;
}
