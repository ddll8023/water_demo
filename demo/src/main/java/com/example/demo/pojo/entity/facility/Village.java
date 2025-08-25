package com.example.demo.pojo.entity.facility;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 村庄信息表实体类
 * 对应数据库表: villages
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("villages")
public class Village {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 村庄名称
     */
    @TableField("name")
    private String name;

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
     * 行政区划代码
     */
    @TableField("administrative_code")
    private String administrativeCode;

    /**
     * 现状人口（人）
     */
    @TableField("current_population")
    private Integer currentPopulation;

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
