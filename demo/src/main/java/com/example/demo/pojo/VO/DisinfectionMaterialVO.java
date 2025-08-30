package com.example.demo.pojo.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 消毒药材视图对象
 * 用于API响应，包含消毒药材的完整信息
 */
@Data
@Schema(name = "DisinfectionMaterialVO", description = "消毒药材视图对象")
public class DisinfectionMaterialVO implements Serializable {

    @Schema(name = "id", description = "消毒药材ID")
    private Long id;

    @Schema(name = "name", description = "消毒药材名称")
    private String name;

    @Schema(name = "waterPlantId", description = "所属水厂ID")
    private Long waterPlantId;

    @Schema(name = "waterPlantName", description = "所属水厂名称")
    private String waterPlantName;

    @Schema(name = "storageCondition", description = "存储条件")
    private String storageCondition;

    @Schema(name = "productionDate", description = "生产日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate productionDate;

    @Schema(name = "validityPeriod", description = "有效期")
    private String validityPeriod;

    @Schema(name = "quantity", description = "库存数量")
    private BigDecimal quantity;

    @Schema(name = "unit", description = "单位")
    private String unit;

    @Schema(name = "remark", description = "备注")
    private String remark;

    @Schema(name = "createdAt", description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt", description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // ========== 业务统计字段 ==========

    @Schema(name = "expirationDate", description = "过期日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;

    @Schema(name = "daysUntilExpiration", description = "距离过期天数")
    private Integer daysUntilExpiration;

    @Schema(name = "isExpired", description = "是否已过期")
    private Boolean isExpired;

    @Schema(name = "usageRecordCount", description = "使用记录数量")
    private Integer usageRecordCount;
} 