import { request } from "./client";

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
  status: number;
  role: number;
  avatar?: string;
  bio?: string;
  likeCount: number;
  interestCount: number;
  collectCount: number;
  fansCount: number;
  videoCount: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface VideoCategory {
  id: string;
  videoId: string;
  categoryId: string;
  categoryName: string;
}

export interface Video {
  id: string;
  userId: string;
  userName?: string;
  title: string;
  cover: string;
  duration: number;
  desc?: string;
  status: number;
  linkUrl?: string;
  linkUrk?: string;
  linkCount: number;
  viewCount: number;
  collectCount: number;
  commentCount: number;
  categoryList: VideoCategory[];
  publishAt?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Category {
  id: string;
  name: string;
  sort: number;
  status: number;
  createdAt?: string;
  updatedAt?: string;
}

export const adminApi = {
  login: (email: string, password: string) =>
    request<LoginResult>("/auth/login", { method: "POST", body: JSON.stringify({ email, password }) }),
  logout: () => request<boolean>("/auth/loginOut", { method: "POST" }, false),
  currentUser: () => request<User>("/user/info"),
  users: (payload: { userName?: string; statusList?: number[]; pageNo: number; pageSize: number }) =>
    request<PageResult<User>>("/user/list", { method: "POST", body: JSON.stringify(payload) }),
  deleteUser: (id: string) => request<number>(`/user/delete/${encodeURIComponent(id)}`, { method: "DELETE" }),
  videos: (payload: { title?: string; statusList?: number[]; pageNo: number; pageSize: number }) =>
    request<PageResult<Video>>("/admin/video/list", { method: "POST", body: JSON.stringify(payload) }),
  publishVideo: (id: string) => request<boolean>(`/admin/video/publish?vid=${encodeURIComponent(id)}`),
  holdoutVideo: (id: string) => request<boolean>(`/admin/video/holdout?vid=${encodeURIComponent(id)}`),
  categories: (payload: { name?: string; status?: number; pageNo: number; pageSize: number }) =>
    request<PageResult<Category>>("/admin/category/list", { method: "POST", body: JSON.stringify(payload) }),
  addCategory: (name: string, sort: number) =>
    request<string>("/admin/category/add", { method: "POST", body: JSON.stringify({ name, sort }) }),
  editCategory: (id: string, name: string) =>
    request<boolean>("/admin/category/edit", { method: "POST", body: JSON.stringify({ id, name }) }),
  sortCategory: (id: string, sort: number) =>
    request<boolean>("/admin/category/sort", { method: "POST", body: JSON.stringify({ id, sort }) }),
  setCategoryStatus: (id: string, status: number) =>
    request<boolean>("/admin/category/set", { method: "POST", body: JSON.stringify({ id, status }) }),
  deleteCategory: (id: string) =>
    request<boolean>(`/admin/category/delete/${encodeURIComponent(id)}`, { method: "DELETE" }),
};
