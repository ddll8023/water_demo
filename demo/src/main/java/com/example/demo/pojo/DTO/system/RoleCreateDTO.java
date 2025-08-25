package com.example.demo.pojo.DTO.system;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;

/**
 * 角色创建DTO
 * 用于创建新角色的请求
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RoleCreateDTO {

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 100, message = "角色名称长度不能超过100个字符")
    private String name;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 排序值，数值越小优先级越高
     */
    @Min(value = 1, message = "排序值必须大于0")
    private Integer sortOrder = 50;  // 默认排序值
    
    /**
     * 角色是否启用
     */
    private Boolean isActive = true;  // 默认为启用状态
}
