package com.example.demo.exception.system;

import com.example.demo.constant.MessageConstant;
import com.example.demo.exception.BaseException;

/**
 * 用户不存在异常
 */
public class UserNotExistException extends BaseException {

    public UserNotExistException() {
        super(MessageConstant.USER_NOT_EXIST);
    }

    public UserNotExistException(String msg) {
        super(msg);
    }
} 