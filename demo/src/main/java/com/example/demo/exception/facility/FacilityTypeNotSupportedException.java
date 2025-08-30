package com.example.demo.exception.facility;

import com.example.demo.exception.BaseException;

/**
 * 设施类型不支持异常
 * 当请求的设施类型在系统中未配置或不存在时抛出
 */
public class FacilityTypeNotSupportedException extends BaseException {
    
    public FacilityTypeNotSupportedException(String message) {
        super(message);
    }
} 