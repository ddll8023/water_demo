package com.example.demo.dto.monitoring;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 流量图表数据响应DTO
 * 用于流量监测图表数据的API响应
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FlowChartDataResponseDTO {

    /**
     * 时间标签列表
     */
    private List<String> labels;

    /**
     * 流量数值列表
     */
    private List<BigDecimal> values;

    /**
     * 数据集名称
     */
    private String datasetName;

    /**
     * 监测站点名称
     */
    private String stationName;

    /**
     * 时间间隔类型
     */
    private String interval;

    /**
     * 数据单位
     */
    private String unit = "m³/s";
}
