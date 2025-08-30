package com.example.demo.exception.facility;

import com.example.demo.exception.BaseException;

/**
 * 设施删除异常
 * 当设施删除操作失败时抛出
 */
public class FacilityDeleteException extends BaseException {
    
    public FacilityDeleteException(String message) {
        super(message);
    }
    
    public FacilityDeleteException(String message, Throwable cause) {
        super(message);
    }
} 