package com.minibili.config;

import com.minibili.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 安全配置
 * 用来设定哪些接口需要校验token 哪些不需要
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 开启之后 可以使用 PreAuthorize来进行权限控制
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {


        return http

                // 关闭csrf
                .csrf(AbstractHttpConfigurer::disable
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                // 请求权限配置
                .authorizeHttpRequests(auth ->
                        auth

                                // 登录接口允许访问
                                // 还有一下接口默认不需要登录 比如视频列表等
                                .requestMatchers(
                                        "/auth/login",
                                        "/auth/refresh",
                                        "/auth/loginOut",
                                        "/user/register",
                                        "/error"
                                )
                                .permitAll()
                                // 首页视频模块不需要登录
                                .requestMatchers(
                                        "/video/homeList",
                                        "/video/detail"
                                )
                                .permitAll()

                                // 所有后台接口都必须是管理员
                                .requestMatchers("/admin/**")
                                .hasRole("ADMIN")


                                // 其他接口暂时全部允许
                                .anyRequest()
                                .authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }

}
