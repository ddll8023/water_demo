package com.example.demo.pojo.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 管道视图对象
 * 用于API响应，包含管道的完整信息
 */
@Data
@Schema(name = "PipelineVO", description = "管道视图对象")
public class PipelineVO implements Serializable {

    @Schema(name = "id", description = "管道ID")
    private Long id;

    @Schema(name = "pipelineCode", description = "管道编码")
    private String pipelineCode;

    @Schema(name = "name", description = "管道名称")
    private String name;

    @Schema(name = "pipelineType", description = "管道类型")
    private String pipelineType;

    @Schema(name = "pipelineTypeLabel", description = "管道类型标签")
    private String pipelineTypeLabel;

    @Schema(name = "startLongitude", description = "起点经度")
    private BigDecimal startLongitude;

    @Schema(name = "startLatitude", description = "起点纬度")
    private BigDecimal startLatitude;

    @Schema(name = "endLongitude", description = "终点经度")
    private BigDecimal endLongitude;

    @Schema(name = "endLatitude", description = "终点纬度")
    private BigDecimal endLatitude;

    @Schema(name = "length", description = "管道长度（km）")
    private BigDecimal length;

    @Schema(name = "diameter", description = "管径（mm）")
    private BigDecimal diameter;

    @Schema(name = "designPressure", description = "设计压力（MPa）")
    private BigDecimal designPressure;

    @Schema(name = "designFlow", description = "设计流量（m³/h）")
    private BigDecimal designFlow;

    @Schema(name = "burialDepth", description = "埋深（m）")
    private BigDecimal burialDepth;

    @Schema(name = "constructionDate", description = "建设年月")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate constructionDate;

    @Schema(name = "remark", description = "备注")
    private String remark;

    @Schema(name = "createdAt", description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt", description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // ========== 业务统计字段 ==========

    @Schema(name = "inspectionCount", description = "巡检次数")
    private Integer inspectionCount;

    @Schema(name = "lastInspectionDate", description = "最近巡检日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastInspectionDate;
} 