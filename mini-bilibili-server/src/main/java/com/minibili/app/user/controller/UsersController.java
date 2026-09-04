package com.minibili.app.user.controller;

import com.minibili.app.user.domain.dto.CollectListDTO;
import com.minibili.common.api.ApiResponse;
import com.minibili.common.comment.PageResult;
import com.minibili.app.user.domain.dto.EditUserDTO;
import com.minibili.app.user.domain.dto.RegisterDTO;
import com.minibili.app.user.domain.dto.UserListDTO;
import com.minibili.app.user.domain.vo.UsersVO;
import com.minibili.module.collect.domain.vo.CollectListVO;
import com.minibili.module.collect.service.CollectService;
import com.minibili.module.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UsersController {
    private final UserService userService;
    private final CollectService collectService;
    public UsersController(UserService userService, CollectService collectService) {
        this.userService = userService;
        this.collectService = collectService;
    }

    /**
     * 注册用户
     * @param dto
     * @return
     */
    @PostMapping("/register")
    public ApiResponse<Integer> register(@Valid @RequestBody RegisterDTO dto){
        return ApiResponse.success(userService.register(dto));
    }
    @GetMapping("/info")
    public ApiResponse<UsersVO> userInfo(@RequestAttribute("userId") Long userId){
        return ApiResponse.success(userService.findById(userId));
    }
    /**
     * 根据用户id获取用户信息
     * @param id
     * @return
     */
    @GetMapping("/getUserInfo/{id}")
    public ApiResponse<UsersVO> getUserInfo(@PathVariable Long id){
        return ApiResponse.success(userService.findById(id));
    }

    /**
     * 修改用户信息
     * @param editUserDTO
     * @return
     */
    @PutMapping("/edit")
    public ApiResponse<Integer> editUser(@Valid @RequestBody EditUserDTO editUserDTO){
        return ApiResponse.success(userService.editUser(editUserDTO));
    }

    /**
     * 获取用户列表
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public ApiResponse<PageResult<UsersVO>> getUserList(@Valid @RequestBody UserListDTO dto){
        return ApiResponse.success(userService.getUserList(dto));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Integer> deleteUser(@RequestAttribute("userId") Long userId, @PathVariable Long id){
        return ApiResponse.success(userService.deleteUser(userId, id));
    }

    /**
     * 获取用户收藏列表
     */
    @PostMapping("/collectList")
    public ApiResponse<PageResult<CollectListVO>>getCollectList(@RequestAttribute("userId") Long userId, @Valid @RequestBody CollectListDTO dto){
        return ApiResponse.success(collectService.getCollectList(userId,dto));
    }
}
