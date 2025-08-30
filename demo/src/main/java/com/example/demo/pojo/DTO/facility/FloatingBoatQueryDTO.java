package com.example.demo.pojo.DTO.facility;

import com.example.demo.pojo.DTO.common.BaseQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 浮船查询DTO
 * 用于浮船分页查询的请求参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "FloatingBoatQueryDTO", description = "浮船查询DTO")
public class FloatingBoatQueryDTO extends BaseQueryDTO {

    @Schema(name = "pumpingStatus", description = "抽水状态", example = "running")
    private String pumpingStatus;
}
