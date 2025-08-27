package com.example.demo.pojo.DTO.system;

import java.io.Serializable;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * 用户查询DTO
 * 用于用户分页查询的请求参数
 */
@Data
@Schema(description = "用户查询DTO")
public class UserQueryDTO implements Serializable {

    @Min(value = 1, message = "页码必须大于0")
    @Schema(description = "当前页码", example = "1")
    private Integer page = 1;

    @Min(value = 1, message = "每页记录数必须大于0")
    @Max(value = 100, message = "每页记录数不能超过100")
    @Schema(description = "每页记录数", example = "10")
    private Integer size = 10;

    @Size(max = 50, message = "用户名长度不能超过50个字符")
    @Schema(description = "用户名搜索", example = "admin")
    private String username;

    @Schema(description = "角色ID筛选", example = "1")
    private Long roleId;

    @Schema(description = "用户状态筛选", example = "1")
    private String isActive;

    @Schema(description = "排序字段", example = "createdAt")
    private String sortBy = "createdAt";

    @Schema(description = "排序方向", example = "DESC")
    private String sortOrder = "DESC";
} 