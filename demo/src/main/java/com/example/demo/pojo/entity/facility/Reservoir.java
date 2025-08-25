package com.example.demo.pojo.entity.facility;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 水库信息表实体类
 * 对应数据库表: reservoirs
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("reservoirs")
public class Reservoir {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 水库编码
     */
    @TableField("reservoir_code")
    private String reservoirCode;

    /**
     * 水库名称
     */
    @TableField("name")
    private String name;

    /**
     * 所属水利工程
     */
    @TableField("water_project")
    private String waterProject;

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
     * 水库所在位置
     */
    @TableField("location")
    private String location;

    /**
     * 水库注册登记号
     */
    @TableField("registration_no")
    private String registrationNo;

    /**
     * 所属行政区划（关联regions.code）
     */
    @TableField("admin_region_code")
    private String adminRegionCode;

    /**
     * 工程等级（关联dict_data.data_value，type_code=engineering_grade）
     */
    @TableField("engineering_grade")
    private String engineeringGrade;

    /**
     * 工程规模（关联dict_data.data_value，type_code=engineering_scale）
     */
    @TableField("engineering_scale")
    private String engineeringScale;

    /**
     * 总库容（万m³）
     */
    @TableField("total_capacity")
    private BigDecimal totalCapacity;

    /**
     * 调节库容（万m³）
     */
    @TableField("regulating_capacity")
    private BigDecimal regulatingCapacity;

    /**
     * 死库容（万m³）
     */
    @TableField("dead_capacity")
    private BigDecimal deadCapacity;

    /**
     * 建库年月
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
     * 工程等级名称（非数据库字段）
     */
    @TableField(exist = false)
    private String engineeringGradeName;

    /**
     * 工程规模名称（非数据库字段）
     */
    @TableField(exist = false)
    private String engineeringScaleName;
}
