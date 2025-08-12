package com.example.demo.dto.facility;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 泵站创建DTO
 * 用于创建新泵站的请求
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PumpingStationCreateDTO {

    /**
     * 泵站名称
     */
    @NotBlank(message = "泵站名称不能为空")
    private String name;

    /**
     * 泵站编码
     */
    private String stationCode;

    /**
     * 泵站类型（关联dict_data.data_value，type_code=facility_type）
     */
    private String stationType;

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
    @DecimalMin(value = "-180.0", message = "经度必须在-180到180之间")
    private BigDecimal longitude;

    /**
     * 纬度
     */
    @DecimalMin(value = "-90.0", message = "纬度必须在-90到90之间")
    private BigDecimal latitude;

    /**
     * 地址
     */
    private String address;

    /**
     * 运行方式（关联dict_data.data_value，type_code=operation_mode）
     */
    private String operationMode;

    /**
     * 机组数量（台）
     */
    private Integer unitCount;

    /**
     * 设计规模（m³/天）
     */
    @DecimalMin(value = "0.0", message = "设计规模不能为负数")
    private BigDecimal designScale;

    /**
     * 装机容量（kW）
     */
    @DecimalMin(value = "0.0", message = "装机容量不能为负数")
    private BigDecimal installedCapacity;

    /**
     * 扬程（m）
     */
    @DecimalMin(value = "0.0", message = "扬程不能为负数")
    private BigDecimal liftHead;

    /**
     * 建站年月
     */
    private LocalDate establishmentDate;
}
