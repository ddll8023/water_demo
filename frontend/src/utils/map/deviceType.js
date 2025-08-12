/**
 * 统一设备类型识别工具
 * 用于统一所有组件的设备类型识别逻辑
 */

/**
 * 获取设备的统一类型
 * @param {Object} item - 设备对象
 * @returns {string} 统一的设备类型
 */
export function getUnifiedDeviceType(item) {
	// 如果是监测站数据，统一归类为监测站点
	if (item.monitoringItem || item.stationName || item.stationId) {
		return "monitoring_station";
	}
	// 如果是设施数据，直接使用type字段
	return item.type || "unknown";
}

/**
 * 判断是否为监测站
 * @param {Object} item - 设备对象
 * @returns {boolean} 是否为监测站
 */
export function isMonitoringStation(item) {
	return Boolean(item.monitoringItem || item.stationName || item.stationId);
}

/**
 * 获取设备唯一标识符
 * @param {Object} device - 设备对象
 * @returns {string} 设备唯一标识符
 */
export function generateDeviceId(device) {
	if (!device) return "";

	// 监测站使用stationId或stationName
	if (device.stationId) {
		return `station_${device.stationId}`;
	}

	if (device.stationName) {
		return `station_${device.stationName}_${device.longitude}_${device.latitude}`;
	}

	// 设施使用id或name
	if (device.id) {
		return `facility_${device.id}`;
	}

	if (device.name) {
		return `facility_${device.name}_${device.longitude}_${device.latitude}`;
	}

	// 最后使用坐标作为标识
	return `device_${device.longitude}_${device.latitude}`;
}

/**
 * 获取设备名称
 * @param {Object} item - 设备对象
 * @returns {string} 设备名称
 */
export function getDeviceName(item) {
	return item.name || item.stationName || "未知设备";
}

/**
 * 检查是否有有效的地理位置
 * @param {Object} item - 设备对象
 * @returns {boolean} 是否有有效位置
 */
export function hasValidLocation(item) {
	return Boolean(item.longitude && item.latitude);
}

/**
 * 获取设备位置信息
 * @param {Object} item - 设备对象
 * @returns {string} 位置信息
 */
export function getDeviceLocation(item) {
	if (item.address) return item.address;
	if (item.location) return item.location;
	if (item.longitude && item.latitude) {
		return `${item.longitude.toFixed(4)}, ${item.latitude.toFixed(4)}`;
	}
	return "位置未知";
}

/**
 * 获取设备状态样式类名
 * @param {Object} item - 设备对象
 * @returns {string} 状态样式类名
 */
export function getDeviceStatusClass(item) {
	const status = item.status || item.stationStatus || item.operationStatus;
	const statusMap = {
		NORMAL: "status-normal",
		normal: "status-normal",
		ONLINE: "status-online",
		online: "status-online",
		OFFLINE: "status-offline",
		offline: "status-offline",
		MAINTENANCE: "status-maintenance",
		maintenance: "status-maintenance",
		FAULT: "status-fault",
		fault: "status-fault",
		error: "status-error",
		warning: "status-warning",
		运行正常: "status-normal",
		正常运行: "status-normal",
		正常蓄水: "status-normal",
		自动运行: "status-normal",
		维护中: "status-maintenance",
		故障: "status-fault",
		告警: "status-warning",
	};
	return statusMap[status] || "status-default";
}

/**
 * 获取设备状态文本
 * @param {Object} item - 设备对象
 * @returns {string} 状态文本
 */
export function getDeviceStatusText(item) {
	const status = item.status || item.stationStatus || item.operationStatus;
	const statusMap = {
		NORMAL: "正常",
		normal: "正常",
		ONLINE: "在线",
		online: "在线",
		OFFLINE: "离线",
		offline: "离线",
		MAINTENANCE: "维护中",
		maintenance: "维护中",
		FAULT: "故障",
		fault: "故障",
		error: "故障",
		warning: "告警",
	};

	return (
		item.statusName ||
		item.stationStatusName ||
		statusMap[status] ||
		status ||
		"未知"
	);
}
