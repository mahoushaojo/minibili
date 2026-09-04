package com.minibili.module.category.service.impl;

import com.github.pagehelper.PageInfo;
import com.minibili.common.comment.PageResult;
import com.minibili.common.exception.BusinessException;
import com.minibili.common.id.IdGenerator;
import com.minibili.module.category.domain.dto.AddCategoryDTO;
import com.minibili.module.category.domain.dto.CategoryListDTO;
import com.minibili.module.category.domain.dto.EditCategoryDTO;
import com.minibili.module.category.domain.entity.Category;
import com.minibili.module.category.domain.vo.CategoryVO;
import com.minibili.module.category.mapper.CategoryMapper;
import com.minibili.module.category.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private IdGenerator idGenerator;
    /**
     * 根据id查询分类
     */
    private Category findCategory(Long id){
        Category category = categoryMapper.findById(id);
        if (category == null){
            throw new BusinessException(40900,"该分类不存在");
        }
        return category;
    }

    /**
     * 新增分类
     * @param addCategoryDTO
     * @return
     */
    @Override
    public String addCategory(AddCategoryDTO addCategoryDTO){
        boolean have = categoryMapper.findByName(addCategoryDTO.getName()) == 1;
        if (have){
            throw new BusinessException(40900,"存在相同的分类名称");
        }
        Category category = new Category();
        category.setId(idGenerator.nextId());
        category.setName(addCategoryDTO.getName());
        category.setSort(addCategoryDTO.getSort());
        categoryMapper.addCategory(category);
        return category.getId().toString();
    };

    /**
     * 获取分类列表
     * @param dto
     * @return
     */
    @Override
    public PageResult<CategoryVO> categoryList(CategoryListDTO dto){
        List<CategoryVO> list = categoryMapper.findList(dto.getStatus(), dto.getName());
        return PageResult.from(new PageInfo<>(list));
    }

    /**
     * 编辑分类
     * @param dto
     * @return
     */
    @Override
    public Boolean editCategory(EditCategoryDTO dto){
        findCategory(dto.getId());
        int rows = categoryMapper.editCategory(dto);
        if (rows != 1){
            throw new BusinessException(40900,"修改失败");
        }
        return true;
    }

    /**
     * 删除分类
     * @param id
     * @return
     */
    @Override
    public Boolean deleteCategory(Long id){
        findCategory(id);
        int rows = categoryMapper.deleteCategory(id);
        if (rows != 1){
            throw new BusinessException(40900,"删除失败");
        }
        return true;
    }

    /**
     * 分类排序
     * @param id
     * @param sort
     * @return
     */
    @Override
    public Boolean sortCategory(Long id, Integer sort){
        if (sort == null){
            throw new BusinessException(40900,"sort不能为null");
        }
        findCategory(id);
        int rows = categoryMapper.setCategorySort(sort, id);
        if (rows != 1){
            throw new BusinessException(40900,"设置失败");
        }
        return true;
    };

    /**
     * 禁用/启用分类
     * @param id
     * @param status
     * @return
     */
    @Override
    public Boolean setCategoryStatus(Long id, Integer status){
        if (status == null){
            throw new BusinessException(40900,"sort不能为null");
        }
        int oldStatus = findCategory(id).getStatus();

        if (Objects.equals(oldStatus, status)){
            throw new BusinessException(40900,"状态没有发生变化");
        }
        int rows = categoryMapper.setCategoryStatus(status, id);
        if (rows != 1){
            throw new BusinessException(40900,"设置失败");
        }
        return true;
    }
}
