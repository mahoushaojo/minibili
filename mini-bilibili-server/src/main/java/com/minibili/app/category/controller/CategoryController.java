package com.minibili.app.category.controller;

import com.minibili.common.api.ApiResponse;
import com.minibili.common.comment.PageResult;
import com.minibili.module.category.domain.dto.CategoryListDTO;
import com.minibili.module.category.domain.vo.CategoryVO;
import com.minibili.module.category.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 获取分类列表
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public ApiResponse<PageResult<CategoryVO>> categoryList(@Valid @RequestBody CategoryListDTO dto){
        return ApiResponse.success(categoryService.categoryList(dto));
    }
}
