package com.example.demo.pojo.entity.monitoring;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 水位监测数据实体类
 * 对应数据库表: water_level_monitoring_data
 *
 * 设计说明：
 * - 水位监测数据表用于存储各监测站点的水位监测数据
 * - station_id 关联监测站点表，建立数据与站点的关系
 * - 支持水位高度的精确记录，保留3位小数
 * - data_quality 表示数据质量：1-正常，2-异常，3-缺失
 * - collection_method 表示采集方式：AUTO-自动，MANUAL-手动
 * - 支持软删除机制，便于数据恢复和审计
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("water_level_monitoring_data")
public class WaterLevelMonitoringData {

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
     * 水位高度(m)
     */
    @TableField("water_level")
    private BigDecimal waterLevel;

    /**
     * 数据质量(1:正常,2:异常,3:缺失)
     */
    @TableField("data_quality")
    private Integer dataQuality = 1;

    /**
     * 采集方式(AUTO:自动,MANUAL:手动)
     */
    @TableField("collection_method")
    private String collectionMethod = "AUTO";

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
    @TableLogic
    @TableField("deleted_at")
    private LocalDateTime deletedAt;

    // ========== 关联信息字段（非数据库字段，用于存储关联查询结果） ==========

    /**
     * 监测站点名称（非数据库字段）
     * 通过关联查询monitoring_stations表获取站点名称
     */
    @TableField(exist = false)
    private String stationName;

    /**
     * 监测站点编码（非数据库字段）
     * 通过关联查询monitoring_stations表获取站点编码
     */
    @TableField(exist = false)
    private String stationCode;

    /**
     * 数据质量文本（非数据库字段）
     * 将数据质量代码转换为可读文本：1-正常，2-异常，3-缺失
     */
    @TableField(exist = false)
    private String dataQualityText;

    // 注意：MyBatis-Plus中不直接支持关联查询
    // 以下关联数据需要通过Service层或者自定义SQL来处理：
    // - 监测站点信息：通过station_id查询monitoring_stations表获取站点详情
    // - 数据质量文本转换：在Service层或Mapper层进行转换
    // - 相关预警记录：通过监测数据触发的预警记录关联
}
