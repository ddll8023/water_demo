package com.example.demo.exception.system;

import com.example.demo.exception.BaseException;

/**
 * 部门不存在异常
 */
public class DepartmentNotExistException extends BaseException {
    public DepartmentNotExistException(String message) {
        super(message);
    }
} 