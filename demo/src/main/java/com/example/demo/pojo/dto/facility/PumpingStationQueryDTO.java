package com.example.demo.pojo.dto.facility;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 泵站查询DTO
 * 用于泵站列表查询的条件参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PumpingStationQueryDTO {

    /**
     * 关键字搜索（泵站名称、编码）
     */
    private String keyword;

    /**
     * 泵站名称搜索
     */
    private String name;

    /**
     * 泵站类型
     */
    private String stationType;

    /**
     * 所属供水工程
     */
    private String waterProject;

    /**
     * 运行方式
     */
    private String operationMode;

    /**
     * 页码（从1开始）
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
