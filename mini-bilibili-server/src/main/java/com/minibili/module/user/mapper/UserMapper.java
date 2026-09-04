package com.minibili.module.user.mapper;

import com.minibili.app.user.domain.dto.EditUserDTO;
import com.minibili.app.user.domain.dto.RegisterDTO;
import com.minibili.app.user.domain.dto.UserListDTO;
import com.minibili.module.user.domain.entity.Users;
import com.minibili.module.user.domain.enums.UserCountType;
import org.apache.ibatis.annotations.*;

import java.util.List;

//用户的mapper
@Mapper
public interface UserMapper {
    /**
     * 注册用户
     * @param dto
     * @return
     */
    @Insert("""
    insert into users (name,phone,email,password)
    values (#{name},#{phone} ,#{email} ,#{password} )
""")
    Integer register(RegisterDTO dto);

    /**
     * 根据用户名查询用户
     */
    @Select("select * from users where name = #{name}")
    public Users findByName(String name);

    /**
     * 根据用户名查询用户
     * @param phone
     * @return
     */
    @Select("select * from users where phone = #{phone} ")
    Users findByPhone(String phone);

    /**
     * 根据邮箱查询用户信息
     * @param email
     * @return
     */
    @Select("select * from users where email = #{email} ")
    Users findByEmail(String email);

    /**
     * 根据用户id查询用户信息
     * @param id
     * @return
     */
    @Select("select * from users where id = #{id} ")
    Users findById(Long id);

    /**
     * 修改用户基本信息
     * @param dto
     * @return
     */
    int editUser(EditUserDTO dto);

    /**
     * 根据条件查询用户列表
     * page使用了myBatis的插件 不需要在sql中再进行传递和计算limit了
     * @param dto
     * @return
     */
    List<Users> getUserList(UserListDTO dto);

    /**
     * 删除用户
     * @param id
     * @return
     */
    @Delete("delete from users where id = #{id}")
    int deleteUser(Long id);

    /**
     * 跟进type更新用户的各种数量
     * VIDEO 视频
     * LIKE 点赞
     * COMMENT 收藏
     * FANS 粉丝
     * delta +1 / -1
     */
    boolean updateCount(@Param("userId") Long userId, @Param("type") UserCountType type, @Param("delta") int delta);
}
