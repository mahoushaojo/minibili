package com.minibili.module.auth.domain.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @NotBlank(message = "邮箱不能为空")
    private String email; //邮箱
    @NotBlank(message = "密码不能为空")
    private String password; //密码
}