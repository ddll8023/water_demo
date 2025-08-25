package com.example.demo.pojo.DTO.facility;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 监测站点响应DTO
 */
@Data
public class MonitoringStationResponseDTO {

    /**
     * 监测站点ID
     * 示例: 1
     */
    private Long id;

    /**
     * 监测站点编码
     * 示例: MS001
     */
    private String stationCode;

    /**
     * 监测站点名称
     * 示例: 水库入口监测站
     */
    private String name;

    /**
     * 水系名称
     * 示例: 长江
     */
    private String waterSystemName;

    /**
     * 河流名称
     * 示例: 汉江
     */
    private String riverName;

    /**
     * 监测项目码
     * 示例: Q
     */
    private String monitoringItemCode;

    /**
     * 监测项目名称
     * 示例: 流量站
     */
    private String monitoringItemName;

    /**
     * 行政区划代码
     * 示例: 420100
     */
    private String adminRegionCode;

    /**
     * 行政区划名称
     * 示例: 武汉市
     */
    private String adminRegionName;

    /**
     * 设站年月
     * 示例: 2020-01-15
     */
    private LocalDate establishmentDate;

    /**
     * 经度
     * 示例: 112.123456
     */
    private BigDecimal longitude;

    /**
     * 纬度
     * 示例: 30.654321
     */
    private BigDecimal latitude;

    /**
     * 备注
     * 示例: 重要监测点
     */
    private String remark;

    /**
     * 创建时间
     * 示例: 2024-01-01 10:00:00
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     * 示例: 2024-01-01 10:00:00
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
