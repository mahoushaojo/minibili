alter table users
-- 增加token版本号，用户修改密码全部退出
add column token_version INT default 0;