create table if not exists users(
    id bigint not null primary key auto_increment comment '用户id',
    name varchar(30) not null comment '用户名称',
    phone varchar(20) not null unique comment '手机号',
    email varchar(100) not null unique comment '邮箱',
    password varchar(32) not null comment '密码',
    status tinyint not null default 1 comment '用户状态: 1 正常 2 已禁用',
    role tinyint not null default 1 comment '用户角色: 1 普通用户 2 管理员',
    like_count bigint default 0 comment '喜欢数量',
    collect_count bigint default 0 comment '收藏数量',
    fans_count bigint default 0 comment '粉丝数量',
    video_count bigint default 0 comment '视频数量',
    created_at datetime default current_timestamp comment '创建时间',
    updated_at datetime default  current_timestamp on update current_timestamp comment '更新时间',

    -- 创建索引 根据status、role、created_at 来查询
    index idx_status(status),
    index idx_role(role),
    index idx_created_at(created_at)

) comment '用户表'