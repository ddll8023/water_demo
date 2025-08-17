package com.example.demo.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 人员信息表实体类
 * 对应数据库表: personnel
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("personnel")
public class Personnel {

    /**
     * 人员 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 姓名
     */
    @TableField("full_name")
    private String fullName;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 电子邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 岗位 ID
     */
    @TableField("position_id")
    private Long positionId;

    /**
     * 所属部门 ID
     */
    @TableField("department_id")
    private Long departmentId;

    /**
     * 关联用户 ID (可选)
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 工号
     */
    @TableField("employee_no")
    private String employeeNo;

    /**
     * 入职日期
     */
    @TableField("hire_date")
    private LocalDate hireDate;

    /**
     * 工作职责
     */
    @TableField("work_responsibilities")
    private String workResponsibilities;

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
     * 部门名称（非数据库字段）
     * 通过关联查询departments表获取
     */
    @TableField(exist = false)
    private String departmentName;

    /**
     * 岗位名称（非数据库字段）
     * 通过关联查询positions表获取
     */
    @TableField(exist = false)
    private String positionName;

    /**
     * 关联用户名（非数据库字段）
     * 通过关联查询users表获取
     */
    @TableField(exist = false)
    private String username;

    // 注意：MyBatis-Plus中不直接支持关联查询
    // 以下关联数据需要通过Service层或者自定义SQL来处理：
    // - 部门信息：通过departmentId查询Department表
    // - 岗位信息：通过positionId查询Position表
    // - 关联用户：通过userId查询User表（可选）
    // - 负责的巡检任务：通过assignee_id查询InspectionTask表
    // - 巡检记录：通过inspector_id查询InspectionRecord表
    // - 创建和处理的问题：通过creator_id/assignee_id查询Issue表
    // - 问题跟进记录：通过operator_id查询IssueFollowUp表
    // - 确认的预警记录：通过confirmed_by查询WarningRecord表
    // - 处理的视频告警：通过handler_id查询VideoAlarmEvent表
    // - 下发的设备控制指令：通过issuer_id查询DeviceControlCommand表
    // - 抄表记录：通过reader_id查询MeterReading表
}
