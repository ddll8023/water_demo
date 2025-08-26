package com.example.demo.exception;

/**
 * 部门有子部门异常
 */
public class DepartmentHasChildrenException extends BaseException {
    public DepartmentHasChildrenException(String message) {
        super(message);
    }
} 