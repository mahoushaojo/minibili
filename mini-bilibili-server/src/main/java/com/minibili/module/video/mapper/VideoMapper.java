package com.minibili.module.video.mapper;

import com.minibili.admin.video.domain.dto.AdminVideoPageDTO;
import com.minibili.app.video.domain.dto.EditVideoDTO;
import com.minibili.app.video.domain.dto.HomeListDTO;
import com.minibili.app.video.domain.dto.PageVideoDTO;
import com.minibili.module.video.domain.entity.Videos;
import com.minibili.module.video.domain.enums.VideoCountType;
import org.apache.ibatis.annotations.*;

import java.util.List;

// 视频模块的mapper
@Mapper
public interface VideoMapper {
    // 新增视频
    @Insert("""
        insert into videos (id, user_id, title, cover, duration, `desc`, link_url)
        values (#{id},#{userId},#{title}, #{cover},#{duration}, #{desc}, #{linkUrl})
    """)
     void addVideo(Videos videos);

    // 根据视频id获取视频信息
    @Select("""
        select v.*,u.name as user_name from videos v
            inner join users u on v.user_id=u.id
            where v.id = #{vid};
    """)
    Videos findVideoById(Long vid);

    // Admin 获取视频列表
    List<Videos> getAdminVideoList(AdminVideoPageDTO dto);

    // Admin 发布视频
    @Update("""
        update videos set status = 1,
                          publish_at = now()
                      where id = #{vid}
                        and status in(0,2);
    """)
    Boolean publishVideo(Long vid);

    // 下架视频
    @Update("""
        update videos set status = 2
            where id = #{vid} and status = 1
    """)
    Boolean videoHoldOut(Long vid);

    // 修改视频信息
    int editVideo(@Param("userId") Long userId,@Param("dto") EditVideoDTO editVideoDTO);

    // app-根据status获取个人的视频列表
    @Select("""
        select * from videos where user_id = #{userId} and status = #{dto.status}
    """)
    List<Videos> videoList(@Param("userId") Long userId,@Param("dto") PageVideoDTO dto);

    // app-获取首页视频列表 需要根据热度、推荐、最新
    List<Videos> videoHomeList(HomeListDTO dto);

    // app-隐藏个人视频
    @Update("""
    update videos set status = 2 where id = #{vid} and user_id = #{userId} and status = 1;
    """)
    int hiddenVideo(Long userId,Long vid);

    // 重新提审视频
    @Update("""
        update videos set status = 0 where id = #{id} and user_id = #{userId} and status = 2;
    """)
    int submitVideo(Long userId,Long id);

    // 删除视频-软删除
    @Update("""
        update videos set status = 3 where id = #{id} and user_id = #{userId} and status = 2;
    """)
    int deleteVideo(Long userId, Long vid);

    // 视频点赞、观看、收藏、评论数量增加减少
    int updateCount(@Param("vid") Long vid, @Param("type") VideoCountType type, @Param("delta") int delta);

}

