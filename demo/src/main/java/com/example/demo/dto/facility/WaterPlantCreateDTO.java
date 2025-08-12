package com.example.demo.dto.facility;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 水厂创建DTO
 * 用于创建新水厂的请求
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WaterPlantCreateDTO {

    /**
     * 水厂编码
     */
    @Size(max = 100, message = "水厂编码长度不能超过100个字符")
    private String plantCode;

    /**
     * 水厂名称
     */
    @NotBlank(message = "水厂名称不能为空")
    @Size(max = 255, message = "水厂名称长度不能超过255个字符")
    private String name;

    /**
     * 所属供水工程
     */
    @Size(max = 255, message = "所属供水工程长度不能超过255个字符")
    private String waterProject;

    /**
     * 管理部门ID（关联departments.id）
     */
    private Long departmentId;

    /**
     * 负责人ID（关联personnel.id）
     */
    private Long managerId;

    /**
     * 地址
     */
    @Size(max = 255, message = "地址长度不能超过255个字符")
    private String address;

    /**
     * 管理单位
     */
    @Size(max = 255, message = "管理单位长度不能超过255个字符")
    private String managementUnit;

    /**
     * 经度
     */
    @DecimalMin(value = "-180.0", message = "经度必须在-180到180之间")
    private BigDecimal longitude;

    /**
     * 纬度
     */
    @DecimalMin(value = "-90.0", message = "纬度必须在-90到90之间")
    private BigDecimal latitude;

    /**
     * 设计规模（m³/天）
     */
    @DecimalMin(value = "0.0", message = "设计规模不能为负数")
    private BigDecimal designScale;

    /**
     * 供水范围（村镇）
     */
    @Size(max = 500, message = "供水范围长度不能超过500个字符")
    private String supplyArea;

    /**
     * 供水负荷率（%）
     */
    @DecimalMin(value = "0.0", message = "供水负荷率不能为负数")
    private BigDecimal supplyLoadRatio;

    /**
     * 供水人口（万人）
     */
    @Min(value = 0, message = "供水人口不能为负数")
    private Integer supplyPopulation;

    /**
     * 联系电话
     */
    @Size(max = 50, message = "联系电话长度不能超过50个字符")
    private String contactPhone;

    /**
     * 建站年月
     */
    private LocalDate establishmentDate;
}
