package com.example.demo.exception.system;

import com.example.demo.exception.BaseException;

/**
 * 字典查询参数无效异常
 */
public class DictQueryParamInvalidException extends BaseException {

    public DictQueryParamInvalidException(String message) {
        super(message);
    }
} 