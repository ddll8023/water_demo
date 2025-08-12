package com.example.demo.dto.system;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 角色权限关联创建DTO
 * 用于创建新的角色权限关联，包含必填字段和验证注解
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RolePermissionCreateDTO {

    /**
     * 角色 ID
     */
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    /**
     * 权限 ID
     */
    @NotNull(message = "权限ID不能为空")
    private Long permissionId;
}
