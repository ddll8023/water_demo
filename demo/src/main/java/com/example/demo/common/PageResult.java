package com.example.demo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应DTO
 * 统一的分页数据返回格式，用于封装后端分页查询结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /**
     * 数据列表
     * 当前页的数据项集合
     */
    private List<T> items;

    /**
     * 总记录数
     * 满足查询条件的所有记录数量
     */
    private int total;

    /**
     * 当前页码
     * 从1开始的页码编号
     */
    private int page;

    /**
     * 每页大小
     * 每页显示的记录数量
     */
    private int size;

    /**
     * 总页数
     * 根据总记录数和每页大小计算得出
     */
    private Long totalPages;

    /**
     * 是否有上一页
     * 当前页是否为首页
     */
    private boolean hasPrevious;

    /**
     * 是否有下一页
     * 当前页是否为末页
     */
    private boolean hasNext;

    /**
     * 构造函数（自动计算分页信息）
     * 
     * @param items 数据项列表
     * @param total 总记录数
     * @param page 当前页码
     * @param size 每页大小
     */
    public PageResult(List<T> items, int total, int page, int size) {
        this.items = items;
        this.total = total;
        this.page = page;
        this.size = size;
        this.totalPages = (long) Math.ceil((double) total / size);
        this.hasPrevious = page > 1;
        this.hasNext = page < totalPages;
    }

    /**
     * 构造函数（参考代码兼容版本）
     * 用于支持 new PageResult(total, records) 的调用方式
     * 
     * @param total 总记录数
     * @param items 数据项列表
     */
    public PageResult(long total, List<T> items) {
        this.total = (int) total;
        this.items = items;
        // 对于此构造函数，分页信息设为默认值
        this.page = 1;
        this.size = items != null ? items.size() : 0;
        this.totalPages = this.size > 0 ? (long) Math.ceil((double) total / this.size) : 0L;
        this.hasPrevious = false;
        this.hasNext = this.totalPages > 1;
    }
}
