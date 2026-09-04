import { readonly, ref } from "vue";

export type RouteName = "dashboard" | "users" | "videos" | "categories";

export interface AdminRoute {
  name: RouteName;
  label: string;
  description: string;
  icon: string;
}

export const routes: AdminRoute[] = [
  { name: "dashboard", label: "数据总览", description: "平台运营概况", icon: "⌂" },
  { name: "users", label: "用户管理", description: "查询与删除用户", icon: "◉" },
  { name: "videos", label: "视频管理", description: "审核发布与下架", icon: "▶" },
  { name: "categories", label: "分类管理", description: "维护投稿分类", icon: "▦" },
];

function routeFromHash(): RouteName {
  const value = window.location.hash.replace(/^#\/?/, "") as RouteName;
  return routes.some((route) => route.name === value) ? value : "dashboard";
}

const currentRoute = ref<RouteName>(routeFromHash());
window.addEventListener("hashchange", () => (currentRoute.value = routeFromHash()));

export function navigate(name: RouteName) {
  window.location.hash = `/${name}`;
  currentRoute.value = name;
}

export function useRouter() {
  return { currentRoute: readonly(currentRoute), navigate };
}
