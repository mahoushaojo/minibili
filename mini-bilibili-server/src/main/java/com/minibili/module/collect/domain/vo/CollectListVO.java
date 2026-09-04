package com.minibili.module.collect.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectListVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;        // 收藏记录id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;    // 收藏人id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long videoId; // 视频id

    private String title; // 视频标题
    private String cover; // 视频封面

    private String userName; // 视频作者
}
