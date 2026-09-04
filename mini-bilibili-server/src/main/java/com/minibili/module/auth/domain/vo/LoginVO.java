package com.minibili.module.auth.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 登录返回数据
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String userName;
}
