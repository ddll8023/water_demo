package com.example.demo.dto.facility;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 泵站更新DTO
 * 用于更新泵站信息的请求
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PumpingStationUpdateDTO {

    /**
     * 泵站ID
     */
    @NotNull(message = "泵站ID不能为空")
    private Long id;

    /**
     * 泵站名称
     */
    @NotBlank(message = "泵站名称不能为空")
    @Size(max = 255, message = "泵站名称长度不能超过255个字符")
    private String name;

    /**
     * 泵站编码
     */
    @Size(max = 100, message = "泵站编码长度不能超过100个字符")
    private String stationCode;

    /**
     * 泵站类型（通过工程服务API获取类型选项）
     */
    @Size(max = 100, message = "泵站类型长度不能超过100个字符")
    private String stationType;

    /**
     * 所属供水工程
     */
    @Size(max = 255, message = "所属供水工程长度不能超过255个字符")
    private String waterProject;

    /**
     * 所属供水公司
     */
    @Size(max = 255, message = "所属供水公司长度不能超过255个字符")
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
    @Size(max = 255, message = "地址长度不能超过255个字符")
    private String address;

    /**
     * 运行方式（关联dict_data.data_value，type_code=operation_mode）
     */
    @Size(max = 100, message = "运行方式长度不能超过100个字符")
    private String operationMode;

    /**
     * 机组数量（台）
     */
    @Min(value = 0, message = "机组数量不能为负数")
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
