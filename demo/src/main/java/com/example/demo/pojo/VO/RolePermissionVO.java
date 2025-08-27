package com.example.demo.pojo.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色权限视图对象
 * 专门用于角色权限查询的返回，不包含权限树结构
 */
@Data
@Schema(description = "角色权限视图对象")
public class RolePermissionVO implements Serializable {

    @Schema(description = "权限ID")
    private Long id;

    @Schema(description = "权限名称")
    private String name;

    @Schema(description = "权限编码")
    private String code;

    @Schema(description = "权限描述")
    private String description;

    @Schema(description = "权限类型")
    private String type;

    @Schema(description = "权限URL")
    private String url;

    @Schema(description = "HTTP方法")
    private String method;

    @Schema(description = "父权限ID")
    private Long parentId;

    @Schema(description = "排序顺序")
    private Integer sortOrder;

    @Schema(description = "权限图标")
    private String icon;

    @Schema(description = "是否启用")
    private String isActive;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
} 