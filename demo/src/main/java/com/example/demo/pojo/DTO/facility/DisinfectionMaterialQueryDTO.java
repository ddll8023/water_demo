package com.example.demo.pojo.DTO.facility;

import lombok.Data;

/**
 * 消毒药材查询条件DTO
 * 用于查询消毒药材信息的筛选条件
 */
@Data
public class DisinfectionMaterialQueryDTO {

    /**
     * 页码，默认为1
     */
    private int page = 1;

    /**
     * 每页大小，默认为10
     */
    private int size = 10;

    /**
     * 搜索关键词（药材名称），用于按名称模糊查询
     */
    private String keyword;

    /**
     * 所属水厂ID，用于按水厂筛选
     */
    private Long waterPlantId;
}
