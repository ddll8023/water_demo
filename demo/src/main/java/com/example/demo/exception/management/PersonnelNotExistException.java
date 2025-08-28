package com.example.demo.exception.management;

import com.example.demo.exception.BaseException;

/**
 * 人员不存在异常
 * 当尝试操作一个不存在的人员记录时抛出此异常
 */
public class PersonnelNotExistException extends BaseException {
    public PersonnelNotExistException(String message) {
        super(message);
    }
}
