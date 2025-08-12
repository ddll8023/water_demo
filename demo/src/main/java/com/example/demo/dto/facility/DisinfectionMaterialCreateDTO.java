package com.example.demo.dto.facility;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 消毒药材创建请求DTO
 * 用于创建新的消毒药材信息
 */
@Data
public class DisinfectionMaterialCreateDTO {

    /**
     * 名称，例如：次氯酸钠
     */
    @NotBlank(message = "名称不能为空")
    private String name;

    /**
     * 所属水厂ID
     */
    @NotNull(message = "所属水厂ID不能为空")
    private Long waterPlantId;

    /**
     * 存储条件，例如：阴凉干燥处
     */
    private String storageCondition;

    /**
     * 生产日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate productionDate;

    /**
     * 有效期，例如：12个月
     */
    private String validityPeriod;

    /**
     * 库存数量
     */
    private BigDecimal quantity;

    /**
     * 单位，例如：kg
     */
    private String unit;

    /**
     * 备注，例如：10%浓度液氯，水厂消毒用
     */
    private String remark;
}
