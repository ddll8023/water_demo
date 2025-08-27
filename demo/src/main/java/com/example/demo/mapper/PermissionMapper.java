package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.pojo.entity.system.Permission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 权限Mapper接口
 * 提供权限相关的数据访问操作
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 查询所有可用权限（用于构建权限树）
     */
    List<Permission> selectAllAvailable();
}
