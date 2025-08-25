package com.example.demo.pojo.dto.system;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;

/**
 * 岗位更新DTO
 * 用于更新岗位信息的请求
 * 岗位是纯业务概念，不参与权限控制
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PositionUpdateDTO {

    /**
     * 岗位名称
     */
    @Size(max = 100, message = "岗位名称长度不能超过100个字符")
    private String name;

    /**
     * 岗位描述
     */
    private String description;

    /**
     * 岗位职责
     */
    private String responsibilities;

    /**
     * 岗位级别
     */
    private String level;
}
