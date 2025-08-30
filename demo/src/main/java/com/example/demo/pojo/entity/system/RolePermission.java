package com.example.demo.pojo.entity.system;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * 角色权限关联表实体类
 * 对应数据库表: role_permissions
 */
@Data
 @Schema(name = "RolePermission", description = "角色权限关联表实体类")
public class RolePermission {

    @Schema(description = "关联 ID")
    private Long id;

    @Schema(description = "角色 ID")
    private Long roleId;

    @Schema(description = "权限 ID")
    private Long permissionId;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "软删除标记")
    private LocalDateTime deletedAt;


}
