package com.prashantlabs.documents.web;

import java.time.OffsetDateTime;

public record ApiResponse<T>(boolean success, String code, String message, T data, OffsetDateTime timestamp) {

    public static <T> ApiResponse<T> ok(String msg, T data) {
        return new ApiResponse<>(true, "OK", msg, data, OffsetDateTime.now());
    }

    public static ApiResponse<Void> error(String code, String msg) {
        return new ApiResponse<>(false, code, msg, null, OffsetDateTime.now());
    }
}
