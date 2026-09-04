package com.minibili.app.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.minibili.common.comment.PageUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserListDTO extends PageUtils.PageQuery {
    private String userName;
    private List<Integer> statusList; //状态
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss") //识别前端返回的时间
    private LocalDateTime createdStartAt; //创建时间范围start
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss") //识别前端返回的时间
    private LocalDateTime createdEndAt; // 创建时间范围end
}
