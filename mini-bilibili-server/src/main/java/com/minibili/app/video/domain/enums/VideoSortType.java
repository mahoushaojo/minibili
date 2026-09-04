package com.minibili.app.video.domain.enums;

public enum VideoSortType {
    RECOMMEND(1, "推荐"),
    HOT(2, "热门"),
    LATEST(3, "最新");

    private final Integer code;
    private final String text;

    VideoSortType(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public Integer getCode() {
        return code;
    }
    public String getText(){
        return text;
    }
    public static VideoSortType fromCode(Integer code) {
        for (VideoSortType sortType : values()) {
            if (sortType.code.equals(code)) {
                return sortType;
            }
        }

        throw new IllegalArgumentException("未知的状态：" + code);
    }
}
