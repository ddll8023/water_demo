package com.example.demo.exception;

/**
 * 岗位不存在异常
 */
public class PositionNotExistException extends BaseException {
    public PositionNotExistException(String message) {
        super(message);
    }
} 