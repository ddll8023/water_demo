package com.example.demo.pojo.DTO.facility;

import com.example.demo.pojo.DTO.common.BaseQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管道查询DTO
 * 用于管道分页查询的请求参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "PipelineQueryDTO", description = "管道查询DTO")
public class PipelineQueryDTO extends BaseQueryDTO {

    @Schema(name = "pipelineType", description = "管道类型", example = "water_supply")
    private String pipelineType;
}
