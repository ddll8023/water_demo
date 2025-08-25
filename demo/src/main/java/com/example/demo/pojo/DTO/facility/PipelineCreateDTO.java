package com.example.demo.pojo.DTO.facility;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 管道信息创建请求DTO
 */
@Data
public class PipelineCreateDTO {

    /**
     * 管道编码（系统自动生成）
     * 示例: PL001
     */
    private String pipelineCode;

    /**
     * 管道名称
     * 示例: 主输水管道
     */
    @NotBlank(message = "管道名称不能为空")
    private String name;

    /**
     * 管道类型
     * 示例: main_pipeline
     */
    @NotBlank(message = "管道类型不能为空")
    private String pipelineType;

    /**
     * 起点经度
     * 示例: 114.123456
     */
    private BigDecimal startLongitude;

    /**
     * 起点纬度
     * 示例: 30.123456
     */
    private BigDecimal startLatitude;

    /**
     * 终点经度
     * 示例: 114.234567
     */
    private BigDecimal endLongitude;

    /**
     * 终点纬度
     * 示例: 30.234567
     */
    private BigDecimal endLatitude;

    /**
     * 管道长度（km）
     * 示例: 10.5
     */
    private BigDecimal length;

    /**
     * 管径（mm）
     * 示例: 800
     */
    private BigDecimal diameter;

    /**
     * 管道材质
     * 示例: steel
     */
    private String material;

    /**
     * 设计压力（MPa）
     * 示例: 1.6
     */
    private BigDecimal designPressure;

    /**
     * 设计流量（m³/h）
     * 示例: 500
     */
    private BigDecimal designFlow;

    /**
     * 埋深（m）
     * 示例: 2.5
     */
    private BigDecimal burialDepth;

    /**
     * 运行状态
     * 示例: normal
     */
    private String operationStatus;

    /**
     * 建设年月
     * 示例: 2024-01-01
     */
    private LocalDate constructionDate;

    /**
     * 备注
     * 示例: 主要供水管道，负责城区供水
     */
    private String remark;
}
