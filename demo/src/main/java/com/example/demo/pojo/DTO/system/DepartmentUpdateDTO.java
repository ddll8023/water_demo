package com.example.demo.pojo.DTO.system;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;

/**
 * 部门更新DTO
 * 用于更新部门信息的请求
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DepartmentUpdateDTO {

    /**
     * 部门名称
     */
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

    /**
     * 部门是否启用
     */
    private String isActive;
}
