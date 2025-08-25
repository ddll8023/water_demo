package com.example.demo.pojo.dto.monitoring;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 流量监测数据响应DTO
 * 用于API响应，包含流量监测数据的完整信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FlowMonitoringDataResponseDTO {

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
     * 监测站点编码
     */
    private String stationCode;

    /**
     * 监测时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime monitoringTime;

    /**
     * 瞬时流量(m³/s) - 前端显示为flowRate
     */
    private BigDecimal flowRate;

    /**
     * 累计流量(m³)
     */
    private BigDecimal cumulativeFlow;

    /**
     * 数据质量(1:正常,2:异常,3:缺失)
     */
    private Integer dataQuality;

    /**
     * 数据质量文本
     */
    private String dataQualityText;

    /**
     * 采集方式(AUTO:自动,MANUAL:手动)
     */
    private String collectionMethod;

    /**
     * 采集方式文本
     */
    private String collectionMethodText;

    /**
     * 数据来源设备
     */
    private String dataSource;

    /**
     * 备注信息
     */
    private String remarks;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
