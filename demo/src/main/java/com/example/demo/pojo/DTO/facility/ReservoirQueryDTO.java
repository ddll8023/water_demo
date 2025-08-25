package com.example.demo.pojo.DTO.facility;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 水库查询DTO
 * 用于水库列表查询的条件参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ReservoirQueryDTO {

    /**
     * 关键字搜索（水库名称、编码）
     */
    private String keyword;

    /**
     * 工程等级（用于兼容前端传入的reservoirLevel参数）
     */
    private String engineeringGrade;

    /**
     * 工程规模
     */
    private String engineeringScale;

    /**
     * 页码（从1开始）
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
