package com.example.demo.dto.monitoring;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 数据质量统计响应DTO
 * 用于数据质量统计的API响应
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DataQualityStatsResponseDTO {

    /**
     * 总体数据质量率(%)
     */
    private BigDecimal overallQualityRate;

    /**
     * 正常数据占比(%)
     */
    private BigDecimal normalRate;

    /**
     * 异常数据占比(%)
     */
    private BigDecimal abnormalRate;

    /**
     * 缺失数据占比(%)
     */
    private BigDecimal missingRate;

    /**
     * 各站点数据质量统计
     * Key: 站点名称, Value: 质量率
     */
    private Map<String, BigDecimal> stationQualityMap;

    /**
     * 时间趋势数据
     * 按时间维度统计的数据质量变化
     */
    private List<Map<String, Object>> trendData;

    /**
     * 统计时间范围
     */
    private String timeRange;
}
