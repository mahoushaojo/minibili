-- 为视频增加播放数量字段
alter table videos
    add column `desc` varchar(255) comment '视频描述' after cover,
    add column `duration` bigint not null default 0 comment '视频时长' after cover,
    modify column `view_count` int not null default 0 comment '播放数量';