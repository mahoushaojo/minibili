package com.minibili.module.auth.service.impl;

import com.minibili.common.exception.BusinessException;
import com.minibili.module.auth.domain.dto.LoginDTO;
import com.minibili.module.auth.domain.vo.LoginVO;
import com.minibili.module.auth.service.AuthService;
import com.minibili.module.user.domain.entity.Users;
import com.minibili.module.user.mapper.UserMapper;
import com.minibili.security.JwtService;
import com.minibili.security.LoginSession;
import com.minibili.security.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    // 定义操作user数据库的mapper
    private final UserMapper userMapper;
    // 定义验证用户密码的方法
    private final PasswordEncoder passwordEncoder;
    // 定义jwt的方法
    private final JwtService jwtService;
    // 定义RefreshToken的方法
    private final RefreshTokenService refreshTokenService;

    // 用户登录
    @Override
    @Transactional
    public LoginVO login(LoginDTO dto){
        // 1. 先判断用户是否存在
        Users users = userMapper.findByEmail(dto.getEmail());
        if (users == null){
            throw new BusinessException(403,"该用户不存在");
        }
        // 2. 判断密码是否正确
        boolean matches = passwordEncoder.matches(dto.getPassword(), users.getPassword());
        if (!matches){
            throw new BusinessException(403,"用户名或密码错误");
        }
        // 3. 每次登录创建一个独立的设备会话
        LoginSession session = new LoginSession();
        session.setSessionId(UUID.randomUUID().toString().replace("-", ""));
        session.setUserName(users.getName());
        session.setUserId(users.getId());
        session.setLoginTime(LocalDateTime.now());

        // 4. accessToken和refreshToken绑定同一个sessionId
        String accessToken = jwtService.createToken(users, session.getSessionId());
        String refreshToken = refreshTokenService.createRefreshToken(session);

        // 5.返回accessToken与用户基本信息给前端
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setUserId(users.getId());
        loginVO.setUserName(users.getName());
        return loginVO;
    }

    // 刷新token
    @Override
    public LoginVO refresh(String oldToken){
        // 1. 检查有没有携带cookie
        if (oldToken == null || oldToken.isBlank()){
            throw new BusinessException(403, "未提供刷新凭证，请重新登录");
        }
        // 2. 查询redis中是否存在token
        LoginSession session = refreshTokenService.getSession(oldToken);
        log.info("session:{}",session);
        if (session == null){
            throw new BusinessException(40001, "登录状态已过期，请重新登录");
        }
        // 3. 创建新的token
        Users users = userMapper.findById(session.getUserId());
        if (users == null){
            throw new BusinessException(40002, "用户不存在，请重新登录");
        }
        String accessToken = jwtService.createToken(users, session.getSessionId());
        String newRefreshToken = refreshTokenService.rotate(oldToken, session);
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(newRefreshToken);
        loginVO.setUserId(users.getId());
        loginVO.setUserName(users.getName());
        return loginVO;
    }

    // 登出用户
    @Override
    public Boolean loginOut(String refreshToken){
        // 删除redis中存储的refreshToken
       if (refreshToken != null && !refreshToken.isBlank()) {
           refreshTokenService.delete(refreshToken);
       }
       return true;
    }
}
