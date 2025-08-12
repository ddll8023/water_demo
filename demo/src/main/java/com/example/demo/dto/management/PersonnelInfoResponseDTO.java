package com.example.demo.dto.management;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 人员信息响应DTO
 * 用于管理信息服务模块的人员档案管理
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PersonnelInfoResponseDTO {

    /**
     * 人员ID
     */
    private Long id;

    /**
     * 姓名
     */
    private String fullName;

    /**
     * 岗位名称
     */
    private String positionName;

    /**
     * 岗位ID
     */
    private Long positionId;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 部门ID
     */
    private Long departmentId;

    /**
     * 联系电话
     */
    private String phone;

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
     * 人员状态 (在职/离职)
     */
    private String status;

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
}
