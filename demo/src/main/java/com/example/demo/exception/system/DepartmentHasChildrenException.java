package com.example.demo.exception.system;

import com.example.demo.exception.BaseException;

/**
 * 部门有子部门异常
 */
public class DepartmentHasChildrenException extends BaseException {
    public DepartmentHasChildrenException(String message) {
        super(message);
    }
} 