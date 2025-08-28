package com.example.demo.exception.system;

import com.example.demo.exception.BaseException;

/**
 * Token无效异常
 */
public class TokenInvalidException extends BaseException {
    public TokenInvalidException(String msg) {
        super(msg);
    }
} 