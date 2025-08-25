package com.example.demo.pojo.DTO.facility;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 管道信息更新DTO
 * 用于更新管道信息的请求
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PipelineUpdateDTO {

    /**
     * 管道ID
     */
    @NotNull(message = "管道ID不能为空")
    private Long id;

    /**
     * 管道编码
     */
    @NotBlank(message = "管道编码不能为空")
    private String pipelineCode;

    /**
     * 管道名称
     */
    @NotBlank(message = "管道名称不能为空")
    private String name;

    /**
     * 管道类型
     */
    @NotBlank(message = "管道类型不能为空")
    private String pipelineType;

    /**
     * 起点经度
     */
    private BigDecimal startLongitude;

    /**
     * 起点纬度
     */
    private BigDecimal startLatitude;

    /**
     * 终点经度
     */
    private BigDecimal endLongitude;

    /**
     * 终点纬度
     */
    private BigDecimal endLatitude;

    /**
     * 管道长度（km）
     */
    private BigDecimal length;

    /**
     * 管径（mm）
     */
    private BigDecimal diameter;

    /**
     * 管道材质
     */
    private String material;

    /**
     * 设计压力（MPa）
     */
    private BigDecimal designPressure;

    /**
     * 设计流量（m³/h）
     */
    private BigDecimal designFlow;

    /**
     * 埋深（m）
     */
    private BigDecimal burialDepth;

    /**
     * 运行状态
     */
    private String operationStatus;

    /**
     * 建设年月
     */
    private LocalDate constructionDate;

    /**
     * 备注
     */
    private String remark;
}
