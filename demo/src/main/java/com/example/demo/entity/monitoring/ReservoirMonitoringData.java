package com.example.demo.entity.monitoring;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 水情监测数据实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("reservoir_monitoring_data")
public class ReservoirMonitoringData {

    /**
     * 数据ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 监测站点ID (关联monitoring_stations表)
     */
    private Long stationId;

    /**
     * 监测时间
     */
    private LocalDateTime monitoringTime;

    /**
     * 水位高度(m)
     */
    private BigDecimal waterLevel;

    /**
     * 蓄水量(10⁴m³)
     */
    private BigDecimal storageCapacity;

    /**
     * 超汛限(m)
     */
    private BigDecimal floodLimitDiff;

    /**
     * 入库流量(m³/s)
     */
    private BigDecimal inflow;

    /**
     * 出库流量(m³/s)
     */
    private BigDecimal outflow;

    /**
     * 数据质量(1:正常,2:异常,3:缺失)
     */
    private Integer dataQuality;

    /**
     * 采集方式(AUTO:自动,MANUAL:手动)
     */
    private String collectionMethod;

    /**
     * 数据来源设备
     */
    private String dataSource;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 软删除标记
     */
    @TableLogic
    private LocalDateTime deletedAt;
} 