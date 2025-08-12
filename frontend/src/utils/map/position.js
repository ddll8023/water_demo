/**
 * 地图位置计算工具类
 * 提供弹窗位置计算、防抖、缓存等功能
 */

/**
 * 防抖函数
 * @param {Function} func 要防抖的函数
 * @param {number} wait 防抖延迟时间(ms)
 * @param {boolean} immediate 是否立即执行
 * @returns {Function} 防抖后的函数
 */
export function debounce(func, wait = 100, immediate = false) {
	let timeout;
	return function executedFunction(...args) {
		const later = () => {
			timeout = null;
			if (!immediate) func.apply(this, args);
		};
		const callNow = immediate && !timeout;
		clearTimeout(timeout);
		timeout = setTimeout(later, wait);
		if (callNow) func.apply(this, args);
	};
}

/**
 * 节流函数
 * @param {Function} func 要节流的函数
 * @param {number} wait 节流间隔时间(ms)
 * @returns {Function} 节流后的函数
 */
export function throttle(func, wait = 16) {
	let timeout;
	let previous = 0;
	return function (...args) {
		const now = Date.now();
		const remaining = wait - (now - previous);
		if (remaining <= 0 || remaining > wait) {
			if (timeout) {
				clearTimeout(timeout);
				timeout = null;
			}
			previous = now;
			func.apply(this, args);
		} else if (!timeout) {
			timeout = setTimeout(() => {
				previous = Date.now();
				timeout = null;
				func.apply(this, args);
			}, remaining);
		}
	};
}

/**
 * 位置缓存管理器
 */
class PositionCache {
	constructor(maxSize = 50, ttl = 5000) {
		this.cache = new Map();
		this.maxSize = maxSize;
		this.ttl = ttl; // 缓存生存时间(ms)
	}

	/**
	 * 生成缓存键
	 */
	generateKey(device, bounds, iconSize) {
		return `${device.id || device.stationId || device.name}_${bounds.width}_${
			bounds.height
		}_${iconSize.width}_${iconSize.height}`;
	}

	/**
	 * 获取缓存的位置信息
	 */
	get(key) {
		const item = this.cache.get(key);
		if (!item) return null;

		if (Date.now() - item.timestamp > this.ttl) {
			this.cache.delete(key);
			return null;
		}

		return item.data;
	}

	/**
	 * 设置缓存
	 */
	set(key, data) {
		// 清理过期缓存
		if (this.cache.size >= this.maxSize) {
			this.cleanup();
		}

		this.cache.set(key, {
			data,
			timestamp: Date.now(),
		});
	}

	/**
	 * 清理过期缓存
	 */
	cleanup() {
		const now = Date.now();
		for (const [key, item] of this.cache.entries()) {
			if (now - item.timestamp > this.ttl) {
				this.cache.delete(key);
			}
		}

		// 如果还是太多，删除最老的
		if (this.cache.size >= this.maxSize) {
			const oldestKey = this.cache.keys().next().value;
			this.cache.delete(oldestKey);
		}
	}

	/**
	 * 清空缓存
	 */
	clear() {
		this.cache.clear();
	}
}

// 全局位置缓存实例
export const positionCache = new PositionCache();

/**
 * 生成设备唯一标识符
 * @param {Object} device 设备对象
 * @returns {string} 唯一标识符
 */
export function generateDeviceId(device) {
	return (
		device.id ||
		device.stationId ||
		device.name ||
		`${device.longitude}_${device.latitude}`
	);
}

/**
 * 检查点是否在矩形区域内
 * @param {Object} point 点坐标 {x, y}
 * @param {Object} bounds 边界 {left, top, width, height}
 * @param {number} margin 边距
 * @returns {boolean}
 */
export function isPointInBounds(point, bounds, margin = 0) {
	return (
		point.x >= bounds.left - margin &&
		point.x <= bounds.left + bounds.width + margin &&
		point.y >= bounds.top - margin &&
		point.y <= bounds.top + bounds.height + margin
	);
}

/**
 * 计算弹窗最佳显示位置
 * @param {Object} options 配置选项
 * @returns {Object} 位置信息
 */
export function calculateOptimalPosition(options) {
	const {
		iconPosition,
		iconSize,
		cardSize,
		mapBounds,
		preferredDirection = "bottom-right",
		margin = 8,
	} = options;

	const x = iconPosition.x || 0;
	const y = iconPosition.y || 0;
	const iconWidth = iconSize.width || 24;
	const iconHeight = iconSize.height || 24;
	const cardWidth = cardSize.width || 260;
	const cardHeight = cardSize.height || 180;

	// 检查图标是否在可视区域内
	const isIconVisible = isPointInBounds(
		{ x, y },
		mapBounds,
		Math.max(iconWidth, iconHeight)
	);

	if (!isIconVisible) {
		return {
			visible: false,
			position: { x, y },
			direction: preferredDirection,
		};
	}

	// 尝试不同的显示方向
	const directions = [
		{
			name: "bottom-right",
			x: x + iconWidth + margin,
			y: y - 10,
			check: (pos) =>
				pos.x + cardWidth <= mapBounds.left + mapBounds.width &&
				pos.y + cardHeight <= mapBounds.top + mapBounds.height,
		},
		{
			name: "bottom-left",
			x: x - cardWidth - margin,
			y: y - 10,
			check: (pos) =>
				pos.x >= mapBounds.left &&
				pos.y + cardHeight <= mapBounds.top + mapBounds.height,
		},
		{
			name: "top-right",
			x: x + iconWidth + margin,
			y: y - cardHeight + iconHeight + 10,
			check: (pos) =>
				pos.x + cardWidth <= mapBounds.left + mapBounds.width &&
				pos.y >= mapBounds.top,
		},
		{
			name: "top-left",
			x: x - cardWidth - margin,
			y: y - cardHeight + iconHeight + 10,
			check: (pos) => pos.x >= mapBounds.left && pos.y >= mapBounds.top,
		},
	];

	// 优先选择首选方向
	const preferredDir = directions.find(
		(dir) => dir.name === preferredDirection
	);
	if (
		preferredDir &&
		preferredDir.check({ x: preferredDir.x, y: preferredDir.y })
	) {
		return {
			visible: true,
			position: { x: preferredDir.x, y: preferredDir.y },
			direction: preferredDir.name,
		};
	}

	// 选择第一个可用的方向
	for (const dir of directions) {
		if (dir.check({ x: dir.x, y: dir.y })) {
			return {
				visible: true,
				position: { x: dir.x, y: dir.y },
				direction: dir.name,
			};
		}
	}

	// 如果没有理想位置，选择最不会超出边界的位置
	const fallbackX = Math.min(
		Math.max(x + iconWidth + margin, mapBounds.left + margin),
		mapBounds.left + mapBounds.width - cardWidth - margin
	);
	const fallbackY = Math.min(
		Math.max(y - 10, mapBounds.top + margin),
		mapBounds.top + mapBounds.height - cardHeight - margin
	);

	return {
		visible: true,
		position: { x: fallbackX, y: fallbackY },
		direction: "bottom-right",
	};
}

/**
 * 计算箭头样式
 * @param {string} direction 显示方向
 * @returns {Object} 箭头样式
 */
export function calculateArrowStyle(direction) {
	const arrowStyles = {
		"bottom-right": {
			left: "-6px",
			top: "20px",
			borderRight: "6px solid rgba(255, 255, 255, 0.92)",
			borderTop: "6px solid transparent",
			borderBottom: "6px solid transparent",
			borderLeft: "none",
		},
		"bottom-left": {
			right: "-6px",
			top: "20px",
			borderLeft: "6px solid rgba(255, 255, 255, 0.92)",
			borderTop: "6px solid transparent",
			borderBottom: "6px solid transparent",
			borderRight: "none",
		},
		"top-right": {
			left: "-6px",
			bottom: "20px",
			borderRight: "6px solid rgba(255, 255, 255, 0.92)",
			borderTop: "6px solid transparent",
			borderBottom: "6px solid transparent",
			borderLeft: "none",
		},
		"top-left": {
			right: "-6px",
			bottom: "20px",
			borderLeft: "6px solid rgba(255, 255, 255, 0.92)",
			borderTop: "6px solid transparent",
			borderBottom: "6px solid transparent",
			borderRight: "none",
		},
	};

	return arrowStyles[direction] || arrowStyles["bottom-right"];
}

/**
 * 创建平滑动画帧调度器
 */
export class AnimationScheduler {
	constructor() {
		this.tasks = new Set();
		this.isRunning = false;
	}

	/**
	 * 添加动画任务
	 */
	add(task) {
		this.tasks.add(task);
		if (!this.isRunning) {
			this.start();
		}
	}

	/**
	 * 移除动画任务
	 */
	remove(task) {
		this.tasks.delete(task);
		if (this.tasks.size === 0) {
			this.stop();
		}
	}

	/**
	 * 开始动画循环
	 */
	start() {
		if (this.isRunning) return;
		this.isRunning = true;
		this.tick();
	}

	/**
	 * 停止动画循环
	 */
	stop() {
		this.isRunning = false;
	}

	/**
	 * 动画帧回调
	 */
	tick() {
		if (!this.isRunning) return;

		for (const task of this.tasks) {
			try {
				task();
			} catch (error) {
				console.error("Animation task error:", error);
				this.tasks.delete(task);
			}
		}

		if (this.tasks.size > 0) {
			requestAnimationFrame(() => this.tick());
		} else {
			this.isRunning = false;
		}
	}
}

// 全局动画调度器实例
export const animationScheduler = new AnimationScheduler();

/**
 * 性能监控器
 */
export class PerformanceMonitor {
	constructor() {
		this.metrics = {
			positionUpdates: 0,
			totalUpdateTime: 0,
			averageUpdateTime: 0,
			maxUpdateTime: 0,
		};
	}

	/**
	 * 开始性能测量
	 */
	startMeasure() {
		return performance.now();
	}

	/**
	 * 结束性能测量并记录
	 */
	endMeasure(startTime, operation = "position_update") {
		const endTime = performance.now();
		const duration = endTime - startTime;

		this.metrics.positionUpdates++;
		this.metrics.totalUpdateTime += duration;
		this.metrics.averageUpdateTime =
			this.metrics.totalUpdateTime / this.metrics.positionUpdates;
		this.metrics.maxUpdateTime = Math.max(this.metrics.maxUpdateTime, duration);

		// 在开发环境下输出性能警告
		if (process.env.NODE_ENV === "development" && duration > 16) {
			console.warn(
				`${operation} took ${duration.toFixed(2)}ms (> 16ms threshold)`
			);
		}

		return duration;
	}

	/**
	 * 获取性能指标
	 */
	getMetrics() {
		return { ...this.metrics };
	}

	/**
	 * 重置性能指标
	 */
	reset() {
		this.metrics = {
			positionUpdates: 0,
			totalUpdateTime: 0,
			averageUpdateTime: 0,
			maxUpdateTime: 0,
		};
	}
}

// 全局性能监控器实例
export const performanceMonitor = new PerformanceMonitor();
