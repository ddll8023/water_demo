package com.example.demo.pojo.DTO.facility;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 水库创建DTO
 * 用于创建新水库的请求
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ReservoirCreateDTO {

    /**
     * 水库编码
     */
    @Size(max = 100, message = "水库编码长度不能超过100个字符")
    private String reservoirCode;

    /**
     * 水库名称
     */
    @NotBlank(message = "水库名称不能为空")
    @Size(max = 255, message = "水库名称长度不能超过255个字符")
    private String name;

    /**
     * 所属水利工程
     */
    @Size(max = 255, message = "所属水利工程长度不能超过255个字符")
    private String waterProject;

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
     * 水库所在位置
     */
    @Size(max = 255, message = "水库位置长度不能超过255个字符")
    private String location;

    /**
     * 水库注册登记号
     */
    @Size(max = 100, message = "注册登记号长度不能超过100个字符")
    private String registrationNo;

    /**
     * 所属行政区划（关联regions.code）
     */
    @Size(max = 100, message = "行政区划代码长度不能超过100个字符")
    private String adminRegionCode;

    /**
     * 工程等级（关联dict_data.data_value，type_code=engineering_grade）
     */
    @Size(max = 100, message = "工程等级长度不能超过100个字符")
    private String engineeringGrade;

    /**
     * 工程规模（关联dict_data.data_value，type_code=engineering_scale）
     */
    @Size(max = 100, message = "工程规模长度不能超过100个字符")
    private String engineeringScale;

    /**
     * 总库容（万m³）
     */
    @DecimalMin(value = "0.0", message = "总库容不能为负数")
    private BigDecimal totalCapacity;

    /**
     * 调节库容（万m³）
     */
    @DecimalMin(value = "0.0", message = "调节库容不能为负数")
    private BigDecimal regulatingCapacity;

    /**
     * 死库容（万m³）
     */
    @DecimalMin(value = "0.0", message = "死库容不能为负数")
    private BigDecimal deadCapacity;

    /**
     * 建库年月
     */
    private LocalDate establishmentDate;
}
