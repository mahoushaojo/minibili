package com.minibili.module.category.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private Long id; // 主键id
    private String name; // 分类名称
    private Integer sort; //排序
    private int status; //状态
    private LocalDateTime createdAt; //创建时间
    private LocalDateTime updatedAt; //更新时间
}
