package com.example.demo.pojo.entity.facility;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 管道信息表实体类
 * 对应数据库表: pipelines
 */
@Data
@Schema(name = "Pipeline", description = "管道信息实体")
public class Pipeline implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "id", description = "主键ID")
    private Long id;

    @Schema(name = "pipelineCode", description = "管道编码")
    private String pipelineCode;

    @Schema(name = "name", description = "管道名称")
    private String name;

    @Schema(name = "pipelineType", description = "管道类型（关联dict_data.data_value，type_code=pipeline_type）")
    private String pipelineType;

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
    private LocalDate constructionDate;

    @Schema(name = "remark", description = "备注")
    private String remark;

    @Schema(name = "createdAt", description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt", description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(name = "deletedAt", description = "软删除标记")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;
}
