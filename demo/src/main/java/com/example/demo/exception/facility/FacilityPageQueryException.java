package com.example.demo.exception.facility;

import com.example.demo.exception.BaseException;

/**
 * 设施分页查询异常
 * 当设施分页查询操作失败时抛出
 */
public class FacilityPageQueryException extends BaseException {
    
    public FacilityPageQueryException(String message) {
        super(message);
    }
    
    public FacilityPageQueryException(String message, Throwable cause) {
        super(message);
    }
} 