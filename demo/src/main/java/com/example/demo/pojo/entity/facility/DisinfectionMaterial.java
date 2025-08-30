package com.example.demo.pojo.entity.facility;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 消毒药材表实体类
 * 对应数据库表: disinfection_materials
 */
@Data
@Schema(name = "DisinfectionMaterial", description = "消毒药材实体")
public class DisinfectionMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "id", description = "主键ID")
    private Long id;

    @Schema(name = "name", description = "名称")
    private String name;

    @Schema(name = "waterPlantId", description = "所属水厂ID（关联water_plants.id）")
    private Long waterPlantId;

    @Schema(name = "storageCondition", description = "存储条件")
    private String storageCondition;

    @Schema(name = "productionDate", description = "生产日期")
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

    @Schema(name = "deletedAt", description = "软删除标记")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;
}
