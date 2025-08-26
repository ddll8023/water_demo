package com.example.demo.exception;

/**
 * 字典类型不存在异常
 */
public class DictTypeNotExistException extends BaseException {
    public DictTypeNotExistException(String message) {
        super(message);
    }
} 