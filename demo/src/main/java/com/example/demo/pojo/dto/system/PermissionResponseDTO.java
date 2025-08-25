package com.example.demo.pojo.dto.system;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 权限响应DTO
 * 用于API响应，包含权限的完整信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PermissionResponseDTO {

    /**
     * 权限 ID
     * 系统中权限的唯一标识符
     */
    private Long id;

    /**
     * 权限的友好名称 
     * 例如: "创建用户", "删除角色" 等
     */
    private String name;

    /**
     * 权限的唯一编码
     * 使用冒号分隔的格式表示权限的层级和操作
     * 例如: "user:create", "role:delete" 等
     */
    private String code;

    /**
     * 权限类型
     * 可能的值: "菜单", "按钮", "API", "模块" 等
     */
    private String type;

    /**
     * 权限详细描述
     * 对该权限功能的详细说明
     */
    private String description;

    /**
     * 权限对应的URL路径
     * 当权限类型为API时，表示该权限对应的接口URL
     */
    private String url;

    /**
     * HTTP方法
     * 当权限类型为API时，表示该权限对应的HTTP请求方法
     * 例如: "GET", "POST", "PUT", "DELETE" 等
     */
    private String method;

    /**
     * 父权限ID
     * 用于构建权限的层级关系，顶级权限的父ID为null
     */
    private Long parentId;

    /**
     * 排序顺序
     * 用于在同级权限中的显示顺序
     */
    private Integer sortOrder;

    /**
     * 图标
     * 权限在UI界面上显示的图标，通常使用字体图标的名称
     */
    private String icon;

    /**
     * 子权限列表
     * 用于构建权限树形结构，包含当前权限的所有子权限
     */
    private List<PermissionResponseDTO> children;
}
