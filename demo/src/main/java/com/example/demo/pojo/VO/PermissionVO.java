package com.example.demo.pojo.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 权限视图对象
 * 用于权限树查询的返回，包含权限的完整展示信息
 */
@Data
@Schema(name = "PermissionVO", description = "权限视图对象")
public class PermissionVO implements Serializable {

    /**
     * 权限 ID
     * 系统中权限的唯一标识符
     */
    @Schema(name = "id", description = "权限ID")
    private Long id;

    /**
     * 权限的友好名称 
     * 例如: "创建用户", "删除角色" 等
     */
    @Schema(name = "name", description = "权限名称")
    private String name;

    /**
     * 权限的唯一编码
     * 使用冒号分隔的格式表示权限的层级和操作
     * 例如: "user:create", "role:delete" 等
     */
    @Schema(name = "code", description = "权限编码")
    private String code;

    /**
     * 权限类型
     * 可能的值: "菜单", "按钮", "API", "模块" 等
     */
    @Schema(name = "type", description = "权限类型")
    private String type;

    /**
     * 权限详细描述
     * 对该权限功能的详细说明
     */
    @Schema(name = "description", description = "权限描述")
    private String description;

    /**
     * 权限对应的URL路径
     * 当权限类型为API时，表示该权限对应的接口URL
     */
    @Schema(name = "url", description = "权限URL")
    private String url;

    /**
     * HTTP方法
     * 当权限类型为API时，表示该权限对应的HTTP请求方法
     * 例如: "GET", "POST", "PUT", "DELETE" 等
     */
    @Schema(name = "method", description = "HTTP方法")
    private String method;

    /**
     * 父权限ID
     * 用于构建权限的层级关系，顶级权限的父ID为null
     */
    @Schema(name = "parentId", description = "父权限ID")
    private Long parentId;

    /**
     * 排序顺序
     * 用于在同级权限中的显示顺序
     */
    @Schema(name = "sortOrder", description = "排序顺序")
    private Integer sortOrder;

    /**
     * 图标
     * 权限在UI界面上显示的图标，通常使用字体图标的名称
     */
    @Schema(name = "icon", description = "权限图标")
    private String icon;

    /**
     * 子权限列表
     * 用于构建权限树形结构，包含当前权限的所有子权限
     */
    @Schema(name = "children", description = "子权限列表")
    private List<PermissionVO> children;
} 