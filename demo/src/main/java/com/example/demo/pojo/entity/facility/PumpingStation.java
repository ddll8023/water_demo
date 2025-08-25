package com.example.demo.pojo.entity.facility;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 泵站信息表实体类
 * 对应数据库表: pumping_stations
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("pumping_stations")
public class PumpingStation {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 泵站名称
     */
    @TableField("name")
    private String name;

    /**
     * 泵站编码
     */
    @TableField("station_code")
    private String stationCode;

    /**
     * 泵站类型（通过工程服务API获取类型选项）
     */
    @TableField("station_type")
    private String stationType;

    /**
     * 所属供水工程
     */
    @TableField("water_project")
    private String waterProject;

    /**
     * 所属供水公司
     */
    @TableField("water_company")
    private String waterCompany;

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
     * 地址
     */
    @TableField("address")
    private String address;

    /**
     * 运行方式（关联dict_data.data_value，type_code=operation_mode）
     */
    @TableField("operation_mode")
    private String operationMode;

    /**
     * 机组数量（台）
     */
    @TableField("unit_count")
    private Integer unitCount;

    /**
     * 设计规模（m³/天）
     */
    @TableField("design_scale")
    private BigDecimal designScale;

    /**
     * 装机容量（kW）
     */
    @TableField("installed_capacity")
    private BigDecimal installedCapacity;

    /**
     * 扬程（m）
     */
    @TableField("lift_head")
    private BigDecimal liftHead;

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

    /**
     * 泵站类型名称（非数据库字段）
     */
    @TableField(exist = false)
    private String stationTypeName;

    /**
     * 运行方式名称（非数据库字段）
     */
    @TableField(exist = false)
    private String operationModeName;
}
