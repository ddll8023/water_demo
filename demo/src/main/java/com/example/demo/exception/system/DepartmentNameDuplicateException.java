package com.example.demo.exception.system;

import com.example.demo.exception.BaseException;

/**
 * 部门名称重复异常
 */
public class DepartmentNameDuplicateException extends BaseException {
    public DepartmentNameDuplicateException(String message) {
        super(message);
    }
} 