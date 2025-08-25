package com.example.demo.pojo.entity.monitoring;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 水质监测数据实体类
 * 对应数据库表: water_quality_monitoring_data
 *
 * 设计说明：
 * - 水质监测数据表用于存储各监测站点的水质监测数据（水平存储模式）
 * - station_id 关联监测站点表，建立数据与站点的关系
 * - 支持8种水质监测项目：水温、浊度、PH值、电导率、溶解氧、氨氮、化学需氧量、余氯
 * - 采用水平存储模式，每条记录包含同一时间点的所有监测项目数据
 * - data_quality 表示数据质量：1-正常，2-异常，3-缺失
 * - collection_method 表示采集方式：AUTO-自动，MANUAL-手动
 * - 支持软删除机制，便于数据恢复和审计
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("water_quality_monitoring_data")
public class WaterQualityMonitoringData {

    /**
     * 数据ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 监测站点ID (关联monitoring_stations表)
     */
    @TableField("station_id")
    private Long stationId;

    /**
     * 监测时间
     */
    @TableField("monitoring_time")
    private LocalDateTime monitoringTime;

    /**
     * 水温(℃)
     */
    @TableField("water_temperature")
    private BigDecimal waterTemperature;

    /**
     * 浊度(NTU)
     */
    @TableField("turbidity")
    private BigDecimal turbidity;

    /**
     * PH值
     */
    @TableField("ph_value")
    private BigDecimal phValue;

    /**
     * 电导率(uS/cm)
     */
    @TableField("conductivity")
    private BigDecimal conductivity;

    /**
     * 溶解氧(mg/L)
     */
    @TableField("dissolved_oxygen")
    private BigDecimal dissolvedOxygen;

    /**
     * 氨氮(mg/L)
     */
    @TableField("ammonia_nitrogen")
    private BigDecimal ammoniaNitrogen;

    /**
     * 化学需氧量(mg/L)
     */
    @TableField("cod_value")
    private BigDecimal codValue;

    /**
     * 余氯(mg/L)
     */
    @TableField("residual_chlorine")
    private BigDecimal residualChlorine;

    /**
     * 数据质量(1:正常,2:异常,3:缺失)
     */
    @TableField("data_quality")
    private Integer dataQuality;

    /**
     * 采集方式(AUTO:自动,MANUAL:手动)
     */
    @TableField("collection_method")
    private String collectionMethod;

    /**
     * 数据来源设备
     */
    @TableField("data_source")
    private String dataSource;

    /**
     * 备注信息
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
    @TableField("deleted_at")
    @TableLogic
    private LocalDateTime deletedAt;
}
