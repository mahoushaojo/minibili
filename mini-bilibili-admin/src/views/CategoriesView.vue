<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { adminApi, type Category } from "../api/admin";

const categories = ref<Category[]>([]);
const total = ref(0);
const loading = ref(false);
const error = ref("");
const notice = ref("");
const filters = reactive({ name: "", status: "" });
const editor = reactive({ open: false, id: "", name: "", sort: 0 });
const submitting = ref(false);

async function load() {
  loading.value = true; error.value = "";
  try {
    const result = await adminApi.categories({ name: filters.name || undefined, status: filters.status === "" ? undefined : Number(filters.status), pageNo: 1, pageSize: 100 });
    categories.value = result.list; total.value = result.total;
  } catch (reason) { error.value = reason instanceof Error ? reason.message : "分类加载失败"; }
  finally { loading.value = false; }
}
function openCreate() { Object.assign(editor, { open: true, id: "", name: "", sort: categories.value.length ? Math.max(...categories.value.map((item) => item.sort)) + 1 : 1 }); }
function openEdit(category: Category) { Object.assign(editor, { open: true, id: category.id, name: category.name, sort: category.sort }); }
async function save() {
  submitting.value = true; error.value = "";
  try {
    if (editor.id) {
      const current = categories.value.find((item) => item.id === editor.id);
      await adminApi.editCategory(editor.id, editor.name);
      if (current?.sort !== editor.sort) await adminApi.sortCategory(editor.id, editor.sort);
      notice.value = "分类已更新";
    } else {
      await adminApi.addCategory(editor.name, editor.sort); notice.value = "分类已创建";
    }
    editor.open = false; await load();
  } catch (reason) { error.value = reason instanceof Error ? reason.message : "保存失败"; }
  finally { submitting.value = false; }
}
async function toggleStatus(category: Category) {
  try { await adminApi.setCategoryStatus(category.id, category.status === 1 ? 0 : 1); notice.value = category.status === 1 ? "分类已禁用" : "分类已启用"; await load(); }
  catch (reason) { error.value = reason instanceof Error ? reason.message : "状态修改失败"; }
}
async function remove(category: Category) {
  if (!window.confirm(`确认删除分类“${category.name}”吗？后端仅允许删除已禁用分类。`)) return;
  try { await adminApi.deleteCategory(category.id); notice.value = "分类已删除"; await load(); }
  catch (reason) { error.value = reason instanceof Error ? reason.message : "删除失败"; }
}
onMounted(load);
</script>

<template>
  <div class="page-stack"><div class="section-lead"><div><h2>视频分类</h2><p>这里的启用分类会供 C 端发布作品时选择。</p></div><button class="primary-button compact" @click="openCreate">＋ 新增分类</button></div><form class="filter-card" @submit.prevent="load"><label><span>分类名称</span><input v-model.trim="filters.name" placeholder="输入分类名称" /></label><label><span>使用状态</span><select v-model="filters.status"><option value="">全部状态</option><option value="1">已启用</option><option value="0">已禁用</option></select></label><button class="primary-button compact">查询</button><button type="button" class="ghost-button" @click="filters.name = ''; filters.status = ''; load()">重置</button></form><div v-if="error" class="alert error-alert">{{ error }}</div><div v-if="notice" class="alert success-alert">{{ notice }}</div><section class="panel-card"><div class="panel-title"><div><h3>分类列表</h3><p>共 {{ total }} 个分类，排序值越大越靠前。</p></div></div><div class="category-grid"><article v-for="category in categories" :key="category.id"><div class="category-symbol">{{ category.name.slice(0, 1) }}</div><div class="category-copy"><span class="status-dot" :class="{ disabled: category.status === 0 }">{{ category.status === 1 ? '已启用' : '已禁用' }}</span><h3>{{ category.name }}</h3><p>排序值 {{ category.sort }} · ID {{ category.id }}</p></div><div class="card-actions"><button @click="openEdit(category)">编辑</button><button @click="toggleStatus(category)">{{ category.status === 1 ? '禁用' : '启用' }}</button><button class="danger-link" @click="remove(category)">删除</button></div></article><p v-if="!loading && !categories.length" class="empty-grid">没有符合条件的分类</p></div></section><div v-if="editor.open" class="modal-mask" @click.self="editor.open = false"><section class="edit-dialog"><button class="dialog-close" @click="editor.open = false">×</button><p class="eyebrow">CATEGORY EDITOR</p><h3>{{ editor.id ? '编辑分类' : '新增分类' }}</h3><form @submit.prevent="save"><label>分类名称<input v-model.trim="editor.name" required maxlength="30" placeholder="例如：科技" /></label><label>排序值<input v-model.number="editor.sort" type="number" required min="0" step="1" /></label><div class="dialog-actions"><button type="button" class="ghost-button" @click="editor.open = false">取消</button><button class="primary-button compact" :disabled="submitting">{{ submitting ? '保存中…' : '保存' }}</button></div></form></section></div></div>
</template>
