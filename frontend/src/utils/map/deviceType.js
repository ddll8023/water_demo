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
 * 获取设备状态值（私有辅助函数）
 * @param {Object} item - 设备对象
 * @returns {string} 状态值
 */
function getDeviceStatusValue(item) {
	return (
		item.status ||
		item.stationStatus ||
		item.operationStatus ||
		item.pumpingStatus
	);
}

/**
 * 状态映射配置（私有常量）
 */
const STATUS_MAPPINGS = {
	// 字典状态值
	normal: { class: "status-normal", text: "正常" },
	fault: { class: "status-fault", text: "故障" },
	maintenance: { class: "status-maintenance", text: "维护" },
	// 兼容旧有状态值
	NORMAL: { class: "status-normal", text: "正常" },
	ONLINE: { class: "status-normal", text: "正常" },
	online: { class: "status-normal", text: "正常" },
	OFFLINE: { class: "status-fault", text: "故障" },
	offline: { class: "status-fault", text: "故障" },
	MAINTENANCE: { class: "status-maintenance", text: "维护" },
	FAULT: { class: "status-fault", text: "故障" },
	error: { class: "status-fault", text: "故障" },
	warning: { class: "status-fault", text: "故障" },
	// 中文状态值
	正常: { class: "status-normal", text: "正常" },
	运行正常: { class: "status-normal", text: "正常" },
	正常运行: { class: "status-normal", text: "正常" },
	正常蓄水: { class: "status-normal", text: "正常" },
	自动运行: { class: "status-normal", text: "正常" },
	维护中: { class: "status-maintenance", text: "维护" },
	故障: { class: "status-fault", text: "故障" },
	告警: { class: "status-fault", text: "故障" },
};

/**
 * 获取设备状态样式类名
 * @param {Object} item - 设备对象
 * @returns {string} 状态样式类名
 */
export function getDeviceStatusClass(item) {
	const status = getDeviceStatusValue(item);
	const mapping = STATUS_MAPPINGS[status];
	return mapping ? mapping.class : "status-default";
}

/**
 * 获取设备状态文本
 * @param {Object} item - 设备对象
 * @returns {string} 状态文本
 */
export function getDeviceStatusText(item) {
	const status = getDeviceStatusValue(item);
	const mapping = STATUS_MAPPINGS[status];

	return (
		item.statusName ||
		item.stationStatusName ||
		item.pumpingStatusName ||
		(mapping && mapping.text) ||
		status ||
		"未知"
	);
}
