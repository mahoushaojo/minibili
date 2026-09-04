package com.minibili.app.upload.controller;

import com.minibili.common.api.ApiResponse;
import com.minibili.common.exception.BusinessException;
import com.minibili.common.oss.OssService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping("/upload")
public class UploadController {
    @Resource
    private OssService ossService;

    private static final Set<String> VIDEO_TYPES = Set.of(
            "video/mp4",
            "video/webm"
    );
    // 校验视频
    private void validateVideo(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BusinessException(40000, "视频不能为空");
        }

        String contentType = file.getContentType();

        if (!VIDEO_TYPES.contains(contentType)) {
            throw new BusinessException(40000, "只允许上传视频文件");
        }

        // 例如先限制 500MB
        if (file.getSize() > 500L * 1024 * 1024) {
            throw new BusinessException(40000, "视频不能超过500MB");
        }
    }
    // 上传图片
    @PostMapping("/image")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file){
        String objectKey =
                ossService.upload(file, "avatar");
        return ApiResponse.success(objectKey);
    }
    // 上传视频
    @PostMapping("/video")
    public ApiResponse<String> uploadVideo(
            @RequestParam("file") MultipartFile file) {

        validateVideo(file);

        String objectKey = ossService.upload(file, "video");

        return ApiResponse.success(objectKey);
    }
}
