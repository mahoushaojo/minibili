package com.minibili.module.category.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditCategoryDTO {
    @NotNull(message = "分类id不能为null")
    private Long id;
    @NotBlank(message = "分类名称不能为空")
    private String name;
}
