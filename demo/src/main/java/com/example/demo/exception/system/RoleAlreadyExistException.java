package com.example.demo.exception.system;

import com.example.demo.exception.BaseException;

/**
 * 角色已存在异常
 */
public class RoleAlreadyExistException extends BaseException {
    public RoleAlreadyExistException(String message) {
        super(message);
    }
} 