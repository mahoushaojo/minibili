package com.minibili.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "refresh-token") //和配置项中的配置指定
@Data
public class RefreshTokenProperties {
    /**
     * refresh token过期时间
     */
    private Long expire;
}
