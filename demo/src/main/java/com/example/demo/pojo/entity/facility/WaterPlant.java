package com.example.demo.pojo.entity.facility;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 水厂信息表实体类
 * 对应数据库表: water_plants
 */
@Data
@Schema(name = "WaterPlant", description = "水厂信息实体")
public class WaterPlant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "id", description = "主键ID")
    private Long id;

    @Schema(name = "plantCode", description = "水厂编码")
    private String plantCode;

    @Schema(name = "name", description = "水厂名称")
    private String name;

    @Schema(name = "waterProject", description = "所属供水工程")
    private String waterProject;

    @Schema(name = "departmentId", description = "管理部门ID（关联departments.id）")
    private Long departmentId;

    @Schema(name = "managerId", description = "负责人ID（关联personnel.id）")
    private Long managerId;

    @Schema(name = "address", description = "地址")
    private String address;

    @Schema(name = "managementUnit", description = "管理单位")
    private String managementUnit;

    @Schema(name = "longitude", description = "经度")
    private BigDecimal longitude;

    @Schema(name = "latitude", description = "纬度")
    private BigDecimal latitude;

    @Schema(name = "designScale", description = "设计规模（m³/天）")
    private BigDecimal designScale;

    @Schema(name = "supplyArea", description = "供水范围（村镇）")
    private String supplyArea;

    @Schema(name = "supplyLoadRatio", description = "供水负荷率（%）")
    private BigDecimal supplyLoadRatio;

    @Schema(name = "supplyPopulation", description = "供水人口（万人）")
    private Integer supplyPopulation;

    @Schema(name = "contactPhone", description = "联系电话")
    private String contactPhone;

    @Schema(name = "establishmentDate", description = "建站年月")
    private LocalDate establishmentDate;

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
