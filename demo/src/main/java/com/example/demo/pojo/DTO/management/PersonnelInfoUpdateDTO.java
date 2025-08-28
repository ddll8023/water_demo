package com.example.demo.pojo.DTO.management;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 人员信息更新DTO
 * 用于管理信息服务模块的人员档案更新
 */
@Data
@Schema(name = "PersonnelInfoUpdateDTO", description = "人员信息更新DTO")
public class PersonnelInfoUpdateDTO {

    @NotBlank(message = "姓名不能为空")
    @Size(max = 100, message = "姓名长度不能超过100个字符")
    @Schema(name = "name", description = "姓名")
    private String name;

    @Schema(name = "positionId", description = "岗位ID")
    private Long positionId;

    @Schema(name = "departmentId", description = "所属部门ID")
    private Long departmentId;

    @Size(max = 50, message = "联系电话长度不能超过50个字符")
    @Schema(name = "phone", description = "联系电话")
    private String phone;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(name = "hireDate", description = "入职日期")
    private LocalDate hireDate;

    @Size(max = 500, message = "工作职责描述不能超过500个字符")
    @Schema(name = "workResponsibilities", description = "工作职责")
    private String workResponsibilities;
}
