<script setup lang="ts">
import { reactive, ref } from "vue";
import { useAuthStore } from "../stores/auth";

const auth = useAuthStore();
const form = reactive({ email: "", password: "" });
const error = ref("");

async function submit() {
  error.value = "";
  try {
    await auth.login(form.email, form.password);
  } catch (reason) {
    error.value = reason instanceof Error ? reason.message : "登录失败";
  }
}
</script>

<template>
  <main class="login-page">
    <section class="login-visual"><div class="visual-grid"></div><div class="visual-copy"><span>MINIBILI MANAGEMENT</span><h1>让每一份好内容，<br />被认真看见。</h1><p>集中管理用户、视频审核与内容分类，保持社区健康运转。</p></div><div class="visual-orbit orbit-one"></div><div class="visual-orbit orbit-two"></div></section>
    <section class="login-panel"><div class="login-box"><div class="brand login-brand"><span class="brand-mark">M</span><div><b>MiniBili</b><small>ADMIN CONSOLE</small></div></div><p class="eyebrow">WELCOME BACK</p><h2>管理员登录</h2><p class="login-tip">请使用 role=2 的管理员账号登录。</p><form @submit.prevent="submit"><label>邮箱地址<input v-model.trim="form.email" type="email" autocomplete="username" required placeholder="admin@example.com" /></label><label>登录密码<input v-model="form.password" type="password" autocomplete="current-password" required placeholder="请输入密码" /></label><p v-if="error" class="form-error">{{ error }}</p><button class="primary-button" :disabled="auth.state.submitting">{{ auth.state.submitting ? '正在验证…' : '进入管理后台' }}</button></form><small class="login-footer">MiniBili 内容管理系统 · 安全登录</small></div></section>
  </main>
</template>
