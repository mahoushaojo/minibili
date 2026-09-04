<script setup lang="ts">
import { computed, onMounted, ref, type Component } from "vue";
import { routes, useRouter, type RouteName } from "./router";
import { useAuthStore } from "./stores/auth";
import LoginView from "./views/LoginView.vue";
import DashboardView from "./views/DashboardView.vue";
import UsersView from "./views/UsersView.vue";
import VideosView from "./views/VideosView.vue";
import CategoriesView from "./views/CategoriesView.vue";

const auth = useAuthStore();
const router = useRouter();
const sidebarOpen = ref(false);
const components: Record<RouteName, Component> = {
  dashboard: DashboardView,
  users: UsersView,
  videos: VideosView,
  categories: CategoriesView,
};
const activeRoute = computed(() => routes.find((route) => route.name === router.currentRoute.value) ?? routes[0]);
const activeComponent = computed(() => components[router.currentRoute.value]);

function goTo(name: RouteName) {
  router.navigate(name);
  sidebarOpen.value = false;
}

onMounted(auth.restore);
</script>

<template>
  <div v-if="!auth.state.ready" class="app-loading"><span class="brand-mark">M</span><p>正在恢复管理会话…</p></div>
  <LoginView v-else-if="!auth.isAuthenticated.value" />
  <div v-else class="admin-shell">
    <button v-if="sidebarOpen" class="sidebar-mask" aria-label="关闭菜单" @click="sidebarOpen = false"></button>
    <aside class="sidebar" :class="{ open: sidebarOpen }">
      <div class="brand"><span class="brand-mark">M</span><div><b>MiniBili</b><small>ADMIN CONSOLE</small></div></div>
      <nav aria-label="后台导航">
        <button v-for="route in routes" :key="route.name" :class="{ active: router.currentRoute.value === route.name }" @click="goTo(route.name)">
          <span class="nav-icon">{{ route.icon }}</span><span><b>{{ route.label }}</b><small>{{ route.description }}</small></span>
        </button>
      </nav>
      <div class="sidebar-foot"><span class="admin-avatar">{{ auth.state.user?.name.slice(0, 1) }}</span><span><b>{{ auth.state.user?.name }}</b><small>管理员</small></span><button title="退出登录" @click="auth.logout">↗</button></div>
    </aside>
    <main class="main-panel">
      <header class="page-header"><button class="menu-button" aria-label="打开菜单" @click="sidebarOpen = true">☰</button><div><p>MINIBILI CONTROL CENTER</p><h1>{{ activeRoute.label }}</h1></div><div class="server-badge"><i></i><span>后端服务 · 8084</span></div></header>
      <section class="page-body"><component :is="activeComponent" /></section>
    </main>
  </div>
</template>
