package com.example.demo.pojo.dto.system;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;

/**
 * 角色更新DTO
 * 用于更新角色信息的请求
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RoleUpdateDTO {

    /**
     * 角色名称
     */
    @Size(max = 100, message = "角色名称长度不能超过100个字符")
    private String name;

    /**
     * 角色描述
     */
    private String description;
    
    /**
     * 排序值，数值越小优先级越高
     */
    private Integer sortOrder;
    
    /**
     * 角色是否启用
     */
    private Boolean isActive;
}
