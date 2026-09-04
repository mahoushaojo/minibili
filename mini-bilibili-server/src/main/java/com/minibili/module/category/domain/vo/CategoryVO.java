package com.minibili.module.category.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String name; // 分类名称
    private Integer sort; //排序
    private int status; //状态
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss")
    private LocalDateTime createdAt; //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss")
    private LocalDateTime updatedAt; //更新时间
}
