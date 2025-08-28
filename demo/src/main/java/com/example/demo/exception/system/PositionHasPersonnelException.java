package com.example.demo.exception.system;

import com.example.demo.exception.BaseException;

/**
 * 岗位下有人员异常
 */
public class PositionHasPersonnelException extends BaseException {
    public PositionHasPersonnelException(String message) {
        super(message);
    }
} 