package com.minibili.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 用来存储refresh_token配置的redis配置项
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginSession {
    private String sessionId; // 当前设备的登录会话ID
    private Long userId; //用户id
    private String userName; //用户名
    private LocalDateTime loginTime; //登录时间
}
