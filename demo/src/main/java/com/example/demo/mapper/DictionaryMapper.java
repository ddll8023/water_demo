package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.pojo.entity.system.DictData;
import com.example.demo.pojo.entity.system.DictType;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import com.example.demo.pojo.DTO.system.DictTypeQueryDTO;

/**
 * 数据字典Mapper接口
 * 整合了字典类型和字典数据的数据访问操作
 */
@Mapper
public interface DictionaryMapper extends BaseMapper<Object> {

    // =================== 字典类型相关操作 ===================
    
    /**
     * 根据类型编码查询字典类型
     */
    DictType selectDictTypeByCode(@Param("typeCode") String typeCode);

    /**
     * 检查类型编码是否存在（排除指定ID）
     */
    int countDictTypeByCodeExcluding(@Param("typeCode") String typeCode, @Param("excludeId") Long excludeId);
            
    /**
     * 查询字典类型列表（包含字典数据统计）
     * 使用PageHelper分页插件
     */
    Page<DictType> selectDictTypeList(DictTypeQueryDTO queryDTO);

    /**
     * 根据ID查询字典类型详情（包含字典数据统计）
     */
    DictType selectDictTypeById(@Param("id") Long id);
    
    /**
     * 更新字典类型
     */
    int updateDictType(DictType dictType);
    
    /**
     * 插入字典类型
     */
    int insertDictType(DictType dictType);

    // =================== 字典数据相关操作 ===================

    /**
     * 根据类型编码查询字典数据
     */
    List<DictData> selectDictDataByTypeCode(@Param("typeCode") String typeCode);

    /**
     * 根据类型ID查询字典数据
     */
    List<DictData> selectDictDataByTypeId(@Param("typeId") Long typeId);

    /**
     * 检查同一类型下键值是否存在（排除指定ID）
     */
    int countDictDataByTypeIdAndValueExcluding(@Param("typeId") Long typeId,
            @Param("dataValue") String dataValue,
            @Param("excludeId") Long excludeId,
            @Param("includeDeleted") boolean includeDeleted);

    /**
     * 根据类型ID统计字典数据数量
     */
    int countDictDataByTypeId(@Param("typeId") Long typeId);

    /**
     * 根据ID查询字典数据详情（包含类型信息）
     */
    DictData selectDictDataById(@Param("id") Long id);
    
    /**
     * 根据类型ID和数据值查询字典数据
     */
    DictData selectDictDataByTypeIdAndValue(@Param("typeId") Long typeId, @Param("dataValue") String dataValue);
    
    /**
     * 插入字典数据
     */
    int insertDictData(DictData dictData);
    
    /**
     * 更新字典数据
     */
    int updateDictData(DictData dictData);
    
    /**
     * 删除字典数据
     */
    int deleteDictDataById(@Param("id") Long id);

}