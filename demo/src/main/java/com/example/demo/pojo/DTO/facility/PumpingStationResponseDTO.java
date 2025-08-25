package com.example.demo.pojo.DTO.facility;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 泵站响应DTO
 * 用于API响应，包含泵站的完整信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PumpingStationResponseDTO {

    /**
     * 泵站ID
     */
    private Long id;

    /**
     * 泵站名称
     */
    private String name;

    /**
     * 泵站编码
     */
    private String stationCode;

    /**
     * 泵站类型
     */
    private String stationType;

    /**
     * 泵站类型名称
     */
    private String stationTypeName;

    /**
     * 所属供水工程
     */
    private String waterProject;

    /**
     * 所属供水公司
     */
    private String waterCompany;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 地址
     */
    private String address;

    /**
     * 运行方式
     */
    private String operationMode;

    /**
     * 运行方式名称
     */
    private String operationModeName;

    /**
     * 机组数量（台）
     */
    private Integer unitCount;

    /**
     * 设计规模（m³/天）
     */
    private BigDecimal designScale;

    /**
     * 装机容量（kW）
     */
    private BigDecimal installedCapacity;

    /**
     * 扬程（m）
     */
    private BigDecimal liftHead;

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
