package com.example.demo.exception.system;

import com.example.demo.exception.BaseException;

/**
 * 岗位名称为空异常
 */
public class PositionNameEmptyException extends BaseException {
    public PositionNameEmptyException(String message) {
        super(message);
    }
} 