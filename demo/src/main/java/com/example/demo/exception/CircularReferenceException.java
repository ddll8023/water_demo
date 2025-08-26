package com.example.demo.exception;

/**
 * 循环引用异常
 */
public class CircularReferenceException extends BaseException {
    public CircularReferenceException(String message) {
        super(message);
    }
} 