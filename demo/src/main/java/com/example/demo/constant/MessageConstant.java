package com.example.demo.constant;
/**
 * 信息提示常量类
 */
public class MessageConstant {
    public static final String ACCOUNT_NOT_EXIST = "用户不存在";
    public static final String PASSWORD_ERROR = "密码错误";
    public static final String USER_DISABLED = "用户已被禁用";
    public static final String TOKEN_INVALID = "Token无效";
    
    // 部门相关常量
    public static final String DEPARTMENT_NAME_AVAILABLE = "部门名称可用";
    public static final String DEPARTMENT_NAME_DUPLICATE_IN_SAME_LEVEL = "同级部门中已存在相同名称";
    public static final String DEPARTMENT_NOT_EXIST = "部门不存在";
    public static final String DEPARTMENT_NAME_DUPLICATE = "同级部门下已存在相同名称的部门";
    public static final String PARENT_DEPARTMENT_NOT_EXIST = "父部门不存在";
    public static final String DEPARTMENT_SELF_REFERENCE = "不能将部门设置为自己的父部门";
    public static final String DEPARTMENT_CIRCULAR_REFERENCE = "不能将部门设置为其子部门的子部门";
    public static final String DEPARTMENT_HAS_CHILDREN = "该部门下还有子部门，无法删除";
    public static final String DEPARTMENT_HAS_USERS = "该部门下还有用户，无法删除";

    // 字典相关常量
    public static final String DICT_TYPE_NOT_EXIST = "字典类型不存在";
    public static final String DICT_DATA_NOT_EXIST = "字典数据不存在";
    public static final String DICT_TYPE_CODE_DUPLICATE = "字典类型编码已存在";
    public static final String DICT_DATA_VALUE_DUPLICATE = "同一字典类型下的键值已存在";
    public static final String DICT_QUERY_PARAM_INVALID = "查询参数无效";
    public static final String DICT_TYPE_ID_NULL = "字典类型ID不能为空";
    public static final String DICT_TYPE_CODE_NULL = "字典类型编码不能为空";
    public static final String DICT_TYPE_CREATE_FAILED = "字典类型创建失败";
    public static final String DICT_TYPE_CREATE_PARAM_INVALID = "字典类型创建参数无效";
    public static final String DICT_TYPE_UPDATE_FAILED = "字典类型更新失败";
    public static final String DICT_TYPE_UPDATE_PARAM_INVALID = "字典类型更新参数无效";
    public static final String DICT_TYPE_DELETE_FAILED = "字典类型删除失败";
    public static final String DICT_TYPE_HAS_DATA = "该字典类型下还有字典数据，无法删除";
    
    // 字典数据查询相关常量
    public static final String DICT_DATA_TYPE_CODE_NULL = "字典类型编码不能为空";
    public static final String DICT_DATA_TYPE_ID_NULL = "字典类型ID不能为空";
    
    // 字典数据操作相关常量
    public static final String DICT_DATA_CREATE_FAILED = "字典数据创建失败";
    public static final String DICT_DATA_CREATE_PARAM_INVALID = "字典数据创建参数无效";
    public static final String DICT_DATA_UPDATE_FAILED = "字典数据更新失败";
    public static final String DICT_DATA_DELETE_FAILED = "字典数据删除失败";

    // 权限相关常量
    public static final String PERMISSION_QUERY_FAILED = "权限查询失败";

    // 岗位相关常量
    public static final String POSITION_NOT_EXIST = "岗位不存在";
    public static final String POSITION_NAME_DUPLICATE = "岗位名称已存在";
    public static final String POSITION_HAS_PERSONNEL = "该岗位下还有人员，无法删除";
    public static final String POSITION_NAME_EMPTY = "岗位名称不能为空";

    // 角色相关常量
    public static final String ROLE_NOT_EXIST = "角色不存在";
    public static final String ROLE_NAME_DUPLICATE = "角色名称已存在";
    public static final String ROLE_NAME_AVAILABLE = "角色名称可用";
    public static final String ROLE_NAME_EMPTY = "角色名称不能为空";
    public static final String ROLE_HAS_USERS = "该角色下还有用户，无法删除";

}
