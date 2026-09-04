package com.minibili.app.video.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.minibili.app.user.domain.vo.UsersVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoDetailVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id; //视频id
    private Long userId; //作者id
    private UsersVO userInfo; //作者详细信息
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
    private Boolean isLike; //是否点赞
    private Boolean isCollect; //是否收藏
    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private List<Long> categoryIdList; //分类id列表
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss") //识别前端返回的时间
    private LocalDateTime publishAt; //视频发布时间
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss") //识别前端返回的时间
    private LocalDateTime createdAt; //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss") //识别前端返回的时间
    private LocalDateTime updatedAt; //更新时间
}
