<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { adminApi, type Video } from "../api/admin";

const loading = ref(true);
const error = ref("");
const counts = ref({ users: 0, videos: 0, categories: 0, pending: 0 });
const recentVideos = ref<Video[]>([]);
const pendingRate = computed(() => counts.value.videos ? Math.round((counts.value.pending / counts.value.videos) * 100) : 0);

async function load() {
  loading.value = true; error.value = "";
  try {
    const [users, videos, categories, pending] = await Promise.all([
      adminApi.users({ pageNo: 1, pageSize: 1 }),
      adminApi.videos({ pageNo: 1, pageSize: 5 }),
      adminApi.categories({ pageNo: 1, pageSize: 100 }),
      adminApi.videos({ statusList: [0], pageNo: 1, pageSize: 1 }),
    ]);
    counts.value = { users: users.total, videos: videos.total, categories: categories.total, pending: pending.total };
    recentVideos.value = videos.list;
  } catch (reason) {
    error.value = reason instanceof Error ? reason.message : "数据加载失败";
  } finally { loading.value = false; }
}

function statusText(status: number) { return ["待审核", "已发布", "已下架", "已删除"][status] ?? "未知"; }
function formatDate(value?: string) { return value ? value.replace("T", " ").slice(0, 16) : "—"; }
onMounted(load);
</script>

<template>
  <div class="page-stack">
    <div class="section-lead"><div><h2>平台概览</h2><p>快速查看 MiniBili 当前内容与用户状态。</p></div><button class="ghost-button" :disabled="loading" @click="load">刷新数据 ↻</button></div>
    <div v-if="error" class="alert error-alert">{{ error }}<button @click="load">重试</button></div>
    <div class="metric-grid">
      <article><span class="metric-icon purple">◉</span><div><small>用户总数</small><b>{{ loading ? '—' : counts.users }}</b></div><em>USER</em></article>
      <article><span class="metric-icon blue">▶</span><div><small>视频总数</small><b>{{ loading ? '—' : counts.videos }}</b></div><em>VIDEO</em></article>
      <article><span class="metric-icon orange">⌛</span><div><small>待审核视频</small><b>{{ loading ? '—' : counts.pending }}</b></div><em>{{ pendingRate }}%</em></article>
      <article><span class="metric-icon green">▦</span><div><small>内容分类</small><b>{{ loading ? '—' : counts.categories }}</b></div><em>CATEGORY</em></article>
    </div>
    <section class="panel-card"><div class="panel-title"><div><h3>最近投稿</h3><p>按创建时间显示最新视频。</p></div></div><div class="table-wrap"><table><thead><tr><th>作品</th><th>作者</th><th>状态</th><th>播放 / 点赞</th><th>创建时间</th></tr></thead><tbody><tr v-for="video in recentVideos" :key="video.id"><td><div class="media-cell"><img :src="video.cover" alt="" /><span><b>{{ video.title }}</b><small>ID {{ video.id }}</small></span></div></td><td>{{ video.userName || `用户 ${video.userId}` }}</td><td><span class="status-chip" :class="`status-${video.status}`">{{ statusText(video.status) }}</span></td><td>{{ video.viewCount }} / {{ video.linkCount }}</td><td>{{ formatDate(video.createdAt) }}</td></tr><tr v-if="!loading && !recentVideos.length"><td colspan="5" class="empty-cell">暂无视频数据</td></tr></tbody></table></div></section>
  </div>
</template>
