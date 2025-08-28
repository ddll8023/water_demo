package com.example.demo.exception.system;

import com.example.demo.exception.BaseException;

/**
 * 岗位名称重复异常
 */
public class PositionNameDuplicateException extends BaseException {
    public PositionNameDuplicateException(String message) {
        super(message);
    }
} 