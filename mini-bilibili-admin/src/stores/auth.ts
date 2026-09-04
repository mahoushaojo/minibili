import { computed, reactive, readonly } from "vue";
import { adminApi, type User } from "../api/admin";
import { getAccessToken, setAccessToken } from "../api/client";

const state = reactive({
  user: null as User | null,
  ready: false,
  submitting: false,
});

async function loadAdmin() {
  const user = await adminApi.currentUser();
  if (user.role !== 2) {
    setAccessToken("");
    throw new Error("当前账号不是管理员，无法进入管理后台");
  }
  state.user = user;
}

async function restore() {
  if (state.ready) return;
  try {
    if (getAccessToken()) await loadAdmin();
  } catch {
    setAccessToken("");
    state.user = null;
  } finally {
    state.ready = true;
  }
}

async function login(email: string, password: string) {
  state.submitting = true;
  try {
    const result = await adminApi.login(email, password);
    setAccessToken(result.accessToken);
    await loadAdmin();
  } catch (error) {
    setAccessToken("");
    state.user = null;
    throw error;
  } finally {
    state.submitting = false;
  }
}

async function logout() {
  try {
    await adminApi.logout();
  } finally {
    setAccessToken("");
    state.user = null;
  }
}

export function useAuthStore() {
  return {
    state: readonly(state),
    isAuthenticated: computed(() => Boolean(state.user)),
    restore,
    login,
    logout,
  };
}
