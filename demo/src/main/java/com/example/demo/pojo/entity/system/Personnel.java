package com.example.demo.pojo.entity.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 人员信息表实体类
 * 对应数据库表: personnel
 */
@Data
@Schema(name = "Personnel", description = "人员信息表")
public class Personnel implements Serializable {

    @Schema(name = "id", description = "人员ID")
    private Long id;

    @Schema(name = "name", description = "姓名")
    private String name;

    @Schema(name = "phone", description = "联系电话")
    private String phone;

    @Schema(name = "email", description = "电子邮箱")
    private String email;

    @Schema(name = "positionId", description = "岗位ID")
    private Long positionId;

    @Schema(name = "departmentId", description = "所属部门ID")
    private Long departmentId;

    @Schema(name = "userId", description = "关联用户ID")
    private Long userId;

    @Schema(name = "employeeNo", description = "工号")
    private String employeeNo;

    @Schema(name = "hireDate", description = "入职日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime hireDate;

    @Schema(name = "workResponsibilities", description = "工作职责")
    private String workResponsibilities;

    @Schema(name = "isActive", description = "人员是否启用")
    private String isActive = "1";

    @Schema(name = "createdAt", description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt", description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(name = "deletedAt", description = "软删除时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;


}
