package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.pojo.entity.system.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限Mapper接口
 * 提供权限相关的数据访问操作
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据权限代码查询权限
     */
    Permission selectByCode(@Param("code") String code);

    /**
     * 检查权限代码是否已存在
     */
    int countByCode(@Param("code") String code);

    /**
     * 查询所有菜单权限（用于构建菜单树）
     */
    List<Permission> selectAllMenus();

    /**
     * 根据父权限ID查询子权限
     */
    List<Permission> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询所有可用权限（用于角色分配）
     */
    List<Permission> selectAllAvailable();

    /**
     * 根据权限类型查询权限
     */
    List<Permission> selectByType(@Param("type") String type);

    /**
     * 查询根权限（顶级菜单）
     */
    List<Permission> selectRootPermissions();

    /**
     * 检查权限编码是否已存在（排除指定ID）
     */
    int countByCodeExcluding(@Param("code") String code, @Param("excludeId") Long excludeId);
    
    /**
     * 带过滤条件查询权限列表（用于PageHelper分页）
     * 
     * @param keyword 关键字搜索（名称、代码、描述）
     * @param type 权限类型
     * @return 权限列表
     */
    List<Permission> selectPermissionListWithFilter(@Param("keyword") String keyword, @Param("type") String type);
}
