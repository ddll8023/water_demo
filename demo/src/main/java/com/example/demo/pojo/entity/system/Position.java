package com.example.demo.pojo.entity.system;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 岗位表实体类
 * 对应数据库表: positions
 */
@Data
@Schema(name = "Position", description = "岗位信息表")
public class Position implements Serializable {

    @Schema(name = "id", description = "岗位ID")
    private Long id;

    @Schema(name = "name", description = "岗位名称")
    private String name;

    @Schema(name = "description", description = "岗位描述")
    private String description;

    @Schema(name = "responsibilities", description = "岗位职责")
    private String responsibilities;

    @Schema(name = "level", description = "岗位级别")
    private String level;

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
