package com.example.demo.dto.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 人员创建DTO
 * 用于创建人员时的数据传输
 */
@Data
public class PersonnelCreateDTO {

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    @Size(max = 100, message = "姓名长度不能超过100个字符")
    private String fullName;

    /**
     * 联系电话
     */
    @Size(max = 50, message = "联系电话长度不能超过50个字符")
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
    @Size(max = 50, message = "工号长度不能超过50个字符")
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
