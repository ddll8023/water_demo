package com.example.demo.pojo.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 村庄视图对象
 * 用于API响应，包含村庄的完整信息
 */
@Data
@Schema(name = "VillageVO", description = "村庄视图对象")
public class VillageVO implements Serializable {

    @Schema(name = "id", description = "村庄ID")
    private Long id;

    @Schema(name = "name", description = "村庄名称")
    private String name;

    @Schema(name = "longitude", description = "经度")
    private BigDecimal longitude;

    @Schema(name = "latitude", description = "纬度")
    private BigDecimal latitude;

    @Schema(name = "administrativeCode", description = "行政区划代码")
    private String administrativeCode;

    @Schema(name = "administrativeName", description = "行政区划名称")
    private String administrativeName;

    @Schema(name = "currentPopulation", description = "现状人口（人）")
    private Integer currentPopulation;

    @Schema(name = "plannedPopulation", description = "规划人口（人）")
    private Integer plannedPopulation;

    @Schema(name = "dailyWaterConsumption", description = "日用水量（m³/天）")
    private BigDecimal dailyWaterConsumption;

    @Schema(name = "waterSupplyGuaranteeRate", description = "供水保证率（%）")
    private BigDecimal waterSupplyGuaranteeRate;

    @Schema(name = "contactPerson", description = "联系人")
    private String contactPerson;

    @Schema(name = "contactPhone", description = "联系电话")
    private String contactPhone;

    @Schema(name = "remark", description = "备注")
    private String remark;

    @Schema(name = "createdAt", description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt", description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // ========== 业务统计字段 ==========

    @Schema(name = "waterSupplyFacilityCount", description = "供水设施数量")
    private Integer waterSupplyFacilityCount;

    @Schema(name = "populationGrowthRate", description = "人口增长率（%）")
    private BigDecimal populationGrowthRate;
} 