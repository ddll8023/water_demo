package com.example.demo.pojo.DTO.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 角色权限关联响应DTO
 * 用于API响应，包含角色权限关联的完整信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RolePermissionResponseDTO {

    /**
     * 关联 ID
     */
    private Long id;

    /**
     * 角色 ID
     */
    private Long roleId;

    /**
     * 权限 ID
     */
    private Long permissionId;

    /**
     * 角色名称 (冗余字段，便于前端显示)
     */
    private String roleName;

    /**
     * 权限名称 (冗余字段，便于前端显示)
     */
    private String permissionName;

    /**
     * 权限编码 (冗余字段，便于前端显示)
     */
    private String permissionCode;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
