package com.example.demo.exception.system;

import com.example.demo.exception.BaseException;

/**
 * 密码错误异常
 */
public class PasswordErrorException extends BaseException {
    public PasswordErrorException(String message) {
        super(message);
    }
}
