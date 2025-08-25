package com.example.demo.pojo.dto.facility;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 消毒药材响应DTO
 * 用于返回消毒药材详细信息
 */
@Data
public class DisinfectionMaterialResponseDTO {

    /**
     * 药材ID
     */
    private Long id;

    /**
     * 药材名称，例如：次氯酸钠
     */
    private String name;

    /**
     * 所属水厂ID
     */
    private Long waterPlantId;

    /**
     * 所属水厂名称，例如：襄阳第一水厂
     */
    private String waterPlantName;

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
