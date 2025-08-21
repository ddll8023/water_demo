import { formatLocalTimeForAPI } from "@/utils/shared/common";

/**
 * 处理时间范围参数，转换为API所需格式
 * @param {Array} timeRange - 时间范围数组 [startTime, endTime]
 * @param {Object} options - 额外选项（保留扩展性）
 * @returns {Object} 包含startTime和endTime的对象
 */
export const processTimeRangeParams = (timeRange, options = {}) => {
	if (!timeRange || !Array.isArray(timeRange) || timeRange.length !== 2) {
		return {};
	}

	return {
		startTime: formatLocalTimeForAPI(timeRange[0]),
		endTime: formatLocalTimeForAPI(timeRange[1]),
	};
};

/**
 * 获取当前有效的时间范围
 * @param {Object} params - 参数对象
 * @param {Object} params.searchForm - 搜索表单ref对象
 * @param {Object} params.quickSearchTimeRange - 快捷搜索时间范围ref对象
 * @returns {Array} 当前有效的时间范围
 */
export const getCurrentTimeRange = ({ searchForm, quickSearchTimeRange }) => {
	// 如果是快捷搜索，使用快捷搜索的时间范围
	if (quickSearchTimeRange.value) {
		return quickSearchTimeRange.value;
	}
	// 否则使用时间选择器的值
	return searchForm.value.timeRange;
};

/**
 * 计算快捷时间范围
 * @param {string} timeRangeType - 时间范围类型 ('7days', '30days', 'all')
 * @returns {Array} 时间范围数组 [startTime, endTime] 或空数组
 */
export const calculateQuickTimeRange = (timeRangeType) => {
	const now = new Date();
	let startTime, endTime;

	switch (timeRangeType) {
		case "7days":
			// 最近7天
			startTime = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
			endTime = now;
			break;
		case "30days":
			// 最近30天
			startTime = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
			endTime = now;
			break;
		case "all":
			// 全部数据，返回空数组
			return [];
		default:
			return [];
	}

	return [startTime, endTime];
};

/**
 * 处理快捷按钮搜索
 * @param {Object} params - 参数对象
 * @param {Array} params.timeRange - 时间范围
 * @param {Object} params.quickSearchTimeRange - 快捷搜索时间范围ref对象
 * @param {Object} params.pagination - 分页对象
 * @param {Function} params.loadTableData - 加载表格数据函数
 * @param {Object} params.hasSearched - 是否已搜索ref对象
 * @param {Object} params.searchForm - 搜索表单ref对象
 * @param {Object} params.chartSearchTriggered - 图表搜索触发ref对象
 * @param {Function} params.loadChartData - 加载图表数据函数
 */
export const handleQuickSearch = ({
	timeRange,
	quickSearchTimeRange,
	pagination,
	loadTableData,
	hasSearched,
	searchForm,
	chartSearchTriggered,
	loadChartData,
}) => {
	// 设置快捷搜索标记，防止监听器重置按钮状态
	quickSearchTimeRange.value = timeRange;

	pagination.currentPage = 1;
	loadTableData();

	// 标记已执行搜索
	hasSearched.value = true;

	// 只有在选择了站点时才触发图表搜索
	if (searchForm.value.stationId) {
		chartSearchTriggered.value = true;
		loadChartData();
	} else {
		chartSearchTriggered.value = false;
	}
};
