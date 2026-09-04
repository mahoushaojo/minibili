package com.minibili.module.category.mapper;

import com.minibili.module.category.domain.dto.AddCategoryDTO;
import com.minibili.module.category.domain.dto.EditCategoryDTO;
import com.minibili.module.category.domain.entity.Category;
import com.minibili.module.category.domain.vo.CategoryVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {
    // 新增分类
    @Insert("""
        insert into categories(id, name, sort) values (#{category.id}, #{category.name}, #{category.sort});
    """)
    void addCategory(@Param("category") Category category);

    // 根据name查询是否存在相同的分类
    @Select("""
        select count(1) from categories where name = #{name};
    """)
    int findByName(@Param("name") String name);

    // 根据id查询
    @Select("""
        select * from categories where id = #{id};
    """)
    Category findById(@Param("id") Long id);

    // 分类列表
    List<CategoryVO> findList(@Param("status")Integer status, @Param("name") String name);

    // 编辑分类
    @Update("""
        update categories set name = #{dto.name} where id = #{dto.id};
    """)
    int editCategory(@Param("dto")EditCategoryDTO dto);

    // 删除分类
    @Delete("""
        delete from categories where id = #{id} and status = 0;
    """)
    int deleteCategory(@Param("id") Long id);

    // 分类排序
    @Update("""
        update categories set sort = #{sort} where id = #{id};
    """)
    int setCategorySort(@Param("sort") Integer sort, Long id);

    // 禁用/启用分类
    @Update("""
        update categories set status = #{status} where id = #{id};
    """)
    int setCategoryStatus(@Param("status") Integer status, Long id);
}
