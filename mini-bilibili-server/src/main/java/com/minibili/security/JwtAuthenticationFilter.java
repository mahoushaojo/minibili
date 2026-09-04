package com.minibili.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minibili.common.api.ApiResponse;
import com.minibili.module.user.domain.enums.UserRole;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7).trim();
        try {
            JwtService.JwtPayload payload = jwtService.parseToken(token);

            LoginSession loginSession = refreshTokenService.getSessionById(
                    payload.getSessionId()
            );
            if (loginSession == null
                    || !payload.getUserId().equals(loginSession.getUserId())) {
                throw new IllegalArgumentException("登录会话已失效");
            }

            String authority;

            if (payload.getRole() == UserRole.ADMIN.getCode()) {
                authority = "ROLE_ADMIN";
            } else {
                authority = "ROLE_USER";
            }
            logger.info("authority:"+authority);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            payload.getUserId(),
                            null,
                            List.of(new SimpleGrantedAuthority(authority))
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            request.setAttribute("userId", payload.getUserId());
            request.setAttribute("userName", payload.getUserName());
            request.setAttribute("role", payload.getRole());
            request.setAttribute("sessionId", payload.getSessionId());

            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    objectMapper.writeValueAsString(
                            ApiResponse.failure(40100, "access token无效或已过期")
                    )
            );
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/auth/login")
                || path.equals("/auth/refresh")
                || path.equals("/auth/loginOut")
                || path.equals("/user/register");
    }
}
