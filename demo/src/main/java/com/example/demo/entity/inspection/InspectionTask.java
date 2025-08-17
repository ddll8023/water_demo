package com.example.demo.entity.inspection;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("inspection_tasks")
public class InspectionTask {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String title;

    /**
     * 设施类型（通过工程服务API获取类型选项）
     */
    @TableField("facility_type")
    private String facilityType;

    @TableField("facility_id")
    private Long facilityId;

    @TableField("frequency")
    private String frequency;

    @TableField("content")
    private String content;

    @TableField("assignee_id")
    private Long assigneeId;

    @TableField("status")
    private String status;

    @TableField("scheduled_date")
    private LocalDate scheduledDate;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("deleted_at")
    private LocalDateTime deletedAt;
} 