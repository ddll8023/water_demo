package com.example.demo.service.base;

import com.example.demo.common.PageResult;

import java.util.List;

/**
 * 设施服务基础接口
 * 定义了所有设施服务必须实现的通用方法
 * 使用泛型支持不同的实体类型和DTO类型
 * 
 * @param <Entity> 实体类型
 * @param <ResponseDTO> 响应DTO类型
 * @param <QueryDTO> 查询DTO类型
 * @param <CreateDTO> 创建DTO类型
 * @param <UpdateDTO> 更新DTO类型
 * @param <VO> 视图对象类型
 */
public interface FacilityService<Entity, ResponseDTO, QueryDTO, CreateDTO, UpdateDTO, VO> {

    /**
     * 分页查询
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<VO> queryPage(QueryDTO queryDTO);

    /**
     * 根据ID查询
     * @param id 设施ID
     * @return 设施详情
     */
    ResponseDTO queryById(Long id);

    /**
     * 创建设施
     * @param createDTO 创建数据
     * @return 创建的设施信息
     */
    ResponseDTO create(CreateDTO createDTO);

    /**
     * 更新设施
     * @param updateDTO 更新数据
     * @return 更新后的设施信息
     */
    ResponseDTO update(UpdateDTO updateDTO);

    /**
     * 删除设施
     * @param id 设施ID
     */
    void delete(Long id);

    /**
     * 查询可用设施列表
     * @return 可用设施列表
     */
    List<VO> queryAvailable();

    /**
     * 统计设施总数
     * @return 设施总数
     */
    long countTotal();
} 