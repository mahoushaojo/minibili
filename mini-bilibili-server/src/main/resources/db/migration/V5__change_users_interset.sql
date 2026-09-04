alter table users
change column interest interest_count bigint default 0 comment '关注数量';