package com.example.demo.pojo.dto.monitoring;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 水质监测图表数据响应DTO
 * 用于返回水质监测图表所需的数据
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WaterQualityChartDataResponseDTO {

    /**
     * 图表标签（时间点）
     */
    private List<String> labels;

    /**
     * 图表数据值
     */
    private List<BigDecimal> values;

    /**
     * 数据集名称
     */
    private String datasetName;

    /**
     * 监测项目代码
     */
    private String monitoringItemCode;

    /**
     * 监测项目名称
     */
    private String monitoringItemName;

    /**
     * 单位
     */
    private String unit;

    /**
     * 图表主题色
     */
    private String themeColor;

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
