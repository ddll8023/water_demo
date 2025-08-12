package com.example.demo.dto.map;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 设施地理位置DTO
 * 用于"一张图"模块展示水利设施的地理位置和基本信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FacilityLocationDTO {

    /**
     * 设施ID
     */
    private Long id;

    /**
     * 设施名称
     */
    private String name;

    /**
     * 设施类型 (pumping_station:泵站, water_plant:水厂, reservoir:水库, monitoring_station:监测站)
     */
    private String type;

    /**
     * 设施类型名称
     */
    private String typeName;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 运行状态 (NORMAL:正常, MAINTENANCE:维护中, FAULT:故障, OFFLINE:离线)
     */
    private String status;

    /**
     * 运行状态名称
     */
    private String statusName;

    /**
     * 设施编码
     */
    private String code;

    /**
     * 管理部门ID
     */
    private Long departmentId;

    /**
     * 管理部门名称
     */
    private String departmentName;

    /**
     * 备注信息
     */
    private String remarks;
}