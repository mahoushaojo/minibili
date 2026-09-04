package com.minibili.module.user.domain.enums;
// 用户状态
public enum UserStatus {
    NORMAL(1, "正常"),
    DISABLED(2,"已禁用");

    private final Integer code;
    private final String text;

    UserStatus(Integer code, String text){
        this.code = code;
        this.text = text;
    }

    public Integer getCode(){
        return this.code;
    }

    public String getText() {
        return text;
    }

    // 根据数据库的值转换枚举
    public static UserStatus fromCode(Integer code){
        for (UserStatus status : values()){
            if (status.code.equals(code)){
                return  status;
            }
        }
        throw new RuntimeException("未知状态");
    }
}
