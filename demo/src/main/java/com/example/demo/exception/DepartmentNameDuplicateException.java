package com.example.demo.exception;

/**
 * 部门名称重复异常
 */
public class DepartmentNameDuplicateException extends BaseException {
    public DepartmentNameDuplicateException(String message) {
        super(message);
    }
} 