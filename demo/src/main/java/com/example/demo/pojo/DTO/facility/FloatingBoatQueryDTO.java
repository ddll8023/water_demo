package com.example.demo.pojo.DTO.facility;

import lombok.Data;

/**
 * 浮船信息查询条件DTO
 */
@Data
public class FloatingBoatQueryDTO {

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
     * 搜索关键词（浮船名称）
     * 例如: 取水浮船
     */
    private String keyword;

    /**
     * 抽水状态
     * 例如: normal(正常)、maintenance(维护)、fault(故障)
     */
    private String pumpingStatus;
}
