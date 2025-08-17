package com.example.demo.dto.management;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 人员信息创建DTO
 * 用于管理信息服务模块的人员档案创建
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PersonnelInfoCreateDTO {

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    @Size(max = 100, message = "姓名长度不能超过100个字符")
    private String fullName;

    /**
     * 岗位ID（可选）
     */
    private Long positionId;

    /**
     * 部门ID（可选）
     */
    private Long departmentId;

    /**
     * 联系电话
     */
    @Size(max = 50, message = "联系电话长度不能超过50个字符")
    private String phone;

    /**
     * 电子邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

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
    @Size(max = 500, message = "工作职责描述不能超过500个字符")
    private String workResponsibilities;
}
