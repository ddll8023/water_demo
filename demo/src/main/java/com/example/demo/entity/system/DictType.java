package com.example.demo.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 字典类型表实体类
 * 对应数据库表: dict_types
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dict_types")
public class DictType {

    /**
     * 字典类型ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 字典类型编码
     */
    @TableField("type_code")
    private String typeCode;

    /**
     * 字典类型名称
     */
    @TableField("type_name")
    private String typeName;

    /**
     * 描述信息
     */
    @TableField("description")
    private String description;

    /**
     * 排序值，数值越小优先级越高
     */
    @TableField("sort_order")
    private Integer sortOrder = 0;

    /**
     * 是否启用
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
     * 字典数据数量（非数据库字段）
     * 通过关联查询dict_data表获取
     */
    @TableField(exist = false)
    private Integer dataCount;

    // 注意：MyBatis-Plus中不直接支持关联查询
    // 以下关联数据需要通过Service层或者自定义SQL来处理：
    // - 字典数据列表：通过type_id查询DictData表
    // - 字典数据统计：通过type_id统计DictData表记录数
}
