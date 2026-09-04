package com.minibili.module.category.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCategoryDTO {
    @NotBlank(message = "分类名称不能为空")
    private String name;
    @NotNull(message = "sort不能为null")
    private int sort;
}
