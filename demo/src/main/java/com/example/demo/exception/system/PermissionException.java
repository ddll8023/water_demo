package com.example.demo.exception.system;

import com.example.demo.exception.BaseException;

/**
 * 权限相关异常
 */
public class PermissionException extends BaseException {
    public PermissionException(String message) {
        super(message);
    }
} 