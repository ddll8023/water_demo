package com.example.demo.pojo.DTO.facility;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 水厂响应DTO
 * 用于API响应，包含水厂的完整信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WaterPlantResponseDTO {

    /**
     * 水厂ID
     */
    private Long id;

    /**
     * 水厂编码
     */
    private String plantCode;

    /**
     * 水厂名称
     */
    private String name;

    /**
     * 所属供水工程
     */
    private String waterProject;

    /**
     * 管理部门ID
     */
    private Long departmentId;

    /**
     * 管理部门名称
     */
    private String departmentName;

    /**
     * 负责人ID
     */
    private Long managerId;

    /**
     * 负责人姓名
     */
    private String managerName;

    /**
     * 负责人电话
     */
    private String managerPhone;

    /**
     * 地址
     */
    private String address;

    /**
     * 管理单位
     */
    private String managementUnit;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 设计规模（m³/天）
     */
    private BigDecimal designScale;

    /**
     * 供水范围（村镇）
     */
    private String supplyArea;

    /**
     * 供水负荷率（%）
     */
    private BigDecimal supplyLoadRatio;

    /**
     * 供水人口（万人）
     */
    private Integer supplyPopulation;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 建站年月
     */
    private LocalDate establishmentDate;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
