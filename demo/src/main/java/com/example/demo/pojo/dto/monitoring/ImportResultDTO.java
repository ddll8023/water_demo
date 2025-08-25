package com.example.demo.pojo.dto.monitoring;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 导入结果DTO
 * 用于返回Excel数据导入的结果统计
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImportResultDTO {

    /**
     * 总处理行数
     * 例如: 1000
     */
    private Integer totalRows;

    /**
     * 成功导入行数
     * 例如: 950
     */
    private Integer successRows;

    /**
     * 导入失败行数
     * 例如: 30
     */
    private Integer errorRows;

    /**
     * 重复数据行数
     * 例如: 20
     */
    private Integer duplicateRows;

    /**
     * 错误详情列表
     * 包含所有导入过程中发生错误的详细信息
     */
    private List<ImportErrorDTO> errors;

    /**
     * 导入错误详情DTO
     * 用于记录导入过程中的具体错误信息
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImportErrorDTO {

        /**
         * Excel行号
         * 例如: 5
         */
        private Integer rowNumber;

        /**
         * 站码
         * 例如: 00000003
         */
        private String stationCode;

        /**
         * 错误信息
         * 例如: 站码不存在
         */
        private String error;
    }
}
