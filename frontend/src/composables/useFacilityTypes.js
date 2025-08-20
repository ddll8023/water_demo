import { ref } from "vue";
import {
	getFacilityTypes,
	getFacilityTypeMap,
} from "@/api/engineering-service";

/**
 * 设施类型管理组合函数
 *
 * 用于统一管理设施类型数据，提供缓存机制
 */
export function useFacilityTypes() {
	// 设施类型列表缓存
	const facilityTypesCache = ref([]);

	// 设施类型映射缓存
	const facilityTypeMapCache = ref({});

	/**
	 * 获取设施类型映射
	 * @param {boolean} forceRefresh 是否强制刷新
	 * @returns {Promise<Object>} 设施类型映射
	 */
	const loadFacilityTypeMap = async (forceRefresh = false) => {
		if (Object.keys(facilityTypeMapCache.value).length > 0 && !forceRefresh) {
			return facilityTypeMapCache.value;
		}

		try {
			const response = await getFacilityTypeMap();
			facilityTypeMapCache.value = response.data || {};
			return facilityTypeMapCache.value;
		} catch (err) {
			console.error("获取设施类型映射失败:", err);
			return {};
		}
	};

	/**
	 * 获取设施类型选项（用于下拉框）
	 * @returns {Promise<Array>} 格式化的选项列表
	 */
	const getFacilityTypeOptions = async () => {
		if (facilityTypesCache.value.length > 0) {
			return facilityTypesCache.value.map((type) => ({
				value: type.value,
				label: type.label,
				description: type.description,
			}));
		}

		try {
			const response = await getFacilityTypes();
			const data = Array.isArray(response) ? response : response.data || [];
			facilityTypesCache.value = data;

			return data.map((type) => ({
				value: type.value,
				label: type.label,
				description: type.description,
			}));
		} catch (err) {
			console.error("获取设施类型失败:", err);
			return [];
		}
	};

	/**
	 * 同步获取设施类型标签（需要先加载数据）
	 * @param {string} value 设施类型值
	 * @returns {string} 设施类型标签
	 */
	const getFacilityTypeLabelSync = (value) => {
		if (!value) return "-";
		return facilityTypeMapCache.value[value] || value || "-";
	};

	return {
		// 方法
		loadFacilityTypeMap,
		getFacilityTypeOptions,
		getFacilityTypeLabelSync,
	};
}
