<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { adminApi, type Video } from "../api/admin";

const videos = ref<Video[]>([]);
const total = ref(0);
const loading = ref(false);
const error = ref("");
const notice = ref("");
const selectedVideo = ref<Video | null>(null);
const filters = reactive({ title: "", status: "", pageNo: 1, pageSize: 10 });
const busyId = ref("");

async function load() {
  loading.value = true; error.value = "";
  try {
    const result = await adminApi.videos({ title: filters.title || undefined, statusList: filters.status === "" ? undefined : [Number(filters.status)], pageNo: filters.pageNo, pageSize: filters.pageSize });
    videos.value = result.list; total.value = result.total;
  } catch (reason) { error.value = reason instanceof Error ? reason.message : "视频加载失败"; }
  finally { loading.value = false; }
}
function search() { filters.pageNo = 1; void load(); }
async function changeStatus(video: Video, action: "publish" | "holdout") {
  const label = action === "publish" ? "发布" : "下架";
  if (!window.confirm(`确认${label}视频“${video.title}”吗？`)) return;
  busyId.value = video.id; error.value = "";
  try {
    if (action === "publish") await adminApi.publishVideo(video.id); else await adminApi.holdoutVideo(video.id);
    notice.value = `视频已${label}`; await load();
  } catch (reason) { error.value = reason instanceof Error ? reason.message : `${label}失败`; }
  finally { busyId.value = ""; }
}
function statusText(status: number) { return ["待审核", "已发布", "已下架", "已删除"][status] ?? "未知"; }
function formatDate(value?: string) { return value ? value.replace("T", " ").slice(0, 16) : "—"; }
function formatDuration(seconds = 0) { return `${String(Math.floor(seconds / 60)).padStart(2, "0")}:${String(seconds % 60).padStart(2, "0")}`; }
function changePage(delta: number) { filters.pageNo += delta; void load(); }
onMounted(load);
</script>

<template>
  <div class="page-stack"><div class="section-lead"><div><h2>视频审核与管理</h2><p>查看投稿信息，并按后端状态规则发布或下架视频。</p></div><span class="count-label">共 {{ total }} 条视频</span></div><form class="filter-card" @submit.prevent="search"><label><span>视频标题</span><input v-model.trim="filters.title" placeholder="输入标题关键词" /></label><label><span>视频状态</span><select v-model="filters.status"><option value="">全部状态</option><option value="0">待审核</option><option value="1">已发布</option><option value="2">已下架</option><option value="3">已删除</option></select></label><button class="primary-button compact">查询</button><button type="button" class="ghost-button" @click="filters.title = ''; filters.status = ''; search()">重置</button></form><div v-if="error" class="alert error-alert">{{ error }}</div><div v-if="notice" class="alert success-alert">{{ notice }}</div><section class="panel-card"><div class="table-wrap"><table><thead><tr><th>作品</th><th>作者</th><th>分类</th><th>数据</th><th>状态</th><th>创建时间</th><th class="align-right">操作</th></tr></thead><tbody><tr v-for="video in videos" :key="video.id"><td><button class="media-cell media-button" @click="selectedVideo = video"><img :src="video.cover" alt="" /><span><b>{{ video.title }}</b><small>{{ formatDuration(video.duration) }} · ID {{ video.id }}</small></span></button></td><td>{{ video.userName || `用户 ${video.userId}` }}</td><td><div class="tag-list"><span v-for="category in video.categoryList" :key="category.id">{{ category.categoryName }}</span><small v-if="!video.categoryList?.length">未分类</small></div></td><td><span class="stacked-text"><b>▶ {{ video.viewCount }}</b><small>赞 {{ video.linkCount }} · 藏 {{ video.collectCount }}</small></span></td><td><span class="status-chip" :class="`status-${video.status}`">{{ statusText(video.status) }}</span></td><td>{{ formatDate(video.createdAt) }}</td><td class="align-right"><button v-if="video.status === 0" class="action-link" :disabled="busyId === video.id" @click="changeStatus(video, 'publish')">发布</button><button v-else-if="video.status === 1" class="danger-link" :disabled="busyId === video.id" @click="changeStatus(video, 'holdout')">下架</button><span v-else class="muted-text">暂无操作</span></td></tr><tr v-if="!loading && !videos.length"><td colspan="7" class="empty-cell">没有符合条件的视频</td></tr></tbody></table></div><div class="pagination"><span>第 {{ filters.pageNo }} 页</span><div><button :disabled="filters.pageNo <= 1 || loading" @click="changePage(-1)">上一页</button><button :disabled="filters.pageNo * filters.pageSize >= total || loading" @click="changePage(1)">下一页</button></div></div></section><div v-if="selectedVideo" class="modal-mask" @click.self="selectedVideo = null"><section class="detail-dialog"><button class="dialog-close" @click="selectedVideo = null">×</button><img :src="selectedVideo.cover" :alt="selectedVideo.title" /><div><span class="status-chip" :class="`status-${selectedVideo.status}`">{{ statusText(selectedVideo.status) }}</span><h3>{{ selectedVideo.title }}</h3><p>{{ selectedVideo.desc || '作者未填写视频简介。' }}</p><dl><div><dt>作者</dt><dd>{{ selectedVideo.userName || selectedVideo.userId }}</dd></div><div><dt>时长</dt><dd>{{ formatDuration(selectedVideo.duration) }}</dd></div><div><dt>分类</dt><dd>{{ selectedVideo.categoryList?.map(item => item.categoryName).join('、') || '未分类' }}</dd></div><div><dt>创建时间</dt><dd>{{ formatDate(selectedVideo.createdAt) }}</dd></div></dl></div></section></div></div>
</template>
