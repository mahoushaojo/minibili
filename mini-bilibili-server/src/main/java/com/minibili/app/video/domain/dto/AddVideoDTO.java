package com.minibili.app.video.domain.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 新增视频的dto
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddVideoDTO {
    private Long id;
    @NotBlank(message = "视频标题不能为空")
    @Size(max = 100, message = "视频标题长度不能超过100")
    private String title;
    @NotBlank(message = "视频封面不能为空")
    private String cover;
    @NotBlank(message = "请传入视频地址")
    private String linkUrl;
    @NotNull(message = "请传递视频时长")
    @Min(value = 1,message = "视频时长不能小于1")
    private Integer duration;
    @Size(max = 255, message = "视频描述长度不能超过255字符")
    private String desc; //视频描述
    @NotEmpty(message = "视频分类不能为空")
    private List<Long> categoryIdList; // 视频分类
}
