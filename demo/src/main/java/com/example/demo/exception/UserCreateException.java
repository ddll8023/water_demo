package com.example.demo.exception;

import com.example.demo.constant.MessageConstant;

/**
 * 用户创建异常
 */
public class UserCreateException extends BaseException {

    public UserCreateException() {
        super(MessageConstant.USER_CREATE_FAILED);
    }

    public UserCreateException(String msg) {
        super(msg);
    }
} 