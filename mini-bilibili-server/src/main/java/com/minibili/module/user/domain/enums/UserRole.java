package com.minibili.module.user.domain.enums;

public enum UserRole {
    USER(1,"普通用户"),
    ADMIN(2,"管理员");

    private final Integer code;
    private final String text;

    UserRole(Integer code, String text){
        this.code = code;
        this.text = text;
    }

    public String getText() {
        return text;
    }
    public Integer getCode() {
        return code;
    }
}
