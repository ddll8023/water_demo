package com.example.demo.exception.facility;

/**
 * 字段设置异常
 * 当反射设置对象字段失败时抛出
 */
public class FieldSettingException extends FacilityOperationException {
    
    public FieldSettingException(String message) {
        super(message);
    }
    
    public FieldSettingException(String message, Throwable cause) {
        super(message);
        this.initCause(cause);
    }
} 