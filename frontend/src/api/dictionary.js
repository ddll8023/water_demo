// 鄂北地区水资源管理系统 - 字典管理API接口
// 基于阶段一后端API的字典管理相关接口

import request from "./request";

// ================================
// 字典类型相关接口
// ================================

/**
 * 分页查询字典类型列表
 * @param {Object} params 查询参数
 * @param {number} params.page 页码
 * @param {number} params.size 每页大小
 * @param {string} params.keyword 关键词搜索（可选）
 * @param {string} params.typeCode 字典类型编码（可选）
 * @param {string} params.typeName 字典类型名称（可选）
 * @param {boolean} params.isActive 是否启用（可选）
 * @param {string} params.sort 排序字段（可选）
 * @returns {Promise} 字典类型列表响应
 */
export function getDictTypeList(params) {
	return request({
		url: "/system/dict/types",
		method: "get",
		params,
	});
}

/**
 * 查询字典类型详情
 * @param {number} id 字典类型ID
 * @returns {Promise} 字典类型详情
 */
export function getDictTypeDetail(id) {
	return request({
		url: `/system/dict/types/${id}`,
		method: "get",
	});
}

/**
 * 根据类型编码查询字典类型
 * @param {string} typeCode 字典类型编码
 * @returns {Promise} 字典类型详情
 */
export function getDictTypeByCode(typeCode) {
	return request({
		url: `/system/dict/types/code/${typeCode}`,
		method: "get",
	});
}

/**
 * 创建字典类型
 * @param {Object} data 字典类型数据
 * @param {string} data.typeCode 字典类型编码
 * @param {string} data.typeName 字典类型名称
 * @param {string} data.description 描述信息
 * @param {number} data.sortOrder 排序值
 * @param {boolean} data.isActive 是否启用
 * @returns {Promise} 创建结果
 */
export function createDictType(data) {
	return request({
		url: "/system/dict/types",
		method: "post",
		data,
	});
}

/**
 * 更新字典类型
 * @param {number} id 字典类型ID
 * @param {Object} data 字典类型数据
 * @returns {Promise} 更新结果
 */
export function updateDictType(id, data) {
	return request({
		url: `/system/dict/types/${id}`,
		method: "put",
		data: { ...data, id },
	});
}

/**
 * 删除字典类型
 * @param {number} id 字典类型ID
 * @returns {Promise} 删除结果
 */
export function deleteDictType(id) {
	return request({
		url: `/system/dict/types/${id}`,
		method: "delete",
	});
}

/**
 * 检查类型编码是否存在
 * @param {string} typeCode 字典类型编码
 * @param {number} excludeId 排除的字典类型ID（可选）
 * @returns {Promise} 检查结果
 */
export function checkTypeCodeExists(typeCode, excludeId = null) {
	return request({
		url: "/system/dict/types/check-code",
		method: "get",
		params: { typeCode, excludeId },
	});
}

// ================================
// 字典数据相关接口
// ================================

/**
 * 根据类型编码查询字典数据
 * @param {string} typeCode 字典类型编码
 * @returns {Promise} 字典数据列表
 */
export function getDictDataByTypeCode(typeCode) {
	return request({
		url: `/system/dict/data/type/${typeCode}`,
		method: "get",
	});
}

/**
 * 根据类型ID查询字典数据
 * @param {number} typeId 字典类型ID
 * @returns {Promise} 字典数据列表
 */
export function getDictDataByTypeId(typeId) {
	return request({
		url: `/system/dict/data/type-id/${typeId}`,
		method: "get",
	});
}

/**
 * 创建字典数据
 * @param {Object} data 字典数据
 * @param {number} data.typeId 字典类型ID
 * @param {string} data.dataLabel 字典标签
 * @param {string} data.dataValue 字典键值
 * @param {string} data.description 描述信息
 * @param {number} data.sortOrder 排序值
 * @param {boolean} data.isActive 是否启用
 * @returns {Promise} 创建结果
 */
export function createDictData(data) {
	return request({
		url: "/system/dict/data",
		method: "post",
		data,
	});
}

/**
 * 更新字典数据
 * @param {number} id 字典数据ID
 * @param {Object} data 字典数据
 * @returns {Promise} 更新结果
 */
export function updateDictData(id, data) {
	return request({
		url: `/system/dict/data/${id}`,
		method: "put",
		data: { ...data, id },
	});
}

/**
 * 删除字典数据
 * @param {number} id 字典数据ID
 * @returns {Promise} 删除结果
 */
export function deleteDictData(id) {
	return request({
		url: `/system/dict/data/${id}`,
		method: "delete",
	});
}
