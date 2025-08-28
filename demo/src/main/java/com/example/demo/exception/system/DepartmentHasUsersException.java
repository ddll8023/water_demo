package com.example.demo.exception.system;

import com.example.demo.exception.BaseException;

/**
 * 部门有用户异常
 */
public class DepartmentHasUsersException extends BaseException {
    public DepartmentHasUsersException(String message) {
        super(message);
    }
} 