package com.example.demo.exception.facility;

import com.example.demo.exception.BaseException;

/**
 * 不支持的设施类型异常
 */
public class UnsupportedFacilityTypeException extends BaseException {
    
    public UnsupportedFacilityTypeException(String message) {
        super(message);
    }
} 