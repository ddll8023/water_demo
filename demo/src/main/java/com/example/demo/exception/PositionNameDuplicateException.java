package com.example.demo.exception;

/**
 * 岗位名称重复异常
 */
public class PositionNameDuplicateException extends BaseException {
    public PositionNameDuplicateException(String message) {
        super(message);
    }
} 