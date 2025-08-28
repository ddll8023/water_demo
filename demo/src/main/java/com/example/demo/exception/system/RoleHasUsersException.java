package com.example.demo.exception.system;

import com.example.demo.exception.BaseException;

/**
 * 角色下有用户无法删除异常
 */
public class RoleHasUsersException extends BaseException {
    public RoleHasUsersException(String message) {
        super(message);
    }
} 