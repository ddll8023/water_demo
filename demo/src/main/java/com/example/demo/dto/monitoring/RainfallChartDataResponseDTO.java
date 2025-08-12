package com.example.demo.dto.monitoring;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 雨情监测图表数据响应DTO
 * 用于返回雨情监测的图表数据
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RainfallChartDataResponseDTO {

    /**
     * 时间标签列表
     */
    private List<String> labels;

    /**
     * 降雨量数据值列表
     */
    private List<BigDecimal> rainfallValues;

    /**
     * 累计降雨量数据值列表
     */
    private List<BigDecimal> cumulativeValues;

    /**
     * 降雨强度数据值列表
     */
    private List<BigDecimal> intensityValues;

    /**
     * 时间间隔
     */
    private String interval;

    /**
     * 数据集名称
     */
    private String datasetName;

    /**
     * 监测站点名称
     */
    private String stationName;

    /**
     * 数据单位
     */
    private String unit = "mm";

    /**
     * 数据总数
     */
    private Integer totalCount;

    /**
     * 最大降雨量
     */
    private BigDecimal maxRainfall;

    /**
     * 最大累计降雨量
     */
    private BigDecimal maxCumulative;

    /**
     * 最大降雨强度
     */
    private BigDecimal maxIntensity;

    /**
     * 平均降雨量
     */
    private BigDecimal avgRainfall;

    /**
     * 总降雨量
     */
    private BigDecimal totalRainfall;
} 