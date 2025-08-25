package com.example.demo.pojo.dto.facility;

import lombok.Data;

/**
 * 村庄信息查询条件DTO
 * 用于构建村庄信息列表查询条件
 */
@Data
public class VillageQueryDTO {

    /**
     * 页码
     * 默认值为1
     */
    private int page = 1;

    /**
     * 每页记录数
     * 默认值为10
     */
    private int size = 10;

    /**
     * 搜索关键词
     * 用于按村庄名称进行模糊查询
     * 例如：张家村
     */
    private String keyword;
}
