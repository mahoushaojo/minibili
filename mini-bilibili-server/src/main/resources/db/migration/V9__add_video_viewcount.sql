-- 为视频增加播放数量字段
alter table videos
add column `view_count` int not null default 0 after like_count