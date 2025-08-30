package com.example.demo.pojo.entity.facility;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 水库信息表实体类
 * 对应数据库表: reservoirs
 */
@Data
@Schema(name = "Reservoir", description = "水库信息实体")
public class Reservoir implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "id", description = "主键ID")
    private Long id;

    @Schema(name = "reservoirCode", description = "水库编码")
    private String reservoirCode;

    @Schema(name = "name", description = "水库名称")
    private String name;

    @Schema(name = "waterProject", description = "所属水利工程")
    private String waterProject;

    @Schema(name = "longitude", description = "经度")
    private BigDecimal longitude;

    @Schema(name = "latitude", description = "纬度")
    private BigDecimal latitude;

    @Schema(name = "registrationNo", description = "水库注册登记号")
    private String registrationNo;

    @Schema(name = "adminRegionCode", description = "所属行政区划（关联regions.code）")
    private String adminRegionCode;

    @Schema(name = "engineeringGrade", description = "工程等级（关联dict_data.data_value，type_code=engineering_grade）")
    private String engineeringGrade;

    @Schema(name = "engineeringScale", description = "工程规模（关联dict_data.data_value，type_code=engineering_scale）")
    private String engineeringScale;

    @Schema(name = "totalCapacity", description = "总库容（万m³）")
    private BigDecimal totalCapacity;

    @Schema(name = "regulatingCapacity", description = "调节库容（万m³）")
    private BigDecimal regulatingCapacity;

    @Schema(name = "deadCapacity", description = "死库容（万m³）")
    private BigDecimal deadCapacity;

    @Schema(name = "establishmentDate", description = "建库年月")
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
