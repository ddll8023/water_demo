package com.example.demo.pojo.DTO.warning;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;

/**
 * 预警解除DTO
 * 用于解除预警的请求
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WarningResolveDTO {

    /**
     * 预警记录ID
     * 注意：ID通过路径参数传递，不需要在请求体中验证
     */
    private Long id;

    /**
     * 解除备注
     */
    @Size(max = 500, message = "解除备注长度不能超过500个字符")
    private String resolveRemark;
}
