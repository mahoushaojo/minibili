package com.minibili.app.video.domain.dto;

import com.minibili.common.comment.PageUtils;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PageVideoDTO extends PageUtils.PageQuery {
    @NotNull(message = "status不能为null")
    private Integer status; // 视频状态
}
