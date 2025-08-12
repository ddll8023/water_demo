/**
 * 性能监控和调试工具
 * 提供弹窗位置更新的性能监控、可视化调试和优化建议
 */

/**
 * 扩展的性能监控器
 */
export class ExtendedPerformanceMonitor {
	constructor() {
		this.metrics = new Map();
		this.debugMode = process.env.NODE_ENV === "development";
		this.maxHistorySize = 100;
		this.thresholds = {
			position_update: 16, // 16ms = 60fps
			map_bounds_update: 8,
			cache_hit: 2,
			render_frame: 16,
		};

		// 调试可视化元素
		this.debugOverlay = null;
		this.setupDebugOverlay();
	}

	/**
	 * 开始性能测量（增强版）
	 */
	startMeasure(operation, metadata = {}) {
		const startTime = performance.now();
		const measureId = `${operation}_${Date.now()}_${Math.random()
			.toString(36)
			.substr(2, 9)}`;

		if (!this.metrics.has(operation)) {
			this.metrics.set(operation, {
				count: 0,
				totalTime: 0,
				averageTime: 0,
				maxTime: 0,
				minTime: Infinity,
				history: [],
				lastMeasureTime: 0,
				errorCount: 0,
				metadata: {},
			});
		}

		return {
			measureId,
			operation,
			startTime,
			metadata,
		};
	}

	/**
	 * 结束性能测量（增强版）
	 */
	endMeasure(measureData, additionalMetadata = {}) {
		const endTime = performance.now();
		const duration = endTime - measureData.startTime;
		const operation = measureData.operation;

		const metric = this.metrics.get(operation);
		if (!metric) return duration;

		// 更新指标
		metric.count++;
		metric.totalTime += duration;
		metric.averageTime = metric.totalTime / metric.count;
		metric.maxTime = Math.max(metric.maxTime, duration);
		metric.minTime = Math.min(metric.minTime, duration);
		metric.lastMeasureTime = endTime;

		// 保存历史记录
		const historyEntry = {
			timestamp: endTime,
			duration,
			metadata: { ...measureData.metadata, ...additionalMetadata },
		};

		metric.history.push(historyEntry);
		if (metric.history.length > this.maxHistorySize) {
			metric.history.shift();
		}

		// 性能警告
		const threshold = this.thresholds[operation] || 16;
		if (duration > threshold && this.debugMode) {
			console.warn(
				`🐌 Performance Warning: ${operation} took ${duration.toFixed(
					2
				)}ms (threshold: ${threshold}ms)`,
				{
					duration,
					operation,
					metadata: historyEntry.metadata,
					suggestions: this.getOptimizationSuggestions(operation, duration),
				}
			);
		}

		// 更新调试显示
		if (this.debugMode && this.debugOverlay) {
			this.updateDebugDisplay();
		}

		return duration;
	}

	/**
	 * 获取优化建议
	 */
	getOptimizationSuggestions(operation, duration) {
		const suggestions = [];

		switch (operation) {
			case "position_update":
				if (duration > 20) {
					suggestions.push("考虑增加防抖延迟时间");
					suggestions.push("检查是否有不必要的DOM查询");
					suggestions.push("使用transform代替改变位置属性");
				}
				break;
			case "position_calculation_new":
				if (duration > 10) {
					suggestions.push("增加缓存命中率");
					suggestions.push("简化位置计算逻辑");
				}
				break;
			case "update_map_bounds":
				if (duration > 8) {
					suggestions.push("减少getBoundingClientRect调用频率");
					suggestions.push("缓存边界信息");
				}
				break;
		}

		return suggestions;
	}

	/**
	 * 设置调试覆盖层
	 */
	setupDebugOverlay() {
		if (!this.debugMode || typeof document === "undefined") return;

		this.debugOverlay = document.createElement("div");
		this.debugOverlay.id = "performance-debug-overlay";
		this.debugOverlay.style.cssText = `
            position: fixed;
            top: 10px;
            right: 10px;
            width: 300px;
            max-height: 400px;
            background: rgba(0, 0, 0, 0.9);
            color: #00ff00;
            font-family: 'Courier New', monospace;
            font-size: 11px;
            padding: 10px;
            border-radius: 5px;
            z-index: 10000;
            overflow-y: auto;
            display: none;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
        `;

		// 注释掉切换按钮的创建代码，移除📊按钮
		// const toggleButton = document.createElement("button");
		// toggleButton.textContent = "📊";
		// toggleButton.style.cssText = `
		//     position: fixed;
		//     top: 10px;
		//     right: 320px;
		//     width: 30px;
		//     height: 30px;
		//     background: rgba(0, 0, 0, 0.8);
		//     color: #00ff00;
		//     border: none;
		//     border-radius: 50%;
		//     cursor: pointer;
		//     z-index: 10001;
		//     font-size: 14px;
		// `;

		// toggleButton.onclick = () => {
		// 	const isVisible = this.debugOverlay.style.display !== "none";
		// 	this.debugOverlay.style.display = isVisible ? "none" : "block";
		// };

		document.body.appendChild(this.debugOverlay);
		// document.body.appendChild(toggleButton);
	}

	/**
	 * 更新调试显示
	 */
	updateDebugDisplay() {
		if (!this.debugOverlay) return;

		const content = ["=== 性能监控面板 ===", ""];

		for (const [operation, metric] of this.metrics.entries()) {
			const threshold = this.thresholds[operation] || 16;
			const status = metric.averageTime > threshold ? "🔴" : "🟢";
			const recent = metric.history.slice(-5);
			const recentAvg =
				recent.length > 0
					? recent.reduce((sum, entry) => sum + entry.duration, 0) /
					  recent.length
					: 0;

			content.push(`${status} ${operation}:`);
			content.push(`  总计: ${metric.count} 次`);
			content.push(`  平均: ${metric.averageTime.toFixed(2)}ms`);
			content.push(`  最近: ${recentAvg.toFixed(2)}ms`);
			content.push(`  最大: ${metric.maxTime.toFixed(2)}ms`);
			content.push(`  阈值: ${threshold}ms`);
			content.push("");
		}

		// 添加建议
		const suggestions = this.getPerformanceSuggestions();
		if (suggestions.length > 0) {
			content.push("=== 优化建议 ===");
			suggestions.forEach((suggestion) => {
				content.push(`💡 ${suggestion}`);
			});
			content.push("");
		}

		// 添加FPS监控
		content.push("=== 帧率信息 ===");
		content.push(`当前FPS: ${this.getCurrentFPS()}`);
		content.push(`目标FPS: 60`);

		this.debugOverlay.innerHTML = `<pre>${content.join("\n")}</pre>`;
	}

	/**
	 * 获取整体性能建议
	 */
	getPerformanceSuggestions() {
		const suggestions = [];
		const positionMetric = this.metrics.get("position_update");
		const boundsMetric = this.metrics.get("update_map_bounds");

		if (positionMetric && positionMetric.averageTime > 16) {
			suggestions.push("位置更新过慢，考虑优化计算逻辑");
		}

		if (boundsMetric && boundsMetric.count > 100) {
			suggestions.push("边界更新过于频繁，增加防抖时间");
		}

		const totalOperations = Array.from(this.metrics.values()).reduce(
			(sum, metric) => sum + metric.count,
			0
		);

		if (totalOperations > 1000) {
			suggestions.push("操作次数过多，检查是否有重复计算");
		}

		return suggestions;
	}

	/**
	 * 获取当前FPS
	 */
	getCurrentFPS() {
		if (!this.fpsCounter) {
			this.fpsCounter = new FPSCounter();
		}
		return this.fpsCounter.getCurrentFPS();
	}

	/**
	 * 导出性能报告
	 */
	exportReport() {
		const report = {
			timestamp: new Date().toISOString(),
			metrics: Object.fromEntries(this.metrics),
			summary: {
				totalOperations: Array.from(this.metrics.values()).reduce(
					(sum, metric) => sum + metric.count,
					0
				),
				averageTime:
					Array.from(this.metrics.values()).reduce(
						(sum, metric) => sum + metric.averageTime,
						0
					) / this.metrics.size,
				worstOperation: this.getWorstPerformingOperation(),
			},
			suggestions: this.getPerformanceSuggestions(),
		};

		return report;
	}

	/**
	 * 获取性能最差的操作
	 */
	getWorstPerformingOperation() {
		let worst = null;
		let maxRatio = 0;

		for (const [operation, metric] of this.metrics.entries()) {
			const threshold = this.thresholds[operation] || 16;
			const ratio = metric.averageTime / threshold;
			if (ratio > maxRatio) {
				maxRatio = ratio;
				worst = { operation, ratio, averageTime: metric.averageTime };
			}
		}

		return worst;
	}

	/**
	 * 重置所有指标
	 */
	reset() {
		this.metrics.clear();
		if (this.debugOverlay) {
			this.debugOverlay.innerHTML = "<pre>性能监控已重置</pre>";
		}
	}
}

/**
 * FPS计数器
 */
class FPSCounter {
	constructor() {
		this.fps = 0;
		this.frames = 0;
		this.startTime = performance.now();
		this.lastTime = this.startTime;
		this.requestId = null;
		this.start();
	}

	start() {
		const tick = () => {
			const now = performance.now();
			this.frames++;

			if (now - this.startTime >= 1000) {
				this.fps = Math.round((this.frames * 1000) / (now - this.startTime));
				this.frames = 0;
				this.startTime = now;
			}

			this.requestId = requestAnimationFrame(tick);
		};

		tick();
	}

	getCurrentFPS() {
		return this.fps;
	}

	stop() {
		if (this.requestId) {
			cancelAnimationFrame(this.requestId);
			this.requestId = null;
		}
	}
}

/**
 * 渲染时序优化器
 */
export class RenderScheduler {
	constructor() {
		this.taskQueue = [];
		this.isRunning = false;
		this.frameTime = 16; // 60fps
		this.priorityLevels = {
			IMMEDIATE: 0,
			HIGH: 1,
			NORMAL: 2,
			LOW: 3,
			IDLE: 4,
		};
	}

	/**
	 * 调度任务
	 */
	schedule(task, priority = this.priorityLevels.NORMAL, metadata = {}) {
		const taskItem = {
			task,
			priority,
			metadata,
			timestamp: performance.now(),
		};

		// 按优先级插入
		const insertIndex = this.taskQueue.findIndex(
			(item) => item.priority > priority
		);
		if (insertIndex === -1) {
			this.taskQueue.push(taskItem);
		} else {
			this.taskQueue.splice(insertIndex, 0, taskItem);
		}

		if (!this.isRunning) {
			this.start();
		}
	}

	/**
	 * 开始执行任务队列
	 */
	start() {
		if (this.isRunning) return;
		this.isRunning = true;
		this.processQueue();
	}

	/**
	 * 处理任务队列
	 */
	processQueue() {
		const frameStart = performance.now();

		while (this.taskQueue.length > 0) {
			const now = performance.now();

			// 检查是否还有时间执行任务
			if (now - frameStart >= this.frameTime - 2) {
				// 留2ms缓冲
				requestAnimationFrame(() => this.processQueue());
				return;
			}

			const taskItem = this.taskQueue.shift();
			try {
				taskItem.task();
			} catch (error) {
				console.error("Task execution error:", error, taskItem.metadata);
			}
		}

		this.isRunning = false;
	}

	/**
	 * 清空任务队列
	 */
	clear() {
		this.taskQueue = [];
		this.isRunning = false;
	}
}

// 全局实例
export const extendedPerformanceMonitor = new ExtendedPerformanceMonitor();
export const renderScheduler = new RenderScheduler();

/**
 * 调试工具函数
 */
export const debugUtils = {
	/**
	 * 可视化弹窗位置信息
	 */
	visualizePosition(position, device, bounds) {
		if (process.env.NODE_ENV !== "development") return;

		// 创建可视化标记
		const marker = document.createElement("div");
		marker.style.cssText = `
            position: fixed;
            left: ${position.x - 5}px;
            top: ${position.y - 5}px;
            width: 10px;
            height: 10px;
            background: red;
            border: 2px solid yellow;
            border-radius: 50%;
            z-index: 9999;
            pointer-events: none;
        `;

		document.body.appendChild(marker);

		// 添加设备信息标签
		const label = document.createElement("div");
		label.textContent = device.name || device.stationName || "Unknown";
		label.style.cssText = `
            position: fixed;
            left: ${position.x + 15}px;
            top: ${position.y - 20}px;
            background: rgba(0, 0, 0, 0.8);
            color: white;
            padding: 2px 6px;
            border-radius: 3px;
            font-size: 10px;
            z-index: 9999;
            pointer-events: none;
        `;

		document.body.appendChild(label);

		// 3秒后移除
		setTimeout(() => {
			marker.remove();
			label.remove();
		}, 3000);
	},

	/**
	 * 记录详细的性能信息（已优化输出）
	 */
	logPerformanceDetails(operation, duration, metadata = {}) {
		if (process.env.NODE_ENV !== "development") return;

		// 只记录重要的性能信息，不输出堆栈跟踪
		if (duration > 10 || operation === "error") {
			console.log(
				`⚠️ Performance Alert: ${operation} took ${duration.toFixed(2)}ms`
			);
			if (Object.keys(metadata).length > 0) {
				console.log(`📊 Context:`, metadata);
			}
		}
	},

	/**
	 * 导出性能数据到控制台
	 */
	exportToConsole() {
		const report = extendedPerformanceMonitor.exportReport();
		console.log("📈 Performance Report:", report);
		return report;
	},
};
