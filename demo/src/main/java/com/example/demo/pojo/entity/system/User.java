package com.example.demo.pojo.entity.system;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户信息表实体类
 * 对应数据库表: users
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class User {

    /**
     * 用户 ID
     */
    private Long id;

    /**
     * 用户名 (用于登录)
     */
    private String username;

    /**
     * 加密后的密码
     */
    private String passwordHash;

    /**
     * 角色 ID（兼容旧版保留）
     * 该字段保留用于兼容现有代码，新逻辑中用户可以拥有多个角色
     */
    private Long roleId;

    /**
     * 账户是否激活
     */
    private String isActive = "1";

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLogin;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 软删除标记
     */
    private LocalDateTime deletedAt;

    // ========== 关联信息字段（非数据库字段，用于存储关联查询结果） ==========

    /**
     * 角色名称（非数据库字段）
     * 通过关联查询roles表获取，对应roleId的角色名称
     */
    private String roleName;
    
    /**
     * 用户角色列表（非数据库字段）
     * 通过关联查询user_roles和roles表获取
     */
    private List<Role> roles;

    // 注意：MyBatis中可通过关联查询或其他方式处理关联数据：
    // - 角色信息：通过roleId查询Role表或通过user_roles关联表查询多个角色
    // - 关联的人员信息：通过user_id查询Personnel表（可选关联）
    // - 移动端用户信息：通过user_id查询AppUser表
    // - 操作日志：通过user_id查询AuditLog表
    // - 创建的模拟任务：通过creator_id查询Simulation表
    // - 创建的应急预案：通过creator_id查询EmergencyPlan表
}
