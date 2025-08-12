package com.example.demo.entity.warning;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预警指标设定实体类
 * 对应数据库表: warning_thresholds
 *
 * 设计说明：
 * - 预警指标设定表用于管理各监测点的预警阈值配置
 * - station_name 和 monitoring_item 组合唯一，确保每个测点的每个监测项只有一个预警配置
 * - 支持四级阈值设置：上上限、上限、下限、下下限
 * - 支持启用/禁用状态，便于动态控制预警指标的可用性
 * - monitoring_item 关联字典表 dict_data，type_code=monitoring_item
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("warning_thresholds")
public class WarningThreshold {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 测点名称
     */
    @TableField("station_name")
    private String stationName;

    /**
     * 监测项（关联dict_data，type_code=monitoring_item）
     */
    @TableField("monitoring_item")
    private String monitoringItem;

    /**
     * 上上限
     */
    @TableField("upper_upper_limit")
    private BigDecimal upperUpperLimit;

    /**
     * 上限
     */
    @TableField("upper_limit")
    private BigDecimal upperLimit;

    /**
     * 下限
     */
    @TableField("lower_limit")
    private BigDecimal lowerLimit;

    /**
     * 下下限
     */
    @TableField("lower_lower_limit")
    private BigDecimal lowerLowerLimit;

    /**
     * 单位
     */
    @TableField("unit")
    private String unit;

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
     * 监测项名称（非数据库字段）
     * 通过关联查询dict_data表获取监测项的显示名称
     */
    @TableField(exist = false)
    private String monitoringItemName;

    // 注意：MyBatis-Plus中不直接支持关联查询
    // 以下关联数据需要通过Service层或者自定义SQL来处理：
    // - 监测项名称：通过monitoring_item查询dict_data表获取data_label
    // - 相关预警记录：通过threshold_id查询warning_records表
}
