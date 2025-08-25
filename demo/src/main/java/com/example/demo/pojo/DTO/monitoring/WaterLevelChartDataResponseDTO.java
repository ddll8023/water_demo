package com.example.demo.pojo.DTO.monitoring;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 水位监测图表数据响应DTO
 * 用于返回水位监测的图表数据
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WaterLevelChartDataResponseDTO {

    /**
     * 时间标签列表
     */
    private List<String> labels;

    /**
     * 水位数据值列表
     */
    private List<BigDecimal> values;

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
    private String unit = "m";

    /**
     * 数据总数
     */
    private Integer totalCount;

    /**
     * 最大值
     */
    private BigDecimal maxValue;

    /**
     * 最小值
     */
    private BigDecimal minValue;

    /**
     * 平均值
     */
    private BigDecimal avgValue;
}
