<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { adminApi, type User } from "../api/admin";

const users = ref<User[]>([]);
const total = ref(0);
const loading = ref(false);
const error = ref("");
const notice = ref("");
const filters = reactive({ userName: "", status: "", pageNo: 1, pageSize: 10 });

async function load() {
  loading.value = true; error.value = "";
  try {
    const result = await adminApi.users({ userName: filters.userName || undefined, statusList: filters.status === "" ? undefined : [Number(filters.status)], pageNo: filters.pageNo, pageSize: filters.pageSize });
    users.value = result.list; total.value = result.total;
  } catch (reason) { error.value = reason instanceof Error ? reason.message : "用户加载失败"; }
  finally { loading.value = false; }
}
function search() { filters.pageNo = 1; void load(); }
async function remove(user: User) {
  if (!window.confirm(`确认删除用户“${user.name}”吗？此操作会调用后端删除接口。`)) return;
  try { await adminApi.deleteUser(user.id); notice.value = "用户已删除"; await load(); }
  catch (reason) { error.value = reason instanceof Error ? reason.message : "删除失败"; }
}
function changePage(delta: number) { filters.pageNo += delta; void load(); }
function formatDate(value?: string) { return value ? value.replace("T", " ").slice(0, 16) : "—"; }
onMounted(load);
</script>

<template>
  <div class="page-stack"><div class="section-lead"><div><h2>用户列表</h2><p>按昵称和状态查询用户；当前后端仅提供查询与删除管理能力。</p></div><span class="count-label">共 {{ total }} 位用户</span></div><form class="filter-card" @submit.prevent="search"><label><span>用户昵称</span><input v-model.trim="filters.userName" placeholder="输入昵称关键词" /></label><label><span>用户状态</span><select v-model="filters.status"><option value="">全部状态</option><option value="1">正常</option><option value="2">已禁用</option></select></label><button class="primary-button compact">查询</button><button type="button" class="ghost-button" @click="filters.userName = ''; filters.status = ''; search()">重置</button></form><div v-if="error" class="alert error-alert">{{ error }}</div><div v-if="notice" class="alert success-alert">{{ notice }}</div><section class="panel-card"><div class="table-wrap"><table><thead><tr><th>用户</th><th>联系方式</th><th>角色</th><th>状态</th><th>内容数据</th><th>注册时间</th><th class="align-right">操作</th></tr></thead><tbody><tr v-for="user in users" :key="user.id"><td><div class="user-cell"><span class="table-avatar">{{ user.name.slice(0, 1) }}</span><span><b>{{ user.name }}</b><small>ID {{ user.id }}</small></span></div></td><td><span class="stacked-text"><b>{{ user.email }}</b><small>{{ user.phone }}</small></span></td><td>{{ user.role === 2 ? '管理员' : '普通用户' }}</td><td><span class="status-chip" :class="user.status === 1 ? 'status-1' : 'status-2'">{{ user.status === 1 ? '正常' : '已禁用' }}</span></td><td>{{ user.videoCount }} 视频 · {{ user.fansCount }} 粉丝</td><td>{{ formatDate(user.createdAt) }}</td><td class="align-right"><button class="danger-link" :disabled="user.role === 2" :title="user.role === 2 ? '不允许在此删除管理员' : '删除用户'" @click="remove(user)">删除</button></td></tr><tr v-if="!loading && !users.length"><td colspan="7" class="empty-cell">没有符合条件的用户</td></tr></tbody></table></div><div class="pagination"><span>第 {{ filters.pageNo }} 页</span><div><button :disabled="filters.pageNo <= 1 || loading" @click="changePage(-1)">上一页</button><button :disabled="filters.pageNo * filters.pageSize >= total || loading" @click="changePage(1)">下一页</button></div></div></section></div>
</template>
