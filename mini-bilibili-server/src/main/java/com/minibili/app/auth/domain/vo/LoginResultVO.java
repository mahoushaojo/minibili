package com.minibili.app.auth.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 登录返回数据
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResultVO {
    private String accessToken;
    private Long userId;
    private String userName;
}
