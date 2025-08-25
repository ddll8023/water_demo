package com.example.demo.pojo.entity.facility;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 消毒药材表实体类
 * 对应数据库表: disinfection_materials
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("disinfection_materials")
public class DisinfectionMaterial {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 所属水厂ID（关联water_plants.id）
     */
    @TableField("water_plant_id")
    private Long waterPlantId;

    /**
     * 存储条件
     */
    @TableField("storage_condition")
    private String storageCondition;

    /**
     * 生产日期
     */
    @TableField("production_date")
    private LocalDate productionDate;

    /**
     * 有效期
     */
    @TableField("validity_period")
    private String validityPeriod;

    /**
     * 库存数量
     */
    @TableField("quantity")
    private BigDecimal quantity;

    /**
     * 单位
     */
    @TableField("unit")
    private String unit;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

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
     * 所属水厂名称（非数据库字段）
     */
    @TableField(exist = false)
    private String waterPlantName;
}
