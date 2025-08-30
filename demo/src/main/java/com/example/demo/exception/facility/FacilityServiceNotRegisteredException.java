package com.example.demo.exception.facility;

import com.example.demo.exception.BaseException;

/**
 * 设施服务未注册异常
 * 当指定设施类型的服务Bean未在Spring容器中注册时抛出
 */
public class FacilityServiceNotRegisteredException extends BaseException {
    
    public FacilityServiceNotRegisteredException(String message) {
        super(message);
    }
} 