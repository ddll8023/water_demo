package com.example.demo.entity.warning;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 预警信息记录实体类
 * 对应数据库表: warning_records
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("warning_records")
public class WarningRecord {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 预警地点
     */
    @TableField("warning_location")
    private String warningLocation;

    /**
     * 预警类型（水位、流量、水质等）
     */
    @TableField("warning_type")
    private String warningType;

    /**
     * 预警等级（关联dict_data，type_code=warning_level）
     */
    @TableField("warning_level")
    private String warningLevel;

    /**
     * 预警内容
     */
    @TableField("warning_content")
    private String warningContent;

    /**
     * 预警状态（进行中、已解除）
     */
    @TableField("warning_status")
    private String warningStatus = "进行中";

    /**
     * 所属工程
     */
    @TableField("project_name")
    private String projectName;

    /**
     * 发生时间
     */
    @TableField("occurred_at")
    private LocalDateTime occurredAt;

    /**
     * 解除时间
     */
    @TableField("resolved_at")
    private LocalDateTime resolvedAt;

    /**
     * 关联预警指标ID
     */
    @TableField("threshold_id")
    private Long thresholdId;

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
     * 预警等级名称（非数据库字段）
     * 通过关联查询dict_data表获取预警等级的显示名称
     */
    @TableField(exist = false)
    private String warningLevelName;

    /**
     * 预警类型名称（非数据库字段）
     * 通过关联查询dict_data表获取预警类型的显示名称
     */
    @TableField(exist = false)
    private String warningTypeName;

    /**
     * 预警状态名称（非数据库字段）
     * 通过关联查询dict_data表获取预警状态的显示名称
     */
    @TableField(exist = false)
    private String warningStatusName;

    /**
     * 持续时长（非数据库字段）
     * 通过计算 |resolved_at - occurred_at| 或 |当前时间 - occurred_at| 得出，格式如：2小时30分钟
     * 使用绝对值确保时长始终为正数，即使occurred_at为未来时间
     */
    @TableField(exist = false)
    private String duration;

    // 注意：MyBatis-Plus中不直接支持关联查询
    // 以下关联数据需要通过Service层或者自定义SQL来处理：
    // - 预警等级名称：通过warning_level查询dict_data表获取data_label
    // - 预警类型名称：通过warning_type查询dict_data表获取data_label
    // - 预警状态名称：通过warning_status查询dict_data表获取data_label
    // - 关联预警指标：通过threshold_id查询warning_thresholds表
    // - 持续时长计算：在Mapper层通过SQL的ABS函数计算绝对值，确保结果为正数
}
