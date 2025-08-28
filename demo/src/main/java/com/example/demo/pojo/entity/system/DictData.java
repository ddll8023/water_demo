package com.example.demo.pojo.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典数据表实体类
 * 对应数据库表: dict_data
 */
@Data
@Schema(name = "DictData", description = "字典数据表")
public class DictData {

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

    @Schema(name = "sortOrder", description = "排序值，数值越小优先级越高")
    private Integer sortOrder = 0;

    @Schema(name = "isActive", description = "是否启用")
    private String isActive = "1";

    @Schema(name = "createdAt", description = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt", description = "更新时间")
    private LocalDateTime updatedAt;

    @Schema(name = "deletedAt", description = "软删除标记")
    private LocalDateTime deletedAt;

}
