package com.example.demo.pojo.DTO.warning;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预警指标设定响应DTO
 * 用于返回预警指标信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WarningThresholdResponseDTO {

    /**
     * 预警指标ID
     */
    private Long id;

    /**
     * 测点名称
     */
    private String stationName;

    /**
     * 监测项
     */
    private String monitoringItem;

    /**
     * 监测项名称（显示用）
     */
    private String monitoringItemName;

    /**
     * 上上限
     */
    private BigDecimal upperUpperLimit;

    /**
     * 上限
     */
    private BigDecimal upperLimit;

    /**
     * 下限
     */
    private BigDecimal lowerLimit;

    /**
     * 下下限
     */
    private BigDecimal lowerLowerLimit;

    /**
     * 单位
     */
    private String unit;

    /**
     * 是否启用
     */
    private Boolean isActive;

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
