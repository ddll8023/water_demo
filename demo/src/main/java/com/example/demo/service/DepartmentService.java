package com.example.demo.service;

import com.example.demo.pojo.DTO.system.DepartmentCreateDTO;
import com.example.demo.pojo.DTO.system.DepartmentNameCheckDTO;
import com.example.demo.pojo.DTO.system.DepartmentQueryDTO;
import com.example.demo.pojo.DTO.system.DepartmentUpdateDTO;
import com.example.demo.pojo.VO.DepartmentVO;
import com.example.demo.common.PageResult;
import com.example.demo.pojo.entity.system.Department;
import com.example.demo.mapper.DepartmentMapper;
import com.example.demo.constant.MessageConstant;
import com.example.demo.constant.CommonConstant;
import com.example.demo.exception.system.DepartmentNotExistException;
import com.example.demo.exception.system.DepartmentNameDuplicateException;
import com.example.demo.exception.system.ParentDepartmentNotExistException;
import com.example.demo.exception.system.CircularReferenceException;
import com.example.demo.exception.system.DepartmentHasChildrenException;
import com.example.demo.exception.system.DepartmentHasUsersException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 部门服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentMapper departmentMapper;

    /**
     * 分页查询部门列表
     */
    public PageResult<DepartmentVO> getDepartmentPage(DepartmentQueryDTO departmentQueryDTO) {
        log.info("开始查询部门分页数据，查询条件: {}", departmentQueryDTO);
        
        // 使用PageHelper开始分页
        PageHelper.startPage(departmentQueryDTO.getPage(), departmentQueryDTO.getSize());
        
        // 查询基础部门数据
        Page<Department> departments = departmentMapper.selectDepartmentPageByNameAndActive(
                departmentQueryDTO.getName(),
                departmentQueryDTO.getIsActive()
        );
        
        // 获取分页信息
        long total = departments.getTotal();
        List<Department> records = departments.getResult();
        
        // 转换为VO并补充关联信息
        List<DepartmentVO> departmentVOList = records.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        log.info("查询完成，返回 {} 条记录，总计 {} 条", departmentVOList.size(), total);
        return new PageResult<>(total, departmentVOList);
    }

    /**
     * 创建部门
     *
     * @param departmentCreateDTO 部门创建请求数据传输对象
     * @throws DepartmentNameDuplicateException 当同级部门下已存在相同名称时抛出
     * @throws ParentDepartmentNotExistException 当父部门不存在时抛出
     */
    @Transactional
    public void createDepartment(DepartmentCreateDTO departmentCreateDTO) {
        log.info("开始创建部门，部门名称: {}, 父部门ID: {}", departmentCreateDTO.getName(), departmentCreateDTO.getParentId());

        // 检查部门名称是否已存在（同级部门下）
        if (departmentMapper.countByNameAndParent(departmentCreateDTO.getName(), departmentCreateDTO.getParentId(), null) > 0) {
            throw new DepartmentNameDuplicateException(MessageConstant.DEPARTMENT_NAME_DUPLICATE);
        }

        // 如果有父部门，检查父部门是否存在
        if (departmentCreateDTO.getParentId() != null) {
            Department parentDepartment = departmentMapper.selectDepartmentById(departmentCreateDTO.getParentId());
            if (parentDepartment == null) {
                throw new ParentDepartmentNotExistException(MessageConstant.PARENT_DEPARTMENT_NOT_EXIST);
            }
            log.debug("父部门验证通过: {}", parentDepartment.getName());
        }

        log.debug("部门创建前置验证通过，开始创建部门实体");
        Department department = new Department();
        BeanUtils.copyProperties(departmentCreateDTO, department);
        department.setCreatedAt(LocalDateTime.now());
        department.setUpdatedAt(LocalDateTime.now());
        department.setIsActive(CommonConstant.ACTIVE_STATUS_ENABLED);

        departmentMapper.insertDepartment(department);
        log.info("创建部门成功，部门ID: {}, 部门名称: {}", department.getId(), department.getName());
    }

    /**
     * 更新部门信息
     *
     * @param id 部门ID
     * @param departmentUpdateDTO 部门更新请求数据传输对象
     * @throws DepartmentNotExistException 当部门不存在时抛出
     * @throws DepartmentNameDuplicateException 当同级部门下已存在相同名称时抛出
     * @throws ParentDepartmentNotExistException 当父部门不存在时抛出
     * @throws CircularReferenceException 当存在循环引用时抛出
     */
    @Transactional
    public void updateDepartment(Long id, DepartmentUpdateDTO departmentUpdateDTO) {
        log.info("开始更新部门，部门ID: {}, 更新信息: {}", id, departmentUpdateDTO);
        
        Department department = departmentMapper.selectDepartmentById(id);
        if (department == null) {
            throw new DepartmentNotExistException(MessageConstant.DEPARTMENT_NOT_EXIST);
        }

        // 检查部门名称是否已存在（同级部门下，排除当前部门）
        Long parentId = departmentUpdateDTO.getParentId() != null ? departmentUpdateDTO.getParentId() : department.getParentId();
        String name = departmentUpdateDTO.getName() != null ? departmentUpdateDTO.getName() : department.getName();
        
        if (departmentMapper.countByNameAndParent(name, parentId, id) > 0) {
            throw new DepartmentNameDuplicateException(MessageConstant.DEPARTMENT_NAME_DUPLICATE);
        }

        // 如果要修改父部门，检查父部门是否存在，并防止循环引用
        if (departmentUpdateDTO.getParentId() != null) {
            if (departmentUpdateDTO.getParentId().equals(id)) {
                throw new CircularReferenceException(MessageConstant.DEPARTMENT_SELF_REFERENCE);
            }
            
            Department parentDepartment = departmentMapper.selectDepartmentById(departmentUpdateDTO.getParentId());
            if (parentDepartment == null) {
                throw new ParentDepartmentNotExistException(MessageConstant.PARENT_DEPARTMENT_NOT_EXIST);
            }
            
            // 检查是否会形成循环引用 - 内联实现
            Long checkParentId = departmentUpdateDTO.getParentId();
            if (checkParentId != null) {
                Department parent = departmentMapper.selectDepartmentById(checkParentId);
                while (parent != null) {
                    if (parent.getId().equals(id)) {
                        throw new CircularReferenceException(MessageConstant.DEPARTMENT_CIRCULAR_REFERENCE);
                    }
                    parent = parent.getParentId() != null ? departmentMapper.selectDepartmentById(parent.getParentId()) : null;
                }
            }
        }

        BeanUtils.copyProperties(departmentUpdateDTO, department);
        department.setUpdatedAt(LocalDateTime.now());

        departmentMapper.updateDepartment(department);
        log.debug("更新部门成功，部门ID: {}, 部门名称: {}", department.getId(), department.getName());
    }

    /**
     * 删除部门
     *
     * @param id 部门ID
     * @throws DepartmentNotExistException 当部门不存在时抛出
     * @throws DepartmentHasChildrenException 当部门有子部门时抛出
     * @throws DepartmentHasUsersException 当部门有用户时抛出
     */
    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentMapper.selectDepartmentById(id);
        if (department == null) {
            throw new DepartmentNotExistException(MessageConstant.DEPARTMENT_NOT_EXIST);
        }

        // 检查是否有子部门
        if (departmentMapper.countChildDepartments(id) > 0) {
            throw new DepartmentHasChildrenException(MessageConstant.DEPARTMENT_HAS_CHILDREN);
        }

        // 检查是否有用户关联此部门
        if (departmentMapper.countUsersByDepartmentId(id)>0) {
            throw new DepartmentHasUsersException(MessageConstant.DEPARTMENT_HAS_USERS);
        }

        // 软删除
        department.setDeletedAt(LocalDateTime.now());
        departmentMapper.updateDepartment(department);
        log.debug("删除部门成功，部门ID: {}, 部门名称: {}", department.getId(), department.getName());
    }

    /**
     * 检查部门名称是否可用
     */
    public Map<String, Object> checkNameAvailable(DepartmentNameCheckDTO departmentNameCheckDTO) {
        int count = departmentMapper.countByNameAndParent(departmentNameCheckDTO.getName(), departmentNameCheckDTO.getParentId(), departmentNameCheckDTO.getExcludeId());
        boolean available = count == 0;

        Map<String, Object> result = new HashMap<>();
        result.put("available", available);
        result.put("message", available ? MessageConstant.DEPARTMENT_NAME_AVAILABLE : MessageConstant.DEPARTMENT_NAME_DUPLICATE_IN_SAME_LEVEL);
        return result;
    }
    /**
     * 将Department实体转换为DepartmentVO，并补充关联信息
     */
    private DepartmentVO convertToVO(Department department) {
        DepartmentVO departmentVO = new DepartmentVO();
        BeanUtils.copyProperties(department, departmentVO);

        // 查询并设置关联信息
        departmentVO.setPersonnelCount(departmentMapper.selectPersonnelCountByDepartmentId(department.getId()));
        departmentVO.setParentName(departmentMapper.selectParentNameByDepartmentId(department.getId()));
        departmentVO.setRegionName(departmentMapper.selectRegionNameByDepartmentId(department.getId()));
        departmentVO.setChildren(new ArrayList<>());

        return departmentVO;
    }
}
