package com.example.demo.exception;

import com.example.demo.constant.MessageConstant;

/**
 * 用户不存在异常
 */
public class AccountNotExistException extends BaseException {
    public AccountNotExistException(String msg) {
        super(msg);
    }
}
