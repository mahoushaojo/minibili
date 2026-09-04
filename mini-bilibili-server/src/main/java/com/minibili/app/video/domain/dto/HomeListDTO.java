package com.minibili.app.video.domain.dto;

import com.minibili.common.comment.PageUtils;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HomeListDTO extends PageUtils.PageQuery {
    @NotNull(message = "视频类型(type)不能为null")
    private Integer type; // 视频类型 1-推荐 2-热门 3-最新
}
