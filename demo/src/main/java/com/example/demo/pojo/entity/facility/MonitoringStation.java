package com.example.demo.pojo.entity.facility;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 监测站点信息表实体类
 * 对应数据库表: monitoring_stations
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("monitoring_stations")
public class MonitoringStation {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 监测站点编码
     */
    @TableField("station_code")
    private String stationCode;

    /**
     * 监测站点名称
     */
    @TableField("name")
    private String name;

    /**
     * 水系名称
     */
    @TableField("water_system_name")
    private String waterSystemName;

    /**
     * 河流名称
     */
    @TableField("river_name")
    private String riverName;

    /**
     * 监测项目码（关联dict_data.data_value，type_code=monitoring_item）
     */
    @TableField("monitoring_item_code")
    private String monitoringItemCode;

    /**
     * 行政区划代码（关联regions.code）
     */
    @TableField("admin_region_code")
    private String adminRegionCode;

    /**
     * 设站年月
     */
    @TableField("establishment_date")
    private LocalDate establishmentDate;

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
     * 备注
     */
    @TableField("remark")
    private String remark;

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
     * 负责人姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String managerName;

    /**
     * 监测类型名称（非数据库字段）
     */
    @TableField(exist = false)
    private String monitoringTypeName;

    /**
     * 运行状态名称（非数据库字段）
     */
    @TableField(exist = false)
    private String operationStatusName;
}
