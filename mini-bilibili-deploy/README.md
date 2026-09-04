# Mini Bilibili Docker 部署

这个目录只负责部署编排，三个应用仍然保持独立：

- `mini-bilibili-server`：Spring Boot 后端
- `mini-bilibili-web`：C 端 Vue 应用
- `mini-bilibili-admin`：管理端 Vue 应用
- `mini-bilibili-deploy`：MySQL、Redis、三个应用和可选 HTTPS 网关

## 1. 首次准备

确保四个目录处于同一级：

```text
minibili/
├── mini-bilibili-server/
├── mini-bilibili-web/
├── mini-bilibili-admin/
└── mini-bilibili-deploy/
```

进入当前目录并创建本地配置：

```bash
cp .env.example .env
```

编辑 `.env`，至少替换下面的值：

- MySQL root 密码和应用密码
- Redis 密码
- JWT 密钥（至少 32 个随机字符）
- OSS RAM 用户 AccessKey
- OSS Bucket 和 CDN 域名

不要提交 `.env`。示例文件只能包含占位符。

## 2. 已有数据库先备份

Compose 中的 MySQL 使用新的 Docker Volume，默认不会自动包含原有容器的数据。如果现有数据库有数据，请先导出：

```bash
docker exec EXISTING_MYSQL_CONTAINER mysqldump -uroot -p --databases mini_bili > backups/mini_bili.sql
```

确认备份文件可用后，再把它导入新 MySQL。不要在没有备份的情况下删除旧 MySQL 容器或旧 Volume。

项目当前的 Flyway 历史需要先在数据库副本中验证，所以 `.env.example` 默认设置：

```env
FLYWAY_ENABLED=false
```

已有数据库暂时保持 `false`。全新空数据库可以在验证迁移脚本后改成 `true`。

## 3. 本地构建和启动

检查最终 Compose 配置：

```bash
docker compose config --quiet
```

构建三个应用镜像：

```bash
docker compose build
```

启动本地环境：

```bash
docker compose up -d
```

查看状态：

```bash
docker compose ps
```

默认地址：

- C 端：http://localhost:8080
- 管理端：http://localhost:8081
- 后端调试：http://localhost:8084
- 本机连接 Compose MySQL：`localhost:3307`
- 本机连接 Compose Redis：`localhost:6380`

容器之间不使用这些宿主机端口。后端通过 `mysql:3306`、`redis:6379` 访问依赖，前端 Nginx 通过 `server:8084` 访问后端。

## 4. 常用命令

查看全部日志：

```bash
docker compose logs -f
```

只看后端日志：

```bash
docker compose logs -f server
```

修改后端后重新构建：

```bash
docker compose up -d --build server
```

修改 C 端或管理端后重新构建：

```bash
docker compose up -d --build web
docker compose up -d --build admin
```

停止并删除容器，但保留 MySQL、Redis 数据：

```bash
docker compose down
```

不要随意执行 `docker compose down -v`，`-v` 会删除数据库和 Redis 的 Docker Volume。

## 5. 正式环境 HTTPS

正式服务器的 `.env` 至少修改：

```env
COOKIE_SECURE=true
WEB_DOMAIN=www.your-domain.com
ADMIN_DOMAIN=admin.your-domain.com
OSS_PUBLIC_DOMAIN=https://media.your-domain.com
```

将两个业务域名的 A/AAAA 记录指向服务器，并确保安全组开放 80 和 443。然后启动生产网关：

```bash
docker compose --profile production up -d --build
```

Caddy 会自动申请和续期 HTTPS 证书。MySQL、Redis、Java 后端以及本地调试端口都绑定到 `127.0.0.1`，不会直接暴露到公网。

CDN 媒体域名继续按阿里云提供的 CNAME 配置，不指向本机 Caddy。

## 6. 发布检查

每次发布至少检查：

1. `docker compose ps` 中所有服务正常运行。
2. C 端和管理端能打开。
3. 登录、刷新 Token、退出登录正常。
4. 普通用户不能访问 `/admin/**`。
5. 图片、视频上传正常，返回 HTTPS CDN 地址。
6. 视频大小超过 Nginx 限制时能得到明确提示。
7. 重启 Compose 后 MySQL 数据仍存在。
8. 数据库已经完成可恢复的异机备份。

## 7. 安全提醒

- 正式环境必须使用新的 OSS RAM AccessKey，并限制到指定 Bucket 的最小权限。
- 如果 AccessKey 曾出现在代码、示例文件或 Git 历史中，应立即轮换。
- 不要开放公网 3306、6379 和 8084。
- 不要把 `.env`、数据库备份或日志提交到 Git。
- Docker Volume 不是数据库备份，仍需定时导出到服务器之外。
