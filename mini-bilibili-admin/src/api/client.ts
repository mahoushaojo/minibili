const API_BASE = import.meta.env.VITE_API_BASE_URL || "/api";
const TOKEN_KEY = "mini_bilibili_admin_access_token";

export interface ApiResponse<T> {
  code: number;
  message: string;
  status: boolean;
  data: T;
}

let accessToken = localStorage.getItem(TOKEN_KEY) || "";
let refreshing: Promise<boolean> | null = null;

export function getAccessToken() {
  return accessToken;
}

export function setAccessToken(token: string) {
  accessToken = token;
  token ? localStorage.setItem(TOKEN_KEY, token) : localStorage.removeItem(TOKEN_KEY);
}

async function refreshAccessToken() {
  if (!refreshing) {
    refreshing = fetch(`${API_BASE}/auth/refresh`, { method: "POST", credentials: "include" })
      .then(async (response) => {
        if (!response.ok) return false;
        const result = (await response.json()) as ApiResponse<{ accessToken: string }>;
        if (!result.status || !result.data?.accessToken) return false;
        setAccessToken(result.data.accessToken);
        return true;
      })
      .catch(() => false)
      .finally(() => (refreshing = null));
  }
  return refreshing;
}

export async function request<T>(path: string, init: RequestInit = {}, retry = true): Promise<T> {
  const headers = new Headers(init.headers);
  if (accessToken) headers.set("Authorization", `Bearer ${accessToken}`);
  if (init.body && !(init.body instanceof FormData) && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  let response: Response;
  try {
    response = await fetch(`${API_BASE}${path}`, { ...init, headers, credentials: "include" });
  } catch {
    throw new Error("无法连接后端服务，请确认 mini-bilibili-server 已启动");
  }

  if (response.status === 401 && retry && path !== "/auth/refresh" && (await refreshAccessToken())) {
    return request<T>(path, init, false);
  }

  const result = (await response.json().catch(() => null)) as ApiResponse<T> | null;
  if (!response.ok || !result?.status) {
    if (response.status === 401 || response.status === 403) setAccessToken("");
    throw new Error(result?.message || `请求失败（${response.status}）`);
  }
  return result.data;
}
