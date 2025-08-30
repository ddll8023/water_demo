package com.example.demo.pojo.DTO.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设施查询通用DTO
 * 用于统一接收各类设施的通用查询参数
 * 设施特定字段由各自的QueryDTO处理
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "FacilityQueryDTO", description = "设施查询通用DTO")
public class FacilityQueryDTO extends BaseQueryDTO {

    @Schema(name = "name", description = "设施名称", example = "泵站001")
    private String name;

    @Schema(name = "stationType", description = "站点类型", example = "booster")
    private String stationType;

    @Schema(name = "waterProject", description = "水利工程", example = "南水北调")
    private String waterProject;

    @Schema(name = "operationMode", description = "运行方式", example = "auto")
    private String operationMode;

    @Schema(name = "pumpingStatus", description = "抽水状态", example = "running")
    private String pumpingStatus;

    @Schema(name = "pipelineType", description = "管道类型", example = "transmission_pipeline")
    private String pipelineType;
} 