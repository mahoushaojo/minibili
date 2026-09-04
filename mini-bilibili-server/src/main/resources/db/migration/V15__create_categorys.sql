-- 创建分类表
create table if not exists categories(
    id bigint not null primary key comment '主键id',
    name varchar(100) not null comment '分类名称',
    sort int default 0 comment '排序',
    status tinyint default 0 comment '状态 1:正常 0:禁用',
    created_at datetime default current_timestamp comment '创建时间',
    updated_at datetime default current_timestamp on update current_timestamp comment '更新时间',

    unique key uk_name(name)
) comment '视频分类';

-- 创建视频对应的分类表 多对多关系
create table if not exists video_categories(
    id BIGINT PRIMARY KEY not null  comment '主键id',
    video_id BIGINT NOT NULL comment '视频id',
    category_id BIGINT NOT NULL comment '分类id',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_video_category(video_id, category_id),
    index idx_video_id(video_id),
    index idx_cate_id(category_id)
)