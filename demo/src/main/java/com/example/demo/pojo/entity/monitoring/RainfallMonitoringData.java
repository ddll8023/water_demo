package com.example.demo.pojo.entity.monitoring;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 雨情监测数据实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("rainfall_monitoring_data")
public class RainfallMonitoringData {
    
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
     * 降雨量(mm)
     */
    @TableField("rainfall")
    private BigDecimal rainfall;
    
    /**
     * 降雨强度(mm/h)
     */
    @TableField("rainfall_intensity")
    private BigDecimal rainfallIntensity;
    
    /**
     * 累计降雨量(mm)
     */
    @TableField("cumulative_rainfall")
    private BigDecimal cumulativeRainfall;
    
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
    @TableLogic
    @TableField("deleted_at")
    private LocalDateTime deletedAt;
} 