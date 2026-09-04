package com.minibili.module.video.service;

import com.minibili.admin.video.domain.dto.AdminVideoPageDTO;
import com.minibili.admin.video.domain.vo.AdminVideoListVO;
import com.minibili.app.video.domain.dto.AddVideoDTO;
import com.minibili.app.video.domain.dto.EditVideoDTO;
import com.minibili.app.video.domain.dto.HomeListDTO;
import com.minibili.app.video.domain.dto.PageVideoDTO;
import com.minibili.app.video.domain.vo.VideoDetailVO;
import com.minibili.app.video.domain.vo.VideoVO;
import com.minibili.common.comment.PageResult;
import jakarta.validation.Valid;

// 视频模块
public interface VideoService {
    // 新增视频-app
    String addVideo(Long userId,AddVideoDTO videoDTO);
    // 视频列表-admin
    PageResult<AdminVideoListVO> adminVideoList(AdminVideoPageDTO adminVideoPageDTO);
    // 发布视频-admin
    Boolean publishVideo(Long vid);
    // 下架视频-admin
    Boolean videoHoldOut(Long id);
    // 修改视频-app
    Boolean editVideo(Long userId,EditVideoDTO editVideoDTO);
    // 根据状态获取视频列表-app
    PageResult<VideoVO> videoList(Long userId, PageVideoDTO pageVideoDTO);
    // 获取首页视频列表-app-无需登录
    PageResult<VideoVO> videoHomeList(HomeListDTO dto);
    // 隐藏视频-app
    Boolean hiddenVideo(Long userId,Long vid);
    // 重新提交视频-app
    Boolean submitVideo(Long userId,Long id);
    // 用户删除视频-app
    Boolean deleteVideo(Long userId, Long id);
    // 视频详情-app
    VideoDetailVO videoDetail(Long userId, Long vid);
}
