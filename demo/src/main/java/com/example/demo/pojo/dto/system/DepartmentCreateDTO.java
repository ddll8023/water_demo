package com.example.demo.pojo.dto.system;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 部门创建DTO
 * 用于创建新部门的请求
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DepartmentCreateDTO {

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 255, message = "部门名称长度不能超过255个字符")
    private String name;

    /**
     * 父部门 ID (支持层级)
     */
    private Long parentId;

    /**
     * 部门职责
     */
    private String duty;

    /**
     * 联系方式
     */
    @Size(max = 255, message = "联系方式长度不能超过255个字符")
    private String contact;

    /**
     * 所属行政区域 ID
     */
    private Long regionId;
}
