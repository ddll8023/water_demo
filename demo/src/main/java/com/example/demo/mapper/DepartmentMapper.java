package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.system.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门Mapper接口
 * 提供部门相关的数据访问操作
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {

    /**
     * 查询部门列表（带人员数量统计）- 用于PageHelper分页
     */
    List<Department> selectDepartmentListWithPersonnelCount(@Param("keyword") String keyword, @Param("isActive") Boolean isActive);


    /**
     * 检查部门名称是否已存在（同级部门下）
     */
    int countByNameAndParent(@Param("name") String name, @Param("parentId") Long parentId, @Param("excludeId") Long excludeId);

    /**
     * 检查是否有用户关联此部门
     */
    int countUsersByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * 检查是否有子部门
     */
    int countChildDepartments(@Param("departmentId") Long departmentId);
    
    /**
     * 根据ID查询部门详情
     */
    Department selectDepartmentById(@Param("id") Long id);
    
    /**
     * 更新部门信息
     */
    int updateDepartment(Department department);
    
    /**
     * 插入部门信息
     */
    int insertDepartment(Department department);
}
