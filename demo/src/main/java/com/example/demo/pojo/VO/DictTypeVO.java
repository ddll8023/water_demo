package com.example.demo.pojo.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 字典类型视图对象
 * 用于API响应，包含字典类型的完整信息
 */
@Data
@Schema(name = "DictTypeVO", description = "字典类型视图对象")
public class DictTypeVO implements Serializable {

    @Schema(name = "id", description = "字典类型ID")
    private Long id;

    @Schema(name = "typeCode", description = "字典类型编码")
    private String typeCode;

    @Schema(name = "typeName", description = "字典类型名称")
    private String typeName;

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

    @Schema(name = "dataCount", description = "字典数据数量")
    private Integer dataCount;
} 