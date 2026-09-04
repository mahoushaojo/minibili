package com.minibili.module.collect.mapper;

import com.minibili.module.collect.domain.entity.Collect;
import com.minibili.module.collect.domain.vo.CollectListVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CollectMapper {
    /**
     * 根据userid和videoid进行查询
     * @param userId
     * @param videoId
     * @return
     */
    @Select("""
        select * from collects where video_id = #{videoId} and user_id = #{userId};
    """)
    Collect getCollect(Long userId, Long videoId);

    /**
     * 获取用户的收藏列表
     * @param userId
     * @param keyword
     * @return
     */
    List<CollectListVO> getCollectList(@Param("userId") Long userId,@Param("keyword") String keyword);

    /**
     * 新增收藏
     * @return
     */
    @Insert("""
        insert into collects(id,user_id,video_id) values (#{collect.id},#{collect.userId},#{collect.videoId});
    """)
    int setCollect(@Param("collect") Collect collect);

    /**
     * 取消收藏
     * @param userId
     * @param videoId
     * @return
     */
    @Delete("""
        delete from collects where video_id = #{videoId} and user_id = #{userId};
    """)
    int deleteCollect(@Param("userId") Long userId,@Param("videoId") Long videoId);
}
