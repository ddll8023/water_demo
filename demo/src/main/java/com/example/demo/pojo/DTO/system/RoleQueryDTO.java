package com.example.demo.pojo.DTO.system;

import java.io.Serializable;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 角色查询DTO
 * 用于角色分页查询的请求参数
 */
@Data
@Schema(description = "角色查询DTO")
public class RoleQueryDTO implements Serializable {

    @Min(value = 1, message = "页码必须大于0")
    @Schema(description = "当前页码", example = "1")
    private Integer page = 1;

    @Min(value = 1, message = "每页记录数必须大于0")
    @Max(value = 100, message = "每页记录数不能超过100")
    @Schema(description = "每页记录数", example = "10")
    private Integer size = 10;

    @Schema(description = "角色名称搜索", example = "管理员")
    private String name;

    @Schema(description = "角色状态筛选", example = "1")
    private String isActive;

    @Schema(description = "排序字段", example = "createdAt")
    private String sortBy = "createdAt";

    @Schema(description = "排序方向", example = "DESC")
    private String sortOrder = "DESC";
} 