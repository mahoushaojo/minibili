package com.minibili.security;

import com.minibili.config.JwtProperties;
import com.minibili.module.user.domain.entity.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    // 定义内部类
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JwtPayload {
        private Long userId;
        private String userName;
        private int role;
        private String sessionId;
    }

    // 定义jwt方法
    private final JwtProperties properties;

    // 创建token
    public String createToken(Users users, String sessionId){
        //获取当前时间
        Date now = new Date();
        // 定义过期时间
        Date expire = new Date(now.getTime()+properties.getExpire());
        return Jwts.builder()
                .subject(users.getId().toString())
                .claim("userName", users.getName())
                .claim("role", users.getRole())
                .claim("sessionId", sessionId)
                .claim("type", "access")
                .issuedAt(now)
                .expiration(expire)
                .signWith(getKey())
                .compact();
    }

    // 解析token
    public JwtPayload parseToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // 防止其他类型的JWT被当作access_token使用
        String tokenType = claims.get("type", String.class);

        if (!"access".equals(tokenType)) {
            throw new IllegalArgumentException("Token类型错误");
        }
        // 获取用户id
        Long userId = Long.valueOf(claims.getSubject());
        // 获取用户名
        String userName = claims.get("userName", String.class);
        // 获取用户权限
        Integer role = claims.get("role", Integer.class);
        // 获取当前设备的登录会话ID
        String sessionId = claims.get("sessionId", String.class);
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("Token缺少sessionId");
        }

        return new JwtPayload(userId, userName, role, sessionId);
    }

    private SecretKey getKey(){
        return Keys.hmacShaKeyFor(
                properties.getSecret()
                        .getBytes(StandardCharsets.UTF_8)
        );

    }
}
