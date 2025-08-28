package com.example.demo.exception.system;

import com.example.demo.exception.BaseException;

/**
 * 字典类型创建异常
 * 用于字典类型创建过程中的业务异常处理
 */
public class DictTypeCreateException extends BaseException {
    
    public DictTypeCreateException(String message) {
        super(message);
    }
} 