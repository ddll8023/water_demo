package com.example.demo.pojo.DTO.facility;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 水库响应DTO
 * 用于API响应，包含水库的完整信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ReservoirResponseDTO {

    /**
     * 水库ID
     */
    private Long id;

    /**
     * 水库编码
     */
    private String reservoirCode;

    /**
     * 水库名称
     */
    private String name;

    /**
     * 所属水利工程
     */
    private String waterProject;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 水库所在位置
     */
    private String location;

    /**
     * 水库注册登记号
     */
    private String registrationNo;

    /**
     * 所属行政区划（关联regions.code）
     */
    private String adminRegionCode;

    /**
     * 工程等级（关联dict_data.data_value，type_code=engineering_grade）
     */
    private String engineeringGrade;

    /**
     * 工程等级名称（非数据库字段）
     */
    private String engineeringGradeName;

    /**
     * 工程规模（关联dict_data.data_value，type_code=engineering_scale）
     */
    private String engineeringScale;

    /**
     * 工程规模名称（非数据库字段）
     */
    private String engineeringScaleName;

    /**
     * 总库容（万m³）
     */
    private BigDecimal totalCapacity;

    /**
     * 调节库容（万m³）
     */
    private BigDecimal regulatingCapacity;

    /**
     * 死库容（万m³）
     */
    private BigDecimal deadCapacity;

    /**
     * 建库年月
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
