package com.example.demo.pojo.DTO.facility;

import lombok.Data;

/**
 * 管道信息查询条件DTO
 */
@Data
public class PipelineQueryDTO {

    /**
     * 页码
     * 示例: 1
     */
    private int page = 1;

    /**
     * 每页大小
     * 示例: 10
     */
    private int size = 10;

    /**
     * 搜索关键词（管道名称、编码）
     * 示例: 主管道
     */
    private String keyword;

    /**
     * 管道类型
     * 示例: main_pipeline
     */
    private String pipelineType;

    /**
     * 管道材质
     * 示例: steel
     */
    private String material;

    /**
     * 运行状态
     * 示例: normal
     */
    private String operationStatus;
}
