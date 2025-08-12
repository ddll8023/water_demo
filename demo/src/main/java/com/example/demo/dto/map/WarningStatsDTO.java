package com.example.demo.dto.map;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 预警统计DTO
 * 用于"一张图"模块展示设施、监测点和预警的统计数据
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WarningStatsDTO {

    /**
     * 设施总数
     */
    private Integer facilityCount;

    /**
     * 泵站数量
     */
    private Integer pumpingStationCount;

    /**
     * 水厂数量
     */
    private Integer waterPlantCount;

    /**
     * 水库数量
     */
    private Integer reservoirCount;

    /**
     * 监测点总数
     */
    private Integer stationCount;

    /**
     * 在线监测点数
     */
    private Integer onlineStationCount;

    /**
     * 离线监测点数
     */
    private Integer offlineStationCount;

    /**
     * 预警总数
     */
    private Integer totalWarnings;

    /**
     * 进行中预警数
     */
    private Integer ongoingWarnings;

    /**
     * 已解决预警数
     */
    private Integer resolvedWarnings;

    /**
     * 一级预警数量 (红色预警)
     */
    private Integer level1Warnings;

    /**
     * 二级预警数量 (橙色预警)
     */
    private Integer level2Warnings;

    /**
     * 三级预警数量 (黄色预警)
     */
    private Integer level3Warnings;

    /**
     * 四级预警数量 (蓝色预警)
     */
    private Integer level4Warnings;

    /**
     * 今日新增预警数
     */
    private Integer todayNewWarnings;

    /**
     * 部门总数
     */
    private Integer departmentCount;

    /**
     * 人员总数
     */
    private Integer personnelCount;
}