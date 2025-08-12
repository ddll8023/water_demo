package com.example.demo.dto.facility;

import lombok.Data;

/**
 * 监测站点查询条件DTO
 */
@Data
public class MonitoringStationQueryDTO {

    /**
     * 页码
     * 默认值: 1
     */
    private int page = 1;

    /**
     * 每页大小
     * 默认值: 10
     */
    private int size = 10;

    /**
     * 搜索关键词（站点名称、编码、水系名称、河流名称）
     * 例如: 水库
     */
    private String keyword;

    /**
     * 监测项目码
     * 例如: Q(流量)、Z(水位)、P(降雨)、WQ(水质)
     */
    private String monitoringItemCode;

    /**
     * 水系名称
     * 例如: 长江
     */
    private String waterSystemName;

    /**
     * 监测类型
     * 例如: Q
     */
    private String monitoringType;

    /**
     * 运行状态
     * 例如: normal(正常)、maintenance(维护)、fault(故障)
     */
    private String operationStatus;

    /**
     * 数据传输方式
     * 例如: 4G、GPRS、卫星等
     */
    private String transmissionMethod;
}
