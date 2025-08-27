package com.example.demo.pojo.DTO.system;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Size;

/**
 * 岗位更新DTO
 * 用于更新岗位信息的请求
 * 岗位是纯业务概念，不参与权限控制
 */
@Data
@Schema(description = "岗位更新DTO")
public class PositionUpdateDTO {

    @Size(max = 100, message = "岗位名称长度不能超过100个字符")
    @Schema(description = "岗位名称", example = "项目经理")
    private String name;

    @Size(max = 500, message = "岗位描述长度不能超过500个字符")
    @Schema(description = "岗位描述", example = "负责项目的整体规划和管理")
    private String description;

    @Size(max = 1000, message = "岗位职责长度不能超过1000个字符")
    @Schema(description = "岗位职责", example = "1. 制定项目计划；2. 协调团队工作；3. 监控项目进度")
    private String responsibilities;

    @Size(max = 50, message = "岗位级别长度不能超过50个字符")
    @Schema(description = "岗位级别", example = "中级")
    private String level;
}
