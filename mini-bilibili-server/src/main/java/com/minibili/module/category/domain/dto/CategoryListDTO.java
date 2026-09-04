package com.minibili.module.category.domain.dto;

import com.minibili.common.comment.PageUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryListDTO  extends PageUtils.PageQuery {
    private Integer status; //状态
    private String name; //名称
}
