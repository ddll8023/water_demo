package com.example.demo.pojo.entity.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 字典类型表实体类
 * 对应数据库表: dict_types
 */
@Data
@Schema(name = "DictType", description = "字典类型实体")
public class DictType implements Serializable {

    @Schema(name = "id", description = "字典类型ID")
    private Long id;

    @Schema(name = "typeCode", description = "字典类型编码")
    private String typeCode;

    @Schema(name = "typeName", description = "字典类型名称")
    private String typeName;

    @Schema(name = "description", description = "描述信息")
    private String description;

    @Schema(name = "sortOrder", description = "排序值")
    private Integer sortOrder = 0;

    @Schema(name = "isActive", description = "是否启用")
    private String isActive = "1";

    @Schema(name = "createdAt", description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt", description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(name = "deletedAt", description = "删除时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;
}
