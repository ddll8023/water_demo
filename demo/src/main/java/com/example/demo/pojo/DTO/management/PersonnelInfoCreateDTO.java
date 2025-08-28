package com.example.demo.pojo.DTO.management;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 人员信息创建DTO
 * 用于管理信息服务模块的人员档案创建
 */
@Data
@Schema(description = "人员信息创建DTO")
public class PersonnelInfoCreateDTO implements Serializable {

    @NotBlank(message = "姓名不能为空")
    @Size(max = 100, message = "姓名长度不能超过100个字符")
    @Schema(description = "姓名", example = "张三")
    private String name;

    @Schema(description = "岗位ID", example = "1")
    private Long positionId;

    @Schema(description = "部门ID", example = "1")
    private Long departmentId;

    @Size(max = 50, message = "联系电话长度不能超过50个字符")
    @Schema(description = "联系电话", example = "13800138000")
    private String phone;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "工号", example = "EMP001")
    private String employeeNo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "入职日期", example = "2023-01-01")
    private LocalDate hireDate;
    
    @Size(max = 500, message = "工作职责描述不能超过500个字符")
    @Schema(description = "工作职责", example = "负责系统开发")
    private String workResponsibilities;
}
