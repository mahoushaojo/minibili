package com.minibili.security;
// 用户来管理refresh-token

import com.minibili.common.redis.RedisService;
import com.minibili.config.RefreshTokenProperties;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${cookie.secure}")
    private boolean secure;

    // 引入redis方法
    private final RedisService redisService;
    // 引入刷新token的配置
    private final RefreshTokenProperties properties;
    // 定义redis的key
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String LOGIN_SESSION_PREFIX = "login_session:";

    // 创建refresh_token
    public String createRefreshToken(LoginSession loginSession){
        String refreshToken = UUID.randomUUID().toString().replace("-","");
        redisService.set(
                REFRESH_TOKEN_PREFIX + refreshToken,
                loginSession,
                properties.getExpire(),
                TimeUnit.MILLISECONDS
        );
        redisService.set(
                LOGIN_SESSION_PREFIX + loginSession.getSessionId(),
                loginSession,
                properties.getExpire(),
                TimeUnit.MILLISECONDS
        );
        return refreshToken;
    }

    // 根据token获取用户信息
    public LoginSession getSession(String refreshToken){
        if (refreshToken == null || refreshToken.isBlank()) {
            return null;
        }
        return redisService.get(
                REFRESH_TOKEN_PREFIX + refreshToken,
                LoginSession.class
        );
    }

    // 根据JWT中的sessionId检查当前设备会话是否仍然有效
    public LoginSession getSessionById(String sessionId){
        if (sessionId == null || sessionId.isBlank()) {
            return null;
        }
        return redisService.get(
                LOGIN_SESSION_PREFIX + sessionId,
                LoginSession.class
        );
    }

    // 刷新时旧refresh token立即作废，并生成一个新的token
    public String rotate(String oldRefreshToken, LoginSession loginSession){
        redisService.delete(REFRESH_TOKEN_PREFIX + oldRefreshToken);
        return createRefreshToken(loginSession);
    }

    // 删除token
    public void delete(String key){
        LoginSession loginSession = getSession(key);
        redisService.delete(REFRESH_TOKEN_PREFIX + key);
        if (loginSession != null) {
            redisService.delete(
                    LOGIN_SESSION_PREFIX + loginSession.getSessionId()
            );
        }
    }

    // 把refreshToken放入cookie中
    public void addRefreshCookie(HttpServletResponse response, String refreshToken){
        ResponseCookie cookie = ResponseCookie
                .from(
                        "refresh_token",
                        refreshToken
                )
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(
                        Duration.ofDays(7) //token最多存在7天
                )
                .sameSite("Lax")
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // 让浏览器立即删除refresh_token Cookie
    public void clearRefreshCookie(HttpServletResponse response){
        ResponseCookie cookie = ResponseCookie
                .from("refresh_token", "")
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(Duration.ZERO)
                .sameSite("Lax")
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
