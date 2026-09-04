package com.minibili.common.api;

public record ApiResponse<T>(int code, String message,Boolean status, T data) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "success",true, data);
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(0, "success",true, null);
    }

    public static ApiResponse<Void> failure(int code, String message) {
        return new ApiResponse<>(code, message, false,null);
    }
}
