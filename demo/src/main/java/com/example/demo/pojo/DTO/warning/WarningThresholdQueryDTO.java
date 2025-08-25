package com.example.demo.pojo.DTO.warning;

import lombok.Data;

/**
 * 预警指标设定查询条件DTO
 * 用于查询系统中配置的预警阈值指标设置
 */
@Data
public class WarningThresholdQueryDTO {

    /**
     * 页码
     * 默认值为1
     */
    private int page = 1;

    /**
     * 每页大小
     * 默认值为10
     */
    private int size = 10;

    /**
     * 搜索关键词
     * 用于模糊搜索测点名称
     */
    private String keyword;

    /**
     * 测点名称
     * 如：两河口水库、某监测站等
     */
    private String stationName;

    /**
     * 监测项
     * 如：H(水位)、Q(流量)、R(降雨量)、W(水质)等
     */
    private String monitoringItem;

    /**
     * 是否启用
     * true-启用，false-禁用
     */
    private Boolean isActive;

    /**
     * 排序字段
     * 格式：字段名,排序方向
     * 默认按创建时间降序排列
     */
    private String sort = "created_at,desc";
}
