package com.example.demo.service;

import com.example.demo.dto.system.DepartmentCreateDTO;
import com.example.demo.dto.system.DepartmentResponseDTO;
import com.example.demo.dto.system.DepartmentUpdateDTO;
import com.example.demo.dto.common.PageResponseDTO;
import com.example.demo.dto.common.ValidationResponseDTO;
import com.example.demo.entity.system.Department;
import com.example.demo.mapper.DepartmentMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
     * 分页查询部门列表（支持状态筛选）
     */
    public PageResponseDTO<DepartmentResponseDTO> getDepartmentPage(int page, int size, String keyword, Boolean isActive) {
        // 参数验证
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        // 使用PageHelper开始分页
        PageHelper.startPage(page, size);
        
        // 查询数据，不需要传入分页参数
        List<Department> departments = departmentMapper.selectDepartmentListWithPersonnelCount(keyword, isActive);
        
        // 获取分页信息
        PageInfo<Department> pageInfo = new PageInfo<>(departments);
        
        // 转换为DTO
        List<DepartmentResponseDTO> records = departments.stream()
                .map(department -> {
                    DepartmentResponseDTO dto = new DepartmentResponseDTO();
                    BeanUtils.copyProperties(department, dto);
                    dto.setChildren(new ArrayList<>());
                    return dto;
                })
                .collect(Collectors.toList());

        return new PageResponseDTO<>(records, (int) pageInfo.getTotal(), page, size);
    }

    /**
     * 创建部门
     */
    @Transactional
    public DepartmentResponseDTO createDepartment(DepartmentCreateDTO createDTO) {
        // 检查部门名称是否已存在（同级部门下）
        if (departmentMapper.countByNameAndParent(createDTO.getName(), createDTO.getParentId(), null) > 0) {
            throw new RuntimeException("同级部门下已存在相同名称的部门");
        }

        // 如果有父部门，检查父部门是否存在
        if (createDTO.getParentId() != null) {
            Department parentDepartment = departmentMapper.selectDepartmentById(createDTO.getParentId());
            if (parentDepartment == null) {
                throw new RuntimeException("父部门不存在");
            }
        }

        Department department = new Department();
        BeanUtils.copyProperties(createDTO, department);
        department.setCreatedAt(LocalDateTime.now());
        department.setUpdatedAt(LocalDateTime.now());
        department.setIsActive("1");

        departmentMapper.insertDepartment(department);
        log.debug("创建部门成功，部门ID: {}, 部门名称: {}", department.getId(), department.getName());
        
        DepartmentResponseDTO dto = new DepartmentResponseDTO();
        BeanUtils.copyProperties(department, dto);
        dto.setChildren(new ArrayList<>());
        return dto;
    }

    /**
     * 更新部门信息
     */
    @Transactional
    public DepartmentResponseDTO updateDepartment(Long id, DepartmentUpdateDTO updateDTO) {
        Department department = departmentMapper.selectDepartmentById(id);
        if (department == null) {
            throw new RuntimeException("部门不存在");
        }

        // 检查部门名称是否已存在（同级部门下，排除当前部门）
        Long parentId = updateDTO.getParentId() != null ? updateDTO.getParentId() : department.getParentId();
        String name = updateDTO.getName() != null ? updateDTO.getName() : department.getName();
        
        if (departmentMapper.countByNameAndParent(name, parentId, id) > 0) {
            throw new RuntimeException("同级部门下已存在相同名称的部门");
        }

        // 如果要修改父部门，检查父部门是否存在，并防止循环引用
        if (updateDTO.getParentId() != null) {
            if (updateDTO.getParentId().equals(id)) {
                throw new RuntimeException("不能将部门设置为自己的父部门");
            }
            
            Department parentDepartment = departmentMapper.selectDepartmentById(updateDTO.getParentId());
            if (parentDepartment == null) {
                throw new RuntimeException("父部门不存在");
            }
            
            // 检查是否会形成循环引用 - 内联实现
            Long checkParentId = updateDTO.getParentId();
            if (checkParentId != null) {
                Department parent = departmentMapper.selectDepartmentById(checkParentId);
                while (parent != null) {
                    if (parent.getId().equals(id)) {
                throw new RuntimeException("不能将部门设置为其子部门的子部门");
                    }
                    parent = parent.getParentId() != null ? departmentMapper.selectDepartmentById(parent.getParentId()) : null;
                }
            }
        }

        BeanUtils.copyProperties(updateDTO, department);
        department.setUpdatedAt(LocalDateTime.now());

        departmentMapper.updateDepartment(department);
        log.debug("更新部门成功，部门ID: {}, 部门名称: {}", department.getId(), department.getName());
        
        DepartmentResponseDTO dto = new DepartmentResponseDTO();
        BeanUtils.copyProperties(department, dto);
        dto.setChildren(new ArrayList<>());
        return dto;
    }

    /**
     * 删除部门
     */
    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentMapper.selectDepartmentById(id);
        if (department == null) {
            throw new RuntimeException("部门不存在");
        }

        // 检查是否有子部门
        if (departmentMapper.countChildDepartments(id) > 0) {
            throw new RuntimeException("该部门下还有子部门，无法删除");
        }

        // 检查是否有用户关联此部门
        if (departmentMapper.countUsersByDepartmentId(id)>0) {
            throw new RuntimeException("该部门下还有用户，无法删除");
        }

        // 软删除
        department.setDeletedAt(LocalDateTime.now());
        departmentMapper.updateDepartment(department);
        log.debug("删除部门成功，部门ID: {}, 部门名称: {}", department.getId(), department.getName());
    }

    /**
     * 检查部门名称是否可用
     */
    public ValidationResponseDTO checkNameAvailable(String name, Long parentId, Long excludeId) {
        int count = departmentMapper.countByNameAndParent(name, parentId, excludeId);
        boolean available = count == 0;

        return ValidationResponseDTO.builder()
            .available(available)
            .message(available ? "部门名称可用" : "同级部门中已存在相同名称")
            .build();
    }

}
