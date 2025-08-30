// 鄂北地区水资源管理系统 - 工程信息服务API接口
// 泵站、监测站点、管道、村庄、水库、水厂等工程设施管理

import request from "./request";

// ==================== 设施类型管理 ====================

/**
 * 获取所有设施类型枚举
 * @returns {Promise} 设施类型列表
 * @returns {Promise} 设施类型列表
 */
export function getFacilityTypes() {
	return request({
		url: "/engineering-service/facility-types",
		method: "get",
	});
}

/**
 * 获取设施类型映射
 * @returns {Promise} 设施类型映射
 */
export function getFacilityTypeMap() {
	return request({
		url: "/engineering-service/facility-type-map",
		method: "get",
	});
}

// ==================== 泵站管理 ====================

/**
 * 分页查询泵站列表
 * @param {Object} params 查询参数
 * @param {number} params.page 页码
 * @param {number} params.size 每页大小
 * @param {string} params.keyword 搜索关键词（泵站名称、编码、地址）
 * @param {string} params.stationType 泵站类型

 * @returns {Promise} 泵站列表响应
 */
export function getPumpingStationList(params) {
	return request({
		url: "/engineering-service/pumping-stations",
		method: "get",
		params,
	});
}

/**
 * 查询泵站详情
 * @param {number} id 泵站ID
 * @returns {Promise} 泵站详情
 */
export function getPumpingStationDetail(id) {
	return request({
		url: `/engineering-service/pumping-stations/${id}`,
		method: "get",
	});
}

/**
 * 创建泵站
 * @param {Object} data 泵站数据
 * @param {string} data.name 泵站名称
 * @param {string} data.stationCode 泵站编码
 * @param {string} data.stationType 泵站类型
 * @param {string} data.waterProject 所属供水工程
 * @param {string} data.waterCompany 所属供水公司
 * @param {number} data.departmentId 管理部门ID
 * @param {number} data.managerId 负责人ID
 * @param {number} data.longitude 经度
 * @param {number} data.latitude 纬度
 * @param {string} data.address 地址
 * @param {string} data.operationMode 运行方式
 * @param {number} data.unitCount 机组数量
 * @param {number} data.designScale 设计规模
 * @param {number} data.installedCapacity 装机容量
 * @param {number} data.liftHead 扬程
 * @param {string} data.establishmentDate 建站年月
 * @returns {Promise} 创建结果
 */
export function createPumpingStation(data) {
	return request({
		url: "/engineering-service/pumping-stations",
		method: "post",
		data,
	});
}

/**
 * 更新泵站信息
 * @param {number} id 泵站ID
 * @param {Object} data 更新数据
 * @returns {Promise} 更新结果
 */
export function updatePumpingStation(id, data) {
	return request({
		url: `/engineering-service/pumping-stations/${id}`,
		method: "put",
		data,
	});
}

/**
 * 删除泵站
 * @param {number} id 泵站ID
 * @returns {Promise} 删除结果
 */
export function deletePumpingStation(id) {
	return request({
		url: `/engineering-service/pumping-stations/${id}`,
		method: "delete",
	});
}

// ==================== 水厂管理 ====================

/**
 * 分页查询水厂列表
 * @param {Object} params 查询参数
 * @returns {Promise} 水厂列表响应
 */
export function getWaterPlantList(params) {
	return request({
		url: "/engineering-service/water-plants",
		method: "get",
		params,
	});
}

/**
 * 查询水厂详情
 * @param {number} id 水厂ID
 * @returns {Promise} 水厂详情
 */
export function getWaterPlantDetail(id) {
	return request({
		url: `/engineering-service/water-plants/${id}`,
		method: "get",
	});
}

/**
 * 创建水厂
 * @param {Object} data 水厂数据
 * @returns {Promise} 创建结果
 */
export function createWaterPlant(data) {
	return request({
		url: "/engineering-service/water-plants",
		method: "post",
		data,
	});
}

/**
 * 更新水厂信息
 * @param {number} id 水厂ID
 * @param {Object} data 更新数据
 * @returns {Promise} 更新结果
 */
export function updateWaterPlant(id, data) {
	return request({
		url: `/engineering-service/water-plants/${id}`,
		method: "put",
		data,
	});
}

/**
 * 删除水厂
 * @param {number} id 水厂ID
 * @returns {Promise} 删除结果
 */
export function deleteWaterPlant(id) {
	return request({
		url: `/engineering-service/water-plants/${id}`,
		method: "delete",
	});
}

/**
 * 批量删除水厂
 * @param {Array<number>} ids 水厂ID数组
 * @returns {Promise} 删除结果
 */
export function batchDeleteWaterPlants(ids) {
	return request({
		url: "/engineering-service/water-plants/batch",
		method: "delete",
		data: { ids },
	});
}

// ==================== 水库管理 ====================

/**
 * 分页查询水库列表
 * @param {Object} params 查询参数
 * @returns {Promise} 水库列表响应
 */
export function getReservoirList(params) {
	return request({
		url: "/engineering-service/reservoirs",
		method: "get",
		params,
	});
}

/**
 * 查询水库详情
 * @param {number} id 水库ID
 * @returns {Promise} 水库详情
 */
export function getReservoirDetail(id) {
	return request({
		url: `/engineering-service/reservoirs/${id}`,
		method: "get",
	});
}

/**
 * 创建水库
 * @param {Object} data 水库数据
 * @returns {Promise} 创建结果
 */
export function createReservoir(data) {
	return request({
		url: "/engineering-service/reservoirs",
		method: "post",
		data,
	});
}

/**
 * 更新水库信息
 * @param {number} id 水库ID
 * @param {Object} data 更新数据
 * @returns {Promise} 更新结果
 */
export function updateReservoir(id, data) {
	return request({
		url: `/engineering-service/reservoirs/${id}`,
		method: "put",
		data,
	});
}

/**
 * 删除水库
 * @param {number} id 水库ID
 * @returns {Promise} 删除结果
 */
export function deleteReservoir(id) {
	return request({
		url: `/engineering-service/reservoirs/${id}`,
		method: "delete",
	});
}

/**
 * 批量删除水库
 * @param {Array<number>} ids 水库ID数组
 * @returns {Promise} 删除结果
 */
export function batchDeleteReservoirs(ids) {
	return request({
		url: "/engineering-service/reservoirs/batch",
		method: "delete",
		data: { ids },
	});
}

// ==================== 监测站点管理 ====================

/**
 * 分页查询监测站点列表
 * @param {Object} params 查询参数
 * @returns {Promise} 监测站点列表
 */
export function getMonitoringStationList(params) {
	return request({
		url: "/engineering-service/monitoring-stations",
		method: "get",
		params,
	});
}

/**
 * 查询监测站点详情
 * @param {number} id 监测站点ID
 * @returns {Promise} 监测站点详情
 */
export function getMonitoringStationDetail(id) {
	return request({
		url: `/engineering-service/monitoring-stations/${id}`,
		method: "get",
	});
}

/**
 * 创建监测站点
 * @param {Object} data 监测站点数据
 * @returns {Promise} 创建结果
 */
export function createMonitoringStation(data) {
	return request({
		url: "/engineering-service/monitoring-stations",
		method: "post",
		data,
	});
}

/**
 * 更新监测站点
 * @param {number} id 监测站点ID
 * @param {Object} data 监测站点数据
 * @returns {Promise} 更新结果
 */
export function updateMonitoringStation(id, data) {
	return request({
		url: `/engineering-service/monitoring-stations/${id}`,
		method: "put",
		data,
	});
}

/**
 * 删除监测站点
 * @param {number} id 监测站点ID
 * @returns {Promise} 删除结果
 */
export function deleteMonitoringStation(id) {
	return request({
		url: `/engineering-service/monitoring-stations/${id}`,
		method: "delete",
	});
}

/**
 * 批量删除监测站点
 * @param {Array<number>} ids 监测站点ID数组
 * @returns {Promise} 删除结果
 */
export function batchDeleteMonitoringStations(ids) {
	return request({
		url: "/engineering-service/monitoring-stations/batch",
		method: "delete",
		data: { ids },
	});
}

// ==================== 管道管理 ====================

/**
 * 分页查询管道列表
 * @param {Object} params 查询参数
 * @returns {Promise} 管道列表响应
 */
export function getPipelineList(params) {
	return request({
		url: "/engineering-service/pipelines",
		method: "get",
		params,
	});
}

/**
 * 查询管道详情
 * @param {number} id 管道ID
 * @returns {Promise} 管道详情
 */
export function getPipelineDetail(id) {
	return request({
		url: `/engineering-service/pipelines/${id}`,
		method: "get",
	});
}

/**
 * 创建管道
 * @param {Object} data 管道数据
 * @returns {Promise} 创建结果
 */
export function createPipeline(data) {
	return request({
		url: "/engineering-service/pipelines",
		method: "post",
		data,
	});
}

/**
 * 更新管道信息
 * @param {number} id 管道ID
 * @param {Object} data 更新数据
 * @returns {Promise} 更新结果
 */
export function updatePipeline(id, data) {
	return request({
		url: `/engineering-service/pipelines/${id}`,
		method: "put",
		data,
	});
}

/**
 * 删除管道
 * @param {number} id 管道ID
 * @returns {Promise} 删除结果
 */
export function deletePipeline(id) {
	return request({
		url: `/engineering-service/pipelines/${id}`,
		method: "delete",
	});
}

/**
 * 批量删除管道
 * @param {Array<number>} ids 管道ID数组
 * @returns {Promise} 删除结果
 */
export function batchDeletePipelines(ids) {
	return request({
		url: "/engineering-service/pipelines/batch",
		method: "delete",
		data: { ids },
	});
}

// ==================== 村庄管理 ====================

/**
 * 分页查询村庄列表
 * @param {Object} params 查询参数
 * @returns {Promise} 村庄列表响应
 */
export function getVillageList(params) {
	return request({
		url: "/engineering-service/villages",
		method: "get",
		params,
	});
}

/**
 * 查询村庄详情
 * @param {number} id 村庄ID
 * @returns {Promise} 村庄详情
 */
export function getVillageDetail(id) {
	return request({
		url: `/engineering-service/villages/${id}`,
		method: "get",
	});
}

/**
 * 创建村庄
 * @param {Object} data 村庄数据
 * @returns {Promise} 创建结果
 */
export function createVillage(data) {
	return request({
		url: "/engineering-service/villages",
		method: "post",
		data,
	});
}

/**
 * 更新村庄信息
 * @param {number} id 村庄ID
 * @param {Object} data 更新数据
 * @returns {Promise} 更新结果
 */
export function updateVillage(id, data) {
	return request({
		url: `/engineering-service/villages/${id}`,
		method: "put",
		data,
	});
}

/**
 * 删除村庄
 * @param {number} id 村庄ID
 * @returns {Promise} 删除结果
 */
export function deleteVillage(id) {
	return request({
		url: `/engineering-service/villages/${id}`,
		method: "delete",
	});
}

/**
 * 批量删除村庄
 * @param {Array<number>} ids 村庄ID数组
 * @returns {Promise} 删除结果
 */
export function batchDeleteVillages(ids) {
	return request({
		url: "/engineering-service/villages/batch",
		method: "delete",
		data: { ids },
	});
}

// ==================== 浮船管理 ====================

/**
 * 分页查询浮船列表
 * @param {Object} params 查询参数
 * @returns {Promise} 浮船列表响应
 */
export function getFloatingBoatList(params) {
	return request({
		url: "/engineering-service/floating-boats",
		method: "get",
		params,
	});
}

/**
 * 查询浮船详情
 * @param {number} id 浮船ID
 * @returns {Promise} 浮船详情
 */
export function getFloatingBoatDetail(id) {
	return request({
		url: `/engineering-service/floating-boats/${id}`,
		method: "get",
	});
}

/**
 * 创建浮船
 * @param {Object} data 浮船数据
 * @returns {Promise} 创建结果
 */
export function createFloatingBoat(data) {
	return request({
		url: "/engineering-service/floating-boats",
		method: "post",
		data,
	});
}

/**
 * 更新浮船信息
 * @param {number} id 浮船ID
 * @param {Object} data 更新数据
 * @returns {Promise} 更新结果
 */
export function updateFloatingBoat(id, data) {
	return request({
		url: `/engineering-service/floating-boats/${id}`,
		method: "put",
		data,
	});
}

/**
 * 删除浮船
 * @param {number} id 浮船ID
 * @returns {Promise} 删除结果
 */
export function deleteFloatingBoat(id) {
	return request({
		url: `/engineering-service/floating-boats/${id}`,
		method: "delete",
	});
}

/**
 * 批量删除浮船
 * @param {Array<number>} ids 浮船ID数组
 * @returns {Promise} 删除结果
 */
export function batchDeleteFloatingBoats(ids) {
	return request({
		url: "/engineering-service/floating-boats/batch",
		method: "delete",
		data: { ids },
	});
}

// ==================== 消毒材料管理 ====================

/**
 * 分页查询消毒材料列表
 * @param {Object} params 查询参数
 * @returns {Promise} 消毒材料列表响应
 */
export function getDisinfectionMaterialList(params) {
	return request({
		url: "/engineering-service/disinfection-materials",
		method: "get",
		params,
	});
}

/**
 * 查询消毒材料详情
 * @param {number} id 消毒材料ID
 * @returns {Promise} 消毒材料详情
 */
export function getDisinfectionMaterialDetail(id) {
	return request({
		url: `/engineering-service/disinfection-materials/${id}`,
		method: "get",
	});
}

/**
 * 创建消毒材料
 * @param {Object} data 消毒材料数据
 * @returns {Promise} 创建结果
 */
export function createDisinfectionMaterial(data) {
	return request({
		url: "/engineering-service/disinfection-materials",
		method: "post",
		data,
	});
}

/**
 * 更新消毒材料信息
 * @param {number} id 消毒材料ID
 * @param {Object} data 消毒材料数据
 * @returns {Promise} 更新结果
 */
export function updateDisinfectionMaterial(id, data) {
	return request({
		url: `/engineering-service/disinfection-materials/${id}`,
		method: "put",
		data,
	});
}

/**
 * 删除消毒材料
 * @param {number} id 消毒材料ID
 * @returns {Promise} 删除结果
 */
export function deleteDisinfectionMaterial(id) {
	return request({
		url: `/engineering-service/disinfection-materials/${id}`,
		method: "delete",
	});
}

/**
 * 批量删除消毒材料
 * @param {Array<number>} ids 消毒材料ID数组
 * @returns {Promise} 删除结果
 */
export function batchDeleteDisinfectionMaterials(ids) {
	return request({
		url: "/engineering-service/disinfection-materials/batch",
		method: "delete",
		data: { ids },
	});
}

// ==================== 通用功能 ====================

/**
 * 获取所有工程设施名称列表（用于预警地点选择）
 * @returns {Promise} 设施名称列表
 */
export function getAllFacilityNames() {
	return Promise.all([
		getPumpingStationList({ size: 100 }), // 获取所有泵站
		getWaterPlantList({ size: 100 }), // 获取所有水厂
		getReservoirList({ size: 100 }), // 获取所有水库
	]).then(([pumpingStations, waterPlants, reservoirs]) => {
		const facilities = [];

		// 添加泵站
		if (pumpingStations?.records) {
			facilities.push(
				...pumpingStations.records.map((item) => ({
					label: item.name,
					value: item.name,
					type: "泵站",
				}))
			);
		}

		// 添加水厂
		if (waterPlants?.records) {
			facilities.push(
				...waterPlants.records.map((item) => ({
					label: item.name,
					value: item.name,
					type: "水厂",
				}))
			);
		}

		// 添加水库
		if (reservoirs?.records) {
			facilities.push(
				...reservoirs.records.map((item) => ({
					label: item.name,
					value: item.name,
					type: "水库",
				}))
			);
		}

		return [{ label: "全部", value: "" }, ...facilities];
	});
}

/**
 * 获取可用泵站列表（下拉选择）
 * @returns {Promise} 可用泵站列表
 */
export function getAvailablePumpingStations() {
	return request({
		url: "/engineering-service/pumping-stations",
		method: "get",
		params: { page: 1, size: 100 },
	}).then((data) => data?.items || data?.records || []);
}

/**
 * 获取可用水厂列表（下拉选择）
 * @returns {Promise} 可用水厂列表
 */
export function getAvailableWaterPlants() {
	return request({
		url: "/engineering-service/water-plants/available",
		method: "get",
	}).then((resp) => resp?.data || resp);
}

/**
 * 获取可用水库列表（下拉选择）
 * @returns {Promise} 可用水库列表
 */
export function getAvailableReservoirs() {
	return request({
		url: "/engineering-service/reservoirs/available",
		method: "get",
	}).then((resp) => resp?.data || resp);
}
