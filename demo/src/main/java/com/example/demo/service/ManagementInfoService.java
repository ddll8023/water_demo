package com.example.demo.service;

import com.example.demo.common.PageResult;
import com.example.demo.constant.MessageConstant;
import com.example.demo.exception.management.PersonnelExistException;
import com.example.demo.exception.management.PersonnelNotExistException;
import com.example.demo.exception.system.DepartmentNotExistException;
import com.example.demo.mapper.ManagementInfoMapper;
import com.example.demo.pojo.DTO.management.DepartmentInfoUpdateDTO;
import com.example.demo.pojo.DTO.management.PersonnelInfoCreateDTO;
import com.example.demo.pojo.DTO.management.PersonnelInfoQueryDTO;
import com.example.demo.pojo.DTO.management.PersonnelInfoUpdateDTO;
import com.example.demo.pojo.VO.DepartmentTreeVO;
import com.example.demo.pojo.VO.PersonnelInfoVO;
import com.example.demo.pojo.entity.system.Department;
import com.example.demo.pojo.entity.system.Personnel;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
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
     *
     * @param personnelInfoQueryDTO 查询参数
     * @return 分页结果
     */
    public PageResult<PersonnelInfoVO> getPersonnelList(PersonnelInfoQueryDTO personnelInfoQueryDTO) {
        log.info("人员信息查询参数: {}", personnelInfoQueryDTO);

        PageHelper.startPage(personnelInfoQueryDTO.getPage(), personnelInfoQueryDTO.getSize());

        Page<Personnel> personnels = managementInfoMapper.selectPersonnelPage(personnelInfoQueryDTO);

        long total = personnels.getTotal();
        List<Personnel> records = personnels.getResult();

        // 转换为PersonnelInfoVO
        List<PersonnelInfoVO> voList = records.stream().map(this::convertToPersonnelInfoVO).collect(Collectors.toList());

        log.info("人员信息查询结果: {}", voList);
        return new PageResult<>(total, voList);
    }

    /**
     * 创建人员信息
     */
    @Transactional
    public void createPersonnel(PersonnelInfoCreateDTO personnelInfoCreateDTO) {
        log.info("人员信息创建参数: {}", personnelInfoCreateDTO);
        Personnel existPersonnel = managementInfoMapper.selectPersionnelByName(personnelInfoCreateDTO.getName());
        if (existPersonnel != null) {
            log.info("人员已存在，姓名: {}", existPersonnel.getName());
            throw new PersonnelExistException(MessageConstant.PERSONNEL_NAME_DUPLICATE);
        }
        // 创建Personnel实体并设置属性
        Personnel personnel = new Personnel();
        BeanUtils.copyProperties(personnelInfoCreateDTO, personnel);
        personnel.setCreatedAt(LocalDateTime.now());
        personnel.setUpdatedAt(LocalDateTime.now());
        // 插入数据库
        managementInfoMapper.insertPersonnel(personnel);
        log.info("人员信息创建成功:{}", personnel);
    }

    /**
     * 更新人员信息
     */
    @Transactional
    public void updatePersonnel(Long id, PersonnelInfoUpdateDTO personnelInfoUpdateDTO) {
        log.info("人员信息更新参数: {}", personnelInfoUpdateDTO);
        // 先查询现有记录
        Personnel existPersonnel = managementInfoMapper.selectPersonnelById(id);
        if (existPersonnel == null) {
            log.info("人员不存在，ID: {}", id);
            throw new PersonnelNotExistException(MessageConstant.PERSONNEL_NOT_EXIST);
        }

        // 更新属性
        Personnel newPersonnel = new Personnel();
        newPersonnel.setId(id);
        BeanUtils.copyProperties(personnelInfoUpdateDTO, newPersonnel);
        newPersonnel.setUpdatedAt(LocalDateTime.now());
        // 执行更新
        managementInfoMapper.updatePersonnel(newPersonnel);

        log.info("人员信息更新成功");
    }

    /**
     * 删除人员信息
     *
     * @param id 人员ID
     * @throws RuntimeException 如果删除失败或人员不存在
     */
    @Transactional
    public void deletePersonnel(Long id) {
        log.info("删除人员，ID: {}", id);
        Personnel personnel = managementInfoMapper.selectPersonnelById(id);
        if (personnel == null) {
            log.info("人员不存在，ID: {}", id);
            throw new PersonnelNotExistException(MessageConstant.PERSONNEL_NOT_EXIST);
        }

        // 执行软删除
        personnel.setDeletedAt(LocalDateTime.now());
        managementInfoMapper.updatePersonnel(personnel);

        log.info("删除人员成功");

    }

    /**
     * 批量删除人员信息
     *
     * @param ids 待删除的人员ID列表
     * @throws RuntimeException 如果删除失败或人员不存在
     */
    @Transactional
    public void batchDeletePersonnel(List<Long> ids) {
        log.info("批量删除人员，ID列表: {}", ids);

        // 参数验证
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("删除ID列表不能为空");
        }

        // 批量删除处理
        for (Long id : ids) {
            deletePersonnel(id);
        }

        log.info("批量删除人员成功，共删除{}个人员", ids.size());
    }

    /**
     * 更新部门信息
     *
     * @param id                      部门ID
     * @param departmentInfoUpdateDTO 部门更新DTO
     * @return 更新后的部门信息
     */
    @Transactional
    public void updateDepartment(Long id, DepartmentInfoUpdateDTO departmentInfoUpdateDTO) {
        log.info("部门信息更新参数: {}", departmentInfoUpdateDTO);
        // 先查询现有记录
        Department department = managementInfoMapper.selectDepartmentWithDetails(id);
        if (department == null) {
            log.info("部门不存在，ID: {}", id);
            throw new DepartmentNotExistException(MessageConstant.DEPARTMENT_NOT_EXIST);
        }
        Department newDepartment = new Department();
        newDepartment.setId(id);
        BeanUtils.copyProperties(departmentInfoUpdateDTO, newDepartment);
        newDepartment.setUpdatedAt(LocalDateTime.now());

        // 执行更新
        managementInfoMapper.updateDepartment(newDepartment);

        log.info("部门信息更新成功");

    }

    /**
     * 转换Department实体为DepartmentTreeVO
     */
    private DepartmentTreeVO convertToDepartmentTreeVO(Department department) {
        DepartmentTreeVO vo = new DepartmentTreeVO();
        BeanUtils.copyProperties(department, vo);

        // 查询并设置上级部门名称
        String parentName = managementInfoMapper.selectParentNameByDepartmentId(department.getId());
        vo.setParentDepartment(parentName);

        // 设置部门描述和联系信息
        vo.setDescription(department.getDuty());
        vo.setContactInfo(department.getContact());
        vo.setResponsibilities(department.getDuty());

        // 设置人员数量
        vo.setPersonnelCount(managementInfoMapper.countPersonnelByDepartmentId(department.getId()));

        return vo;
    }

    /**
     * 获取部门树形结构
     * 返回完整的部门层级结构，用于展示组织架构图
     *
     * @return 部门树形结构，每个节点包含其子部门
     */
    public List<DepartmentTreeVO> getDepartmentTree() {
        List<Department> departments = managementInfoMapper.selectDepartmentTree();
        List<DepartmentTreeVO> voList = departments.stream().map(this::convertToDepartmentTreeVO).collect(Collectors.toList());

        return buildTree(voList);
    }

    /**
     * 构建部门树形结构
     */
    private List<DepartmentTreeVO> buildTree(List<DepartmentTreeVO> departments) {
        // 分离根节点和子节点
        List<DepartmentTreeVO> rootNodes = new ArrayList<>();
        List<DepartmentTreeVO> childNodes = new ArrayList<>();

        for (DepartmentTreeVO dept : departments) {
            if (dept.getParentId() == null) {
                rootNodes.add(dept);
            } else {
                childNodes.add(dept);
            }
        }

        // 构建树形结构
        for (DepartmentTreeVO root : rootNodes) {
            buildChildren(root, childNodes);
        }

        return rootNodes;
    }

    /**
     * 递归构建子部门结构
     */
    private void buildChildren(DepartmentTreeVO parent, List<DepartmentTreeVO> allChildren) {
        List<DepartmentTreeVO> children = new ArrayList<>();
        for (DepartmentTreeVO child : allChildren) {
            if (parent.getId().equals(child.getParentId())) {
                children.add(child);
                buildChildren(child, allChildren);
            }
        }
        parent.setChildren(children);
    }

    /**
     * 转换Personnel实体为PersonnelInfoVO
     */
    private PersonnelInfoVO convertToPersonnelInfoVO(Personnel personnel) {
        PersonnelInfoVO vo = new PersonnelInfoVO();

        // 复制基本属性
        BeanUtils.copyProperties(personnel, vo);
        // 设置部门名称
        vo.setDepartmentName(managementInfoMapper.selectDpartmentNameById(personnel.getDepartmentId()));
        // 设置岗位名称
        vo.setPositionName(managementInfoMapper.selectPositionNameById(personnel.getPositionId()));

        return vo;
    }

}
