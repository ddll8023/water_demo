package com.example.demo.pojo.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 监测站点视图对象
 * 用于API响应，包含监测站点的完整信息
 */
@Data
@Schema(name = "MonitoringStationVO", description = "监测站点视图对象")
public class MonitoringStationVO implements Serializable {

    @Schema(name = "id", description = "监测站点ID")
    private Long id;

    @Schema(name = "stationCode", description = "监测站点编码")
    private String stationCode;

    @Schema(name = "name", description = "监测站点名称")
    private String name;

    @Schema(name = "waterSystemName", description = "水系名称")
    private String waterSystemName;

    @Schema(name = "riverName", description = "河流名称")
    private String riverName;

    @Schema(name = "monitoringItemCode", description = "监测项目码")
    private String monitoringItemCode;

    @Schema(name = "monitoringItemName", description = "监测项目名称")
    private String monitoringItemName;

    @Schema(name = "adminRegionCode", description = "行政区划代码")
    private String adminRegionCode;

    @Schema(name = "adminRegionName", description = "行政区划名称")
    private String adminRegionName;

    @Schema(name = "establishmentDate", description = "设站年月")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate establishmentDate;

    @Schema(name = "longitude", description = "经度")
    private BigDecimal longitude;

    @Schema(name = "latitude", description = "纬度")
    private BigDecimal latitude;

    @Schema(name = "remark", description = "备注")
    private String remark;

    @Schema(name = "createdAt", description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt", description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // ========== 业务统计字段 ==========

    @Schema(name = "dataCount", description = "监测数据总数")
    private Long dataCount;

    @Schema(name = "latestDataTime", description = "最新数据时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime latestDataTime;
} 