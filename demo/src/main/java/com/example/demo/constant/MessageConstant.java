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

}
