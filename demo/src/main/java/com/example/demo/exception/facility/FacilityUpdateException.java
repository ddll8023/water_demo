package com.example.demo.exception.facility;

import com.example.demo.exception.BaseException;

/**
 * 设施更新异常
 * 当设施更新操作失败时抛出
 */
public class FacilityUpdateException extends BaseException {
    
    public FacilityUpdateException(String message) {
        super(message);
    }
    
    public FacilityUpdateException(String message, Throwable cause) {
        super(message);
    }
} 