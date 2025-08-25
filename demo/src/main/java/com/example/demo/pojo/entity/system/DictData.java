package com.example.demo.pojo.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 字典数据表实体类
 * 对应数据库表: dict_data
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dict_data")
public class DictData {

    /**
     * 字典数据ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联字典类型ID
     */
    @TableField("type_id")
    private Long typeId;

    /**
     * 字典标签（显示值）
     */
    @TableField("data_label")
    private String dataLabel;

    /**
     * 字典键值（实际值）
     */
    @TableField("data_value")
    private String dataValue;

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
     * 字典类型编码（非数据库字段）
     * 通过关联查询dict_types表获取
     */
    @TableField(exist = false)
    private String typeCode;

    /**
     * 字典类型名称（非数据库字段）
     * 通过关联查询dict_types表获取
     */
    @TableField(exist = false)
    private String typeName;

    // 注意：MyBatis-Plus中不直接支持关联查询
    // 以下关联数据需要通过Service层或者自定义SQL来处理：
    // - 字典类型信息：通过type_id查询DictType表
}
