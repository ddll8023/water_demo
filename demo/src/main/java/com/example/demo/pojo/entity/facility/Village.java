package com.example.demo.pojo.entity.facility;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 村庄信息表实体类
 * 对应数据库表: villages
 */
@Data
@Schema(name = "Village", description = "村庄信息实体")
public class Village implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "id", description = "主键ID")
    private Long id;

    @Schema(name = "name", description = "村庄名称")
    private String name;

    @Schema(name = "longitude", description = "经度")
    private BigDecimal longitude;

    @Schema(name = "latitude", description = "纬度")
    private BigDecimal latitude;

    @Schema(name = "administrativeCode", description = "行政区划代码（关联regions.code）")
    private String administrativeCode;

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

    @Schema(name = "deletedAt", description = "软删除标记")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;
}
