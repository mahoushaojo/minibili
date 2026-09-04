# MiniBili Admin

MiniBili 的独立 PC 管理后台，基于 Vue 3、TypeScript 和 Vite。

## 功能

- 管理员登录与 access token / refresh token 会话恢复
- 用户列表、筛选、分页与删除
- 视频列表、筛选、详情、审核发布与下架
- 分类新增、编辑、排序、启禁用与删除

## 本地运行

后端默认运行在 `http://localhost:8084`，本项目开发服务默认运行在 `http://localhost:5174`。

```bash
pnpm install
pnpm dev
```

若后端地址不同，可复制 `.env.example` 为 `.env.local` 并修改 `VITE_API_BASE_URL`。
