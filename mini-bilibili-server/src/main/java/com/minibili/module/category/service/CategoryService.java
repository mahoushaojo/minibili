package com.minibili.module.category.service;

import com.minibili.common.comment.PageResult;
import com.minibili.module.category.domain.dto.AddCategoryDTO;
import com.minibili.module.category.domain.dto.CategoryListDTO;
import com.minibili.module.category.domain.dto.EditCategoryDTO;
import com.minibili.module.category.domain.entity.Category;
import com.minibili.module.category.domain.vo.CategoryVO;

public interface CategoryService {
    // 新增分类
    String addCategory(AddCategoryDTO addCategoryDTO);
    // 分类列表
    PageResult<CategoryVO> categoryList(CategoryListDTO dto);
    // 编辑分类
    Boolean editCategory(EditCategoryDTO dto);
    // 删除分类
    Boolean deleteCategory(Long id);
    // 分类排序
    Boolean sortCategory(Long id, Integer sort);
    // 禁用/启用分类
    Boolean setCategoryStatus(Long id, Integer status);
}
