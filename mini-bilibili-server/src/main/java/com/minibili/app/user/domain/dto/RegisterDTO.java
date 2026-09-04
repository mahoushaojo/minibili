package com.minibili.app.user.domain.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//注册用户的类
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
    @NotBlank(message = "用户名不能为空")
    private String name; // 用户名
    @NotBlank(message = "手机号不能为空")
    private String phone; // 手机号
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式错误")
    private String email; // 邮箱
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20,message = "密码长度需要大于6小于20")
    private String password; //密码
}
