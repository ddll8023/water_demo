package com.example.demo.exception.system;

import com.example.demo.exception.BaseException;

/**
 * 角色不存在异常
 */
public class RoleNotExistException extends BaseException {
    public RoleNotExistException(String message) {
        super(message);
    }
} 