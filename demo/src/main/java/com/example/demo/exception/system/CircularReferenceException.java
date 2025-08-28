package com.example.demo.exception.system;

import com.example.demo.exception.BaseException;

/**
 * 循环引用异常
 */
public class CircularReferenceException extends BaseException {
    public CircularReferenceException(String message) {
        super(message);
    }
} 