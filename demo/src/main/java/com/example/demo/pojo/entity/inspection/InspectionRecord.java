package com.example.demo.pojo.entity.inspection;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("inspection_records")
public class InspectionRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("task_id")
    private Long taskId;

    @TableField("inspector_id")
    private Long inspectorId;

    /**
     * 设施类型（通过工程服务API获取类型选项）
     */
    @TableField("facility_type")
    private String facilityType;

    @TableField("facility_id")
    private Long facilityId;

    @TableField("record_time")
    private LocalDateTime recordTime;

    @TableField("device_status")
    private String deviceStatus;

    @TableField("issue_flag")
    private Integer issueFlag;

    @TableField("issue_description")
    private String issueDescription;

    @TableField("resolution")
    private String resolution;

    @TableField("resolved_at")
    private LocalDateTime resolvedAt;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("deleted_at")
    private LocalDateTime deletedAt;
} 