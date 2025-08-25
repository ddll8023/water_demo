package com.example.demo.pojo.DTO.facility;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 消毒药材更新请求DTO
 * 用于更新现有消毒药材信息
 */
@Data
public class DisinfectionMaterialUpdateDTO {

    /**
     * 药材ID，用于标识需要更新的药材记录
     */
    @NotNull(message = "药材ID不能为空")
    private Long id;

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
