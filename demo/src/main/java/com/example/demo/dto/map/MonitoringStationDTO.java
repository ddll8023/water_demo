package com.example.demo.dto.map;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 监测站点DTO
 * 用于"一张图"模块展示监测站点的地理位置和最新数据
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MonitoringStationDTO {

    /**
     * 站点ID
     */
    private Long stationId;

    /**
     * 站点名称
     */
    private String stationName;

    /**
     * 站点编码
     */
    private String stationCode;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 监测项目 (WQ:水质, WL:水位, FL:流量, RF:降雨)
     */
    private String monitoringItem;

    /**
     * 监测项目名称
     */
    private String monitoringItemName;

    /**
     * 站点状态 (ONLINE:在线, OFFLINE:离线, MAINTENANCE:维护中)
     */
    private String stationStatus;

    /**
     * 站点状态名称
     */
    private String stationStatusName;

    /**
     * 最新监测数据 (JSON格式存储具体数据)
     */
    private Object latestData;

    /**
     * 最新监测时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMonitoringTime;

    /**
     * 数据质量 (1:正常, 2:异常, 3:缺失)
     */
    private Integer dataQuality;

    /**
     * 数据质量文本
     */
    private String dataQualityText;

    /**
     * 管理部门ID
     */
    private Long departmentId;

    /**
     * 管理部门名称
     */
    private String departmentName;

    /**
     * 安装时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime installationDate;

    /**
     * 备注信息
     */
    private String remarks;
}