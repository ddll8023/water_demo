package com.example.demo.dto.management;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 人员信息更新DTO
 * 用于管理信息服务模块的人员档案更新
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PersonnelInfoUpdateDTO {

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
     * 入职日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;

    /**
     * 工作职责
     */
    @Size(max = 500, message = "工作职责描述不能超过500个字符")
    private String workResponsibilities;

    /**
     * 人员状态 (在职/离职)
     */
    private String status;
}
