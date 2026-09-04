package com.minibili.module.category.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortCategoryDTO {
    @NotNull(message = "分类id不能为null")
    private Long id;
    private Integer sort;
    private Integer status;
}
