package com.example.demo.exception;

/**
 * 角色已存在异常
 */
public class RoleAlreadyExistException extends BaseException {
    public RoleAlreadyExistException(String message) {
        super(message);
    }
} 