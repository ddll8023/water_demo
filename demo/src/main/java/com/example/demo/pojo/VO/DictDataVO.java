package com.example.demo.pojo.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 字典数据视图对象
 * 用于API响应，包含字典数据的完整信息及关联的字典类型信息
 */
@Data
@Schema(name = "DictDataVO", description = "字典数据视图对象")
public class DictDataVO implements Serializable {

    @Schema(name = "id", description = "字典数据ID")
    private Long id;

    @Schema(name = "typeId", description = "关联字典类型ID")
    private Long typeId;

    @Schema(name = "dataLabel", description = "字典标签（显示值）")
    private String dataLabel;

    @Schema(name = "dataValue", description = "字典键值（实际值）")
    private String dataValue;

    @Schema(name = "description", description = "描述信息")
    private String description;

    @Schema(name = "sortOrder", description = "排序值")
    private Integer sortOrder;

    @Schema(name = "isActive", description = "是否启用")
    private String isActive;

    @Schema(name = "createdAt", description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt", description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // ========== 关联信息字段 ==========

    @Schema(name = "typeCode", description = "字典类型编码")
    private String typeCode;

    @Schema(name = "typeName", description = "字典类型名称")
    private String typeName;
} 