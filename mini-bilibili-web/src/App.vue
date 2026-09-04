<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { api, hasToken, setAccessToken, type Category, type CollectItem, type User, type Video } from "./api";

type Modal = "auth" | "detail" | "profile" | "upload" | "";
const videos = ref<Video[]>([]);
const currentUser = ref<User | null>(null);
const selectedVideo = ref<Video | null>(null);
const collections = ref<CollectItem[]>([]);
const myVideos = ref<Video[]>([]);
const modal = ref<Modal>("");
const loading = ref(false);
const detailLoading = ref(false);
const query = ref("");
const sortType = ref(1);
const error = ref("");
const toast = ref("");
const authMode = ref<"login" | "register">("login");
const authSubmitting = ref(false);
const uploadSubmitting = ref(false);
const categoryLoading = ref(false);
const categoryError = ref("");
const categories = ref<Category[]>([]);
const authForm = reactive({ name: "", phone: "", email: "", password: "" });
const uploadForm = reactive<{ title: string; desc: string; cover: string; linkUrl: string; duration: number; categoryIdList: string[] }>({ title: "", desc: "", cover: "", linkUrl: "", duration: 0, categoryIdList: [] });
let toastTimer: number | undefined;

const filteredVideos = computed(() => {
  const keyword = query.value.toLowerCase();
  return videos.value.filter((video) => !keyword || video.title.toLowerCase().includes(keyword) || (video.userName || "").toLowerCase().includes(keyword));
});
const sortTabs = [{ value: 1, label: "推荐" }, { value: 2, label: "热门" }, { value: 3, label: "最新" }];

function notify(message: string) {
  toast.value = message;
  window.clearTimeout(toastTimer);
  toastTimer = window.setTimeout(() => (toast.value = ""), 2200);
}
function formatCount(value = 0) { return value >= 10000 ? `${(value / 10000).toFixed(1)}万` : String(value); }
function formatDuration(seconds = 0) { return `${String(Math.floor(seconds / 60)).padStart(2, "0")}:${String(seconds % 60).padStart(2, "0")}`; }
function requireLogin(action?: () => void) {
  if (currentUser.value) { action?.(); return true; }
  authMode.value = "login"; modal.value = "auth"; notify("登录后即可继续"); return false;
}
async function loadVideos() {
  loading.value = true; error.value = "";
  try { videos.value = (await api.homeVideos(sortType.value)).list; }
  catch (reason) { error.value = reason instanceof Error ? reason.message : "视频加载失败"; videos.value = []; }
  finally { loading.value = false; }
}
async function restoreSession() {
  if (!hasToken()) return;
  try { currentUser.value = await api.currentUser(); } catch { setAccessToken(""); }
}
async function submitAuth() {
  authSubmitting.value = true;
  try {
    if (authMode.value === "register") { await api.register(authForm); notify("注册成功，请登录"); authMode.value = "login"; return; }
    const result = await api.login(authForm.email, authForm.password);
    setAccessToken(result.accessToken); currentUser.value = await api.currentUser(); modal.value = ""; notify(`欢迎回来，${result.userName}`);
  } catch (reason) { notify(reason instanceof Error ? reason.message : "操作失败"); }
  finally { authSubmitting.value = false; }
}
async function logout() {
  try { await api.logout(); } catch { setAccessToken(""); }
  currentUser.value = null; modal.value = ""; notify("已退出登录");
}
async function openDetail(video: Video) {
  selectedVideo.value = video; modal.value = "detail"; detailLoading.value = true;
  try { selectedVideo.value = await api.videoDetail(video.id); }
  catch (reason) { notify(reason instanceof Error ? reason.message : "详情加载失败"); }
  finally { detailLoading.value = false; }
}
async function toggleReaction(kind: "like" | "collect") {
  if (!selectedVideo.value || !requireLogin()) return;
  const video = selectedVideo.value;
  const field = kind === "like" ? "isLike" : "isCollect";
  const countField = kind === "like" ? "linkCount" : "collectCount";
  const next = !video[field];
  try {
    if (kind === "like") await api.likeVideo(video.id, next ? 1 : 0); else await api.collectVideo(video.id, next ? 1 : 0);
    video[field] = next; video[countField] = Math.max(0, video[countField] + (next ? 1 : -1));
    const card = videos.value.find((item) => item.id === video.id); if (card) card[countField] = video[countField];
    notify(next ? (kind === "like" ? "点赞成功" : "收藏成功") : (kind === "like" ? "已取消点赞" : "已取消收藏"));
  } catch (reason) { notify(reason instanceof Error ? reason.message : "操作失败"); }
}
async function openProfile() {
  if (!requireLogin()) return;
  modal.value = "profile";
  try {
    const [collectResult, pending, published, held] = await Promise.all([api.collections(), api.myVideos(0), api.myVideos(1), api.myVideos(2)]);
    collections.value = collectResult.list; myVideos.value = [...pending.list, ...published.list, ...held.list];
  } catch (reason) { notify(reason instanceof Error ? reason.message : "个人数据加载失败"); }
}
async function uploadFile(kind: "image" | "video", event: Event) {
  const file = (event.target as HTMLInputElement).files?.[0]; if (!file) return;
  try {
    notify(`${kind === "image" ? "封面" : "视频"}上传中…`);
    const url = await api.upload(kind, file);
    if (kind === "image") uploadForm.cover = url;
    else {
      uploadForm.linkUrl = url;
      const media = document.createElement("video"); media.preload = "metadata";
      media.onloadedmetadata = () => { uploadForm.duration = Math.max(1, Math.round(media.duration)); URL.revokeObjectURL(media.src); };
      media.src = URL.createObjectURL(file);
    }
    notify("上传成功");
  } catch (reason) { notify(reason instanceof Error ? reason.message : "上传失败"); }
}
async function submitVideo() {
  if (!uploadForm.cover || !uploadForm.linkUrl || !uploadForm.duration) { notify("请先上传封面和视频"); return; }
  if (!uploadForm.categoryIdList.length) { notify("请至少选择一个视频分类"); return; }
  uploadSubmitting.value = true;
  try { await api.publishVideo(uploadForm); Object.assign(uploadForm, { title: "", desc: "", cover: "", linkUrl: "", duration: 0, categoryIdList: [] }); modal.value = ""; notify("投稿已提交，等待审核"); }
  catch (reason) { notify(reason instanceof Error ? reason.message : "投稿失败"); }
  finally { uploadSubmitting.value = false; }
}
async function loadCategories() {
  categoryLoading.value = true; categoryError.value = "";
  try { categories.value = (await api.categories()).list; }
  catch (reason) { categoryError.value = reason instanceof Error ? reason.message : "分类加载失败"; }
  finally { categoryLoading.value = false; }
}
function openUpload() {
  requireLogin(() => {
    modal.value = "upload";
    if (!categories.value.length) void loadCategories();
  });
}
onMounted(async () => { await Promise.all([restoreSession(), loadVideos()]); });
</script>

<template>
  <main>
    <Transition name="toast"><div v-if="toast" class="toast">{{ toast }}</div></Transition>
    <header class="topbar">
      <a class="brand" href="#home"><span class="brand-mark">M</span><span>MiniBili</span><small>ミニビリ</small></a>
      <nav class="desktop-nav"><a href="#home">首页</a><a href="#discover">视频</a><button @click="notify('评论区等待后端接口')">动态</button></nav>
      <label class="search"><span>⌕</span><input v-model.trim="query" placeholder="搜索视频或 UP 主" aria-label="搜索" /></label>
      <div class="actions"><button class="avatar-button" aria-label="个人中心" @click="openProfile">{{ currentUser?.name?.slice(0, 1) || "登" }}</button><button class="upload-button" @click="openUpload">＋ 投稿</button></div>
    </header>
    <section id="home" class="hero">
      <img src="/minibili-hero.png" alt="创作者在城市天台拍摄视频" /><div class="hero-overlay"></div>
      <div class="hero-copy"><p class="eyebrow">MINIBILI CREATOR COMMUNITY</p><h1>让热爱，<br /><em>被世界看见。</em></h1><p>真实视频、真实创作者、真实互动。<br />从这一刻开始，发现你的同频伙伴。</p><div class="hero-buttons"><a class="primary" href="#discover">开始探索 <span>→</span></a><button class="glass" @click="openUpload">成为创作者</button></div></div>
    </section>
    <section id="discover" class="content-section">
      <div class="section-heading"><div><span class="spark">✦</span><h2>发现好内容</h2><p>内容来自 MiniBili 后端，按推荐、热度或发布时间排序。</p></div><button :disabled="loading" @click="loadVideos">刷新 ↻</button></div>
      <div class="filter-row"><button v-for="tab in sortTabs" :key="tab.value" :class="{ selected: sortType === tab.value }" @click="sortType = tab.value; loadVideos()">{{ tab.label }}</button></div>
      <div v-if="loading" class="state-card">正在加载视频…</div>
      <div v-else-if="error" class="state-card error-state"><b>暂时无法获取视频</b><span>{{ error }}</span><button @click="loadVideos">重新加载</button></div>
      <div v-else-if="filteredVideos.length" class="video-grid">
        <article v-for="video in filteredVideos" :key="video.id" class="video-card"><button class="thumbnail" @click="openDetail(video)"><img :src="video.cover" :alt="video.title" loading="lazy" /><span class="play">▶</span><span class="duration">{{ formatDuration(video.duration) }}</span></button><div class="video-info"><h3 @click="openDetail(video)">{{ video.title }}</h3><div class="creator-row"><span class="creator-avatar">{{ (video.userName || 'U').slice(0, 1) }}</span><span>{{ video.userName || 'MiniBili 用户' }}</span><small>▶ {{ formatCount(video.viewCount) }} · ♥ {{ formatCount(video.linkCount) }}</small></div></div></article>
      </div>
      <div v-else class="state-card"><b>还没有已发布的视频</b><span>登录后发布第一支作品吧。</span></div>
    </section>
    <section class="creator-banner"><div><span class="mini-label">CREATOR PLAN</span><h2>你的灵感，值得一个舞台。</h2><p>上传视频与封面、填写作品信息，提交后等待管理员审核发布。</p></div><button @click="openUpload">立即投稿 <span>→</span></button></section>
    <footer><div class="brand footer-brand"><span class="brand-mark">M</span><span>MiniBili</span></div><p>一个由热爱与创造力组成的小小宇宙。</p><small>MiniBili 学习项目 © 2026</small></footer>

    <div v-if="modal" class="modal-backdrop" @click.self="modal = ''">
      <section v-if="modal === 'auth'" class="modal auth-modal"><button class="close" @click="modal = ''">×</button><span class="mini-label">WELCOME TO MINIBILI</span><h2>{{ authMode === 'login' ? '登录 MiniBili' : '创建账号' }}</h2><form @submit.prevent="submitAuth"><label v-if="authMode === 'register'">昵称<input v-model.trim="authForm.name" required maxlength="30" /></label><label v-if="authMode === 'register'">手机号<input v-model.trim="authForm.phone" required /></label><label>邮箱<input v-model.trim="authForm.email" type="email" required /></label><label>密码<input v-model="authForm.password" type="password" required minlength="6" maxlength="20" /></label><button class="submit-button" :disabled="authSubmitting">{{ authSubmitting ? '请稍候…' : (authMode === 'login' ? '登录' : '注册') }}</button></form><button class="text-button" @click="authMode = authMode === 'login' ? 'register' : 'login'">{{ authMode === 'login' ? '没有账号？立即注册' : '已有账号？返回登录' }}</button></section>
      <section v-else-if="modal === 'detail' && selectedVideo" class="modal detail-modal"><button class="close" @click="modal = ''">×</button><div class="player"><video v-if="selectedVideo.linkUrl" :src="selectedVideo.linkUrl" :poster="selectedVideo.cover" controls preload="metadata"></video><img v-else :src="selectedVideo.cover" :alt="selectedVideo.title" /></div><div class="detail-copy"><small v-if="detailLoading">正在同步详情…</small><h2>{{ selectedVideo.title }}</h2><p>{{ selectedVideo.desc || '作者暂未填写视频简介。' }}</p><div class="detail-meta"><span>▶ {{ formatCount(selectedVideo.viewCount) }}</span><span>评论 {{ formatCount(selectedVideo.commentCount) }}</span><span>{{ selectedVideo.userInfo?.name || selectedVideo.userName || 'MiniBili 用户' }}</span></div><div class="reaction-row"><button :class="{ active: selectedVideo.isLike }" @click="toggleReaction('like')">♥ {{ selectedVideo.isLike ? '已点赞' : '点赞' }} {{ formatCount(selectedVideo.linkCount) }}</button><button :class="{ active: selectedVideo.isCollect }" @click="toggleReaction('collect')">★ {{ selectedVideo.isCollect ? '已收藏' : '收藏' }} {{ formatCount(selectedVideo.collectCount) }}</button></div><div class="comment-placeholder"><b>评论区</b><span>后端尚未提供评论接口，暂时无法读取或发布评论。</span></div></div></section>
      <section v-else-if="modal === 'profile' && currentUser" class="modal profile-modal"><button class="close" @click="modal = ''">×</button><div class="profile-head"><div class="profile-avatar">{{ currentUser.name.slice(0, 1) }}</div><div><span class="mini-label">MY MINIBILI</span><h2>{{ currentUser.name }}</h2><p>{{ currentUser.bio || currentUser.email }}</p></div><button class="text-button" @click="logout">退出登录</button></div><div class="stats"><span><b>{{ currentUser.videoCount }}</b>视频</span><span><b>{{ currentUser.collectCount }}</b>收藏</span><span><b>{{ currentUser.fansCount }}</b>粉丝</span><span><b>{{ currentUser.interestCount }}</b>关注</span></div><h3>我的作品</h3><div v-if="myVideos.length" class="mini-list"><button v-for="video in myVideos" :key="video.id" @click="openDetail(video)"><img :src="video.cover" alt="" /><span><b>{{ video.title }}</b><small>状态：{{ ['待审核', '已发布', '已下架', '已删除'][video.status] || video.status }}</small></span></button></div><p v-else class="muted">暂无作品</p><h3>我的收藏</h3><div v-if="collections.length" class="mini-list"><button v-for="item in collections" :key="item.id" @click="openDetail({ id: item.videoId, title: item.title, cover: item.cover } as Video)"><img :src="item.cover" alt="" /><span><b>{{ item.title }}</b><small>{{ item.userName }}</small></span></button></div><p v-else class="muted">暂无收藏</p></section>
      <section v-else-if="modal === 'upload'" class="modal upload-modal"><button class="close" @click="modal = ''">×</button><span class="mini-label">CREATOR STUDIO</span><h2>发布新作品</h2><form @submit.prevent="submitVideo"><label>视频标题<input v-model.trim="uploadForm.title" required maxlength="100" placeholder="给作品起一个好名字" /></label><label>作品简介<textarea v-model.trim="uploadForm.desc" maxlength="255" rows="3" placeholder="介绍一下你的作品"></textarea></label><fieldset class="category-field"><legend>视频分类 <small>至少选择一个</small></legend><div v-if="categoryLoading" class="category-state">正在加载分类…</div><div v-else-if="categoryError" class="category-state category-error"><span>{{ categoryError }}</span><button type="button" @click="loadCategories">重新加载</button></div><div v-else-if="categories.length" class="category-options"><label v-for="category in categories" :key="category.id" :class="{ selected: uploadForm.categoryIdList.includes(category.id) }"><input v-model="uploadForm.categoryIdList" type="checkbox" :value="category.id" /><span>{{ category.name }}</span></label></div><div v-else class="category-state">暂无可用分类，请联系管理员创建或启用分类。</div></fieldset><div class="file-grid"><label class="file-box">上传封面<input type="file" accept="image/*" @change="uploadFile('image', $event)" /><span>{{ uploadForm.cover ? '✓ 已上传' : '选择图片' }}</span></label><label class="file-box">上传视频<input type="file" accept="video/mp4,video/webm" @change="uploadFile('video', $event)" /><span>{{ uploadForm.linkUrl ? `✓ 已上传 · ${formatDuration(uploadForm.duration)}` : 'MP4 / WebM，最大 500MB' }}</span></label></div><button class="submit-button" :disabled="uploadSubmitting || categoryLoading || !categories.length">{{ uploadSubmitting ? '提交中…' : '提交审核' }}</button></form></section>
    </div>
  </main>
</template>
