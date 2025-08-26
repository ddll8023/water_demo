package com.example.demo.exception;

/**
 * 字典数据创建异常
 * 用于字典数据创建过程中的业务异常处理
 */
public class DictDataCreateException extends BaseException {
    
    public DictDataCreateException(String message) {
        super(message);
    }
} 