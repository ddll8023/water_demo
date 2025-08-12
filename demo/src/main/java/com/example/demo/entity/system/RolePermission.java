package com.example.demo.entity.system;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 角色权限关联表实体类
 * 对应数据库表: role_permissions
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("role_permissions")
public class RolePermission {

    /**
     * 关联 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色 ID
     */
    @TableField("role_id")
    private Long roleId;

    /**
     * 权限 ID
     */
    @TableField("permission_id")
    private Long permissionId;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 软删除标记
     */
    @TableLogic
    @TableField("deleted_at")
    private LocalDateTime deletedAt;

    // 注意：角色和权限关联通过Service层处理
    // - 角色信息：通过roleId查询Role表
    // - 权限信息：通过permissionId查询Permission表
}
