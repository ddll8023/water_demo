package com.example.demo.pojo.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 泵站视图对象
 * 用于API响应，包含泵站的完整信息
 */
@Data
@Schema(name = "PumpingStationVO", description = "泵站视图对象")
public class PumpingStationVO implements Serializable {

    @Schema(name = "id", description = "泵站ID")
    private Long id;

    @Schema(name = "name", description = "泵站名称")
    private String name;

    @Schema(name = "stationCode", description = "泵站编码")
    private String stationCode;

    @Schema(name = "stationType", description = "泵站类型")
    private String stationType;

    @Schema(name = "waterProject", description = "所属供水工程")
    private String waterProject;

    @Schema(name = "waterCompany", description = "所属供水公司")
    private String waterCompany;

    @Schema(name = "longitude", description = "经度")
    private BigDecimal longitude;

    @Schema(name = "latitude", description = "纬度")
    private BigDecimal latitude;

    @Schema(name = "address", description = "地址")
    private String address;

    @Schema(name = "operationMode", description = "运行方式")
    private String operationMode;

    @Schema(name = "operationModeLabel", description = "运行方式标签")
    private String operationModeLabel;

    @Schema(name = "unitCount", description = "机组数量（台）")
    private Integer unitCount;

    @Schema(name = "designScale", description = "设计规模（m³/天）")
    private BigDecimal designScale;

    @Schema(name = "installedCapacity", description = "装机容量（kW）")
    private BigDecimal installedCapacity;

    @Schema(name = "liftHead", description = "扬程（m）")
    private BigDecimal liftHead;

    @Schema(name = "establishmentDate", description = "建站年月")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate establishmentDate;

    @Schema(name = "createdAt", description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt", description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // ========== 业务统计字段 ==========

    @Schema(name = "runningHours", description = "累计运行小时数")
    private Long runningHours;

    @Schema(name = "maintenanceCount", description = "维护次数")
    private Integer maintenanceCount;
} 