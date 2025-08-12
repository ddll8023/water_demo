package com.example.demo.dto.facility;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 水厂查询DTO
 * 用于水厂列表查询的条件参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WaterPlantQueryDTO {

    /**
     * 关键字搜索（水厂名称）
     */
    private String keyword;

    /**
     * 页码（从1开始）
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
