package com.minibili.module.like.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 视频点赞表
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Likes {
    private Long id; //主键id
    private Long userId; //用户id
    private Long videoId; //视频id
}
