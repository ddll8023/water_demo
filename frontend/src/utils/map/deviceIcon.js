/**
 * 统一设备图标配置
 * 用于统一所有设备相关组件的图标显示
 */

// 获取CSS变量值的辅助函数
const getCSSVariable = (variableName) => {
	return getComputedStyle(document.documentElement)
		.getPropertyValue(variableName)
		.trim();
};

// 基础设施类型配置
export const FACILITY_TYPE_CONFIG = {
	pumping_station: {
		symbol: "⚙️",
		get color() {
			return getCSSVariable("--facility-pumping-station-color");
		},
		get bgColor() {
			return getCSSVariable("--facility-pumping-station-bg");
		},
		name: "泵站",
	},
	water_plant: {
		symbol: "🏭",
		get color() {
			return getCSSVariable("--facility-water-plant-color");
		},
		get bgColor() {
			return getCSSVariable("--facility-water-plant-bg");
		},
		name: "水厂",
	},
	reservoir: {
		symbol: "💧",
		get color() {
			return getCSSVariable("--facility-reservoir-color");
		},
		get bgColor() {
			return getCSSVariable("--facility-reservoir-bg");
		},
		name: "水库",
	},
	monitoring_station: {
		symbol: "📊",
		get color() {
			return getCSSVariable("--facility-monitoring-station-color");
		},
		get bgColor() {
			return getCSSVariable("--facility-monitoring-station-bg");
		},
		name: "监测站",
	},
	pipeline: {
		symbol: "➡️",
		get color() {
			return getCSSVariable("--facility-pipeline-color");
		},
		get bgColor() {
			return getCSSVariable("--facility-pipeline-bg");
		},
		name: "管道",
	},
	village: {
		symbol: "🏘️",
		get color() {
			return getCSSVariable("--facility-village-color");
		},
		get bgColor() {
			return getCSSVariable("--facility-village-bg");
		},
		name: "村庄",
	},
	floating_boat: {
		symbol: "🚤",
		get color() {
			return getCSSVariable("--facility-floating-boat-color");
		},
		get bgColor() {
			return getCSSVariable("--facility-floating-boat-bg");
		},
		name: "漂浮船只",
	},
	disinfection_material: {
		symbol: "🧪",
		get color() {
			return getCSSVariable("--facility-disinfection-material-color");
		},
		get bgColor() {
			return getCSSVariable("--facility-disinfection-material-bg");
		},
		name: "消毒材料",
	},
	// 新增设施类型
	pressure_station: {
		symbol: "💪",
		get color() {
			return getCSSVariable("--facility-pressure-station-color");
		},
		get bgColor() {
			return getCSSVariable("--facility-pressure-station-bg");
		},
		name: "加压站",
	},
	main_pipeline: {
		symbol: "🔵",
		get color() {
			return getCSSVariable("--facility-main-pipeline-color");
		},
		get bgColor() {
			return getCSSVariable("--facility-main-pipeline-bg");
		},
		name: "供水干管",
	},
	branch_pipeline: {
		symbol: "🔹",
		get color() {
			return getCSSVariable("--facility-branch-pipeline-color");
		},
		get bgColor() {
			return getCSSVariable("--facility-branch-pipeline-bg");
		},
		name: "供水支管",
	},
};

// 监测项目类型配置
export const MONITORING_ITEM_CONFIG = {
	H: {
		symbol: "📈",
		get color() {
			return getCSSVariable("--station-water-level-color");
		},
		get bgColor() {
			return getCSSVariable("--station-water-level-bg");
		},
		name: "水位监测",
	},
	Q: {
		symbol: "🌊",
		get color() {
			return getCSSVariable("--station-flow-color");
		},
		get bgColor() {
			return getCSSVariable("--station-flow-bg");
		},
		name: "流量监测",
	},
	R: {
		symbol: "🌧️",
		get color() {
			return getCSSVariable("--station-rainfall-color");
		},
		get bgColor() {
			return getCSSVariable("--station-rainfall-bg");
		},
		name: "降雨监测",
	},
	V: {
		symbol: "📊",
		get color() {
			return getCSSVariable("--station-comprehensive-color");
		},
		get bgColor() {
			return getCSSVariable("--station-comprehensive-bg");
		},
		name: "综合监测",
	},
	WQ: {
		symbol: "🧪",
		get color() {
			return getCSSVariable("--station-water-quality-color");
		},
		get bgColor() {
			return getCSSVariable("--station-water-quality-bg");
		},
		name: "水质监测",
	},
	WL: {
		symbol: "📈",
		get color() {
			return getCSSVariable("--station-water-level-color");
		},
		get bgColor() {
			return getCSSVariable("--station-water-level-bg");
		},
		name: "水位监测",
	},
	FL: {
		symbol: "🌊",
		get color() {
			return getCSSVariable("--station-flow-color");
		},
		get bgColor() {
			return getCSSVariable("--station-flow-bg");
		},
		name: "流量监测",
	},
	RF: {
		symbol: "🌧️",
		get color() {
			return getCSSVariable("--station-rainfall-color");
		},
		get bgColor() {
			return getCSSVariable("--station-rainfall-bg");
		},
		name: "降雨监测",
	},
	RV: {
		symbol: "🏔️",
		get color() {
			return getCSSVariable("--station-reservoir-color");
		},
		get bgColor() {
			return getCSSVariable("--station-reservoir-bg");
		},
		name: "水库监测",
	},
};

// 预警级别配置
export const WARNING_LEVEL_CONFIG = {
	general: {
		symbol: "⚠️",
		get color() {
			return getCSSVariable("--warning-level-general-color");
		},
		get bgColor() {
			return getCSSVariable("--warning-level-general-bg");
		},
		name: "一般预警",
		level: 1,
	},
	serious: {
		symbol: "🚨",
		get color() {
			return getCSSVariable("--warning-level-serious-color");
		},
		get bgColor() {
			return getCSSVariable("--warning-level-serious-bg");
		},
		name: "严重预警",
		level: 2,
	},
	critical: {
		symbol: "🔥",
		get color() {
			return getCSSVariable("--warning-level-critical-color");
		},
		get bgColor() {
			return getCSSVariable("--warning-level-critical-bg");
		},
		name: "特别严重预警",
		level: 3,
	},
};

// 泵站状态配置
export const PUMP_STATUS_CONFIG = {
	open: {
		symbol: "▶️",
		get color() {
			return getCSSVariable("--pump-status-open-color");
		},
		get bgColor() {
			return getCSSVariable("--pump-status-open-bg");
		},
		name: "泵打开",
		status: "运行",
	},
	closed: {
		symbol: "⏹️",
		get color() {
			return getCSSVariable("--pump-status-closed-color");
		},
		get bgColor() {
			return getCSSVariable("--pump-status-closed-bg");
		},
		name: "泵关闭",
		status: "停止",
	},
	maintenance: {
		symbol: "🔧",
		get color() {
			return getCSSVariable("--pump-status-maintenance-color");
		},
		get bgColor() {
			return getCSSVariable("--pump-status-maintenance-bg");
		},
		name: "维护中",
		status: "维护",
	},
};

// 管线类型配置
export const PIPELINE_TYPE_CONFIG = {
	main: {
		symbol: "━",
		get color() {
			return getCSSVariable("--pipeline-main-color");
		},
		get bgColor() {
			return getCSSVariable("--pipeline-main-bg");
		},
		name: "供水干管",
		width: 4,
		style: "solid",
	},
	branch: {
		symbol: "─",
		get color() {
			return getCSSVariable("--pipeline-branch-color");
		},
		get bgColor() {
			return getCSSVariable("--pipeline-branch-bg");
		},
		name: "供水支管",
		width: 2,
		style: "solid",
	},
	distribution: {
		symbol: "┄",
		get color() {
			return getCSSVariable("--pipeline-distribution-color");
		},
		get bgColor() {
			return getCSSVariable("--pipeline-distribution-bg");
		},
		name: "分配管",
		width: 1,
		style: "dashed",
	},
};

// 默认设备图标
export const DEFAULT_DEVICE_ICON = {
	symbol: "🏢",
	get color() {
		return getCSSVariable("--facility-default-color");
	},
	get bgColor() {
		return getCSSVariable("--facility-default-bg");
	},
	name: "未知设备",
};

// 统一设备类型选项配置
export const DEVICE_TYPE_OPTIONS = [
	{ value: "all", label: "全部设备" },
	{ value: "water_plant", label: "水厂设备" },
	{ value: "reservoir", label: "水库设备" },
	{ value: "pumping_station", label: "泵站设备" },
	{ value: "pressure_station", label: "加压站设备" },
	{ value: "pipeline", label: "管道设备" },
	{ value: "village", label: "村庄设备" },
	{ value: "floating_boat", label: "漂浮船只" },
	{ value: "disinfection_material", label: "消毒材料" },
	{ value: "monitoring_station", label: "监测站点" },
];

/**
 * 获取设备图标配置
 * @param {Object} device - 设备对象
 * @param {string} deviceType - 设备类型 ('facility' | 'station')
 * @returns {Object} 图标配置对象
 */
export function getDeviceIconConfig(device, deviceType = "facility") {
	// 监测站类型处理
	if (
		deviceType === "station" ||
		device.monitoringItem ||
		device.stationName ||
		device.stationId
	) {
		if (
			device.monitoringItem &&
			MONITORING_ITEM_CONFIG[device.monitoringItem]
		) {
			return MONITORING_ITEM_CONFIG[device.monitoringItem];
		}
		// 如果没有具体的监测项目，返回通用监测站图标
		return FACILITY_TYPE_CONFIG.monitoring_station;
	}

	// 设施类型处理
	if (device.type && FACILITY_TYPE_CONFIG[device.type]) {
		return FACILITY_TYPE_CONFIG[device.type];
	}

	return DEFAULT_DEVICE_ICON;
}

/**
 * 获取预警图标配置
 * @param {Object} warning - 预警对象
 * @returns {Object} 图标配置对象
 */
export function getWarningIconConfig(warning) {
	const level = warning.warningLevel || warning.level;
	if (level && WARNING_LEVEL_CONFIG[level]) {
		return WARNING_LEVEL_CONFIG[level];
	}
	return WARNING_LEVEL_CONFIG.general;
}

/**
 * 获取泵站状态图标配置
 * @param {Object} pump - 泵站对象
 * @returns {Object} 图标配置对象
 */
export function getPumpStatusIconConfig(pump) {
	const status = pump.pumpStatus || pump.status || "closed";
	if (status && PUMP_STATUS_CONFIG[status]) {
		return PUMP_STATUS_CONFIG[status];
	}
	return PUMP_STATUS_CONFIG.closed;
}

/**
 * 获取管线类型图标配置
 * @param {Object} pipeline - 管线对象
 * @returns {Object} 图标配置对象
 */
export function getPipelineTypeConfig(pipeline) {
	const type = pipeline.pipelineType || pipeline.type || "branch";
	if (type && PIPELINE_TYPE_CONFIG[type]) {
		return PIPELINE_TYPE_CONFIG[type];
	}
	return PIPELINE_TYPE_CONFIG.branch;
}

/**
 * 获取设备图标符号
 * @param {Object} device - 设备对象
 * @param {string} deviceType - 设备类型
 * @returns {string} 图标符号
 */
export function getDeviceIconSymbol(device, deviceType = "facility") {
	const config = getDeviceIconConfig(device, deviceType);
	return config.symbol;
}

/**
 * 获取设备类型名称
 * @param {Object} device - 设备对象
 * @param {string} deviceType - 设备类型
 * @returns {string} 设备类型名称
 */
export function getDeviceTypeName(device, deviceType = "facility") {
	const config = getDeviceIconConfig(device, deviceType);
	return config.name;
}
