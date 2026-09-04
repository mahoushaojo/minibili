create table if not exists videos(
    `id` bigint primary key auto_increment comment '主键id',
    `user_id` bigint not null comment '作者id',
    `title` varchar(100) not null comment '视频标题',
    `cover` varchar(255) not null comment '视频封面',
    `link_url` varchar(255) not null comment '视频链接',
    `status` tinyint not null default 0  comment '视频状态：0-待审核 1-已发布 2-已下架 3-已删除',
    `like_count` int unsigned not null default 0 comment '点赞数',
    `collect_count` int unsigned not null default 0 comment '收藏数',
    `comment_count` int unsigned not null default 0 comment '评论数',
    `publish_at` datetime default null comment '发布日期',
    `created_at` datetime default current_timestamp comment '创建时间',
    `updated_at` datetime default current_timestamp on update current_timestamp comment '更新时间',

    index idx_id(id),
    index idx_created_at(created_at),
    index idx_status_created(status, created_at),
    index idx_user_status_created (user_id, status, created_at)
) comment '视频列表';