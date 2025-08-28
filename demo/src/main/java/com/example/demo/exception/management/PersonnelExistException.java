package com.example.demo.exception.management;

import com.example.demo.exception.BaseException;

/**
 * 人员已存在异常
 */
public class PersonnelExistException extends BaseException {
    public PersonnelExistException(String message) {
        super(message);
    }
}
