# Mini Bilibili Server

Mini Bilibili 的 Spring Boot 后端，使用 Java 21、Spring Boot 3.5、MyBatis、MySQL、Redis 和 Flyway。

## 本地启动

1. 准备 Java 21、MySQL 和 Redis。
2. 复制环境变量示例：

```bash
cp .env.example .env
```

3. 编辑 `.env`，填写数据库、Redis、JWT 和阿里云 OSS 配置。`.env` 已被 Git 和 Docker 忽略，不要提交真实密钥。
4. 启动应用：

```bash
mvn spring-boot:run
```

默认端口为 `8084`，默认启用 `local` Profile。已有数据库建议先保持 `FLYWAY_ENABLED=false`，备份后再验证迁移脚本。

## Docker

项目根目录的 `Dockerfile` 使用 Java 21 构建并以非 root 用户运行。完整的 MySQL、Redis、后端、C 端和管理端编排位于同级的 `mini-bilibili-deploy` 目录，具体命令见该目录的 `README.md`。
