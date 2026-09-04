package com.minibili.module.like.mapper;

import com.minibili.module.collect.domain.entity.Collect;
import com.minibili.module.like.domain.entity.Likes;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LikeMapper {
    @Select("""
        select * from likes where user_id = #{userId} and video_id = #{videoId};
    """)
    Likes getLike(@Param("userId") Long userId, @Param("videoId") Long videoId);

    /**
     * 新增点赞
     * @param userId
     * @param videoId
     * @return
     */
    @Insert("""
        insert into likes(id, user_id,video_id)
        values (#{like.id},#{like.userId},#{like.videoId});
    """)
    int addLike(@Param("like") Likes likes);

    /**
     * 取消点赞
     * @param userId
     * @param videoId
     * @return
     */
    @Delete("""
        delete from likes where user_id = #{userId} and video_id = #{videoId};
    """)
    int deleteLike(Long userId, Long videoId);
}
