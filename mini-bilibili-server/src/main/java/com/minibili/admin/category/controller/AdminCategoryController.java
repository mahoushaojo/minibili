package com.minibili.admin.category.controller;

import com.minibili.common.api.ApiResponse;
import com.minibili.common.comment.PageResult;
import com.minibili.module.category.domain.dto.AddCategoryDTO;
import com.minibili.module.category.domain.dto.CategoryListDTO;
import com.minibili.module.category.domain.dto.EditCategoryDTO;
import com.minibili.module.category.domain.dto.SortCategoryDTO;
import com.minibili.module.category.domain.entity.Category;
import com.minibili.module.category.domain.vo.CategoryVO;
import com.minibili.module.category.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/category")
public class AdminCategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param dto
     * @return
     */
    @PostMapping("/add")
    public ApiResponse<String> addCategory(@Valid @RequestBody AddCategoryDTO dto){
        return ApiResponse.success(categoryService.addCategory(dto));
    }

    /**
     * 分类列表
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public ApiResponse<PageResult<CategoryVO>> categoryList(@Valid @RequestBody CategoryListDTO dto){
        return ApiResponse.success(categoryService.categoryList(dto));
    }

    /**
     * 编辑分类
     * @param dto
     * @return
     */
    @PostMapping("/edit")
    public ApiResponse<Boolean> editCategory(@Valid @RequestBody EditCategoryDTO dto){
        return ApiResponse.success(categoryService.editCategory(dto));
    }

    /**
     * 删除分类
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Boolean> deleteCategory(@PathVariable("id") Long id){
        return ApiResponse.success(categoryService.deleteCategory(id));
    }

    /**
     * 分类排序
     * @param dto
     * @return
     */
    @PostMapping("/sort")
    public ApiResponse<Boolean> sortCategory(@RequestBody SortCategoryDTO dto){
        return ApiResponse.success(categoryService.sortCategory(dto.getId(),dto.getSort()));
    }

    /**
     * 启用/禁用分类
     * @param dto
     * @return
     */
    @PostMapping("/set")
    public ApiResponse<Boolean> setCategoryStatus(@RequestBody SortCategoryDTO dto){
        return ApiResponse.success(categoryService.setCategoryStatus(dto.getId(),dto.getStatus()));
    }
}
