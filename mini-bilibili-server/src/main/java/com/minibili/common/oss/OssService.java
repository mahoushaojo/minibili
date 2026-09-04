package com.minibili.common.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class OssService {

    @Resource
    private OSS ossClient;

    @Resource
    private OssProperties ossProperties;

    public String upload(MultipartFile file, String directory) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String suffix = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                suffix = originalFilename.substring(
                        originalFilename.lastIndexOf(".")
                );
            }

            String fileName = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    + suffix;

            String objectKey = directory + "/" + fileName;

            ObjectMetadata metadata = new ObjectMetadata();

            if (file.getContentType() != null) {
                metadata.setContentType(file.getContentType());
            }

            // UUID 文件名不会覆盖，可以设置长缓存
            metadata.setCacheControl(
                    "public, max-age=2592000, immutable"
            );

            ossClient.putObject(
                    ossProperties.getBucketName(),
                    objectKey,
                    file.getInputStream(),
                    metadata
            );

            return buildPublicUrl(objectKey);

        } catch (IOException exception) {
            throw new RuntimeException("文件上传失败", exception);
        }
    }

    private String buildPublicUrl(String objectKey) {
        String domain = ossProperties.getDomain();

        if (domain == null || domain.isBlank()) {
            throw new IllegalStateException("OSS_PUBLIC_DOMAIN 未配置");
        }

        domain = domain.replaceAll("/+$", "");
        objectKey = objectKey.replaceFirst("^/+", "");

        return domain + "/" + objectKey;
    }
}