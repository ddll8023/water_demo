package com.example.demo.exception.system;

import com.example.demo.constant.MessageConstant;
import com.example.demo.exception.BaseException;

/**
 * 用户自我禁用异常
 */
public class UserSelfDisableException extends BaseException {

    public UserSelfDisableException() {
        super(MessageConstant.USER_CANNOT_DISABLE_SELF);
    }

    public UserSelfDisableException(String msg) {
        super(msg);
    }
} 