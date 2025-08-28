// 工程信息服务Tab配置API接口
// 管理工程信息服务页面的Tab显示配置

import request from "./request";

/**
 * 分页查询Tab配置列表
 * @param {Object} params 查询参数
 * @param {number} params.page 页码
 * @param {number} params.size 每页大小
 * @param {string} params.tabKey Tab标识键
 * @param {string} params.tabName Tab名称（模糊查询）
 * @param {string} params.isVisible 是否显示
 * @returns {Promise} Tab配置列表响应
 */
export function getEngineeringServiceTabList(params) {
	return request({
		url: "/engineering-service-tabs",
		method: "get",
		params,
	});
}

/**
 * 获取所有可见的Tab配置列表
 * 用于工程信息服务页面动态加载Tab配置
 * @returns {Promise} 可见的Tab配置列表
 */
export function getVisibleEngineeringServiceTabs() {
	return request({
		url: "/engineering-service-tabs/visible",
		method: "get",
	});
}

/**
 * 创建Tab配置
 * @param {Object} data Tab配置数据
 * @param {string} data.tabKey Tab标识键
 * @param {string} data.tabName Tab显示名称
 * @param {string} data.tabIcon Tab图标类名
 * @param {number} data.sortOrder 排序顺序
 * @param {string} data.isVisible 是否显示
 * @returns {Promise} 创建结果
 */
export function createEngineeringServiceTab(data) {
	return request({
		url: "/engineering-service-tabs",
		method: "post",
		data,
	});
}

/**
 * 更新Tab配置信息
 * @param {number} id Tab配置ID
 * @param {Object} data 更新数据
 * @returns {Promise} 更新结果
 */
export function updateEngineeringServiceTab(id, data) {
	return request({
		url: `/engineering-service-tabs/${id}`,
		method: "put",
		data,
	});
}

/**
 * 删除Tab配置
 * @param {number} id Tab配置ID
 * @returns {Promise} 删除结果
 */
export function deleteEngineeringServiceTab(id) {
	return request({
		url: `/engineering-service-tabs/${id}`,
		method: "delete",
	});
}

/**
 * 批量更新Tab配置排序顺序
 * @param {Array} data Tab配置更新列表
 * @param {number} data[].id Tab配置ID
 * @param {number} data[].sortOrder 新的排序顺序
 * @returns {Promise} 批量更新结果
 */
export function batchUpdateTabSortOrder(data) {
	return request({
		url: "/engineering-service-tabs/batch-sort",
		method: "put",
		data,
	});
}

/**
 * 更新Tab配置显示状态
 * @param {string} tabKey Tab标识键
 * @param {string} isVisible 是否显示（1-显示，0-隐藏）
 * @returns {Promise} 更新结果
 */
export function updateTabVisibility(tabKey, isVisible) {
	return request({
		url: `/engineering-service-tabs/${tabKey}/visibility`,
		method: "put",
		params: { isVisible },
	});
}
