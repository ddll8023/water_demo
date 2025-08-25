package com.example.demo.exception;

import com.example.demo.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public ApiResponse<Void> exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return ApiResponse.error(ex.getMessage());
    }

    /**
     *
     * @param ex
     * @return
     */
    @ExceptionHandler
    public ApiResponse<Void> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        String msg = ex.getMessage();
        if(msg.contains("Duplicate entry")){
            String[] split = msg.split(" ");
            String username = split[2];
            msg = username + "已存在";
            return ApiResponse.error(msg);
        }
        return ApiResponse.error(ex.getMessage());
    }

}
