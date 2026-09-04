package com.minibili.common.comment;

import com.github.pagehelper.PageHelper;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// 创建分页公共方法
@Data
public final class PageUtils {
    private static final int DEFAULT_PAGE_NO = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    private PageUtils() {
    }

    public static void startPage(PageQuery query) {
        int pageNo = DEFAULT_PAGE_NO;
        int pageSize = DEFAULT_PAGE_SIZE;

        if (query != null) {
            if (query.getPageNo() != null && query.getPageNo() > 0) {
                pageNo = query.getPageNo();
            }

            if (query.getPageSize() != null && query.getPageSize() > 0) {
                pageSize = Math.min(
                        query.getPageSize(),
                        MAX_PAGE_SIZE
                );
            }
        }

        PageHelper.startPage(pageNo, pageSize);
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageQuery{
        @NotNull
        @Min(1)
        private Integer pageNo = 1;

        @NotNull
        @Min(1)
        @Max(100)
        private Integer pageSize = 10;
    }
}