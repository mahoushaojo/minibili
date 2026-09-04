package com.minibili.admin.video.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.minibili.module.video.domain.entity.VideoCategories;
import com.minibili.module.video.domain.enums.VideoStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

// admin视频列表返回数据
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminVideoListVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id; //视频id
    private Long userId; //作者id
    private String userName; //作者名称
    private String title; //视频标题
    private String cover; //视频封面
    private int duration; //视频时长
    private String desc; //视频描述
    private int status; //视频状态
    private String linkUrl; //视频链接
    private int linkCount; //点赞数量
    private int viewCount; //播放数量
    private int collectCount; //收藏数量
    private int commentCount; //评论数
    private List<VideoCategories> categoryList; // 分类列表
    private LocalDateTime publishAt; //视频发布时间
    private LocalDateTime createdAt; //创建时间
    private LocalDateTime updatedAt; //更新时间
}
