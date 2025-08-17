import { ref, reactive } from "vue";
import {
	getFacilityTypes,
	getFacilityTypeMap,
} from "@/api/engineering-service";

/**
 * 设施类型管理组合函数
 *
 * 用于统一管理设施类型数据，替代字典API的使用
 * 提供缓存机制，避免重复请求
 */
export function useFacilityTypes() {
	// 设施类型列表（完整信息）
	const facilityTypes = ref([]);

	// 设施类型映射（简化信息）
	const facilityTypeMap = ref({});

	// 加载状态
	const loading = ref(false);

	// 错误信息
	const error = ref(null);

	// 缓存标记
	const isLoaded = ref(false);

	/**
	 * 获取设施类型列表
	 * @param {boolean} forceRefresh 是否强制刷新
	 * @returns {Promise<Array>} 设施类型列表
	 */
	const loadFacilityTypes = async (forceRefresh = false) => {
		if (isLoaded.value && !forceRefresh) {
			return facilityTypes.value;
		}

		loading.value = true;
		error.value = null;

		try {
			const response = await getFacilityTypes();

			// 兼容API直接返回数组或包含data字段的对象
			const data = Array.isArray(response) ? response : response.data || [];

			facilityTypes.value = data;
			isLoaded.value = true;

			return facilityTypes.value;
		} catch (err) {
			error.value = err.message || "获取设施类型失败";
			console.error("获取设施类型失败:", err);
			return [];
		} finally {
			loading.value = false;
		}
	};

	/**
	 * 获取设施类型映射
	 * @param {boolean} forceRefresh 是否强制刷新
	 * @returns {Promise<Object>} 设施类型映射
	 */
	const loadFacilityTypeMap = async (forceRefresh = false) => {
		if (Object.keys(facilityTypeMap.value).length > 0 && !forceRefresh) {
			return facilityTypeMap.value;
		}

		try {
			const response = await getFacilityTypeMap();
			facilityTypeMap.value = response.data || {};
			return facilityTypeMap.value;
		} catch (err) {
			error.value = err.message || "获取设施类型映射失败";
			console.error("获取设施类型映射失败:", err);
			return {};
		}
	};

	/**
	 * 获取设施类型选项（用于下拉框）
	 * @returns {Promise<Array>} 格式化的选项列表
	 */
	const getFacilityTypeOptions = async () => {
		const types = await loadFacilityTypes();
		const options = types.map((type) => ({
			value: type.value,
			label: type.label,
			description: type.description,
		}));

		return options;
	};

	/**
	 * 根据值获取设施类型标签
	 * @param {string} value 设施类型值
	 * @returns {Promise<string>} 设施类型标签
	 */
	const getFacilityTypeLabel = async (value) => {
		if (!value) return "-";

		// 先尝试从映射中获取
		if (Object.keys(facilityTypeMap.value).length === 0) {
			await loadFacilityTypeMap();
		}

		return facilityTypeMap.value[value] || value || "-";
	};

	/**
	 * 同步获取设施类型标签（需要先加载数据）
	 * @param {string} value 设施类型值
	 * @returns {string} 设施类型标签
	 */
	const getFacilityTypeLabelSync = (value) => {
		if (!value) return "-";
		return facilityTypeMap.value[value] || value || "-";
	};

	/**
	 * 根据值获取设施类型完整信息
	 * @param {string} value 设施类型值
	 * @returns {Promise<Object|null>} 设施类型信息
	 */
	const getFacilityTypeInfo = async (value) => {
		if (!value) return null;

		const types = await loadFacilityTypes();
		return types.find((type) => type.value === value) || null;
	};

	/**
	 * 检查设施类型是否存在
	 * @param {string} value 设施类型值
	 * @returns {Promise<boolean>} 是否存在
	 */
	const isValidFacilityType = async (value) => {
		if (!value) return false;

		const types = await loadFacilityTypes();
		return types.some((type) => type.value === value);
	};

	/**
	 * 清除缓存
	 */
	const clearCache = () => {
		facilityTypes.value = [];
		facilityTypeMap.value = {};
		isLoaded.value = false;
		error.value = null;
	};

	return {
		// 响应式数据
		facilityTypes,
		facilityTypeMap,
		loading,
		error,
		isLoaded,

		// 方法
		loadFacilityTypes,
		loadFacilityTypeMap,
		getFacilityTypeOptions,
		getFacilityTypeLabel,
		getFacilityTypeLabelSync,
		getFacilityTypeInfo,
		isValidFacilityType,
		clearCache,
	};
}
