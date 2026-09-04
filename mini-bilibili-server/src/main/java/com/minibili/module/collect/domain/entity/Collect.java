package com.minibili.module.collect.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collect {
    private Long id; //收藏id
    private Long userId; //用户id
    private Long videoId; //视频id
}
