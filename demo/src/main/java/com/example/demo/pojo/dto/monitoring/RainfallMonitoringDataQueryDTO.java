package com.example.demo.pojo.dto.monitoring;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 雨情监测数据查询DTO
 * 用于雨情监测数据列表查询的条件参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RainfallMonitoringDataQueryDTO {

    /**
     * 页码
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer size;

    /**
     * 监测站点ID
     */
    private Long stationId;

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
     * 排序字段
     */
    private String sort;
} 