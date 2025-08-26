package com.example.demo.pojo.DTO.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 字典数据响应DTO
 * 用于返回字典数据信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DictDataResponseDTO {

    /**
     * 字典数据ID
     */
    private Long id;

    /**
     * 关联字典类型ID
     */
    private Long typeId;

    /**
     * 字典标签（显示值）
     */
    private String dataLabel;

    /**
     * 字典键值（实际值）
     */
    private String dataValue;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 排序值
     */
    private Integer sortOrder;

    /**
     * 是否启用
     */
    private String isActive;

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

    /**
     * 字典类型编码
     */
    private String typeCode;

    /**
     * 字典类型名称
     */
    private String typeName;
}
