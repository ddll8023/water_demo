package com.example.demo.exception.system;

import com.example.demo.exception.BaseException;

/**
 * 岗位不存在异常
 */
public class PositionNotExistException extends BaseException {
    public PositionNotExistException(String message) {
        super(message);
    }
} 