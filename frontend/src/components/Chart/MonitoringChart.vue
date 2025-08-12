<template>
    <div class="monitoring-chart">
        <!-- 图表轮播区域 -->
        <div class="chart-carousel-section">
            <CustomCard class="carousel-card" :bordered="true" shadow="hover" padding="small">
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
                    <div class="carousel-container">
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

                            <!-- 水平滚动条 - 已简化 -->
                            <div v-if="showMinimap && hasStation && hasSearched" class="chart-minimap-container"
                                @click="handleMinimapClick($event, index)">
                                <!-- 可视区域滑块 - 已恢复调整功能 -->
                                <div class="viewport-slider" :ref="el => setViewportSliderRef(el, index)" :style="{
                                    left: `${getViewportPosition(index).left}%`,
                                    width: `${getViewportPosition(index).width}%`
                                }" @mousedown.stop="startDragging($event, index)"
                                    @touchstart.stop="startDragging($event, index)" @click.stop>
                                    <!-- 左侧调整手柄 -->
                                    <div class="resize-handle left"
                                        @mousedown.stop="startResizing('left', $event, index)"
                                        @touchstart.stop="startResizing('left', $event, index)"></div>
                                    <!-- 右侧调整手柄 -->
                                    <div class="resize-handle right"
                                        @mousedown.stop="startResizing('right', $event, index)"
                                        @touchstart.stop="startResizing('right', $event, index)"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </CustomCard>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, shallowRef, markRaw, watch } from 'vue'
import { ElMessage } from 'element-plus'
import Chart from 'chart.js/auto'
import zoomPlugin from 'chartjs-plugin-zoom'
import { formatDateTime } from '@/utils/shared/common'
import CustomCard from '@/components/Common/CustomCard.vue'

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

const viewportSliderRefs = shallowRef([]) // 视口滑块引用数组

// 视口滑块拖拽状态
const isDragging = ref(false) // 是否正在拖拽
const dragStartX = ref(0) // 拖拽开始X坐标
const dragStartLeft = ref(0) // 拖拽开始左侧位置
const draggingChartIndex = ref(-1) // 当前拖拽的图表索引
const cachedContainer = ref(null) // 缓存的容器引用
const preventClick = ref(false) // 防止拖拽后立即触发点击

// 滑块调整大小状态
const isResizing = ref(false) // 是否正在调整大小
const resizeHandle = ref(null) // 当前调整的手柄('left' | 'right')
const resizeStartX = ref(0) // 调整开始X坐标
const resizeStartWidth = ref(0) // 调整开始宽度
const resizeStartLeft = ref(0) // 调整开始左侧位置
const resizingChartIndex = ref(-1) // 当前调整的图表索引

// 图表缩放和视口状态
const chartZoomLevels = ref([]) // 存储每个图表的缩放级别
const viewportPositions = ref([]) // 存储每个图表的视口位置

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

    // 事件监听器已在对应的stop函数中移除，无需重复清理
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

    // 预先初始化数组长度，避免运行时检查
    chartZoomLevels.value = new Array(props.chartItems.length).fill(1.0);
    viewportPositions.value = new Array(props.chartItems.length).fill().map(() => ({ left: 0, width: 100 }));

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

        // 设置合适的缩放级别，显示最左侧的15个数据点（内部会调用chart.update）
        setChartOptimalZoom(chart, labels.length, chartIndex);

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
    } else {
        // 如果数据点多于15个，只显示前15个
        chart.options.scales.x.min = 0;
        chart.options.scales.x.max = 15;

        // 计算并更新缩放级别状态
        // 缩放级别 = 总数据点 / 可见数据点
        chartZoomLevels.value[chartIndex] = dataCount / 15;

        // 手动同步滑块位置，确保与可见范围一致
        setTimeout(() => syncViewportSliderWithChart(chartIndex), 50);
    }

    // 统一更新图表但不触发动画
    chart.update('none');
    return chartZoomLevels.value[chartIndex];
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

// 同步主图表缩放状态到缩略图滑块
const syncViewportSliderWithChart = (chartIndex) => {
    const chart = chartInstances.value[chartIndex]
    if (!chart || !chart.scales || !chart.scales.x) return

    const min = chart.scales.x.min !== undefined ? chart.scales.x.min : 0
    const max = chart.scales.x.max !== undefined ? chart.scales.x.max : chart.data.labels.length
    const totalPoints = chart.data.labels.length

    if (totalPoints <= 0) return

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
// 点击滑块区域跳转 - 已简化并修复
const handleMinimapClick = (event, chartIndex) => {
    // 防止拖拽或调整大小后立即触发点击
    if (isDragging.value || isResizing.value || preventClick.value) return

    // 直接基于事件target计算
    const container = event.currentTarget
    const containerWidth = container.clientWidth
    const clickX = event.offsetX
    const clickPercent = (clickX / containerWidth) * 100

    // 以点击位置为中心计算新位置
    const halfWidth = viewportPositions.value[chartIndex].width / 2
    const newLeft = Math.max(0, Math.min(100 - viewportPositions.value[chartIndex].width, clickPercent - halfWidth))

    // 更新滑块位置并同步主图表
    viewportPositions.value[chartIndex].left = newLeft
    updateMainChartViewport(chartIndex)
}

/**
 * ----------------------------------------
 * 滑块样式控制工具 - 已简化
 * ----------------------------------------
 */
// 滑块样式控制函数
const setSliderInteractionStyle = (chartIndex, isActive) => {
    const slider = viewportSliderRefs.value[chartIndex]
    if (!slider) return

    if (isActive) {
        slider.style.transition = 'none'
        if (isDragging.value) {
            slider.classList.add('dragging')
        } else if (isResizing.value) {
            slider.classList.add('resizing')
        }
    } else {
        slider.style.transition = ''
        slider.classList.remove('dragging', 'resizing')
    }
}

/**
 * ----------------------------------------
 * 滑块拖拽功能
 * ----------------------------------------
 */
// 开始拖拽滑块 - 已简化并修复
const startDragging = (event, chartIndex) => {
    if (isDragging.value || isResizing.value) return

    event.stopPropagation()
    event.preventDefault()

    isDragging.value = true
    draggingChartIndex.value = chartIndex
    preventClick.value = false

    // 统一处理鼠标和触摸事件
    const clientX = event.type === 'touchstart' ? event.touches[0].clientX : event.clientX
    dragStartX.value = clientX
    dragStartLeft.value = viewportPositions.value[chartIndex]?.left || 0

    // 获取容器引用 - 确保使用chart-minimap-container
    cachedContainer.value = viewportSliderRefs.value[chartIndex]?.parentElement

    // 简化的视觉反馈
    setSliderInteractionStyle(chartIndex, true)

    // 添加事件监听器
    document.addEventListener('mousemove', handleDrag)
    document.addEventListener('mouseup', stopDragging)
    document.addEventListener('touchmove', handleDrag)
    document.addEventListener('touchend', stopDragging)
}

// 停止拖拽滑块 - 已简化并修复
const stopDragging = () => {
    if (!isDragging.value) return

    const chartIndex = draggingChartIndex.value

    // 清理状态
    isDragging.value = false
    draggingChartIndex.value = -1
    cachedContainer.value = null

    // 移除事件监听器
    document.removeEventListener('mousemove', handleDrag)
    document.removeEventListener('mouseup', stopDragging)
    document.removeEventListener('touchmove', handleDrag)
    document.removeEventListener('touchend', stopDragging)

    // 恢复滑块样式
    setSliderInteractionStyle(chartIndex, false)

    // 最后更新图表视口
    updateMainChartViewport(chartIndex)

    // 如果发生了实际拖拽，延迟后重置防点击标志
    if (preventClick.value) {
        setTimeout(() => {
            preventClick.value = false
        }, 150)
    }
}

// 处理拖拽移动 - 已简化并修复
const handleDrag = (event) => {
    if (!isDragging.value) return
    event.preventDefault()

    const chartIndex = draggingChartIndex.value
    if (chartIndex < 0) return

    // 统一处理鼠标和触摸事件
    const clientX = event.type.includes('touch') ? event.touches[0].clientX : event.clientX
    const containerWidth = cachedContainer.value?.clientWidth || 800

    // 计算新位置
    const deltaX = clientX - dragStartX.value
    const deltaPercent = (deltaX / containerWidth) * 100
    const width = viewportPositions.value[chartIndex].width
    const newLeft = Math.max(0, Math.min(100 - width, dragStartLeft.value + deltaPercent))

    // 检测是否有实际移动，如果有则设置防点击标志
    if (Math.abs(deltaX) > 3) { // 移动超过3像素才认为是真正的拖拽
        preventClick.value = true
    }

    // 更新滑块位置
    viewportPositions.value[chartIndex].left = newLeft

    // 同步更新主图表
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

    event.stopPropagation()
    event.preventDefault()

    isResizing.value = true
    resizeHandle.value = handle
    resizingChartIndex.value = chartIndex
    preventClick.value = false

    // 统一处理鼠标和触摸事件
    const clientX = event.type === 'touchstart' ? event.touches[0].clientX : event.clientX
    resizeStartX.value = clientX
    resizeStartLeft.value = viewportPositions.value[chartIndex]?.left || 0
    resizeStartWidth.value = viewportPositions.value[chartIndex]?.width || 100

    // 获取容器引用
    cachedContainer.value = viewportSliderRefs.value[chartIndex]?.parentElement

    // 简化的视觉反馈
    setSliderInteractionStyle(chartIndex, true)

    // 添加事件监听器
    document.addEventListener('mousemove', handleResize)
    document.addEventListener('mouseup', stopResizing)
    document.addEventListener('touchmove', handleResize)
    document.addEventListener('touchend', stopResizing)
}

// 停止调整滑块大小
const stopResizing = () => {
    if (!isResizing.value) return

    const chartIndex = resizingChartIndex.value

    // 清理状态
    isResizing.value = false
    resizingChartIndex.value = -1
    resizeHandle.value = null
    cachedContainer.value = null

    // 移除事件监听器
    document.removeEventListener('mousemove', handleResize)
    document.removeEventListener('mouseup', stopResizing)
    document.removeEventListener('touchmove', handleResize)
    document.removeEventListener('touchend', stopResizing)

    // 恢复滑块样式
    setSliderInteractionStyle(chartIndex, false)

    // 最后更新图表视口
    updateMainChartViewport(chartIndex)

    // 如果发生了实际调整，延迟后重置防点击标志
    if (preventClick.value) {
        setTimeout(() => {
            preventClick.value = false
        }, 150)
    }
}

// 处理调整大小移动
const handleResize = (event) => {
    if (!isResizing.value) return
    event.preventDefault()

    const chartIndex = resizingChartIndex.value
    if (chartIndex < 0) return

    // 统一处理鼠标和触摸事件
    const clientX = event.type.includes('touch') ? event.touches[0].clientX : event.clientX
    const containerWidth = cachedContainer.value?.clientWidth || 800
    const deltaX = clientX - resizeStartX.value
    const deltaPercent = (deltaX / containerWidth) * 100

    // 检测是否有实际移动
    if (Math.abs(deltaX) > 3) {
        preventClick.value = true
    }

    // 根据调整手柄处理不同逻辑
    if (resizeHandle.value === 'left') {
        // 调整左侧手柄 - 更改left和width
        const newLeft = Math.max(0, resizeStartLeft.value + deltaPercent)
        const newWidth = Math.max(5, resizeStartWidth.value - (newLeft - resizeStartLeft.value))

        // 确保不超出右边界
        if (newLeft + newWidth <= 100) {
            viewportPositions.value[chartIndex].left = newLeft
            viewportPositions.value[chartIndex].width = newWidth
        }
    } else if (resizeHandle.value === 'right') {
        // 调整右侧手柄 - 只更改width
        const newWidth = Math.max(5, Math.min(100 - resizeStartLeft.value, resizeStartWidth.value + deltaPercent))
        viewportPositions.value[chartIndex].width = newWidth
    }

    // 同步更新主图表
    requestAnimationFrame(() => updateMainChartViewport(chartIndex))
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
    return viewportPositions.value[chartIndex] || { left: 0, width: 100 }
}

// 更新主图表的可见范围
const updateMainChartViewport = (chartIndex) => {
    const chart = chartInstances.value[chartIndex]
    if (!chart || !chart.data || !chart.data.labels) return

    const totalDataPoints = chart.data.labels.length

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
        const currentLevel = chartZoomLevels.value[chartIndex] || 1.0
        newZoomLevel = currentLevel * zoomFactor
    }

    // 计算基于数据点的最大缩放级别
    const totalDataPoints = chart.data?.labels?.length || 0
    const maxZoomLevel = calculateMaxZoomLevel(totalDataPoints)

    // 限制缩放范围在合理区间 (下限保持0.2，上限基于数据点)
    newZoomLevel = Math.max(0.2, Math.min(maxZoomLevel, newZoomLevel))

    // 更新状态
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
         * 轮播头部样式 - 保留业务专用样式
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
                    background: rgba(255, 255, 255, 0.95);
                    cursor: pointer;
                    transition: all 0.3s ease;
                    display: flex;
                    align-items: center;
                    justify-content: center;

                    .fa {
                        color: var(--text-secondary);
                        font-size: 14px;
                        transition: color 0.3s ease;
                    }

                    &:hover {
                        border-color: var(--primary-color);

                        .fa {
                            color: var(--primary-color);
                        }
                    }

                    &.active {
                        background: var(--primary-color);
                        border-color: var(--primary-color);

                        .fa {
                            color: white;
                        }
                    }
                }
            }

            @include respond-to(sm) {
                flex-direction: column;
                align-items: center;
                gap: clamp(8px, 2vw, 12px);
                position: static;

                .carousel-title {
                    position: static;
                    left: auto;
                }

                .carousel-indicators {
                    gap: clamp(4px, 1vw, 6px);

                    .indicator-dot {
                        width: clamp(24px, 4vw, 32px);
                        height: clamp(24px, 4vw, 32px);

                        .fa {
                            font-size: clamp(8px, 1.5vw, 12px);
                        }
                    }
                }
            }
        }
    }

    // 轮播图样式 - 添加负边距抵消 CustomCard 内容区域的padding
    .carousel-wrapper {
        position: relative;
        overflow: hidden;
        margin: calc(-1 * var(--spacing-small));

        @include respond-to(sm) {
            height: clamp(350px, 50vh, 450px);
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
            padding: clamp(var(--spacing-mini), 1vw, var(--spacing-small));

            // 图表卡片包装器 - 简化样式，背景和圆角由 CustomCard 处理
            .chart-card-wrapper {
                width: 100%;
                flex: 1;
                overflow: hidden;

                // 图表卡片头部 - 简化样式，基础样式由 CustomCard 处理
                .chart-card-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    padding: clamp(var(--spacing-small), 2vw, var(--spacing-large));

                    .chart-info {
                        display: flex;
                        align-items: center;
                        gap: clamp(8px, 2vw, 16px);

                        .fa {
                            font-size: clamp(16px, 3vw, 24px);
                            padding: var(--spacing-small);
                            border-radius: var(--border-radius-large);
                            background: rgba(255, 255, 255, 0.8);
                            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
                        }

                        .chart-title {
                            font-size: clamp(14px, 2.5vw, 20px);
                            font-weight: 600;
                            color: var(--text-primary);
                            margin: 0;
                        }

                        .chart-unit {
                            font-size: clamp(10px, 1.5vw, 12px);
                            color: var(--text-secondary);
                            background: rgba(64, 158, 255, 0.08);
                            padding: var(--spacing-xs) var(--spacing-small);
                            border-radius: var(--border-radius-base);
                            border: 1px solid rgba(64, 158, 255, 0.2);
                            font-weight: 500;
                        }
                    }

                    .chart-meta {
                        .chart-index {
                            font-size: clamp(10px, 1.5vw, 12px);
                            background: var(--primary-color);
                            color: white;
                            padding: var(--spacing-xs) var(--spacing-small);
                            border-radius: var(--border-radius-large);
                            font-weight: 500;
                        }
                    }
                }

                // 图表内容区域
                .chart-content {
                    padding: clamp(var(--spacing-xs), 2vw, var(--spacing-large));
                    height: calc(100% - 60px);
                    background: rgba(255, 255, 255, 0.3);
                    position: relative;

                    .chart-canvas {
                        width: 100% !important;
                        height: 100% !important;
                        display: block;
                        border-radius: var(--border-radius-large);
                        background: rgba(255, 255, 255, 0.9);
                        cursor: grab;

                        &:active {
                            cursor: grabbing;
                        }
                    }

                    .chart-zoom-controls {
                        position: absolute;
                        top: 10px;
                        right: 10px;
                        z-index: 100;
                        display: flex;
                        gap: 4px;
                        background: rgba(255, 255, 255, 0.95);
                        border-radius: var(--border-radius-large);
                        padding: var(--spacing-mini);
                        border: var(--chart-standard-border);

                        .zoom-btn {
                            width: 28px;
                            height: 28px;
                            border: none;
                            border-radius: var(--border-radius-base);
                            background: transparent;
                            color: var(--text-secondary);
                            cursor: pointer;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            transition: var(--chart-quick-transition);

                            &:hover {
                                background: var(--primary-color);
                                color: white;
                            }

                            .fa {
                                font-size: 11px;
                            }
                        }
                    }

                    .chart-controls-hint {
                        position: absolute;
                        bottom: 5px;
                        right: 10px;
                        display: flex;
                        gap: 6px;
                        font-size: 9px;
                        color: var(--text-secondary);
                        opacity: 0.8;

                        .hint-item {
                            display: flex;
                            align-items: center;
                            gap: 2px;
                            background: rgba(255, 255, 255, 0.9);
                            border-radius: var(--border-radius-base);
                            padding: 2px 4px;
                            border: var(--chart-standard-border);

                            .fa {
                                color: var(--primary-color);
                                font-size: 8px;
                            }

                            span {
                                white-space: nowrap;
                                font-weight: 500;
                            }
                        }
                    }

                    // 公共占位符样式基类
                    .chart-placeholder-base {
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        justify-content: center;
                        height: 100%;
                        color: var(--text-secondary);
                        background: rgba(255, 255, 255, 0.8);
                        border-radius: var(--border-radius-xl);

                        .fa {
                            font-size: 48px;
                            margin-bottom: 16px;
                            opacity: 0.6;
                        }

                        p {
                            font-size: 16px;
                            margin: 0;
                            font-weight: 500;
                        }
                    }

                    .no-station-selected {
                        @extend .chart-placeholder-base;

                        .fa {
                            color: var(--primary-color);
                        }
                    }

                    .no-search-performed {
                        @extend .chart-placeholder-base;

                        .fa {
                            color: var(--el-color-warning);
                        }
                    }
                }
            }

            // 水平滚动条样式 - 已简化
            .chart-minimap-container {
                position: relative;
                width: 100%;
                height: 60px;
                min-height: 60px;
                flex-shrink: 0;
                background: linear-gradient(90deg, rgba(248, 250, 252, 0.8) 0%, rgba(240, 244, 248, 0.9) 50%, rgba(248, 250, 252, 0.8) 100%);
                border-radius: 0 0 var(--border-radius-xl) var(--border-radius-xl);
                overflow: hidden;
                margin-top: 0;
                box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.03);
                cursor: pointer;

                // 添加纹理效果
                &::after {
                    content: '';
                    position: absolute;
                    top: 0;
                    left: 0;
                    right: 0;
                    bottom: 0;
                    background: repeating-linear-gradient(90deg,
                            transparent 0px,
                            rgba(64, 158, 255, 0.05) 1px,
                            transparent 2px,
                            transparent 20px);
                    pointer-events: none;
                    z-index: 1;
                }

                // 简化的可视区域滑块
                .viewport-slider {
                    position: absolute;
                    height: 100%;
                    background: rgba(64, 158, 255, 0.15);
                    border: 1px solid rgba(64, 158, 255, 0.5);
                    box-shadow: 0 0 3px rgba(64, 158, 255, 0.2);
                    top: 0;
                    z-index: 3;
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
                        background: rgba(64, 158, 255, 0.25);
                        border-color: rgba(64, 158, 255, 0.8);

                        .resize-handle {
                            opacity: 0.6;
                            background: rgba(64, 158, 255, 0.8);
                        }
                    }

                    // 简化的边缘指示器
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

                    // 调整手柄样式
                    .resize-handle {
                        position: absolute;
                        width: 8px;
                        height: 100%;
                        top: 0;
                        z-index: 4;
                        cursor: col-resize;
                        opacity: 0;
                        transition: opacity 0.2s ease;

                        &:hover {
                            opacity: 0.3;
                            background: rgba(64, 158, 255, 0.5);
                        }

                        &.left {
                            left: -4px;
                        }

                        &.right {
                            right: -4px;
                        }
                    }
                }

                // 简化的容器效果
                &:hover {
                    background: linear-gradient(90deg, rgba(240, 244, 248, 0.9) 0%, rgba(232, 238, 244, 1) 50%, rgba(240, 244, 248, 0.9) 100%);
                }

                // 简化的阴影效果  
                &::before {
                    content: '';
                    position: absolute;
                    top: 0;
                    left: 0;
                    right: 0;
                    bottom: 0;
                    box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.02);
                    pointer-events: none;
                    z-index: 2;
                    border-radius: 0 0 var(--border-radius-xl) var(--border-radius-xl);
                }
            }
        }
    }
}
</style>