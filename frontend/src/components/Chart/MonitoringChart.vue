<template>
    <div class="monitoring-chart">
        <!-- 图表轮播区域 -->
        <div class="chart-carousel-section">
            <el-card class="carousel-card">
                <template #header>
                    <div class="carousel-header">
                        <span class="carousel-title">
                            <i class="fa fa-chart-line"></i>
                            {{ title }}
                        </span>
                        <!-- 轮播指示器 -->
                        <div v-if="enableCarousel" class="carousel-indicators">
                            <button v-for="(item, index) in chartItems" :key="item.code" class="indicator-dot"
                                :class="{ 'active': index === activeIndex }" @click="goToSlide(index)"
                                :title="item.name">
                                <i :class="['fa', item.icon]"></i>
                            </button>
                        </div>
                    </div>
                </template>

                <!-- 轮播图主体 -->
                <div class="carousel-wrapper" v-loading="loading" :style="{ height: chartHeight }">
                    <!-- 轮播容器 -->
                    <div class="carousel-container" ref="carouselContainer">
                        <!-- 轮播项目 -->
                        <div class="carousel-item" v-for="(item, index) in chartItems" :key="item.code" :class="{
                            'active': index === activeIndex
                        }" :style="getCarouselItemStyle(index)">
                            <!-- 图表卡片 -->
                            <div class="chart-card-wrapper">
                                <div class="chart-card-header">
                                    <div class="chart-info">
                                        <i :class="['fa', item.icon]" :style="{ color: item.color }"></i>
                                        <h3 class="chart-title">{{ item.name }}</h3>
                                        <span class="chart-unit">单位: {{ item.unit || '无' }}</span>
                                    </div>
                                    <div class="chart-meta">
                                        <span class="chart-index">{{ index + 1 }} / {{ chartItems.length }}</span>
                                    </div>
                                </div>
                                <div class="chart-content">
                                    <!-- 未选择站点时的提示信息 -->
                                    <div v-if="!hasStation" class="no-station-selected">
                                        <i class="fa fa-info-circle"></i>
                                        <p>请先选择监测站点查看图表数据</p>
                                    </div>
                                    <!-- 已选择站点但未搜索时的提示信息 -->
                                    <div v-else-if="!hasSearched" class="no-search-performed">
                                        <i class="fa fa-search"></i>
                                        <p>请点击搜索按钮查看图表数据</p>
                                    </div>
                                    <!-- 图表画布 - 只有选择站点且已搜索时才显示 -->
                                    <canvas :ref="el => setChartRef(el, index)" class="chart-canvas"
                                        :style="{ display: hasStation && hasSearched ? 'block' : 'none' }">
                                    </canvas>

                                    <!-- 图表缩放控制按钮 -->
                                    <div v-if="hasStation && hasSearched" class="chart-zoom-controls">
                                        <div class="zoom-button-group">
                                            <button class="zoom-btn zoom-in" @click="zoomIn(index)" title="放大">
                                                <i class="fa fa-plus"></i>
                                            </button>
                                            <button class="zoom-btn zoom-out" @click="zoomOut(index)" title="缩小">
                                                <i class="fa fa-minus"></i>
                                            </button>
                                            <button class="zoom-btn zoom-reset" @click="resetZoom(index)" title="重置缩放">
                                                <i class="fa fa-home"></i>
                                            </button>
                                            <button class="zoom-btn zoom-fit" @click="fitToWindow(index)" title="适应窗口">
                                                <i class="fa fa-expand"></i>
                                            </button>
                                        </div>
                                    </div>

                                    <!-- 数据密度提示 -->
                                    <div v-if="hasStation && hasSearched" class="chart-data-info">
                                        <div class="data-info-item">
                                            <i class="fa fa-database"></i>
                                            <span>数据点数: {{ getChartDataCount(index) }}</span>
                                        </div>
                                        <div class="data-info-item">
                                            <i class="fa fa-search-plus"></i>
                                            <span>缩放: {{ getChartZoomLevel(index) }}</span>
                                        </div>
                                    </div>

                                    <!-- 图表操作提示 -->
                                    <div v-if="hasStation && hasSearched" class="chart-controls-hint">
                                        <div class="hint-item">
                                            <i class="fa fa-mouse-pointer"></i>
                                            <span>滚轮缩放</span>
                                        </div>
                                        <div class="hint-item">
                                            <i class="fa fa-hand-paper-o"></i>
                                            <span>拖拽平移</span>
                                        </div>
                                        <div class="hint-item">
                                            <i class="fa fa-refresh"></i>
                                            <span>双击重置</span>
                                        </div>
                                        <div class="hint-item">
                                            <i class="fa fa-plus-square"></i>
                                            <span>按钮缩放</span>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- 水平滚动条 -->
                            <div v-if="showMinimap && hasStation && hasSearched" class="chart-minimap-container">
                                <!-- 缩略图画布 -->
                                <canvas :ref="el => setThumbnailChartRef(el, index)" class="thumbnail-chart"
                                    @click="handleMinimapClick($event, index)"></canvas>

                                <!-- 可视区域滑块 - VSCode风格 -->
                                <div class="viewport-slider" :ref="el => setViewportSliderRef(el, index)" :style="{
                                    left: `${getViewportPosition(index).left}%`,
                                    width: `${getViewportPosition(index).width}%`
                                }" @mousedown="startDragging($event, index)"
                                    @touchstart="startDragging($event, index)">
                                    <!-- 左侧调整手柄 - 视觉上不可见但可交互 -->
                                    <div class="resize-handle left"
                                        @mousedown.stop="startResizing('left', $event, index)"
                                        @touchstart.stop="startResizing('left', $event, index)"></div>
                                    <!-- 右侧调整手柄 - 视觉上不可见但可交互 -->
                                    <div class="resize-handle right"
                                        @mousedown.stop="startResizing('right', $event, index)"
                                        @touchstart.stop="startResizing('right', $event, index)"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </el-card>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, shallowRef, markRaw, watch } from 'vue'
import { ElMessage } from 'element-plus'
import Chart from 'chart.js/auto'
import zoomPlugin from 'chartjs-plugin-zoom'
import { formatDateTime } from '@/utils/shared/common'

// 注册Chart.js缩放插件
Chart.register(zoomPlugin)

/**
 * ----------------------------------------
 * 组件属性定义
 * ----------------------------------------
 */
const props = defineProps({
    // 图表数据
    chartData: {
        type: Object,
        default: () => ({ labels: [], values: [] })
    },
    // 图表项配置数组（支持多个图表）
    chartItems: {
        type: Array,
        required: true
        // 每项包含 code, name, unit, color, icon, description
    },
    // 当前选中的图表索引
    activeIndex: {
        type: Number,
        default: 0
    },
    // 图表标题
    title: {
        type: String,
        default: '监测数据'
    },
    // 图表加载状态
    loading: {
        type: Boolean,
        default: false
    },
    // 是否已执行搜索
    hasSearched: {
        type: Boolean,
        default: false
    },
    // 是否已选择监测站点
    hasStation: {
        type: Boolean,
        default: false
    },
    // 是否启用轮播功能
    enableCarousel: {
        type: Boolean,
        default: true
    },
    // 是否显示缩略图
    showMinimap: {
        type: Boolean,
        default: true
    },
    // 图表高度
    chartHeight: {
        type: String,
        default: '520px'
    }
})

/**
 * ----------------------------------------
 * 组件事件定义
 * ----------------------------------------
 */
const emit = defineEmits([
    'update:activeIndex', // 当前活动图表索引变更
    'chart-zoom',         // 图表缩放事件
    'chart-pan',          // 图表平移事件
    'chart-reset',        // 图表重置事件
    'chart-initialized'   // 图表初始化完成事件
])

/**
 * ----------------------------------------
 * 组件状态和引用
 * ----------------------------------------
 */
// 轮播图相关状态
const isAnimating = ref(false)

// 图表实例数组，为每个监测项目创建独立的图表
const chartInstances = shallowRef([])  // 使用shallowRef避免深度响应式
const chartRefs = shallowRef([])  // 使用shallowRef避免深度响应式

// 缩略图相关状态
const thumbnailChartInstances = shallowRef([]) // 缩略图实例数组
const thumbnailChartRefs = shallowRef([]) // 缩略图引用数组
const viewportSliderRefs = shallowRef([]) // 视口滑块引用数组

// 视口滑块拖拽状态
const isDragging = ref(false) // 是否正在拖拽
const dragStartX = ref(0) // 拖拽开始X坐标
const dragStartLeft = ref(0) // 拖拽开始左侧位置
const draggingChartIndex = ref(-1) // 当前拖拽的图表索引

// 滑块调整大小状态
const isResizing = ref(false) // 是否正在调整大小
const resizeHandle = ref(null) // 当前调整的手柄
const resizeStartX = ref(0) // 调整开始X坐标
const resizeStartWidth = ref(0) // 调整开始宽度
const resizeStartLeft = ref(0) // 调整开始左侧位置
const resizingChartIndex = ref(-1) // 当前调整的图表索引

// 图表缩放和视口状态
const chartZoomLevels = ref([]) // 存储每个图表的缩放级别
const viewportPositions = ref([]) // 存储每个图表的视口位置
const zoomUpdateTrigger = ref(0) // 响应式触发器，用于强制更新缩放比例显示

/**
 * ----------------------------------------
 * 生命周期和监听器
 * ----------------------------------------
 */
// 监听activeIndex变化
watch(() => props.activeIndex, (newIndex) => {
    goToSlide(newIndex)
})

// 监听chartData变化
watch(() => props.chartData, () => {
    if (props.hasStation && props.hasSearched) {
        loadChartData(props.activeIndex)
    }
}, { deep: true })

// 页面初始化
onMounted(async () => {
    try {
        // 初始化图表
        await initAllCharts()
        // 通知父组件图表已初始化
        emit('chart-initialized')
    } catch (error) {
        console.error('图表初始化失败:', error)
        ElMessage.error('图表初始化失败，请刷新重试')
    }
})

// 组件卸载时清理
onUnmounted(() => {
    // 销毁图表实例
    chartInstances.value.forEach((chart, index) => {
        if (chart) {
            try {
                chart.destroy()
                chartInstances.value[index] = null
            } catch (error) {
                console.error(`销毁图表 ${index} 实例时出错:`, error)
            }
        }
    })

    // 销毁缩略图实例
    thumbnailChartInstances.value.forEach((chart, index) => {
        if (chart) {
            try {
                chart.destroy()
                thumbnailChartInstances.value[index] = null
            } catch (error) {
                console.error(`销毁缩略图 ${index} 实例时出错:`, error)
            }
        }
    })

    // 移除全局事件监听器
    document.removeEventListener('mousemove', handleDrag)
    document.removeEventListener('mouseup', stopDragging)
    document.removeEventListener('touchmove', handleDrag)
    document.removeEventListener('touchend', stopDragging)

    // 移除调整大小相关的事件监听器
    document.removeEventListener('mousemove', handleResize)
    document.removeEventListener('mouseup', stopResizing)
    document.removeEventListener('touchmove', handleResize)
    document.removeEventListener('touchend', stopResizing)
})

/**
 * ----------------------------------------
 * 轮播图控制功能
 * ----------------------------------------
 */
// 跳转到指定幻灯片
const goToSlide = async (index) => {
    if (isAnimating.value || index === props.activeIndex) return

    isAnimating.value = true
    emit('update:activeIndex', index)

    // 等待动画完成
    await new Promise(resolve => {
        setTimeout(() => {
            isAnimating.value = false
            resolve()
        }, 600) // 动画时长
    })

    // 只有选择了站点才加载图表数据
    if (chartInstances.value[index] && props.hasStation && props.hasSearched && !props.loading) {
        await loadChartDataSafely(index)
    }
}

// 获取轮播项样式
const getCarouselItemStyle = (index) => {
    const offset = index - props.activeIndex
    return {
        transform: `translateX(${offset * 100}%)`,
        opacity: index === props.activeIndex ? 1 : 0.3,
        zIndex: index === props.activeIndex ? 10 : 1
    }
}

/**
 * ----------------------------------------
 * 图表初始化功能
 * ----------------------------------------
 */
// 单个图表初始化函数
const initSingleChart = async (index) => {
    // 确保DOM已更新
    await nextTick();

    try {
        const chartRef = chartRefs.value[index];
        if (!chartRef) {
            console.warn(`图表引用不存在，索引: ${index}`);
            return null;
        }

        const ctx = chartRef.getContext('2d');
        if (!ctx) return null;

        // 销毁已存在的图表实例
        if (chartInstances.value[index]) {
            chartInstances.value[index].destroy();
        }

        // 获取容器宽度
        const containerWidth = chartRef.parentElement?.clientWidth || 800;
        const item = props.chartItems[index];

        // 使用默认配置
        const defaultPointConfig = getLineStyleConfig();

        // 创建基础数据集配置
        const baseDataset = {
            label: `${item.name} (${item.unit})`,
            data: [],
            borderColor: item.color,
            backgroundColor: `${item.color}20`,
            fill: true,
            pointBackgroundColor: item.color,
            pointBorderColor: '#fff',
            pointBorderWidth: 2,
            pointHoverBackgroundColor: item.color,
            pointHoverBorderColor: '#fff',
            pointHoverBorderWidth: 2
        };

        // 应用点显示配置
        const configuredDataset = configureChartDataset(baseDataset, defaultPointConfig, `${item.name} (${item.unit})`);

        // 创建图表实例
        const chart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: [],
                datasets: [configuredDataset]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                resizeDelay: 100,
                animation: { duration: 200, easing: 'easeOutCubic' },
                parsing: { xAxisKey: 'x', yAxisKey: 'y' },
                normalized: true,
                spanGaps: false,
                interaction: { intersect: false, mode: 'index' },
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        backgroundColor: 'rgba(255, 255, 255, 0.95)',
                        titleColor: '#333',
                        bodyColor: '#333',
                        borderColor: item.color,
                        borderWidth: 1,
                        cornerRadius: 8,
                        padding: 12,
                        displayColors: false,
                        callbacks: {
                            title: function (context) {
                                const label = context[0].label;
                                return `时间: ${formatDateTime(label)}`;
                            },
                            label: function (context) {
                                const value = context.raw;
                                const formattedValue = typeof value === 'number' ?
                                    parseFloat(value.toFixed(3)) : value;
                                return `${item.name}: ${formattedValue} ${item.unit}`;
                            }
                        }
                    },
                    zoom: {
                        zoom: {
                            wheel: { enabled: true, speed: 0.1, modifierKey: null },
                            pinch: { enabled: true },
                            mode: 'x',
                            onZoomComplete: ({ chart }) => {
                                if (chart === chartInstances.value[index]) {
                                    const xScale = chart.scales.x;
                                    const min = xScale.min || 0;
                                    const max = xScale.max || chart.data.labels.length;
                                    const visibleRange = max - min;
                                    const totalRange = chart.data.labels.length;

                                    if (totalRange > 0) {
                                        // 更新缩放级别状态
                                        chartZoomLevels.value[index] = totalRange / visibleRange;

                                        // 更新触发器强制更新UI显示
                                        zoomUpdateTrigger.value += 1;

                                        // 同步滑块位置
                                        syncViewportSliderWithChart(index);

                                        // 触发缩放事件
                                        emit('chart-zoom', {
                                            chartIndex: index,
                                            zoomLevel: chartZoomLevels.value[index],
                                            visibleRange: { min, max }
                                        });
                                    }
                                }
                            },
                            // 添加缩放限制逻辑
                            onZoom: ({ chart }) => {
                                if (chart === chartInstances.value[index]) {
                                    const xScale = chart.scales.x;
                                    const min = xScale.min || 0;
                                    const max = xScale.max || chart.data.labels.length;
                                    const visibleRange = max - min;

                                    // 如果可见数据点少于等于15个，阻止进一步缩放
                                    if (visibleRange <= 15) {
                                        return false; // 阻止缩放
                                    }
                                    return true; // 允许缩放
                                }
                                return true;
                            }
                        },
                        pan: {
                            enabled: true,
                            mode: 'x',
                            onPanComplete: ({ chart }) => {
                                if (chart === chartInstances.value[index]) {
                                    // 更新触发器强制更新UI显示
                                    zoomUpdateTrigger.value += 1;

                                    // 平移完成后同步滑块位置
                                    syncViewportSliderWithChart(index);

                                    // 触发平移事件
                                    const xScale = chart.scales.x;
                                    emit('chart-pan', {
                                        chartIndex: index,
                                        visibleRange: {
                                            min: xScale.min || 0,
                                            max: xScale.max || chart.data.labels.length
                                        }
                                    });
                                }
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        display: true,
                        title: {
                            display: true,
                            text: '时间',
                            color: '#666',
                            font: { size: 12, weight: 'bold' }
                        },
                        grid: { display: false },
                        ticks: {
                            color: '#666',
                            font: { size: 11 },
                            maxRotation: 60,
                            minRotation: 30,
                            maxTicksLimit: getMaxTicksLimit(containerWidth, 'hour'),
                            callback: function (value) {
                                const label = this.getLabelForValue(value);
                                return formatDateTime(label, { format: 'hour', defaultValue: label });
                            }
                        }
                    },
                    y: {
                        display: true,
                        title: {
                            display: true,
                            text: `${item.name} (${item.unit})`,
                            color: '#666',
                            font: { size: 12, weight: 'bold' }
                        },
                        grid: {
                            color: 'rgba(0, 0, 0, 0.05)',
                            lineWidth: 1
                        },
                        ticks: {
                            color: '#666',
                            font: { size: 11 },
                            callback: function (value) {
                                return typeof value === 'number' ?
                                    parseFloat(value.toFixed(3)) : value;
                            }
                        }
                    }
                }
            }
        });

        // 使用markRaw避免Vue响应式处理
        chartInstances.value[index] = markRaw(chart);

        // 初始化缩略图
        await initThumbnailChart(index);

        // 如果已选择站点且已执行搜索，加载数据
        if (props.hasStation && props.hasSearched) {
            loadChartData(index);
        }

        return chart;
    } catch (error) {
        console.error(`初始化图表 ${index} 失败:`, error);
        ElMessage.error(`初始化图表失败`);
        return null;
    }
}

// 初始化所有图表
const initAllCharts = async () => {
    await nextTick();

    // 逐个初始化图表实例，避免资源竞争
    for (let i = 0; i < props.chartItems.length; i++) {
        await initSingleChart(i);
        // 添加延迟，确保每个图表完全初始化
        await new Promise(resolve => setTimeout(resolve, 50));
    }

    // 确保所有图表初始化完成后，如果选择了站点才加载数据
    await nextTick();
    await new Promise(resolve => setTimeout(resolve, 100));

    // 只有选择了站点才加载数据
    if (props.hasStation && props.hasSearched) {
        await loadChartData(props.activeIndex);
    }
}

/**
 * ----------------------------------------
 * 图表数据加载功能
 * ----------------------------------------
 */
// 安全的图表数据加载函数
const loadChartData = async (chartIndex) => {
    try {
        const chart = chartInstances.value[chartIndex];

        // 如果未选择站点或图表未初始化，清空或跳过
        if (!chart || !props.hasStation) {
            if (chart && chart.data) {
                chart.data.labels = [];
                chart.data.datasets[0].data = [];
                const item = props.chartItems[chartIndex];
                configureChartDataset(chart.data.datasets[0], {}, `${item.name} (${item.unit}) - 请选择监测站点`);
                chart.update('none');
            }
            return;
        }

        // 直接使用传入的图表数据
        const labels = props.chartData.labels || [];
        const values = props.chartData.values || [];

        // 获取容器宽度
        const containerWidth = chartRefs.value[chartIndex]?.parentElement?.clientWidth || 800;

        // 固定使用线条样式，不根据数据密度调整
        const pointConfig = getLineStyleConfig();

        // 更新图表数据
        chart.data.labels = labels;
        chart.data.datasets[0].data = values;

        // 使用辅助函数更新数据集配置
        const item = props.chartItems[chartIndex];
        configureChartDataset(
            chart.data.datasets[0],
            pointConfig,
            `${item.name} (${item.unit})`
        );

        // 使用辅助函数更新X轴配置
        updateChartXAxisConfig(chart, containerWidth);

        // 更新图表 - 不触发动画
        chart.update('none');

        // 设置合适的缩放级别，显示最左侧的15个数据点
        setChartOptimalZoom(chart, labels.length, chartIndex);

        // 更新缩略图数据
        updateThumbnailChart(chartIndex);

        // 初始化滑块位置
        syncViewportSliderWithChart(chartIndex);

        // 等待下一个 tick 更新 DOM
        await nextTick();
    } catch (error) {
        console.error('加载图表数据失败:', error);
        ElMessage.error('加载图表数据失败');
    }
}

// 安全的图表数据加载函数
const loadChartDataSafely = async (chartIndex) => {
    try {
        await loadChartData(chartIndex);
    } catch (error) {
        console.error(`加载图表 ${chartIndex} 数据失败:`, error);
        ElMessage.error('加载图表数据失败');

        // 触发UI更新以显示错误状态
        zoomUpdateTrigger.value += 1;
    }
}

/**
 * ----------------------------------------
 * 图表配置辅助功能
 * ----------------------------------------
 */
// 统一的图表配置辅助函数
const configureChartDataset = (dataset, pointConfig, customLabel = null) => {
    // 基础配置
    if (pointConfig) {
        dataset.borderWidth = pointConfig.borderWidth;
        dataset.tension = pointConfig.tension;
        dataset.pointRadius = pointConfig.pointRadius;
        dataset.pointHoverRadius = pointConfig.pointHoverRadius;
    }

    // 如果提供了自定义标签，则更新标签
    if (customLabel !== null) {
        dataset.label = customLabel;
    }

    return dataset;
}

// 更新图表X轴配置的辅助函数
const updateChartXAxisConfig = (chart, containerWidth, interval = 'hour') => {
    if (chart.options && chart.options.scales && chart.options.scales.x && chart.options.scales.x.ticks) {
        chart.options.scales.x.ticks.maxTicksLimit = getMaxTicksLimit(containerWidth, interval);
        chart.options.scales.x.ticks.callback = function (value) {
            const label = this.getLabelForValue(value);
            return formatDateTime(label);
        }
    }
}

// 设置图表最佳缩放级别的辅助函数
const setChartOptimalZoom = (chart, dataCount, chartIndex) => {
    if (!chart || !chart.options || !chart.options.scales || !chart.options.scales.x) {
        return 1.0;
    }

    // 重置任何现有的缩放状态
    if (chart.resetZoom) {
        chart.resetZoom('none');
    }

    if (dataCount <= 15) {
        // 如果数据点少于等于15个，显示全部数据
        chart.options.scales.x.min = 0;
        chart.options.scales.x.max = dataCount;

        // 更新缩放级别状态
        chartZoomLevels.value[chartIndex] = 1.0;

        // 更新图表但不触发动画
        chart.update('none');
        return 1.0;
    } else {
        // 如果数据点多于15个，只显示前15个
        chart.options.scales.x.min = 0;
        chart.options.scales.x.max = 15;

        // 计算并更新缩放级别状态
        // 缩放级别 = 总数据点 / 可见数据点
        chartZoomLevels.value[chartIndex] = dataCount / 15;

        // 更新图表但不触发动画
        chart.update('none');

        // 手动同步滑块位置，确保与可见范围一致
        setTimeout(() => syncViewportSliderWithChart(chartIndex), 50);

        return chartZoomLevels.value[chartIndex];
    }
}

/**
 * ----------------------------------------
 * 图表状态获取功能
 * ----------------------------------------
 */
// 获取图表缩放比例
const getChartZoomLevel = (chartIndex) => {
    // 确保缩放级别数组有足够的长度
    while (chartZoomLevels.value.length <= chartIndex) {
        chartZoomLevels.value.push(1.0)
    }

    const zoomLevel = chartZoomLevels.value[chartIndex] || 1.0
    return `${Math.round(zoomLevel * 100)}%`
}

// 获取图表数据点数量（显示原始数据量）
const getChartDataCount = (chartIndex) => {
    // 检查chartIndex有效性
    if (chartIndex < 0 || chartIndex >= chartInstances.value.length) {
        return 0;
    }

    const chart = chartInstances.value[chartIndex];
    // 使用可选链操作符更安全地访问属性
    return chart?.data?.labels?.length || 0;
}

/**
 * ----------------------------------------
 * 图表样式配置功能
 * ----------------------------------------
 */
// 获取线条样式配置
const getLineStyleConfig = () => {
    return {
        pointRadius: 0,        // 不显示数据点
        pointHoverRadius: 2,   // 鼠标悬停时显示小点
        borderWidth: 1.5,      // 固定线宽
        tension: 0.1           // 轻微平滑
    }
}

// 计算基于数据点数量的最大缩放级别
const calculateMaxZoomLevel = (dataCount) => {
    if (!dataCount || dataCount <= 15) return 1.0;
    return dataCount / 15; // 最多显示15个数据点
}

// 计算图表最大显示标签数量
const getMaxTicksLimit = (containerWidth, interval) => {
    const baseWidth = containerWidth || 800

    // 根据间隔类型确定每个标签的像素宽度
    const pixelsPerTick = {
        'hour': 80,
        'day': 100,
        'month': 120
    }[interval] || 80

    // 计算并返回最大标签数量，最少为5个
    return Math.max(5, Math.floor(baseWidth / pixelsPerTick))
}

/**
 * ----------------------------------------
 * 引用设置功能
 * ----------------------------------------
 */
// 设置图表引用的辅助函数
const setChartRef = (el, index) => {
    if (el) {
        // 确保数组长度足够
        while (chartRefs.value.length <= index) {
            chartRefs.value.push(null)
        }
        chartRefs.value[index] = el
    }
}

/**
 * ----------------------------------------
 * 缩略图功能
 * ----------------------------------------
 */
// 初始化缩略图
const initThumbnailChart = async (index) => {
    // 确保DOM已更新
    await nextTick()

    try {
        const thumbnailRef = thumbnailChartRefs.value[index]
        if (!thumbnailRef) {
            // 静默返回，这是预期行为，因为在初始化时缩略图可能不可见
            return null
        }

        const ctx = thumbnailRef.getContext('2d')
        if (!ctx) {
            console.warn(`无法获取缩略图绘图上下文，索引: ${index}`)
            return null
        }

        // 销毁已存在的缩略图实例
        if (thumbnailChartInstances.value[index]) {
            try {
                thumbnailChartInstances.value[index].destroy()
            } catch (error) {
                console.error(`销毁缩略图 ${index} 实例失败:`, error)
            }
        }

        const item = props.chartItems[index]

        // 创建VSCode风格的数据集配置
        const thumbnailDataset = {
            label: item.name,
            data: [],
            borderColor: item.color,
            backgroundColor: `${item.color}10`, // 更淡的背景色
            fill: true,
            borderWidth: 1,
            pointRadius: 0, // 不显示点
            tension: 0, // 不平滑，显示原始数据形状
            borderDash: [], // 实线
            borderJoinStyle: 'round'
        }

        // 创建缩略图实例
        const thumbnailChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: [],
                datasets: [thumbnailDataset]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                animation: false,
                elements: {
                    line: {
                        tension: 0 // 直线连接，不平滑
                    }
                },
                plugins: {
                    legend: { display: false },
                    tooltip: { enabled: false },
                    decimation: {
                        enabled: true,
                        algorithm: 'min-max' // 使用最小-最大值抽样算法
                    }
                },
                scales: {
                    x: {
                        display: false,
                        grid: { display: false },
                        ticks: { display: false }
                    },
                    y: {
                        display: false,
                        grid: { display: false },
                        ticks: { display: false },
                        beginAtZero: false
                    }
                },
                events: [] // 禁用所有事件
            }
        })

        // 使用markRaw避免Vue响应式处理
        thumbnailChartInstances.value[index] = markRaw(thumbnailChart)

        return thumbnailChart
    } catch (error) {
        console.error(`初始化缩略图 ${index} 失败:`, error)
        return null
    }
}

// 更新缩略图数据
const updateThumbnailChart = (chartIndex) => {
    const mainChart = chartInstances.value[chartIndex]
    const thumbnailChart = thumbnailChartInstances.value[chartIndex]

    if (!mainChart || !thumbnailChart || !mainChart.data || !mainChart.data.labels) return

    // 复制主图表数据到缩略图 - 使用展开运算符创建新数组，避免直接引用
    thumbnailChart.data.labels = [...mainChart.data.labels]
    thumbnailChart.data.datasets[0].data = [...mainChart.data.datasets[0].data]

    // 更新缩略图但不触发动画
    thumbnailChart.update('none')
}

// 同步主图表缩放状态到缩略图滑块
const syncViewportSliderWithChart = (chartIndex) => {
    const chart = chartInstances.value[chartIndex]
    if (!chart || !chart.scales || !chart.scales.x) return

    const min = chart.scales.x.min !== undefined ? chart.scales.x.min : 0
    const max = chart.scales.x.max !== undefined ? chart.scales.x.max : chart.data.labels.length
    const totalPoints = chart.data.labels.length

    if (totalPoints <= 0) return

    // 确保视口位置数组有足够的长度
    while (viewportPositions.value.length <= chartIndex) {
        viewportPositions.value.push({ left: 0, width: 100 })
    }

    // 计算滑块位置和宽度
    viewportPositions.value[chartIndex].left = (min / totalPoints) * 100
    viewportPositions.value[chartIndex].width = ((max - min) / totalPoints) * 100

    // 确保最小宽度
    if (viewportPositions.value[chartIndex].width < 5) {
        viewportPositions.value[chartIndex].width = 5
    }
}

/**
 * ----------------------------------------
 * 缩略图交互功能
 * ----------------------------------------
 */
// 点击缩略图直接跳转到对应位置
const handleMinimapClick = (event, chartIndex) => {
    if (isDragging.value || isResizing.value) return
    if (!thumbnailChartRefs.value[chartIndex]) return

    const container = thumbnailChartRefs.value[chartIndex].parentElement
    const containerWidth = container.clientWidth
    const clickX = event.offsetX
    const clickPercent = (clickX / containerWidth) * 100

    // 计算点击位置为中心的可视区域
    const halfWidth = viewportPositions.value[chartIndex].width / 2
    let newLeft = clickPercent - halfWidth

    // 确保不超出边界
    newLeft = Math.max(0, Math.min(100 - viewportPositions.value[chartIndex].width, newLeft))

    // 更新滑块位置
    viewportPositions.value[chartIndex].left = newLeft

    // 同步更新主图表
    updateMainChartViewport(chartIndex)
}

/**
 * ----------------------------------------
 * 滑块拖拽功能
 * ----------------------------------------
 */
// 开始拖拽滑块
const startDragging = (event, chartIndex) => {
    if (isDragging.value || isResizing.value) return

    // 阻止事件冒泡和默认行为
    event.stopPropagation()
    event.preventDefault()

    isDragging.value = true
    draggingChartIndex.value = chartIndex

    // 记录初始位置
    if (event.type === 'touchstart') {
        dragStartX.value = event.touches[0].clientX
    } else {
        dragStartX.value = event.clientX
    }

    dragStartLeft.value = viewportPositions.value[chartIndex]?.left || 0

    // 添加 VSCode 风格的拖拽时视觉反馈
    const slider = viewportSliderRefs.value[chartIndex]
    if (slider) {
        slider.style.transition = 'none' // 拖拽时禁用过渡效果
        slider.classList.add('dragging')
    }

    // 添加事件监听器
    document.addEventListener('mousemove', handleDrag)
    document.addEventListener('mouseup', stopDragging)
    document.addEventListener('touchmove', handleDrag)
    document.addEventListener('touchend', stopDragging)
}

// 停止拖拽滑块
const stopDragging = () => {
    if (!isDragging.value) return

    // 保存当前索引供后续使用
    const chartIndex = draggingChartIndex.value

    // 清理状态
    isDragging.value = false
    draggingChartIndex.value = -1

    // 移除事件监听器
    document.removeEventListener('mousemove', handleDrag)
    document.removeEventListener('mouseup', stopDragging)
    document.removeEventListener('touchmove', handleDrag)
    document.removeEventListener('touchend', stopDragging)

    // 恢复滑块样式
    const slider = viewportSliderRefs.value[chartIndex]
    if (slider) {
        slider.style.transition = '' // 恢复过渡效果
        slider.classList.remove('dragging')
    }

    // 最后更新一次图表视口，确保拖拽结束后图表状态与滑块位置一致
    updateMainChartViewport(chartIndex);
}

// 处理拖拽移动
const handleDrag = (event) => {
    if (!isDragging.value) return
    event.preventDefault()

    const chartIndex = draggingChartIndex.value
    if (chartIndex < 0 || chartIndex >= viewportPositions.value.length) return

    // 获取当前位置
    const clientX = event.type.includes('touch') ? event.touches[0].clientX : event.clientX

    // 获取滑块容器宽度
    const container = thumbnailChartRefs.value[chartIndex].parentElement
    const containerWidth = container.clientWidth

    // 计算拖拽距离对应的百分比
    const deltaX = clientX - dragStartX.value
    const deltaPercent = (deltaX / containerWidth) * 100

    // 计算新的位置，确保不超出边界
    let newLeft = dragStartLeft.value + deltaPercent
    const width = viewportPositions.value[chartIndex].width
    newLeft = Math.max(0, Math.min(100 - width, newLeft))

    // 更新滑块位置
    viewportPositions.value[chartIndex] = {
        ...viewportPositions.value[chartIndex],
        left: newLeft
    }

    // 同步更新主图表的可见范围 - VSCode风格的即时更新
    requestAnimationFrame(() => updateMainChartViewport(chartIndex))
}

/**
 * ----------------------------------------
 * 滑块调整大小功能
 * ----------------------------------------
 */
// 开始调整滑块大小
const startResizing = (handle, event, chartIndex) => {
    if (isDragging.value || isResizing.value) return

    // 阻止事件冒泡和默认行为
    event.stopPropagation()
    event.preventDefault()

    isResizing.value = true
    resizeHandle.value = handle
    resizingChartIndex.value = chartIndex

    // 记录初始位置和尺寸
    if (event.type === 'touchstart') {
        resizeStartX.value = event.touches[0].clientX
    } else {
        resizeStartX.value = event.clientX
    }

    resizeStartLeft.value = viewportPositions.value[chartIndex]?.left || 0
    resizeStartWidth.value = viewportPositions.value[chartIndex]?.width || 100

    // 添加 VSCode 风格的调整大小时视觉反馈
    const slider = viewportSliderRefs.value[chartIndex]
    if (slider) {
        slider.style.transition = 'none' // 调整大小时禁用过渡效果
        slider.classList.add('resizing')
        slider.classList.add(`resizing-${handle}`)
    }

    // 添加事件监听器
    document.addEventListener('mousemove', handleResize)
    document.addEventListener('mouseup', stopResizing)
    document.addEventListener('touchmove', handleResize)
    document.addEventListener('touchend', stopResizing)
}

// 停止调整滑块大小
const stopResizing = () => {
    if (!isResizing.value) return

    // 保存当前索引和手柄供后续使用
    const chartIndex = resizingChartIndex.value
    const currentHandle = resizeHandle.value

    isResizing.value = false
    resizingChartIndex.value = -1
    resizeHandle.value = null

    // 恢复滑块样式
    const slider = viewportSliderRefs.value[chartIndex]
    if (slider) {
        slider.style.transition = '' // 恢复过渡效果
        slider.classList.remove('resizing')
        slider.classList.remove(`resizing-${currentHandle}`)
    }

    // 移除事件监听器
    document.removeEventListener('mousemove', handleResize)
    document.removeEventListener('mouseup', stopResizing)
    document.removeEventListener('touchmove', handleResize)
    document.removeEventListener('touchend', stopResizing)
}

// 处理调整大小移动
const handleResize = (event) => {
    if (!isResizing.value) return
    event.preventDefault()

    const chartIndex = resizingChartIndex.value
    if (chartIndex < 0 || chartIndex >= viewportPositions.value.length) return

    const container = thumbnailChartRefs.value[chartIndex].parentElement
    const containerWidth = container.clientWidth
    const clientX = event.type.includes('touch') ? event.touches[0].clientX : event.clientX
    const deltaX = clientX - resizeStartX.value
    const deltaPercent = (deltaX / containerWidth) * 100

    // VSCode风格的平滑调整大小
    if (resizeHandle.value === 'left') {
        // 调整左侧手柄 - 更改left和width
        const newLeft = Math.max(0, resizeStartLeft.value + deltaPercent)
        const newWidth = Math.max(5, resizeStartWidth.value - (newLeft - resizeStartLeft.value))

        // 确保不超出右边界
        if (newLeft + newWidth <= 100) {
            viewportPositions.value[chartIndex].left = newLeft
            viewportPositions.value[chartIndex].width = newWidth
        }
    } else {
        // 调整右侧手柄 - 只更改width
        const newWidth = Math.max(5, Math.min(100 - resizeStartLeft.value, resizeStartWidth.value + deltaPercent))
        viewportPositions.value[chartIndex].width = newWidth
    }

    // VSCode风格的即时更新
    requestAnimationFrame(() => updateMainChartViewport(chartIndex))
}

// 设置缩略图引用
const setThumbnailChartRef = (el, index) => {
    if (el) {
        // 确保数组长度足够
        while (thumbnailChartRefs.value.length <= index) {
            thumbnailChartRefs.value.push(null)
        }
        thumbnailChartRefs.value[index] = el
    }
}

// 设置视口滑块引用
const setViewportSliderRef = (el, index) => {
    if (el) {
        // 确保数组长度足够
        while (viewportSliderRefs.value.length <= index) {
            viewportSliderRefs.value.push(null)
        }
        viewportSliderRefs.value[index] = el
    }
}

/**
 * ----------------------------------------
 * 视口管理功能
 * ----------------------------------------
 */
// 获取视口位置
const getViewportPosition = (chartIndex) => {
    // 确保视口位置数组有足够的长度
    while (viewportPositions.value.length <= chartIndex) {
        viewportPositions.value.push({ left: 0, width: 100 })
    }

    return viewportPositions.value[chartIndex] || { left: 0, width: 100 }
}

// 更新主图表的可见范围
const updateMainChartViewport = (chartIndex) => {
    const chart = chartInstances.value[chartIndex]
    if (!chart || !chart.data || !chart.data.labels) return

    const totalDataPoints = chart.data.labels.length

    // 确保视口位置数组有足够的长度
    while (viewportPositions.value.length <= chartIndex) {
        viewportPositions.value.push({ left: 0, width: 100 })
    }

    // 根据滑块位置计算主图表的可见范围
    const viewportPosition = viewportPositions.value[chartIndex]
    const startIndex = Math.floor((viewportPosition.left / 100) * totalDataPoints)
    const visibleCount = Math.ceil((viewportPosition.width / 100) * totalDataPoints)
    const endIndex = Math.min(startIndex + visibleCount, totalDataPoints)

    // 设置图表的可见范围
    chart.options.scales.x.min = startIndex
    chart.options.scales.x.max = endIndex

    // 更新缩放级别状态
    if (visibleCount > 0) {
        chartZoomLevels.value[chartIndex] = totalDataPoints / visibleCount
    }

    // 更新图表
    chart.update('none')
}

/**
 * ----------------------------------------
 * 图表缩放控制功能
 * ----------------------------------------
 */
// 统一的图表缩放管理函数
const updateChartZoom = (zoomFactor, mode = 'set', chartIndex) => {
    const chart = chartInstances.value[chartIndex]
    if (!chart) return false

    let newZoomLevel = zoomFactor

    // 根据模式计算新的缩放级别
    if (mode === 'multiply') {
        // 确保数组有足够长度
        while (chartZoomLevels.value.length <= chartIndex) {
            chartZoomLevels.value.push(1.0)
        }
        const currentLevel = chartZoomLevels.value[chartIndex] || 1.0
        newZoomLevel = currentLevel * zoomFactor
    }

    // 计算基于数据点的最大缩放级别
    const totalDataPoints = chart.data?.labels?.length || 0
    const maxZoomLevel = calculateMaxZoomLevel(totalDataPoints)

    // 限制缩放范围在合理区间 (下限保持0.2，上限基于数据点)
    newZoomLevel = Math.max(0.2, Math.min(maxZoomLevel, newZoomLevel))

    // 更新状态
    while (chartZoomLevels.value.length <= chartIndex) {
        chartZoomLevels.value.push(1.0)
    }
    chartZoomLevels.value[chartIndex] = newZoomLevel

    // 应用缩放到图表
    if (chart.resetZoom && mode === 'set') {
        chart.resetZoom('none')
        setTimeout(() => {
            chart.zoom(newZoomLevel)
            // 同步滑块位置
            syncViewportSliderWithChart(chartIndex)
        }, 50)
    } else if (chart.zoom && mode === 'multiply') {
        chart.zoom(zoomFactor)
        // 同步滑块位置
        setTimeout(() => syncViewportSliderWithChart(chartIndex), 50)
    }

    // 触发缩放事件
    emit('chart-zoom', {
        chartIndex,
        zoomLevel: newZoomLevel,
        visibleRange: {
            min: chart.scales.x.min || 0,
            max: chart.scales.x.max || chart.data.labels.length
        }
    })

    return newZoomLevel
}

// 缩放控制函数
const zoomIn = (chartIndex) => updateChartZoom(1.5, 'multiply', chartIndex)
const zoomOut = (chartIndex) => updateChartZoom(0.7, 'multiply', chartIndex)
const resetZoom = (chartIndex) => {
    const chart = chartInstances.value[chartIndex]
    if (chart && chart.resetZoom) {
        chart.resetZoom('active')
        updateChartZoom(1.0, 'set', chartIndex)

        // 触发重置事件
        emit('chart-reset', { chartIndex })
    }
}
const fitToWindow = (chartIndex) => {
    const chart = chartInstances.value[chartIndex]
    if (!chart || !chart.data || !chart.data.labels) return

    // 使用固定缩放级别1.0，不再基于数据量动态设置
    const fixedFitLevel = 1.0;
    // 确保不超过基于数据点的最大缩放级别
    const dataCount = chart.data.labels.length;
    const maxZoomLevel = calculateMaxZoomLevel(dataCount);
    const fitLevel = Math.min(fixedFitLevel, maxZoomLevel);

    updateChartZoom(fitLevel, 'set', chartIndex);
}
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;



/**
 * ----------------------------------------
 * 响应式断点使用全局mixins
 * ----------------------------------------
 */
// 使用全局响应式混入：
// tablet(767px) → @include respond-to(sm) (768px以下)  
// mobile(480px) → @include respond-to(xs) (480px以下)



/**
 * ----------------------------------------
 * 主组件样式
 * ----------------------------------------
 */
.monitoring-chart {

    /**
     * 图表轮播区域样式
     */
    .chart-carousel-section {
        margin-bottom: 20px;
    }

    .carousel-card {

        /**
         * 轮播头部样式
         */
        .carousel-header {
            display: flex;
            justify-content: center;
            align-items: center;
            position: relative;

            .carousel-title {
                font-size: 18px;
                font-weight: 600;
                color: var(--text-primary);
                display: flex;
                align-items: center;
                position: absolute;
                left: 0;

                .fa {
                    color: var(--primary-color);
                    margin-right: 8px;
                    font-size: 20px;
                }
            }

            /**
             * 头部指示器样式
             */
            .carousel-indicators {
                display: flex;
                gap: 10px;
                align-items: center;

                .indicator-dot {
                    width: 36px;
                    height: 36px;
                    border-radius: 50%;
                    border: 2px solid rgba(64, 158, 255, 0.2);
                    background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 250, 252, 0.95) 100%);
                    cursor: pointer;
                    transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    backdrop-filter: blur(15px);
                    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
                    position: relative;
                    overflow: hidden;

                    &::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: 0;
                        right: 0;
                        bottom: 0;
                        background: linear-gradient(135deg, transparent 0%, rgba(64, 158, 255, 0.1) 100%);
                        opacity: 0;
                        transition: opacity 0.3s ease;
                    }

                    .fa {
                        color: var(--text-secondary);
                        font-size: 14px;
                        z-index: 1;
                        position: relative;
                        transition: all 0.4s ease;
                    }

                    &:hover {
                        background: linear-gradient(135deg, rgba(255, 255, 255, 1) 0%, rgba(248, 250, 252, 1) 100%);
                        border-color: var(--primary-color);
                        box-shadow: 0 6px 20px rgba(64, 158, 255, 0.25);

                        &::before {
                            opacity: 1;
                        }

                        .fa {
                            color: var(--primary-color);
                        }
                    }

                    &.active {
                        background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-color) 100%);
                        border-color: var(--primary-color);
                        box-shadow: 0 4px 16px rgba(64, 158, 255, 0.4);

                        &::before {
                            background: linear-gradient(135deg, rgba(255, 255, 255, 0.2) 0%, transparent 100%);
                            opacity: 1;
                        }

                        .fa {
                            color: white;
                        }
                    }
                }
            }

            @include respond-to(sm) {
                flex-direction: column;
                align-items: center;
                gap: 12px;
                position: static;

                .carousel-title {
                    position: static;
                    left: auto;
                }

                .carousel-indicators {
                    gap: 6px;

                    .indicator-dot {
                        width: 28px;
                        height: 28px;

                        .fa {
                            font-size: 10px;
                        }
                    }
                }
            }

            @include respond-to(xs) {
                gap: 8px;

                .carousel-indicators {
                    gap: 4px;

                    .indicator-dot {
                        width: 24px;
                        height: 24px;

                        .fa {
                            font-size: 8px;
                        }
                    }
                }
            }
        }
    }

    // 轮播图样式
    .carousel-wrapper {
        position: relative;
        overflow: hidden;
        border-radius: var(--border-radius-xl);
        background: var(--chart-card-container-bg);
        border: var(--chart-card-container-border);
        box-shadow: var(--chart-card-container-shadow);
        backdrop-filter: blur(10px);

        @include respond-to(sm) {
            height: 400px;
        }

        @include respond-to(xs) {
            height: 350px;
        }

        // 轮播容器
        .carousel-container {
            position: relative;
            width: 100%;
            height: 100%;
            overflow: hidden;
        }

        // 轮播项目
        .carousel-item {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            transition: all 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
            display: flex;
            flex-direction: column;
            padding: var(--spacing-small);

            @include respond-to(sm) {
                padding: var(--spacing-xs);
            }

            @include respond-to(xs) {
                padding: var(--spacing-mini);
            }

            // 图表卡片包装器
            .chart-card-wrapper {
                width: 100%;
                flex: 1;
                background: var(--chart-white-gradient-bg);
                border-radius: var(--border-radius-xl) var(--border-radius-xl) 0 0;
                box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
                overflow: hidden;
                backdrop-filter: blur(20px);

                // 图表卡片头部
                .chart-card-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    padding: var(--spacing-base) var(--spacing-large) var(--spacing-medium);
                    border-bottom: 1px solid rgba(0, 0, 0, 0.06);
                    background: var(--chart-header-bg-gradient);
                    backdrop-filter: blur(10px);

                    @include respond-to(sm) {
                        padding: var(--spacing-sm) var(--spacing-medium) var(--spacing-xs);
                    }

                    @include respond-to(xs) {
                        padding: var(--spacing-small) var(--spacing-sm) var(--spacing-mini);
                    }

                    .chart-info {
                        display: flex;
                        align-items: center;
                        gap: 16px;

                        @include respond-to(sm) {
                            gap: 6px;
                        }

                        @include respond-to(xs) {
                            gap: 4px;
                        }

                        .fa {
                            font-size: 28px;
                            padding: var(--spacing-medium);
                            border-radius: var(--border-radius-xl);
                            background: var(--chart-white-gradient-bg);
                            box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);

                            @include respond-to(sm) {
                                font-size: 18px;
                                padding: var(--spacing-mini);
                            }

                            @include respond-to(xs) {
                                font-size: 16px;
                                padding: var(--spacing-micro);
                            }
                        }

                        .chart-title {
                            font-size: 22px;
                            font-weight: 700;
                            color: var(--text-primary);
                            margin: 0;
                            text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);

                            @include respond-to(sm) {
                                font-size: 16px;
                            }

                            @include respond-to(xs) {
                                font-size: 14px;
                            }
                        }

                        .chart-unit {
                            font-size: 14px;
                            color: var(--text-secondary);
                            background: linear-gradient(135deg, rgba(64, 158, 255, 0.1) 0%, rgba(74, 144, 226, 0.1) 100%);
                            padding: var(--spacing-xs) var(--spacing-medium);
                            border-radius: var(--border-radius-large);
                            border: var(--chart-primary-border);
                            font-weight: 500;

                            @include respond-to(xs) {
                                font-size: 10px;
                                padding: var(--spacing-1) var(--spacing-mini);
                            }
                        }
                    }

                    .chart-meta {
                        .chart-index {
                            font-size: 14px;
                            background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-color) 100%);
                            color: white;
                            padding: var(--spacing-small) var(--spacing-base);
                            border-radius: var(--border-radius-pill);
                            font-weight: 600;
                            box-shadow: 0 3px 12px rgba(64, 158, 255, 0.3);
                            text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);

                            @include respond-to(xs) {
                                font-size: 10px;
                                padding: var(--spacing-micro) var(--spacing-xs);
                            }
                        }
                    }
                }

                // 图表内容区域
                .chart-content {
                    padding: var(--spacing-large);
                    height: calc(100% - 60px);
                    background: var(--chart-bg-gradient);
                    position: relative;
                    border-bottom: 0;

                    @include respond-to(sm) {
                        padding: var(--spacing-small);
                    }

                    @include respond-to(xs) {
                        padding: var(--spacing-xs);
                    }

                    .chart-canvas {
                        width: 100% !important;
                        height: 100% !important;
                        flex: 1;
                        display: block;
                        border-radius: var(--border-radius-xl);
                        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
                        background: rgba(255, 255, 255, 0.8);
                        backdrop-filter: blur(5px);
                        cursor: grab;
                        margin-bottom: 0;

                        &:active {
                            cursor: grabbing;
                        }
                    }

                    .chart-zoom-controls {
                        position: absolute;
                        top: 10px;
                        right: 10px;
                        z-index: 100;

                        .zoom-button-group {
                            display: flex;
                            gap: 4px;
                            background: rgba(255, 255, 255, 0.95);
                            border-radius: var(--border-radius-large);
                            padding: var(--spacing-mini);
                            box-shadow: var(--chart-button-shadow);
                            border: var(--chart-standard-border);

                            .zoom-btn {
                                width: 32px;
                                height: 32px;
                                border: none;
                                border-radius: var(--border-radius-md);
                                background: transparent;
                                color: var(--text-secondary);
                                cursor: pointer;
                                display: flex;
                                align-items: center;
                                justify-content: center;
                                transition: var(--chart-quick-transition);
                                font-size: 14px;

                                &:hover {
                                    background: var(--primary-color);
                                    color: white;
                                    transform: translateY(-1px);
                                    box-shadow: 0 2px 6px rgba(64, 158, 255, 0.3);
                                }

                                &:active {
                                    transform: translateY(0);
                                }

                                .fa {
                                    font-size: 12px;
                                }
                            }
                        }
                    }

                    .chart-data-info {
                        position: absolute;
                        top: 5px;
                        left: 10px;
                        @include chart-info-panel;
                        opacity: 0.8;
                        z-index: 50;

                        &:hover {
                            opacity: 1;
                        }

                        .data-info-item {
                            @include chart-info-item;
                            padding: var(--spacing-3) var(--spacing-xs);
                            border: var(--chart-primary-border);

                            .fa {
                                font-size: 9px;
                            }

                            span {
                                font-size: 9px;
                            }
                        }
                    }

                    .chart-controls-hint {
                        position: absolute;
                        bottom: 5px;
                        right: 10px;
                        @include chart-info-panel;
                        opacity: 0.7;

                        &:hover {
                            opacity: 1;
                        }

                        .hint-item {
                            @include chart-info-item;
                            padding: var(--spacing-micro) var(--spacing-5);
                            border: var(--chart-standard-border);

                            .fa {
                                font-size: 9px;
                            }

                            span {
                                font-size: 9px;
                            }
                        }
                    }

                    .no-station-selected {
                        @include chart-empty-state-message;

                        .fa {
                            color: var(--primary-color);
                        }
                    }

                    .no-search-performed {
                        @include chart-empty-state-message;

                        .fa {
                            color: var(--el-color-warning);
                        }
                    }
                }
            }

            // 水平滚动条样式 - VSCode风格
            .chart-minimap-container {
                position: relative;
                width: 100%;
                height: 60px;
                min-height: 60px;
                flex-shrink: 0;
                background: rgba(248, 250, 252, 0.7);
                border-radius: 0 0 var(--border-radius-xl) var(--border-radius-xl);
                overflow: hidden;
                margin-top: 0;
                box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.03);

                // 缩略图样式
                .thumbnail-chart {
                    width: 100%;
                    height: 100%;
                    position: absolute;
                    top: 0;
                    left: 0;
                    z-index: 1;
                    cursor: pointer;
                    opacity: 0.9;
                    background: rgba(255, 255, 255, 0.3);
                    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.5);
                }

                // VSCode风格的可视区域滑块
                .viewport-slider {
                    position: absolute;
                    height: 100%;
                    background: rgba(64, 158, 255, 0.15);
                    border: 1px solid rgba(64, 158, 255, 0.5);
                    box-shadow: 0 0 3px rgba(64, 158, 255, 0.2);
                    top: 0;
                    z-index: 2;
                    cursor: grab;
                    transition: background-color 0.15s ease;

                    &:hover {
                        background: rgba(64, 158, 255, 0.25);
                    }

                    &:active,
                    &.dragging {
                        cursor: grabbing;
                        background: rgba(64, 158, 255, 0.3);
                    }

                    // 调整大小时的样式
                    &.resizing {
                        background: rgba(64, 158, 255, 0.35);

                        &.resizing-left {
                            border-left-width: 2px;
                            border-left-color: rgba(64, 158, 255, 0.9);
                        }

                        &.resizing-right {
                            border-right-width: 2px;
                            border-right-color: rgba(64, 158, 255, 0.9);
                        }
                    }

                    // VSCode风格的滑块边缘指示器
                    &::before,
                    &::after {
                        content: '';
                        position: absolute;
                        top: 0;
                        bottom: 0;
                        width: 1px;
                        background: rgba(64, 158, 255, 0.8);
                    }

                    &::before {
                        left: 0;
                    }

                    &::after {
                        right: 0;
                    }

                    // VSCode风格的调整手柄 - 视觉上不可见但可交互
                    .resize-handle {
                        position: absolute;
                        width: 8px;
                        height: 100%;
                        top: 0;
                        z-index: 3;
                        cursor: col-resize;
                        opacity: 0;

                        &.left {
                            left: -4px;
                        }

                        &.right {
                            right: -4px;
                        }
                    }
                }

                // 添加拖拽区域指示
                &:hover::after {
                    content: '';
                    position: absolute;
                    top: 0;
                    left: 0;
                    right: 0;
                    height: 3px;
                    background: rgba(64, 158, 255, 0.5);
                    z-index: 3;
                    opacity: 0.5;
                    cursor: row-resize;
                }

                // VSCode风格的缩略图阴影效果
                &::before {
                    content: '';
                    position: absolute;
                    top: 0;
                    left: 0;
                    right: 0;
                    bottom: 0;
                    box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.02);
                    pointer-events: none;
                    z-index: 4;
                    border-radius: 0 0 var(--border-radius-xl) var(--border-radius-xl);
                    background: linear-gradient(to bottom, rgba(255, 255, 255, 0.4), transparent 20%);
                }
            }
        }
    }
}
</style>