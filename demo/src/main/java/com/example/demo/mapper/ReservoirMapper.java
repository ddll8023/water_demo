package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.dto.facility.ReservoirResponseDTO;
import com.example.demo.entity.facility.Reservoir;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 水库Mapper接口
 * 提供水库相关的数据访问操作
 */
@Mapper
public interface ReservoirMapper extends BaseMapper<Reservoir> {

    /**
     * 分页查询水库列表（包含关联信息）
     * 已废弃，请使用 selectReservoirList
     */
    @Deprecated
    IPage<ReservoirResponseDTO> selectReservoirPage(IPage<ReservoirResponseDTO> page,
                                                    @Param("keyword") String keyword,
                                                    @Param("reservoirLevel") String reservoirLevel);

    /**
     * 查询水库列表（包含关联信息）
     * 配合 PageHelper 使用
     */
    List<ReservoirResponseDTO> selectReservoirList(@Param("keyword") String keyword,
                                               @Param("reservoirLevel") String reservoirLevel);

    /**
     * 根据ID查询水库详情（包含关联信息）
     */
    ReservoirResponseDTO selectReservoirById(@Param("id") Long id);

    /**
     * 检查水库编码是否存在
     */
    boolean existsByReservoirCode(@Param("reservoirCode") String reservoirCode, @Param("excludeId") Long excludeId);

    /**
     * 检查水库名称是否存在
     */
    boolean existsByName(@Param("name") String name, @Param("excludeId") Long excludeId);

    /**
     * 检查注册登记号是否存在
     */
    boolean existsByRegistrationNo(@Param("registrationNo") String registrationNo, @Param("excludeId") Long excludeId);

    /**
     * 获取所有可用水库（用于下拉选择）
     */
    List<Reservoir> selectAvailableReservoirs();

    /**
     * 统计水库总数
     */
    long countTotal();

    /**
     * 根据工程等级统计数量
     */
    long countByEngineeringGrade(@Param("engineeringGrade") String engineeringGrade);
}
