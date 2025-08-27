package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.pojo.entity.system.Permission;
import com.example.demo.pojo.entity.system.Role;
import com.example.demo.pojo.entity.system.RolePermission;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色Mapper接口
 * 提供角色相关的数据访问操作及角色权限关联操作
 */
@Mapper
public interface RoleMapper extends BaseMapper<RolePermission> {

    /**
     * 根据角色ID查询权限列表
     * 用于获取角色拥有的所有权限
     */
    List<Permission> selectPermissionsByRoleId(@Param("roleId") Long roleId);

    /**
     * 检查角色名称是否已存在
     */
    int countByName(@Param("name") String name, @Param("excludeId") Long excludeId);

    /**
     * 查询所有可用角色（用于下拉选择）
     */
    List<Role> selectAllAvailable();

    /**
     * 检查角色是否存在且处于活跃状态
     *
     * @param roleId 角色ID
     * @return 如果角色有效返回true，否则返回false
     */
    boolean isRoleActive(@Param("roleId") Long roleId);

    /**
     * 根据条件查询角色列表，支持PageHelper分页
     *
     * @param name 角色名称，用于模糊匹配角色名称和描述
     * @return 角色列表
     */
    Page<Role> selectRoleList(@Param("name") String name);

    /**
     * 根据ID查询角色
     *
     * @param id 角色ID
     * @return 角色实体
     */
    Role selectRoleById(@Param("id") Long id);

    /**
     * 插入新角色
     *
     * @param role 角色实体
     * @return 受影响的行数
     */
    int insertRole(Role role);

    /**
     * 更新角色
     *
     * @param role 角色实体
     * @return 受影响的行数
     */
    int updateRole(Role role);

    /**
     * 批量插入角色权限关联
     * 用于为角色分配多个权限
     */
    int rolePermissionInsertBatch(@Param("list") List<RolePermission> list);

    /**
     * 根据角色ID删除权限关联（物理删除）
     * 用于清空角色的所有权限
     */
    int rolePermissionDeleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 统计使用指定角色的用户数量
     * 用于删除角色前检查是否有用户关联此角色
     *
     * @param roleId 角色ID
     * @return 使用该角色的用户数量
     */
    int countUsersByRoleId(@Param("roleId") Long roleId);
}
