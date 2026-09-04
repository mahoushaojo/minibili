package com.minibili.module.user.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    // 把用户id设置成string类型返回
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id; //用户id
    private String name; //用户名称
    private String phone; //手机号
    private String email; //邮箱
    private String password; //密码
    private int status; //用户状态
    private int role; //用户角色
    private String avatar; //用户头像
    private String bio; //个人简介
    private Long likeCount; // 点赞数量
    private Long interestCount; //关注数量
    private Long collectCount; //收藏数量
    private Long fansCount; //粉丝数量
    private Long videoCount; //视频数量
    private LocalDateTime createdAt; //创建时间
    private LocalDateTime updatedAt; //更新时间
    private int tokenVersion; //用户版本 用来登出
}
