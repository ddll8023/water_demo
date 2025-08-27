package com.example.demo.service;

import com.example.demo.mapper.PermissionMapper;
import com.example.demo.pojo.VO.PermissionVO;
import com.example.demo.pojo.entity.system.Permission;
import com.example.demo.exception.PermissionException;
import com.example.demo.constant.MessageConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限管理服务
 * 处理权限的查询，构建权限树结构
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionMapper permissionMapper;

    /**
     * 获取权限树结构
     */
    public List<PermissionVO> getPermissionTree() {
        log.info("开始构建权限树结构");
        
        // 获取所有权限
        List<Permission> allPermissions = permissionMapper.selectAllAvailable();
        log.info("查询到 {} 个权限记录", allPermissions.size());

        // 转换为VO
        List<PermissionVO> permissionVOs = allPermissions.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 构建树结构
        List<PermissionVO> tree = buildPermissionTree(permissionVOs);
        log.info("权限树结构构建完成，共 {} 个根权限", tree.size());
        
        return tree;
    }

    /**
     * 构建权限树结构
     */
    private List<PermissionVO> buildPermissionTree(List<PermissionVO> permissions) {
        // 按父ID分组
        Map<Long, List<PermissionVO>> parentMap = permissions.stream()
                .filter(p -> p.getParentId() != null)
                .collect(Collectors.groupingBy(PermissionVO::getParentId));

        // 设置子权限
        for (PermissionVO permission : permissions) {
            List<PermissionVO> children = parentMap.get(permission.getId());
            if (children != null) {
                permission.setChildren(children);
            } else {
                permission.setChildren(new ArrayList<>());
            }
        }

        // 返回根权限
        return permissions.stream()
                .filter(p -> p.getParentId() == null)
                .collect(Collectors.toList());
    }

    /**
     * 转换为VO
     */
    private PermissionVO convertToVO(Permission permission) {
        PermissionVO vo = new PermissionVO();
        BeanUtils.copyProperties(permission, vo);
        return vo;
    }
}
