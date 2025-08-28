package com.example.demo.pojo.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 人员信息查询结果视图对象
 */
@Data
@Schema(description = "人员信息查询结果")
public class PersonnelInfoVO implements Serializable {

    @Schema(description = "人员ID", example = "1")
    private Long id;

    @Schema(description = "工号", example = "EMP001")
    private String employeeNo;

    @Schema(description = "姓名", example = "张三")
    private String name;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "岗位ID", example = "1")
    private Long positionId;

    @Schema(description = "岗位名称", example = "工程师")
    private String positionName;

    @Schema(description = "部门ID", example = "1")
    private Long departmentId;

    @Schema(description = "部门名称", example = "技术部")
    private String departmentName;

    @Schema(description = "入职日期", example = "2023-01-01T00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime hireDate;

    @Schema(description = "工作职责", example = "负责系统开发")
    private String workResponsibilities;

    @Schema(description = "创建时间", example = "2023-01-01T00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", example = "2023-01-01T00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
} 