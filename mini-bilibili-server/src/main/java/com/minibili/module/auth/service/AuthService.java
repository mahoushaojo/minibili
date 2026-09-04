package com.minibili.module.auth.service;

import com.minibili.module.auth.domain.dto.LoginDTO;
import com.minibili.module.auth.domain.vo.LoginVO;

public interface AuthService {
    // 用户登录
    LoginVO login(LoginDTO dto);
    // 刷新token
    LoginVO refresh(String oldToken);
    // 用户登出
    Boolean loginOut(String refreshToken);
}
