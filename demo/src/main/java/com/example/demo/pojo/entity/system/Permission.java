package com.example.demo.pojo.entity.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限表实体类
 * 对应数据库表: permissions
 */
@Data
@Schema(name = "Permission", description = "权限表实体类")
public class Permission {

    @Schema(name = "id", description = "权限ID")
    private Long id;

    @Schema(name = "name", description = "权限名称")
    private String name;

    @Schema(name = "code", description = "权限编码")
    private String code;

    @Schema(name = "type", description = "权限类型")
    private String type;

    @Schema(name = "description", description = "权限描述")
    private String description;

    @Schema(name = "parentId", description = "父权限ID")
    private Long parentId;

    @Schema(name = "sortOrder", description = "排序值")
    private Integer sortOrder = 0;

    @Schema(name = "createdAt", description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt", description = "更新时间")
    private LocalDateTime updatedAt;


    @Schema(name = "deletedAt", description = "软删除时间")
    private LocalDateTime deletedAt;


}
