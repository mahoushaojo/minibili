const API_BASE = import.meta.env.VITE_API_BASE_URL || "/api";
const TOKEN_KEY = "mini_bili_access_token";

export interface ApiResponse<T> {
  code: number;
  message: string;
  status: boolean;
  data: T;
}

export interface PageResult<T> {
  list: T[];
  total: number;
}

export interface LoginResult {
  accessToken: string;
  userId: string;
  userName: string;
}

export interface User {
  id: string;
  name: string;
  phone: string;
  email: string;
  avatar?: string;
  bio?: string;
  likeCount: number;
  collectCount: number;
  fansCount: number;
  interestCount: number;
  videoCount: number;
}

export interface Video {
  id: string;
  userId: string;
  userName?: string;
  userInfo?: User;
  title: string;
  cover: string;
  duration: number;
  desc?: string;
  status: number;
  linkUrl: string;
  linkCount: number;
  viewCount: number;
  collectCount: number;
  commentCount: number;
  isLike?: boolean;
  isCollect?: boolean;
  publishAt?: string;
}

export interface Category {
  id: string;
  name: string;
  sort: number;
  status: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface CollectItem {
  id: string;
  userId: string;
  videoId: string;
  title: string;
  cover: string;
  userName: string;
}

let accessToken = localStorage.getItem(TOKEN_KEY) || "";
let refreshing: Promise<boolean> | null = null;

export function hasToken() {
  return Boolean(accessToken);
}

export function setAccessToken(token: string) {
  accessToken = token;
  token ? localStorage.setItem(TOKEN_KEY, token) : localStorage.removeItem(TOKEN_KEY);
}

async function refreshAccessToken() {
  if (!refreshing) {
    refreshing = fetch(`${API_BASE}/auth/refresh`, {
      method: "POST",
      credentials: "include",
    })
      .then(async (response) => {
        if (!response.ok) return false;
        const result = (await response.json()) as ApiResponse<LoginResult>;
        if (!result.status || !result.data?.accessToken) return false;
        setAccessToken(result.data.accessToken);
        return true;
      })
      .catch(() => false)
      .finally(() => (refreshing = null));
  }
  return refreshing;
}

async function request<T>(path: string, init: RequestInit = {}, retry = true): Promise<T> {
  const headers = new Headers(init.headers);
  if (accessToken) headers.set("Authorization", `Bearer ${accessToken}`);
  if (!(init.body instanceof FormData) && init.body && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  let response: Response;
  try {
    response = await fetch(`${API_BASE}${path}`, { ...init, headers, credentials: "include" });
  } catch {
    throw new Error("无法连接后端服务，请确认服务已启动");
  }

  if (response.status === 401 && retry && path !== "/auth/refresh" && (await refreshAccessToken())) {
    return request<T>(path, init, false);
  }

  const result = (await response.json().catch(() => null)) as ApiResponse<T> | null;
  if (!response.ok || !result?.status) {
    if (response.status === 401) setAccessToken("");
    throw new Error(result?.message || `请求失败（${response.status}）`);
  }
  return result.data;
}

export const api = {
  login: (email: string, password: string) =>
    request<LoginResult>("/auth/login", { method: "POST", body: JSON.stringify({ email, password }) }),
  register: (payload: { name: string; phone: string; email: string; password: string }) =>
    request<number>("/user/register", { method: "POST", body: JSON.stringify(payload) }),
  logout: async () => {
    try {
      await request<boolean>("/auth/loginOut", { method: "POST" }, false);
    } finally {
      setAccessToken("");
    }
  },
  currentUser: () => request<User>("/user/info"),
  homeVideos: (type: number, pageNo = 1, pageSize = 12) =>
    request<PageResult<Video>>("/video/homeList", {
      method: "POST",
      body: JSON.stringify({ type, pageNo, pageSize }),
    }),
  videoDetail: (vid: string) => request<Video>(`/video/detail?vid=${encodeURIComponent(vid)}`),
  likeVideo: (vid: string, type: 0 | 1) =>
    request<boolean>(`/video/like?vid=${encodeURIComponent(vid)}&type=${type}`),
  collectVideo: (vid: string, type: 0 | 1) =>
    request<boolean>(`/video/collect?vid=${encodeURIComponent(vid)}&type=${type}`),
  collections: (keyword = "", pageNo = 1, pageSize = 20) =>
    request<PageResult<CollectItem>>("/user/collectList", {
      method: "POST",
      body: JSON.stringify({ keyword, pageNo, pageSize }),
    }),
  myVideos: (status: number, pageNo = 1, pageSize = 20) =>
    request<PageResult<Video>>("/video/list", {
      method: "POST",
      body: JSON.stringify({ status, pageNo, pageSize }),
    }),
  categories: (status = 1, pageNo = 1, pageSize = 100) =>
    request<PageResult<Category>>("/category/list", {
      method: "POST",
      body: JSON.stringify({ status, name: "", pageNo, pageSize }),
    }),
  upload: async (kind: "image" | "video", file: File) => {
    const body = new FormData();
    body.append("file", file);
    return request<string>(`/upload/${kind}`, { method: "POST", body });
  },
  publishVideo: (payload: { title: string; cover: string; linkUrl: string; duration: number; desc: string; categoryIdList: string[] }) =>
    request<string>("/video/add", { method: "POST", body: JSON.stringify(payload) }),
};
