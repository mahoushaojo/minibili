package com.minibili.app.video.controller;

import com.minibili.app.video.domain.dto.EditVideoDTO;
import com.minibili.app.video.domain.dto.HomeListDTO;
import com.minibili.app.video.domain.dto.PageVideoDTO;
import com.minibili.app.video.domain.vo.VideoDetailVO;
import com.minibili.app.video.domain.vo.VideoVO;
import com.minibili.common.api.ApiResponse;
import com.minibili.app.video.domain.dto.AddVideoDTO;
import com.minibili.common.comment.PageResult;
import com.minibili.module.collect.service.CollectService;
import com.minibili.module.like.service.LikeService;
import com.minibili.module.video.service.VideoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/video")
public class VideoController {
    @Autowired
    private VideoService videoService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private CollectService collectService;

    /**
     * App
     * 新增视频
     * @param userId
     * @param addVideoDTO
     * @return
     */
    @PostMapping("/add")
    public ApiResponse<String> addVideo(@RequestAttribute("userId") Long userId, @Valid @RequestBody AddVideoDTO addVideoDTO){
        return ApiResponse.success(videoService.addVideo(userId,addVideoDTO));
    }

    /**
     * App
     * 获取视频列表-根据热门、推荐、最新
     * @param dto
     * @return
     */
    @PostMapping("/homeList")
    public ApiResponse<PageResult<VideoVO>> videoHomeList(@Valid @RequestBody HomeListDTO dto){
        return ApiResponse.success(videoService.videoHomeList(dto));
    };

    /**
     * App
     * 获取我的视频列表
     * @param userId
     * @param pageVideoDTO
     * @return
     */
    @PostMapping("/list")
    public ApiResponse<PageResult<VideoVO>> videoList(@RequestAttribute("userId") Long userId, @Valid @RequestBody PageVideoDTO pageVideoDTO){
        return  ApiResponse.success(videoService.videoList(userId, pageVideoDTO));
    };

    /**
     * App
     * 编辑视频
     * @param userId
     * @param editVideoDTO
     * @return
     */
    @PostMapping("/edit")
    public ApiResponse<Boolean> editVideo(@RequestAttribute("userId") Long userId,@Valid @RequestBody EditVideoDTO editVideoDTO){
        return ApiResponse.success(videoService.editVideo(userId, editVideoDTO));
    }

    /**
     * App
     * 隐藏视频
     * @param userId
     * @param vid
     * @return
     */
    @GetMapping("/hidden")
    public ApiResponse<Boolean> hiddenVideo(@RequestAttribute("userId") Long userId, @RequestParam Long vid){
        return ApiResponse.success(videoService.hiddenVideo(userId,vid));
    }

    /**
     * App
     * 重新提交视频审核
     * @param userId
     * @param id
     * @return
     */
    @PutMapping("/submit/{id}")
    public ApiResponse<Boolean> submitVideo(@RequestAttribute("userId") Long userId,@PathVariable Long id){
        return ApiResponse.success(videoService.submitVideo(userId,id));
    }
    // 删除视频
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Boolean> deleteVideo(@RequestAttribute("userId") Long userId,@PathVariable Long id){
        return ApiResponse.success(videoService.deleteVideo(userId, id));
    }
    // 视频点赞/取消点赞
    @GetMapping("/like")
    public ApiResponse<Boolean> setLike(@RequestAttribute("userId") Long userId,@RequestParam("vid") Long vid, @RequestParam("type") Integer type){
        return ApiResponse.success(likeService.setLike(userId,vid, type));
    };
    // 视频收藏/取消收藏
    @GetMapping("/collect")
    public ApiResponse<Boolean> setCollect(@RequestAttribute("userId") Long userId,@RequestParam("vid") Long vid, @RequestParam("type") Integer type){
        return ApiResponse.success(collectService.setCollect(userId,vid,type));
    };
    // 视频详情
    @GetMapping("/detail")
    public ApiResponse<VideoDetailVO> videoDetail(@RequestAttribute(value = "userId", required = false) Long userId,@RequestParam("vid") Long vid){
        return ApiResponse.success(videoService.videoDetail(userId,vid));
    }
}
