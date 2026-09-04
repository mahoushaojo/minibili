package com.minibili.module.user.service.impl;

import com.github.pagehelper.PageInfo;
import com.minibili.common.comment.PageResult;
import com.minibili.common.comment.PageUtils;
import com.minibili.common.exception.BusinessException;
import com.minibili.app.user.domain.dto.EditUserDTO;
import com.minibili.app.user.domain.dto.RegisterDTO;
import com.minibili.app.user.domain.dto.UserListDTO;
import com.minibili.module.user.domain.entity.Users;
import com.minibili.module.user.domain.enums.UserRole;
import com.minibili.app.user.domain.vo.UsersVO;
import com.minibili.module.user.mapper.UserMapper;
import com.minibili.module.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder){
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 注册用户
     * @param dto
     * @return
     */
    @Override
    public Integer register(RegisterDTO dto){
        log.info("注册用户,phone:{}",dto.getPhone());
        if (findByName(dto.getName()) != null){
            throw new BusinessException(403, "该用户名已存在");
        }
        if (findByEmail(dto.getEmail()) != null){
            throw new BusinessException(403, "该邮箱已被使用");
        }
        if (findByPhone(dto.getPhone())!=null){
            throw new BusinessException(403, "该邮箱已被使用");
        }
        // 加密密码
        String encodePassword = passwordEncoder.encode(dto.getPassword());
        log.info("encodePassword:{}", encodePassword);
        dto.setPassword(encodePassword);
        return userMapper.register(dto);
    }

    /**
     * 根据用户名获取用户信息
     * @param name
     * @return
     */
    @Override
    public Users findByName(String name){
        Users users = userMapper.findByName(name);

        if (users == null){
            return null;
        }
        return users;
    }

    /**
     * 根据手机号查询用户
     * @param phone
     * @return
     */
    @Override
    public Users findByPhone(String phone){
        Users users = userMapper.findByPhone(phone);
        if (users == null){
            return null;
        }
        return users;
    }

    /**
     * 根据邮箱查询用户
     * @param email
     * @return
     */
    @Override
    public Users findByEmail(String email){
        Users users = userMapper.findByEmail(email);

        if (users == null){
            return null;
        }
        return users;
    }

    /**
     * 根据用户ID查询用户
     * @param id
     * @return
     */
    @Override
    public UsersVO findById(Long id){
        Users users = userMapper.findById(id);
        if (users == null){
            throw new BusinessException(403,"不存在该用户");
        }
        return toUserVO(users);

    }
    /**
     * 修改用户信息
     * @param editUserDTO
     * @return
     */
    @Override
    public int editUser(EditUserDTO editUserDTO){
        // 判断是否存在该用户
        Users users = userMapper.findById(editUserDTO.getId());
        if (users == null){
            throw new BusinessException(403, "不存在该用户");
        }
        return userMapper.editUser(editUserDTO);
    }

    /**
     * 获取用户列表
     * @param userListDTO
     * @return
     */
    @Override
    public PageResult<UsersVO> getUserList(UserListDTO userListDTO){
        PageUtils.startPage(userListDTO);
        List<Users> list = userMapper.getUserList(userListDTO);
        // 处理并转换数据
        List<UsersVO> dataList = list.stream().map(this::toUserVO).toList();
        return PageResult.from(new PageInfo<>(dataList));
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @Override
    public int deleteUser(Long myUserId,Long id){
        // 需要判断当前用户的权限是否为管理员 不然不允许删除用户
        Users myInfo = userMapper.findById(myUserId);
        if (myInfo.getRole() != UserRole.ADMIN.getCode()){
            throw new BusinessException(403,"当前用户权限不足");
        }
        return userMapper.deleteUser(id);
    }

    private UsersVO toUserVO(Users users){
        if (users == null){
            return null;
        }
        UsersVO vo = new UsersVO();
        BeanUtils.copyProperties(users, vo);
        return vo;
    }
}
