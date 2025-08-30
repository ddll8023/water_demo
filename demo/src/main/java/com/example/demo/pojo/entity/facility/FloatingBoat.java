package com.example.demo.pojo.entity.facility;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 浮船信息表实体类
 * 对应数据库表: floating_boats
 */
@Data
@Schema(name = "FloatingBoat", description = "浮船信息实体")
public class FloatingBoat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "id", description = "主键ID")
    private Long id;

    @Schema(name = "boatCode", description = "浮船编码")
    private String boatCode;

    @Schema(name = "name", description = "浮船名称")
    private String name;

    @Schema(name = "waterPlantId", description = "所属水厂ID（关联water_plants.id）")
    private Long waterPlantId;

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

    @Schema(name = "pumpingStatus", description = "抽水状态（关联dict_data.data_value，type_code=device_status）")
    private String pumpingStatus;

    @Schema(name = "maintenanceDate", description = "上次维护日期")
    private LocalDate maintenanceDate;

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
