package com.example.demo.pojo.DTO.monitoring;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 水情监测数据查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WaterConditionMonitoringDataQueryDTO {

    /**
     * 监测站点ID
     */
    private Long stationId;

    /**
     * 监测站点名称（用于模糊查询）
     */
    private String stationName;

    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 数据质量(1:正常,2:异常,3:缺失)
     */
    private Integer dataQuality;

    /**
     * 采集方式(AUTO:自动,MANUAL:手动)
     */
    private String collectionMethod;

    /**
     * 数据来源设备
     */
    private String dataSource;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页记录数
     */
    private Integer size = 10;

    /**
     * 排序字段
     */
    private String sort;
}
