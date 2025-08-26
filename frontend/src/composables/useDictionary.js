// ==========================================================================
// 鄂北地区水资源管理系统 - 字典数据组合式函数
// ==========================================================================
// 提供字典数据获取、缓存和转换功能

import { ref, reactive } from "vue";
import { getDictDataByTypeCode } from "@/api/dictionary";

// ==========================================================================
// 基础配置与状态
// ==========================================================================

// 全局字典数据缓存
const dictCache = reactive({});

// 缓存过期时间（毫秒）- 5分钟
const CACHE_EXPIRE_TIME = 5 * 60 * 1000;

/**
 * 字典数据组合式函数
 * @returns {Object} 字典相关的方法和状态
 */
export function useDictionary() {
	// ==========================================================================
	// 核心字典数据操作
	// ==========================================================================

	/**
	 * 根据字典类型编码获取字典数据
	 * @param {string} typeCode 字典类型编码
	 * @param {boolean} useCache 是否使用缓存，默认true
	 * @returns {Promise<Array>} 字典数据数组
	 */
	const getDictData = async (typeCode, useCache = true) => {
		if (!typeCode) {
			console.warn("字典类型编码不能为空");
			return [];
		}

		// 检查缓存
		if (useCache && dictCache[typeCode]) {
			const cached = dictCache[typeCode];
			const now = Date.now();

			// 缓存未过期，直接返回
			if (now - cached.timestamp < CACHE_EXPIRE_TIME) {
				return cached.data;
			}

			// 缓存过期，删除缓存
			delete dictCache[typeCode];
		}

		try {
			// console.log(`正在获取字典数据: ${typeCode}`)
			const response = await getDictDataByTypeCode(typeCode);

			// 处理响应数据
			const dictData = Array.isArray(response) ? response : [];

			// 转换为标准格式
			const formattedData = dictData
				.filter((item) => item.isActive === "1") // 只返回启用的数据
				.sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0)) // 按排序值排序
				.map((item) => ({
					label: item.dataLabel,
					value: item.dataValue,
					description: item.description,
					sortOrder: item.sortOrder,
				}));

			// 更新缓存
			if (useCache) {
				dictCache[typeCode] = {
					data: formattedData,
					timestamp: Date.now(),
				};
			}

			// console.log(`字典数据获取成功: ${typeCode}`, formattedData);
			return formattedData;
		} catch (err) {
			console.error(`获取字典数据失败: ${typeCode}`, err);
			return [];
		}
	};

	/**
	 * 根据字典类型编码和数据值获取显示标签
	 * @param {string} typeCode 字典类型编码
	 * @param {string} dataValue 字典数据值
	 * @param {string} defaultLabel 默认显示标签
	 * @returns {Promise<string>} 显示标签
	 */
	const getDictLabel = async (typeCode, dataValue, defaultLabel = "") => {
		if (!typeCode || dataValue === undefined || dataValue === null) {
			return defaultLabel;
		}

		try {
			const dictData = await getDictData(typeCode);
			const item = dictData.find((d) => String(d.value) === String(dataValue));
			return item ? item.label : defaultLabel;
		} catch (err) {
			console.error(`获取字典标签失败: ${typeCode}/${dataValue}`, err);
			return defaultLabel;
		}
	};

	return {
		// 方法
		getDictData,
		getDictLabel,
	};
}

// ==========================================================================
// 预定义字典类型和默认数据
// ==========================================================================

/**
 * 常用字典类型编码常量
 */
export const DICT_TYPES = {
	USER_STATUS: "user_status", // 用户状态
	DEPARTMENT_STATUS: "department_status", // 部门状态
	DEVICE_STATUS: "device_status", // 设备状态
	WARNING_LEVEL: "warning_level", // 预警级别
	WARNING_TYPE: "warning_type", // 预警类型
	WARNING_STATUS: "warning_status", // 预警状态
	MONITORING_ITEM: "monitoring_item", // 监测项目
	DATA_QUALITY: "data_quality", // 数据质量
	COLLECTION_METHOD: "collection_method", // 采集方式
	// 工程信息服务模块字典类型
	OPERATION_MODE: "operation_mode", // 运行方式
	ENGINEERING_GRADE: "engineering_grade", // 工程等级
	ENGINEERING_SCALE: "engineering_scale", // 工程规模
	PIPELINE_TYPE: "pipeline_type", // 管道类型
	// 巡检管理模块字典类型
	INSPECTION_STATUS: "inspection_status", // 巡检状态
	INSPECTION_FREQUENCY: "inspection_frequency", // 巡检频次
};
