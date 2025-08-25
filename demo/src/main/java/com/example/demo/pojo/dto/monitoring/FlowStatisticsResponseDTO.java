package com.example.demo.pojo.dto.monitoring;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 流量统计响应DTO
 * 用于流量监测统计数据的API响应
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FlowStatisticsResponseDTO {

    /**
     * 监测站点总数
     */
    private Integer totalStations;

    /**
     * 平均流量(m³/s)
     */
    private BigDecimal avgFlowRate;

    /**
     * 最大流量(m³/s)
     */
    private BigDecimal maxFlowRate;

    /**
     * 最小流量(m³/s)
     */
    private BigDecimal minFlowRate;

    /**
     * 数据质量率(%)
     */
    private BigDecimal dataQualityRate;

    /**
     * 总数据量
     */
    private Long totalDataCount;

    /**
     * 正常数据量
     */
    private Long normalDataCount;

    /**
     * 异常数据量
     */
    private Long abnormalDataCount;

    /**
     * 缺失数据量
     */
    private Long missingDataCount;
}
