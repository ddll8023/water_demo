package com.example.demo.dto.monitoring;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 水质监测统计响应DTO
 * 用于返回水质监测数据的统计信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WaterQualityStatisticsResponseDTO {

    /**
     * 监测站点数量
     */
    private Integer stationCount;

    /**
     * 在线站点数量
     */
    private Integer onlineStationCount;

    /**
     * 离线站点数量
     */
    private Integer offlineStationCount;

    /**
     * 水温平均值
     */
    private BigDecimal avgWaterTemperature;

    /**
     * 浊度平均值
     */
    private BigDecimal avgTurbidity;

    /**
     * PH值平均值
     */
    private BigDecimal avgPh;

    /**
     * 电导率平均值
     */
    private BigDecimal avgConductivity;

    /**
     * 溶解氧平均值
     */
    private BigDecimal avgDissolvedOxygen;

    /**
     * 氨氮平均值
     */
    private BigDecimal avgAmmoniaNitrogen;

    /**
     * 化学需氧量平均值
     */
    private BigDecimal avgCod;

    /**
     * 余氯平均值
     */
    private BigDecimal avgResidualChlorine;

    /**
     * 数据质量率（正常数据占比）
     */
    private BigDecimal dataQualityRate;

    /**
     * 今日数据量
     */
    private Long todayDataCount;

    /**
     * 各监测项目数据质量统计
     */
    private List<Map<String, Object>> qualityStats;

    /**
     * 各监测项目数据趋势
     */
    private List<Map<String, Object>> trendStats;
}
