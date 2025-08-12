package com.example.demo.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 部门信息表实体类
 * 对应数据库表: departments
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("departments")
public class Department {

    /**
     * 部门 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 部门名称
     */
    @TableField("name")
    private String name;

    /**
     * 父部门 ID (支持层级)
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 部门职责
     */
    @TableField("duty")
    private String duty;

    /**
     * 联系方式
     */
    @TableField("contact")
    private String contact;

    /**
     * 所属行政区域 ID
     */
    @TableField("region_id")
    private Long regionId;

    /**
     * 部门是否启用
     */
    @TableField("is_active")
    private Boolean isActive = true;

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

    // ========== 关联信息字段（非数据库字段，用于存储关联查询结果） ==========

    /**
     * 部门人员数量（非数据库字段）
     * 通过关联查询personnel表统计获取，对应SQL中的personnel_count字段
     */
    @TableField(exist = false)
    private Integer personnelCount;

    /**
     * 父部门名称（非数据库字段）
     * 通过关联查询departments表获取，对应SQL中的parent_name字段
     */
    @TableField(exist = false)
    private String parentName;

    /**
     * 所属行政区域名称（非数据库字段）
     * 通过关联查询regions表获取，对应SQL中的region_name字段
     */
    @TableField(exist = false)
    private String regionName;


}
