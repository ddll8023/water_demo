package com.example.demo.pojo.dto.map;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理体系DTO
 * 用于"一张图"模块展示管理体系的部门、人员及区域信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ManagementSystemDTO {

    /**
     * 部门ID
     */
    private Long departmentId;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 部门编码
     */
    private String departmentCode;

    /**
     * 部门类型
     */
    private String departmentType;

    /**
     * 人员ID
     */
    private Long personnelId;

    /**
     * 人员姓名
     */
    private String personnelName;

    /**
     * 人员岗位ID
     */
    private Long personnelPositionId;

    /**
     * 人员岗位名称
     */
    private String personnelPosition;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 负责区域边界 (GeoJSON格式)
     */
    private String regionBoundary;

    /**
     * 负责区域名称
     */
    private String regionName;

    /**
     * 部门层级
     */
    private Integer departmentLevel;

    /**
     * 上级部门ID
     */
    private Long parentDepartmentId;

    /**
     * 上级部门名称
     */
    private String parentDepartmentName;
}