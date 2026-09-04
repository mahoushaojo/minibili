create table if not exists collects(
    id bigint primary key not null comment '主键id',
    user_id bigint not null comment '用户id',
    video_id bigint not null comment '视频id',

    unique key uk_user_video(user_id,video_id)
) comment '收藏列表'