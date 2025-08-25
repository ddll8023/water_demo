package com.example.demo.pojo.dto.monitoring;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 监测概览响应DTO
 * 用于实时监测概览数据的API响应
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MonitoringOverviewResponseDTO {

    /**
     * 监测站点总数
     */
    private Integer totalStations;

    /**
     * 在线站点数
     */
    private Integer onlineStations;

    /**
     * 离线站点数
     */
    private Integer offlineStations;

    /**
     * 平均流量(m³/s)
     */
    private BigDecimal avgFlowRate;

    /**
     * 最大流量(m³/s)
     */
    private BigDecimal maxFlowRate;

    /**
     * 数据质量率(%)
     */
    private BigDecimal dataQualityRate;

    /**
     * 今日数据量
     */
    private Long todayDataCount;

    /**
     * 预警数量
     */
    private Integer warningCount;

    /**
     * 异常数量
     */
    private Integer abnormalCount;
}
