package com.minibili.app.video.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditVideoDTO {
    @NotNull(message = "视频id不能为null")
    private Long id;
    @NotBlank(message = "视频标题不能为空")
    @Size(max = 100,message = "视频标题长度不能超过100")
    private String title;
    @NotBlank(message = "视频封面不能为空")
    private String cover;
    @Size(max = 255,message = "视频描述不能大于255个字符")
    private String desc;
    @NotEmpty(message = "视频分类不能为空")
    private List<Long> categoryIdList;
}
