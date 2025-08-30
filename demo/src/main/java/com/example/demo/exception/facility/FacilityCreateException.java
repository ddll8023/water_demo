package com.example.demo.exception.facility;

import com.example.demo.exception.BaseException;

/**
 * 设施创建异常
 * 当设施创建操作失败时抛出
 */
public class FacilityCreateException extends BaseException {
    
    public FacilityCreateException(String message) {
        super(message);
    }
    
    public FacilityCreateException(String message, Throwable cause) {
        super(message);
    }
} 