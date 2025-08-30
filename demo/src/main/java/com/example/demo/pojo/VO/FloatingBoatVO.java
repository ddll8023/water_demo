package com.example.demo.pojo.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 浮船视图对象
 * 用于API响应，包含浮船的完整信息
 */
@Data
@Schema(name = "FloatingBoatVO", description = "浮船视图对象")
public class FloatingBoatVO implements Serializable {

    @Schema(name = "id", description = "浮船ID")
    private Long id;

    @Schema(name = "boatCode", description = "浮船编码")
    private String boatCode;

    @Schema(name = "name", description = "浮船名称")
    private String name;

    @Schema(name = "waterPlantId", description = "所属水厂ID")
    private Long waterPlantId;

    @Schema(name = "waterPlantName", description = "所属水厂名称")
    private String waterPlantName;

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

    @Schema(name = "length", description = "船长（m）")
    private BigDecimal length;

    @Schema(name = "width", description = "船宽（m）")
    private BigDecimal width;

    @Schema(name = "draft", description = "吃水深度（m）")
    private BigDecimal draft;

    @Schema(name = "capacity", description = "抽水能力（m³/h）")
    private BigDecimal capacity;

    @Schema(name = "powerConsumption", description = "功率（kW）")
    private BigDecimal powerConsumption;

    @Schema(name = "pumpingStatus", description = "抽水状态")
    private String pumpingStatus;

    @Schema(name = "pumpingStatusLabel", description = "抽水状态标签")
    private String pumpingStatusLabel;

    @Schema(name = "maintenanceDate", description = "上次维护日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate maintenanceDate;

    @Schema(name = "remark", description = "备注")
    private String remark;

    @Schema(name = "createdAt", description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt", description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // ========== 业务统计字段 ==========

    @Schema(name = "totalRunningHours", description = "累计运行小时数")
    private Long totalRunningHours;

    @Schema(name = "maintenanceCount", description = "维护次数")
    private Integer maintenanceCount;

    @Schema(name = "daysSinceLastMaintenance", description = "距离上次维护天数")
    private Integer daysSinceLastMaintenance;
} 