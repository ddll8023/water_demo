package com.example.demo.mapper;

import com.example.demo.pojo.entity.system.Permission;
import com.example.demo.pojo.entity.system.Role;
import com.example.demo.pojo.entity.system.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户Mapper接口
 * 提供用户相关的数据访问操作
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户（包含角色信息）
     * 用于登录认证时获取用户基本信息和角色
     */
    User selectByUsernameWithRole(@Param("username") String username);

    /**
     * 根据用户ID查询用户权限列表
     * 用于构建用户的权限信息
     * 权限获取路径：用户 → 角色 → 权限（标准RBAC模型）
     */
    List<Permission> selectPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 根据用户名查询用户（简单查询，仅用于验证用户存在性）
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 检查用户名是否已存在
     */
    int countByUsername(@Param("username") String username);

    /**
     * 根据用户ID查询用户详细信息（包含角色信息）
     */
    User selectUserDetailById(@Param("userId") Long userId);

    /**
     * 分页查询用户列表（包含关联信息）
     * 用于用户列表页面显示完整信息
     * 支持PageHelper分页插件和多字段查询
     * 
     * 排序默认使用创建时间降序
     */
    List<User> selectUserPageWithDetails(
            @Param("keyword") String keyword,
            @Param("username") String username,
            @Param("roleId") Long roleId,
            @Param("isActive") Boolean isActive,
            @Param("sort") String sort);
            
    /**
     * 插入用户记录
     */
    int insertUser(User user);
    
    /**
     * 根据ID查询用户
     */
    User selectById(@Param("id") Long id);
    
    /**
     * 根据ID更新用户
     */
    int updateById(User user);

    /**
     * 获取用户的角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> selectUserRoles(@Param("userId") Long userId);
    
    /**
     * 删除用户的所有角色关联
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteUserRoles(@Param("userId") Long userId);
    
    /**
     * 为用户添加角色关联
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 影响行数
     */
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
    
    /**
     * 统计系统中超级管理员的数量
     * @return 超级管理员数量
     */
    int countSuperAdmins();
}
