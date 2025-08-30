package com.example.demo.pojo.DTO.facility;

import com.example.demo.pojo.DTO.common.BaseQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 泵站查询DTO
 * 用于泵站分页查询的请求参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "PumpingStationQueryDTO", description = "泵站查询DTO")
public class PumpingStationQueryDTO extends BaseQueryDTO {
    // 泵站特有的查询字段可以在此添加
    // 通用的分页、排序、搜索字段已由BaseQueryDTO提供
}
