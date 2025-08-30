package com.example.demo.exception.facility;

import com.example.demo.exception.BaseException;

/**
 * 设施未找到异常
 */
public class FacilityNotFoundException extends BaseException {
    
    public FacilityNotFoundException(String message) {
        super(message);
    }
} 