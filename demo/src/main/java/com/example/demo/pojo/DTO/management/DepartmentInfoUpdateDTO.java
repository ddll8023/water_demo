package com.example.demo.pojo.DTO.management;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 部门信息更新数据传输对象
 */
@Data
public class DepartmentInfoUpdateDTO {

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 50, message = "部门名称长度不能超过50个字符")
    private String name;

    /**
     * 上级部门ID
     */
    private Long parentId;

    /**
     * 排序序号
     */
    private Integer orderNum;

    /**
     * 负责人
     */
    private String leader;

    /**
     * 联系电话
     */
    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    private String phone;

    /**
     * 联系邮箱
     */
    @Size(max = 50, message = "联系邮箱长度不能超过50个字符")
    private String email;

    /**
     * 部门职责
     */
    @Size(max = 500, message = "部门职责长度不能超过500个字符")
    private String duty;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 联系信息
     */
    @Size(max = 200, message = "联系信息长度不能超过200个字符")
    private String contact;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
} 