package com.minibili.admin.video.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.minibili.common.comment.PageUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdminVideoPageDTO extends PageUtils.PageQuery {
    private String title; //视频标题
    private List<Integer> statusList; //状态
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss") //识别前端返回的时间
    private LocalDateTime createdStartAt; //创建时间开始
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss") //识别前端返回的时间
    private LocalDateTime createdEndAt; //创建时间结束
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss") //识别前端返回的时间
    private LocalDateTime publishStartAt; //发布时间范围 开始
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss") //识别前端返回的时间
    private LocalDateTime publishEndAt; //发布时间范围 结束
}
