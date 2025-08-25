package com.example.demo.pojo.DTO.facility;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 浮船信息响应DTO
 */
@Data
public class FloatingBoatResponseDTO {

    /**
     * 浮船ID
     * 例如: 1
     */
    private Long id;

    /**
     * 浮船名称
     * 例如: 取水浮船1号
     */
    private String name;

    /**
     * 经度
     * 例如: 114.123456
     */
    private BigDecimal longitude;

    /**
     * 纬度
     * 例如: 30.123456
     */
    private BigDecimal latitude;

    /**
     * 抽水能力（m³/h）
     * 例如: 200
     */
    private BigDecimal capacity;

    /**
     * 功率（kW）
     * 例如: 50
     */
    private BigDecimal powerConsumption;

    /**
     * 抽水状态
     * 例如: normal
     */
    private String pumpingStatus;

    /**
     * 抽水状态名称
     * 例如: 正常运行
     */
    private String pumpingStatusName;

    /**
     * 备注
     * 例如: 正常运行
     */
    private String remark;

    /**
     * 创建时间
     * 例如: 2024-01-01 10:00:00
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     * 例如: 2024-01-01 10:00:00
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
