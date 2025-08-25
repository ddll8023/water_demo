package com.example.demo.pojo.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证结果响应DTO
 * 用于返回数据验证的结果信息，主要应用于前端表单验证场景
 * 例如：检查用户名是否已存在、编码是否重复等
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResponseDTO {
    
    /**
     * 是否可用
     * true表示验证通过，数据可以使用
     * false表示验证不通过，数据不可用（如已存在）
     */
    private Boolean available;
    
    /**
     * 验证消息
     * 对验证结果的详细说明，用于向用户展示提示信息
     * 例如："用户名已存在"、"编码可用"等
     */
    private String message;
}
