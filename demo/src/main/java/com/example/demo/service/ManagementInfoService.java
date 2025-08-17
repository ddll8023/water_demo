package com.example.demo.service;

import com.example.demo.dto.common.PageResponseDTO;
import com.example.demo.dto.management.DepartmentInfoResponseDTO;
import com.example.demo.dto.management.DepartmentInfoUpdateDTO;
import com.example.demo.dto.management.PersonnelInfoCreateDTO;
import com.example.demo.dto.management.PersonnelInfoUpdateDTO;
import com.example.demo.dto.management.PersonnelInfoResponseDTO;
import com.example.demo.entity.system.Department;
import com.example.demo.entity.system.Personnel;
import com.example.demo.mapper.ManagementInfoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理信息服务
 * 专门处理业务人员和部门信息管理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ManagementInfoService {

    private final ManagementInfoMapper managementInfoMapper;

    /**
     * 分页查询人员信息
     * @param page 页码
     * @param size 每页大小  
     * @param name 姓名过滤
     * @param departmentId 部门ID过滤
     * @param departmentName 部门名称过滤（暂未使用）
     * @param positionId 岗位ID过滤
     * @return 分页结果
     */
    public PageResponseDTO<PersonnelInfoResponseDTO> getPersonnelList(
        int page, int size, String name, Long departmentId, String departmentName, Long positionId
    ) {
        try {
            // 计算分页参数
            int offset = (page - 1) * size;
            int limit = size;

            // 查询总记录数
            int total = managementInfoMapper.countPersonnel(name, departmentId, positionId);

            // 查询数据列表
            List<Personnel> personnelList = managementInfoMapper.selectPersonnelPage(
                offset, limit, name, departmentId, positionId, "created_at DESC");

            // 转换为PersonnelInfoResponseDTO
            List<PersonnelInfoResponseDTO> dtoList = personnelList.stream()
                    .map(this::convertToPersonnelInfoDTO)
                    .collect(Collectors.toList());

            return new PageResponseDTO<>(
                    dtoList,
                    total,
                    page,
                    size
            );
        } catch (Exception e) {
            log.error("查询人员列表失败: {}", e.getMessage(), e);
            throw new RuntimeException("查询人员列表失败: " + e.getMessage());
        }
    }

    /**
     * 创建人员信息
     */
    @Transactional
    public PersonnelInfoResponseDTO createPersonnel(PersonnelInfoCreateDTO createDTO) {
        try {
            // 创建Personnel实体并设置属性
            Personnel personnel = new Personnel();
            personnel.setFullName(createDTO.getFullName());
            personnel.setPhone(createDTO.getPhone());
            personnel.setEmail(createDTO.getEmail());
            personnel.setPositionId(createDTO.getPositionId());
            personnel.setDepartmentId(createDTO.getDepartmentId());
            personnel.setEmployeeNo(createDTO.getEmployeeNo());
            personnel.setHireDate(createDTO.getHireDate());
            personnel.setWorkResponsibilities(createDTO.getWorkResponsibilities());

            // 插入数据库
            managementInfoMapper.insertPersonnel(personnel);
            
            // 查询完整信息（包含关联数据）
            Personnel inserted = managementInfoMapper.selectPersonnelById(personnel.getId());
            
            return convertToPersonnelInfoDTO(inserted);
        } catch (Exception e) {
            log.error("创建人员失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建人员失败: " + e.getMessage());
        }
    }

    /**
     * 更新人员信息
     */
    @Transactional
    public PersonnelInfoResponseDTO updatePersonnel(Long id, PersonnelInfoUpdateDTO updateDTO) {
        try {
            // 先查询现有记录
            Personnel personnel = managementInfoMapper.selectPersonnelById(id);
            if (personnel == null) {
                throw new RuntimeException("人员不存在，ID: " + id);
            }
            
            // 更新属性
            personnel.setFullName(updateDTO.getFullName());
            personnel.setPhone(updateDTO.getPhone());
            personnel.setPositionId(updateDTO.getPositionId());
            personnel.setDepartmentId(updateDTO.getDepartmentId());
            personnel.setHireDate(updateDTO.getHireDate());
            personnel.setWorkResponsibilities(updateDTO.getWorkResponsibilities());
            
            // 执行更新
            managementInfoMapper.updatePersonnel(personnel);
            
            // 查询更新后的完整信息（包含关联数据）
            Personnel updated = managementInfoMapper.selectPersonnelById(id);
            
            return convertToPersonnelInfoDTO(updated);
        } catch (Exception e) {
            log.error("更新人员失败: {}", e.getMessage(), e);
            throw new RuntimeException("更新人员失败: " + e.getMessage());
        }
    }

    /**
     * 删除人员信息
     * 
     * @param id 人员ID
     * @throws RuntimeException 如果删除失败或人员不存在
     */
    @Transactional
    public void deletePersonnel(Long id) {
        try {
            // 先检查人员是否存在
            Personnel personnel = managementInfoMapper.selectPersonnelById(id);
            if (personnel == null) {
                throw new RuntimeException("人员不存在，ID: " + id);
            }
            
            // 执行软删除
            personnel.setDeletedAt(LocalDateTime.now());
            managementInfoMapper.updatePersonnel(personnel);
            
            log.info("删除人员成功，人员ID: {}, 姓名: {}", personnel.getId(), personnel.getFullName());
        } catch (Exception e) {
            log.error("删除人员失败: {}", e.getMessage(), e);
            throw new RuntimeException("删除人员失败: " + e.getMessage());
        }
    }

    /**
     * 更新部门信息
     * 
     * @param id 部门ID
     * @param updateDTO 部门更新DTO
     * @return 更新后的部门信息
     */
    @Transactional
    public DepartmentInfoResponseDTO updateDepartment(Long id, DepartmentInfoUpdateDTO updateDTO) {
        try {
            // 先查询现有记录
            Department department = managementInfoMapper.selectDepartmentWithDetails(id);
            if (department == null) {
                throw new RuntimeException("部门不存在，ID: " + id);
            }
            
            // 更新属性
            department.setName(updateDTO.getName());
            
            // 设置父部门ID - 包括null值，这表示将部门设置为顶级部门
            department.setParentId(updateDTO.getParentId());
            
            // 设置部门职责
            department.setDuty(updateDTO.getDuty());

            // 更新创建时间 - 只有当传入的创建时间不为空时才更新
            if (updateDTO.getCreatedAt() != null) {
                department.setCreatedAt(updateDTO.getCreatedAt());
            }
            
            // 设置联系信息
            department.setContact(updateDTO.getContact());

            // 执行更新
            managementInfoMapper.updateDepartment(department);
            
            // 查询更新后的完整信息（包含关联数据）
            Department updated = managementInfoMapper.selectDepartmentWithDetails(id);
            
            return convertToDepartmentInfoDTO(updated);
        } catch (Exception e) {
            log.error("更新部门失败: {}", e.getMessage(), e);
            throw new RuntimeException("更新部门失败: " + e.getMessage());
        }
    }

    /**
     * 转换Personnel实体为PersonnelInfoResponseDTO
     */
    private PersonnelInfoResponseDTO convertToPersonnelInfoDTO(Personnel personnel) {
        PersonnelInfoResponseDTO dto = new PersonnelInfoResponseDTO();
        
        // 复制基本属性
        BeanUtils.copyProperties(personnel, dto);

        return dto;
    }

    /**
     * 转换Department实体为DepartmentInfoResponseDTO
     */
    private DepartmentInfoResponseDTO convertToDepartmentInfoDTO(Department department) {
        DepartmentInfoResponseDTO dto = new DepartmentInfoResponseDTO();
        BeanUtils.copyProperties(department, dto);

        // 设置上级部门名称
        dto.setParentDepartment(department.getParentName());

        // 设置部门描述和联系信息
        dto.setDescription(department.getDuty());
        dto.setContactInfo(department.getContact());
        dto.setResponsibilities(department.getDuty());

        // 设置人员数量
        dto.setPersonnelCount(managementInfoMapper.countPersonnelByDepartmentId(department.getId()));

        return dto;
    }
    
    /**
     * 获取部门树形结构
     * 返回完整的部门层级结构，用于展示组织架构图
     * 
     * @return 部门树形结构，每个节点包含其子部门
     */
    public List<DepartmentInfoResponseDTO> getDepartmentTree() {
        List<Department> departments = managementInfoMapper.selectDepartmentTree();
        List<DepartmentInfoResponseDTO> dtoList = departments.stream()
                .map(this::convertToDepartmentInfoDTO)
                .collect(Collectors.toList());
        
        return buildTree(dtoList);
    }
    
    /**
     * 构建部门树形结构
     */
    private List<DepartmentInfoResponseDTO> buildTree(List<DepartmentInfoResponseDTO> departments) {
        // 分离根节点和子节点
        List<DepartmentInfoResponseDTO> rootNodes = new ArrayList<>();
        List<DepartmentInfoResponseDTO> childNodes = new ArrayList<>();
        
        for (DepartmentInfoResponseDTO dept : departments) {
            if (dept.getParentId() == null) {
                rootNodes.add(dept);
            } else {
                childNodes.add(dept);
            }
        }
        
        // 构建树形结构
        for (DepartmentInfoResponseDTO root : rootNodes) {
            buildChildren(root, childNodes);
        }
        
        return rootNodes;
    }
    
    /**
     * 递归构建子部门结构
     */
    private void buildChildren(DepartmentInfoResponseDTO parent, List<DepartmentInfoResponseDTO> allChildren) {
        List<DepartmentInfoResponseDTO> children = new ArrayList<>();
        for (DepartmentInfoResponseDTO child : allChildren) {
            if (parent.getId().equals(child.getParentId())) {
                children.add(child);
                buildChildren(child, allChildren);
            }
        }
        parent.setChildren(children);
    }

}
