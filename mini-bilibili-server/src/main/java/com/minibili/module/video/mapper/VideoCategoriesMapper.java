package com.minibili.module.video.mapper;

import com.minibili.module.video.domain.entity.VideoCategories;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

// 用于关联视频与分类
@Mapper
public interface VideoCategoriesMapper {
    /**
     * 批量新增关联
     * @param videoId
     * @param categoryIdList
     */
    void batchAdd(@Param("videoId") Long videoId, @Param("categoryIdList") List<Long> categoryIdList);

    /**
     * 批量删除
     * @param videoId
     */
    void batchDelete(@Param("videoId") Long videoId);

    /**
     * 获取单个视频的关联分类
     * @param vid
     * @return
     */
    @Select("""
        select v.*,c.name as categoryName from video_categories v
            inner join categories c on v.category_id = c.id
        where video_id = #{vid}
        order by created_at;
    """)
    List<VideoCategories> findByVid(@Param("vid") Long vid);

    /**
     * 批量获取视频对应的分类并分组
     */
    List<VideoCategories> findByVideoIds(@Param("videoIdList") List<Long> videoIdList);
}
