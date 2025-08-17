package com.example.demo.mapper;

import com.example.demo.entity.system.Department;
import com.example.demo.entity.system.Personnel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 管理信息Mapper接口
 * 提供管理信息服务相关的数据访问操作
 */
@Mapper
public interface ManagementInfoMapper {

    /**
     * 根据用户ID查询人员ID（用于绑定巡检记录inspector_id）
     */
    Long selectPersonnelIdByUserId(@Param("userId") Long userId);

    /**
     * 获取部门树形结构
     * 返回完整的部门层级结构，用于展示组织架构图
     */
    List<Department> selectDepartmentTree();
    
    /**
     * 根据ID查询部门详情（包含父部门名称）
     * 用于管理信息服务展示部门详细信息
     */
    Department selectDepartmentWithDetails(@Param("id") Long id);
    
    /**
     * 获取子部门列表
     * 用于管理信息服务展示部门层级结构
     */
    List<Department> selectChildDepartments(@Param("parentId") Long parentId);
    
    /**
     * 更新部门信息
     * 
     * @param department 部门信息
     * @return 影响的行数
     */
    int updateDepartment(Department department);
    
    /**
     * 分页查询人员信息
     * @param offset 偏移量
     * @param limit 查询数量限制
     * @param name 姓名关键词
     * @param departmentId 部门ID
     * @param positionId 岗位ID
     * @param sort 排序条件
     * @return 人员列表
     */
    List<Personnel> selectPersonnelPage(@Param("offset") int offset,
                                       @Param("limit") int limit,
                                       @Param("name") String name,
                                       @Param("departmentId") Long departmentId,
                                       @Param("positionId") Long positionId,
                                       @Param("sort") String sort);
            
    /**
     * 查询符合条件的人员记录总数
     * @param name 姓名关键词
     * @param departmentId 部门ID
     * @param positionId 岗位ID
     * @return 记录总数
     */
    int countPersonnel(@Param("name") String name,
                      @Param("departmentId") Long departmentId,
                      @Param("positionId") Long positionId);
            
    /**
     * 插入人员记录
     * 
     * @param personnel 人员信息
     * @return 影响的行数
     */
    int insertPersonnel(Personnel personnel);
    
    /**
     * 更新人员记录
     * 
     * @param personnel 人员信息
     * @return 影响的行数
     */
    int updatePersonnel(Personnel personnel);
    
    /**
     * 根据ID查询人员信息
     * 
     * @param id 人员ID
     * @return 人员信息
     */
    Personnel selectPersonnelById(@Param("id") Long id);
    
    /**
     * 统计部门下的人员数量
     * 
     * @param departmentId 部门ID
     * @return 人员数量
     */
    int countPersonnelByDepartmentId(@Param("departmentId") Long departmentId);
} 