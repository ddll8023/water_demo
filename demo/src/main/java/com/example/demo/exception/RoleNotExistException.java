package com.example.demo.exception;

/**
 * 角色不存在异常
 */
public class RoleNotExistException extends BaseException {
    public RoleNotExistException(String message) {
        super(message);
    }
} 