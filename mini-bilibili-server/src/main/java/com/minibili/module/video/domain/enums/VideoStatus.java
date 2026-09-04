package com.minibili.module.video.domain.enums;

public enum VideoStatus {
    PENDING(0,"待发布"),
    PUBLISH(1,"已发布"),
    HOLDOUT(2,"已下架"),
    DELETE(3,"已删除");

    private final String text;
    private final Integer code;

    VideoStatus(Integer code, String text){
        this.text = text;
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public static VideoStatus fromCode(Integer code) {
        for (VideoStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }

        throw new IllegalArgumentException("未知的视频状态：" + code);
    }
}
