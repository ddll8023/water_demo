package com.example.demo.exception.system;

import com.example.demo.constant.MessageConstant;
import com.example.demo.exception.BaseException;

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