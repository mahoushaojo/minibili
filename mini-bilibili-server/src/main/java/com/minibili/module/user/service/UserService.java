package com.minibili.module.user.service;

import com.minibili.common.comment.PageResult;
import com.minibili.app.user.domain.dto.EditUserDTO;
import com.minibili.app.user.domain.dto.RegisterDTO;
import com.minibili.app.user.domain.dto.UserListDTO;
import com.minibili.module.user.domain.entity.Users;
import com.minibili.app.user.domain.vo.UsersVO;

import java.util.List;

public interface UserService {
    // 注册用户
    Integer register(RegisterDTO dto);
    // 根据用户名获取信息
    Users findByName(String name);
    // 根据手机号获取用户信息
    Users findByPhone(String phone);
    // 根据邮箱获取用户信息
    Users findByEmail(String phone);
    // 根据用户id获取用户信息
    UsersVO findById(Long id);
    // 修改用户信息
    int editUser(EditUserDTO editUserDTO);
    // 查询用户列表
    PageResult<UsersVO> getUserList(UserListDTO userListDTO);
    // 删除用户
    int deleteUser(Long myUserId ,Long id);
    // 为用户
}

