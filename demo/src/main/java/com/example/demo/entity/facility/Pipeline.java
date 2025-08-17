package com.example.demo.entity.facility;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 管道信息表实体类
 * 对应数据库表: pipelines
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("pipelines")
public class Pipeline {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 管道编码
     */
    @TableField("pipeline_code")
    private String pipelineCode;

    /**
     * 管道名称
     */
    @TableField("name")
    private String name;

    /**
     * 管道类型（关联dict_data.data_value，type_code=pipeline_type）
     */
    @TableField("pipeline_type")
    private String pipelineType;

    /**
     * 起点经度
     */
    @TableField("start_longitude")
    private BigDecimal startLongitude;

    /**
     * 起点纬度
     */
    @TableField("start_latitude")
    private BigDecimal startLatitude;

    /**
     * 终点经度
     */
    @TableField("end_longitude")
    private BigDecimal endLongitude;

    /**
     * 终点纬度
     */
    @TableField("end_latitude")
    private BigDecimal endLatitude;

    /**
     * 管道长度（km）
     */
    @TableField("length")
    private BigDecimal length;

    /**
     * 管径（mm）
     */
    @TableField("diameter")
    private BigDecimal diameter;

    /**
     * 设计压力（MPa）
     */
    @TableField("design_pressure")
    private BigDecimal designPressure;

    /**
     * 设计流量（m³/h）
     */
    @TableField("design_flow")
    private BigDecimal designFlow;

    /**
     * 埋深（m）
     */
    @TableField("burial_depth")
    private BigDecimal burialDepth;

    /**
     * 建设年月
     */
    @TableField("construction_date")
    private LocalDate constructionDate;

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
     * 管道类型名称（非数据库字段）
     */
    @TableField(exist = false)
    private String pipelineTypeName;

}
