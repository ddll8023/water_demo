package com.example.demo.pojo.dto.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 人员响应DTO
 * 用于API响应，包含人员的完整信息
 */
@Data
public class PersonnelResponseDTO {

    /**
     * 人员 ID
     */
    private Long id;

    /**
     * 姓名
     */
    private String fullName;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 岗位 ID
     */
    private Long positionId;

    /**
     * 所属部门 ID
     */
    private Long departmentId;

    /**
     * 关联用户 ID (可选)
     */
    private Long userId;

    /**
     * 工号
     */
    private String employeeNo;

    /**
     * 入职日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;

    /**
     * 工作职责
     */
    private String workResponsibilities;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // ========== 关联信息字段 ==========

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 岗位名称
     */
    private String positionName;

    /**
     * 关联用户名
     */
    private String username;
}
