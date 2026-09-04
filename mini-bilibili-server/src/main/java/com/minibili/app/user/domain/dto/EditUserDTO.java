package com.minibili.app.user.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditUserDTO {
    @NotNull(message = "用户ID不能为null")
    private Long id;
    @Size(max = 255, message = "个人介绍长度不能超过255个字符")
    private String bio;
    @Size(max = 255, message = "头像链接长度过长")
    private String avatar;
}
