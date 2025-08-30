package com.example.demo.exception.facility;

import com.example.demo.exception.BaseException;

/**
 * 设施DTO转换异常
 * 当Map转换为DTO对象失败或DTO类型不匹配时抛出
 */
public class FacilityDTOConvertException extends BaseException {
    
    public FacilityDTOConvertException(String message) {
        super(message);
    }
    
    public FacilityDTOConvertException(String message, Throwable cause) {
        super(message);
    }
} 