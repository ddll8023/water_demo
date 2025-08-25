package com.example.demo.pojo.entity.facility;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 浮船信息表实体类
 * 对应数据库表: floating_boats
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FloatingBoat {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 浮船名称
     */
    @TableField("name")
    private String name;

    /**
     * 经度
     */
    @TableField("longitude")
    private BigDecimal longitude;

    /**
     * 纬度
     */
    @TableField("latitude")
    private BigDecimal latitude;

    /**
     * 抽水能力（m³/h）
     */
    @TableField("capacity")
    private BigDecimal capacity;

    /**
     * 功率（kW）
     */
    @TableField("power_consumption")
    private BigDecimal powerConsumption;

    /**
     * 抽水状态（关联dict_data.data_value，type_code=device_status）
     */
    @TableField("pumping_status")
    private String pumpingStatus;



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
     * 抽水状态名称（非数据库字段）
     */
    @TableField(exist = false)
    private String pumpingStatusName;
}
