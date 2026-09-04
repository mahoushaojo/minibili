package com.minibili.app.auth.controller;

import com.minibili.common.api.ApiResponse;
import com.minibili.module.auth.domain.dto.LoginDTO;
import com.minibili.app.auth.domain.vo.LoginResultVO;
import com.minibili.module.auth.domain.vo.LoginVO;
import com.minibili.module.auth.service.AuthService;
import com.minibili.security.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    /**
     * 用户登录
     * @param loginDTO
     * @return
     */
    @PostMapping("/login")
    public ApiResponse<LoginResultVO> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletResponse response){
        LoginVO loginVO = authService.login(loginDTO); //登录
        //设置cookie
        refreshTokenService.addRefreshCookie(response, loginVO.getRefreshToken());
        //处理数据并返回
        LoginResultVO resultVO = new LoginResultVO();
        resultVO.setAccessToken(loginVO.getAccessToken());
        resultVO.setUserId(loginVO.getUserId());
        resultVO.setUserName(loginVO.getUserName());
        return ApiResponse.success(resultVO);
    }

    /**
     * 刷新token
     * @param oldToken
     * @param response
     * @return
     */
    @PostMapping("/refresh")
    public ApiResponse<LoginResultVO> refresh(@CookieValue(name = "refresh_token",required = false) String oldToken, HttpServletResponse response){
        LoginVO loginVO = authService.refresh(oldToken);
        refreshTokenService.addRefreshCookie(response,loginVO.getRefreshToken());
        LoginResultVO resultVO = new LoginResultVO();
        resultVO.setAccessToken(loginVO.getAccessToken());
        resultVO.setUserId(loginVO.getUserId());
        resultVO.setUserName(loginVO.getUserName());
        return ApiResponse.success(resultVO);
    }

    /**
     * 用户登出
     * @param refreshToken
     * @return
     */
    @PostMapping("/loginOut")
    public ApiResponse<Boolean> loginOut(@CookieValue(name = "refresh_token", required = false) String refreshToken, HttpServletResponse response){
        // 清空cookie
        refreshTokenService.clearRefreshCookie(response);
        return ApiResponse.success(authService.loginOut(refreshToken));
    }

}
