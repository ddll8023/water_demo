package com.example.demo.exception.system;

import com.example.demo.exception.BaseException;

/**
 * 用户不存在异常
 */
public class AccountNotExistException extends BaseException {
    public AccountNotExistException(String msg) {
        super(msg);
    }
}
