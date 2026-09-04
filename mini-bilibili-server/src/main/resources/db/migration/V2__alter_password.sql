-- 修改密码的长度为255
alter table users
modify column password varchar(255)