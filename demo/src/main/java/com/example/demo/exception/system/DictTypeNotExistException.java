package com.example.demo.exception.system;

import com.example.demo.exception.BaseException;

/**
 * 字典类型不存在异常
 */
public class DictTypeNotExistException extends BaseException {
    public DictTypeNotExistException(String message) {
        super(message);
    }
} 