package com.example.demo.pojo.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 水库视图对象
 * 用于API响应，包含水库的完整信息
 */
@Data
@Schema(name = "ReservoirVO", description = "水库视图对象")
public class ReservoirVO implements Serializable {

    @Schema(name = "id", description = "水库ID")
    private Long id;

    @Schema(name = "reservoirCode", description = "水库编码")
    private String reservoirCode;

    @Schema(name = "name", description = "水库名称")
    private String name;

    @Schema(name = "waterProject", description = "所属水利工程")
    private String waterProject;

    @Schema(name = "departmentId", description = "管理部门ID")
    private Long departmentId;

    @Schema(name = "departmentName", description = "管理部门名称")
    private String departmentName;

    @Schema(name = "managerId", description = "负责人ID")
    private Long managerId;

    @Schema(name = "managerName", description = "负责人姓名")
    private String managerName;

    @Schema(name = "longitude", description = "经度")
    private BigDecimal longitude;

    @Schema(name = "latitude", description = "纬度")
    private BigDecimal latitude;

    @Schema(name = "location", description = "水库所在位置")
    private String location;

    @Schema(name = "registrationNo", description = "水库注册登记号")
    private String registrationNo;

    @Schema(name = "adminRegionCode", description = "所属行政区划代码")
    private String adminRegionCode;

    @Schema(name = "adminRegionName", description = "所属行政区划名称")
    private String adminRegionName;

    @Schema(name = "engineeringGrade", description = "工程等级")
    private String engineeringGrade;

    @Schema(name = "engineeringGradeLabel", description = "工程等级标签")
    private String engineeringGradeLabel;

    @Schema(name = "engineeringScale", description = "工程规模")
    private String engineeringScale;

    @Schema(name = "engineeringScaleLabel", description = "工程规模标签")
    private String engineeringScaleLabel;

    @Schema(name = "totalCapacity", description = "总库容（万m³）")
    private BigDecimal totalCapacity;

    @Schema(name = "regulatingCapacity", description = "调节库容（万m³）")
    private BigDecimal regulatingCapacity;

    @Schema(name = "deadCapacity", description = "死库容（万m³）")
    private BigDecimal deadCapacity;

    @Schema(name = "establishmentDate", description = "建库年月")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate establishmentDate;

    @Schema(name = "createdAt", description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt", description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // ========== 业务统计字段 ==========

    @Schema(name = "monitoringDataCount", description = "监测数据总数")
    private Long monitoringDataCount;

    @Schema(name = "currentWaterLevel", description = "当前水位（m）")
    private BigDecimal currentWaterLevel;

    @Schema(name = "currentStorageCapacity", description = "当前蓄水量（万m³）")
    private BigDecimal currentStorageCapacity;
} 