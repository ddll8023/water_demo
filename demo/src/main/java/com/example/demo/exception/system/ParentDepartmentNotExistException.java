package com.example.demo.exception.system;

import com.example.demo.exception.BaseException;

/**
 * 父部门不存在异常
 */
public class ParentDepartmentNotExistException extends BaseException {
    public ParentDepartmentNotExistException(String message) {
        super(message);
    }
} 