package com.example.demo.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 角色表实体类
 * 对应数据库表: roles
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("roles")
public class Role {

    /**
     * 角色 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色名称
     */
    @TableField("name")
    private String name;

    /**
     * 角色描述
     */
    @TableField("description")
    private String description;

    /**
     * 排序值，数值越小优先级越高
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 角色是否启用
     */
    @TableField("is_active")
    private Boolean isActive;

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

    // 注意：MyBatis-Plus中不直接支持关联查询
    // 以下关联数据需要通过Service层处理：
    // - 该角色的用户列表：通过查询role_id = this.id的用户记录
    // - 角色权限关联：通过查询role_id = this.id的角色权限记录
}
