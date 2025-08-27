package com.example.demo.mapper;

import com.example.demo.pojo.DTO.system.UserQueryDTO;
import com.example.demo.pojo.VO.UserVO;
import com.example.demo.pojo.entity.system.Permission;
import com.example.demo.pojo.entity.system.Role;
import com.example.demo.pojo.entity.system.User;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 * 提供用户相关的数据访问操作
 */
@Mapper
public interface UserMapper {

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
     * 根据用户ID查询用户的角色信息（单个角色）
     * 基于users表的role_id字段进行关联查询
     * 
     * @param userId 用户ID
     * @return 用户的角色信息，如果没有角色则返回null
     */
    Role selectUserRole(@Param("userId") Long userId);

    /**
     * 分页查询用户列表（包含关联信息）
     * 用于用户列表页面显示完整信息
     * 支持PageHelper分页插件和多字段查询
     * <p>
     * 排序默认使用创建时间降序
     */
    Page<User> selectUserPageWithDetails(
            UserQueryDTO userQueryDTO);

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
     * 根据用户ID查询角色名称
     * 用于在VO中显示用户的角色名称
     */
    String selectRoleNameByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询用户角色列表
     * 用于在VO中显示用户的完整角色信息
     */
    List<Role> selectRolesByUserId(@Param("userId") Long userId);
}
