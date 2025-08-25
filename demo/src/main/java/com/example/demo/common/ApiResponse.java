package com.example.demo.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统一API响应格式
 * 用于所有API接口的统一响应结构
 */
@Data
@Schema(description = "统一API响应格式")
public class ApiResponse<T> implements Serializable {
    
    @Schema(description = "响应状态码")
    private int code;
    
    @Schema(description = "响应消息")
    private String message;
    
    @Schema(description = "响应数据")
    private T data;
    
    @Schema(description = "响应时间戳")
    private LocalDateTime timestamp;
    
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * 完整构造函数
     */
    public ApiResponse(int code, String message, T data) {
        this();
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    /**
     * 成功响应（无数据，无消息）
     */
    public static <T> ApiResponse<T> success() {
        ApiResponse<T> result = new ApiResponse<T>();
        result.code = 200;
        result.message = "操作成功";
        return result;
    }
    
    /**
     * 成功响应（带数据，默认消息）
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> result = new ApiResponse<T>();
        result.code = 200;
        result.message = "操作成功";
        result.data = data;
        return result;
    }
    
    /**
     * 成功响应（带数据和自定义消息）
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> result = new ApiResponse<T>();
        result.code = 200;
        result.message = message;
        result.data = data;
        return result;
    }
    
    /**
     * 成功响应（只有自定义消息）
     */
    public static <T> ApiResponse<T> success(String message) {
        ApiResponse<T> result = new ApiResponse<T>();
        result.code = 200;
        result.message = message;
        return result;
    }
    
    /**
     * 错误响应（自定义状态码和消息）
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> result = new ApiResponse<T>();
        result.code = code;
        result.message = message;
        return result;
    }
    
    /**
     * 错误响应（默认400状态码）
     */
    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> result = new ApiResponse<T>();
        result.code = 400;
        result.message = message;
        return result;
    }

    /**
     * 错误响应（带数据，默认400状态码）
     */
    public static <T> ApiResponse<T> error(String message, T data) {
        ApiResponse<T> result = new ApiResponse<T>();
        result.code = 400;
        result.message = message;
        result.data = data;
        return result;
    }

}
