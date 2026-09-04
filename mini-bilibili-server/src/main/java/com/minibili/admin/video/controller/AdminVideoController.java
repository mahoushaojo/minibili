package com.minibili.admin.video.controller;

import com.minibili.admin.video.domain.dto.AdminVideoPageDTO;
import com.minibili.admin.video.domain.vo.AdminVideoListVO;
import com.minibili.common.api.ApiResponse;
import com.minibili.common.comment.PageResult;
import com.minibili.module.video.service.VideoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// 针对视频的接口
@RestController
@RequestMapping("/admin/video")
public class AdminVideoController {
    @Autowired
    private VideoService videoService;

    /**
     * Admin
     * 获取视频列表
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public ApiResponse<PageResult<AdminVideoListVO>> getAdminVideoList(@Valid @RequestBody AdminVideoPageDTO dto){
        return ApiResponse.success(videoService.adminVideoList(dto));
    }

    /**
     * Admin
     * 发布视频
     * @param vid
     * @return
     */
    @GetMapping("/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Boolean> videoPublish(@RequestParam("vid") Long vid){
        return ApiResponse.success(videoService.publishVideo(vid));
    }

    /**
     * Admin
     * 下架视频
     * @param vid
     * @return
     */
    @GetMapping("/holdout")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Boolean> videoHoldOut(@RequestParam("vid") Long vid){
        return ApiResponse.success(videoService.videoHoldOut(vid));
    }

    // 批量发布视频

    // 批量下架视频
}
