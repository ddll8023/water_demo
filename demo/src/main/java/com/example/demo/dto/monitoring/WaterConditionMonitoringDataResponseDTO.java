package com.example.demo.dto.monitoring;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 水情监测数据响应DTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WaterConditionMonitoringDataResponseDTO {

    /**
     * 数据ID
     */
    private Long id;

    /**
     * 监测站点ID
     */
    private Long stationId;

    /**
     * 监测站点名称
     */
    private String stationName;

    /**
     * 监测时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
     * 数据质量描述
     */
    private String dataQualityDesc;

    /**
     * 采集方式(AUTO:自动,MANUAL:手动)
     */
    private String collectionMethod;

    /**
     * 采集方式描述
     */
    private String collectionMethodDesc;

    /**
     * 数据来源设备
     */
    private String dataSource;

    /**
     * 备注信息
     */
    private String remark;
}
