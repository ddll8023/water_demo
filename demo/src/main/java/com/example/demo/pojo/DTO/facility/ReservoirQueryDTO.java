package com.example.demo.pojo.DTO.facility;

import com.example.demo.pojo.DTO.common.BaseQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 水库查询DTO
 * 用于水库分页查询的请求参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "ReservoirQueryDTO", description = "水库查询DTO")
public class ReservoirQueryDTO extends BaseQueryDTO {
    // 水库特有的查询字段可以在此添加
    // 通用的分页、排序、搜索字段已由BaseQueryDTO提供
}
