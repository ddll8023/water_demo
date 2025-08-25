package com.example.demo.pojo.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 权限表实体类
 * 对应数据库表: permissions
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("permissions")
public class Permission {

    /**
     * 权限 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 权限名称
     */
    @TableField("name")
    private String name;

    /**
     * 权限编码
     */
    @TableField("code")
    private String code;

    /**
     * 权限类型(菜单/按钮/API)
     */
    @TableField("type")
    private String type;

    /**
     * 权限描述
     */
    @TableField("description")
    private String description;

    /**
     * 权限对应的URL路径
     */
    @TableField("url")
    private String url;

    /**
     * HTTP方法(GET/POST/PUT/DELETE)
     */
    @TableField("method")
    private String method;

    /**
     * 父权限ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 排序值
     */
    @TableField("sort_order")
    private Integer sortOrder = 0;

    /**
     * 图标
     */
    @TableField("icon")
    private String icon;

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

    // 注意：角色权限关联通过Service层处理
}
