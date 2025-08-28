package com.example.demo.pojo.DTO.management;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 部门信息更新数据传输对象
 */

 @Data
 @Schema(name = "DepartmentInfoUpdateDTO", description = "部门信息更新数据传输对象")
public class DepartmentInfoUpdateDTO implements Serializable {

    @Schema(name = "name", description = "部门名称")
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 50, message = "部门名称长度不能超过50个字符")
    private String name;

    @Schema(name = "parentId", description = "上级部门ID")
    private Long parentId;

    @Schema(name = "orderNum", description = "排序序号")
    private Integer orderNum;

    @Schema(name = "leader", description = "负责人")
    private String leader;

    @Schema(name = "phone", description = "联系电话")
    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    private String phone;

    @Schema(name = "email", description = "联系邮箱")
    @Size(max = 50, message = "联系邮箱长度不能超过50个字符")
    private String email;

    @Schema(name = "duty", description = "部门职责")
    @Size(max = 500, message = "部门职责长度不能超过500个字符")
    private String duty;

        @Schema(name = "status", description = "状态（0正常 1停用）")
    private String status;

    @Schema(name = "contact", description = "联系信息")
    @Size(max = 200, message = "联系信息长度不能超过200个字符")
    private String contact;

    @Schema(name = "createdAt", description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
} 