package com.minibili.module.video.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoCategories {
    private Long id;
    private Long videoId;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
}
