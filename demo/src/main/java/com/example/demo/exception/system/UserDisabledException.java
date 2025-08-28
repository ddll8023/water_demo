package com.example.demo.exception.system;

import com.example.demo.exception.BaseException;

/**
 * 用户禁用异常
 */
public class UserDisabledException extends BaseException {
    public UserDisabledException(String message) {
        super(message);
    }
}
