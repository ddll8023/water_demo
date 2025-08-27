package com.example.demo.exception;

import com.example.demo.constant.MessageConstant;

/**
 * 用户名重复异常
 */
public class UsernameDuplicateException extends BaseException {

    public UsernameDuplicateException() {
        super(MessageConstant.USER_USERNAME_DUPLICATE);
    }

    public UsernameDuplicateException(String msg) {
        super(msg);
    }
} 