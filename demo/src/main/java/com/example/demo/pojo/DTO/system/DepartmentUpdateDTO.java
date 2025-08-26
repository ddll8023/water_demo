package com.example.demo.pojo.DTO.system;

import java.io.Serializable;

import lombok.Data;

import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 部门更新DTO
 * 用于更新部门信息的请求
 */
@Data
@Schema(description = "部门更新DTO")
public class DepartmentUpdateDTO implements Serializable {

    @Size(max = 255, message = "部门名称长度不能超过255个字符")
    @Schema(description = "部门名称", required = true, example = "研发部")
    private String name;

    @Schema(description = "父部门 ID (支持层级)", required = true, example = "1")
    private Long parentId;

    @Schema(description = "部门职责", required = true, example = "负责研发工作")
    private String duty;

    @Size(max = 255, message = "联系方式长度不能超过255个字符")
    @Schema(description = "联系方式", required = true, example = "13800138000")
    private String contact;

    @Schema(description = "所属行政区域 ID", required = true, example = "1")
    private Long regionId;

    @Schema(description = "部门是否启用", required = true, example = "1")
    private String isActive;
}
