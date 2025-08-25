package com.example.demo.pojo.entity.facility;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 水厂信息表实体类
 * 对应数据库表: water_plants
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("water_plants")
public class WaterPlant {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 水厂编码
     */
    @TableField("plant_code")
    private String plantCode;

    /**
     * 水厂名称
     */
    @TableField("name")
    private String name;

    /**
     * 所属供水工程
     */
    @TableField("water_project")
    private String waterProject;

    /**
     * 管理部门ID（关联departments.id）
     */
    @TableField("department_id")
    private Long departmentId;

    /**
     * 负责人ID（关联personnel.id）
     */
    @TableField("manager_id")
    private Long managerId;

    /**
     * 地址
     */
    @TableField("address")
    private String address;

    /**
     * 管理单位
     */
    @TableField("management_unit")
    private String managementUnit;

    /**
     * 经度
     */
    @TableField("longitude")
    private BigDecimal longitude;

    /**
     * 纬度
     */
    @TableField("latitude")
    private BigDecimal latitude;

    /**
     * 设计规模（m³/天）
     */
    @TableField("design_scale")
    private BigDecimal designScale;

    /**
     * 供水范围（村镇）
     */
    @TableField("supply_area")
    private String supplyArea;

    /**
     * 供水负荷率（%）
     */
    @TableField("supply_load_ratio")
    private BigDecimal supplyLoadRatio;

    /**
     * 供水人口（万人）
     */
    @TableField("supply_population")
    private Integer supplyPopulation;

    /**
     * 联系电话
     */
    @TableField("contact_phone")
    private String contactPhone;

    /**
     * 建站年月
     */
    @TableField("establishment_date")
    private LocalDate establishmentDate;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 软删除标记
     */
    @TableLogic
    @TableField("deleted_at")
    private LocalDateTime deletedAt;

    // ========== 关联信息字段（非数据库字段，用于存储关联查询结果） ==========

    /**
     * 管理部门名称（非数据库字段）
     */
    @TableField(exist = false)
    private String departmentName;

    /**
     * 负责人姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String managerName;
}
