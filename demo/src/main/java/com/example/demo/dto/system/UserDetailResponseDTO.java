package com.example.demo.dto.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户详细信息响应DTO
 * 用于API响应，包含用户的完整信息、角色岗位详情和权限列表
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserDetailResponseDTO {

    /**
     * 用户 ID
     */
    private Long id;

    /**
     * 用户名 (用于登录)
     */
    private String username;

    /**
     * 姓名
     */
    private String fullName;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 账户是否激活
     */
    private Boolean isActive;

    /**
     * 最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLogin;

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

    // ========== 部门信息 ==========

    /**
     * 所属部门 ID
     */
    private Long departmentId;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 部门详细信息（可选）
     */
    private DepartmentResponseDTO departmentDetail;

    // ========== 岗位信息 ==========

    /**
     * 岗位 ID
     */
    private Long positionId;

    /**
     * 岗位名称
     */
    private String positionName;

    /**
     * 岗位详细信息
     */
    private PositionResponseDTO positionDetail;

    // ========== 角色信息 ==========

    /**
     * 角色 ID
     */
    private Long roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色详细信息
     */
    private RoleResponseDTO roleDetail;

    // ========== 权限信息 ==========

    /**
     * 用户权限代码列表
     */
    private List<String> permissions;

    /**
     * 用户权限详细信息列表
     */
    private List<PermissionResponseDTO> permissionDetails;

    // ========== 统计信息 ==========

    /**
     * 用户登录次数（可选统计字段）
     */

    private Integer loginCount;

    /**
     * 最近活跃时间（可选统计字段）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastActiveTime;

    // ========== 辅助方法 ==========

    /**
     * 是否有部门信息
     */
    public boolean hasDepartment() {
        return departmentId != null;
    }

    /**
     * 是否有岗位信息
     */
    public boolean hasPosition() {
        return positionId != null;
    }

    /**
     * 是否有角色信息
     */
    public boolean hasRole() {
        return roleId != null;
    }

    /**
     * 是否有权限信息
     */
    public boolean hasPermissions() {
        return permissions != null && !permissions.isEmpty();
    }

    /**
     * 获取权限数量
     */
    public int getPermissionCount() {
        return hasPermissions() ? permissions.size() : 0;
    }

    /**
     * 是否为激活用户
     */
    public boolean isActiveUser() {
        return Boolean.TRUE.equals(isActive);
    }

    /**
     * 是否为新用户（创建时间在7天内）
     */
    public boolean isNewUser() {
        if (createdAt == null) {
            return false;
        }
        return createdAt.isAfter(LocalDateTime.now().minusDays(7));
    }

    /**
     * 是否最近登录过（最后登录时间在24小时内）
     */
    public boolean isRecentlyActive() {
        if (lastLogin == null) {
            return false;
        }
        return lastLogin.isAfter(LocalDateTime.now().minusHours(24));
    }
}
