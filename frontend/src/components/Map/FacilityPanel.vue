<template>
    <!-- 根据是否启用悬浮模式选择不同的容器结构 -->
    <div v-if="enableFloating" class="floating-panel" :class="{
        collapsed: panelCollapsed,
        dragging: isDragging
    }" :style="panelStyle" @mouseenter="panelHovered = true" @mouseleave="panelHovered = false">
        <!-- 悬浮面板标题栏 -->
        <div class="floating-panel-header" @mousedown="startDrag" @dblclick="togglePanelCollapse">
            <div class="header-content">
                <i class="fa fa-bars drag-handle"></i>
                <span class="panel-title">设备管理</span>
                <div class="header-actions">
                    <div :title="panelCollapsed ? '展开' : '折叠'">
                        <CustomButton type="secondary" size="small" shape="circle" :icon-only="true"
                            @click="togglePanelCollapse">
                            <i class="fa" :class="panelCollapsed ? 'fa-chevron-down' : 'fa-chevron-up'"></i>
                        </CustomButton>
                    </div>
                </div>
            </div>
        </div>

        <!-- 悬浮面板内容 -->
        <div class="floating-panel-content" :class="{ 'content-hidden': panelCollapsed }">
            <div class="facility-panel device-management-panel floating-mode">
                <!-- 标签页组件 -->
                <TabSection v-model="activeTab" :tabs="tabOptions" @tab-change="handleTabChange" />

                <div class="panel-content">
                    <!-- 设备标签页内容 -->
                    <div v-if="activeTab === 'facilities'" class="facilities-tab">
                        <!-- 设备类型选择器 -->
                        <div class="device-type-selector">
                            <div class="selector-group">
                                <CustomSelect v-model="selectedFacilityType" :options="facilityTypeOptions"
                                    placeholder="请选择设备类型" @update:modelValue="handleFacilityTypeChange" />
                            </div>

                        </div>
                    </div>

                    <!-- 监测站点标签页内容 -->
                    <div v-if="activeTab === 'monitoring-stations'" class="monitoring-stations-tab">
                    </div>

                    <!-- 设备列表 -->
                    <div class="device-list">
                        <div v-if="currentDisplayList.length === 0" class="empty-state">
                            <span class="empty-icon">📪</span>
                            <p>暂无设备数据</p>
                        </div>
                        <div v-else class="device-items">
                            <div v-for="item in currentDisplayList" :key="getItemId(item)" class="device-item" :class="{
                                active: selectedDevice?.id === getItemId(item),
                                'no-location': !hasValidLocation(item)
                            }" @click="handleListItemClick(item)">
                                <div class="device-icon" :style="{
                                    backgroundColor: getItemIconConfig(item).bgColor,
                                    border: `var(--border-width-normal) solid ${getItemIconConfig(item).color}`
                                }">
                                    <span class="icon-symbol" :style="{ color: getItemIconConfig(item).color }">
                                        {{ getItemIconSymbol(item) }}
                                    </span>
                                </div>
                                <div class="device-info">
                                    <div class="device-name">{{ getItemName(item) }}</div>
                                    <div class="device-type">{{ getItemType(item) }}</div>
                                    <div class="device-status" :class="getItemStatusClass(item)">
                                        {{ getItemStatusText(item) }}
                                    </div>
                                    <div v-if="hasValidLocation(item)" class="device-location">
                                        <span class="location-icon">📍</span>
                                        {{ getItemLocation(item) }}
                                    </div>
                                    <div v-else class="device-location invalid">
                                        <span class="warning-icon">⚠️</span>
                                        无地理位置信息
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 普通模式的面板结构 -->
    <div v-else class="facility-panel device-management-panel" :class="{ 'floating-mode': isFloatingMode }">
        <!-- 统一的头部组件 -->
        <div class="panel-header" v-if="!isFloatingMode">
            <span class="panel-icon">🔍</span>
            <span class="panel-title">设备管理</span>
        </div>

        <!-- 标签页组件 -->
        <TabSection v-model="activeTab" :tabs="tabOptions" @tab-change="handleTabChange" />

        <div class="panel-content">
            <!-- 设备标签页内容 -->
            <div v-if="activeTab === 'facilities'" class="facilities-tab">
                <!-- 设备类型选择器 -->
                <div class="device-type-selector">
                    <div class="selector-group">
                        <CustomSelect v-model="selectedFacilityType" :options="facilityTypeOptions"
                            placeholder="请选择设备类型" @update:modelValue="handleFacilityTypeChange" />
                    </div>

                </div>
            </div>

            <!-- 监测站点标签页内容 -->
            <div v-if="activeTab === 'monitoring-stations'" class="monitoring-stations-tab">
            </div>

            <!-- 设备列表 -->
            <div class="device-list">
                <div v-if="currentDisplayList.length === 0" class="empty-state">
                    <span class="empty-icon">📪</span>
                    <p>暂无设备数据</p>
                </div>
                <div v-else class="device-items">
                    <div v-for="item in currentDisplayList" :key="getItemId(item)" class="device-item" :class="{
                        active: selectedDevice?.id === getItemId(item),
                        'no-location': !hasValidLocation(item)
                    }" @click="handleListItemClick(item)">
                        <div class="device-icon" :style="{
                            backgroundColor: getItemIconConfig(item).bgColor,
                            border: `var(--border-width-normal) solid ${getItemIconConfig(item).color}`
                        }">
                            <span class="icon-symbol" :style="{ color: getItemIconConfig(item).color }">
                                {{ getItemIconSymbol(item) }}
                            </span>
                        </div>
                        <div class="device-info">
                            <div class="device-name">{{ getItemName(item) }}</div>
                            <div class="device-type">{{ getItemType(item) }}</div>
                            <div class="device-status" :class="getItemStatusClass(item)">
                                {{ getItemStatusText(item) }}
                            </div>
                            <div v-if="hasValidLocation(item)" class="device-location">
                                <span class="location-icon">📍</span>
                                {{ getItemLocation(item) }}
                            </div>
                            <div v-else class="device-location invalid">
                                <span class="warning-icon">⚠️</span>
                                无地理位置信息
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from "vue";
import { ElMessage } from "element-plus";
import CustomSelect from "@/components/Common/CustomSelect.vue";
import CustomButton from "@/components/Common/CustomButton.vue";
import TabSection from "@/components/Common/TabSection.vue";
import {
    getDeviceIconConfig,
    DEVICE_TYPE_OPTIONS
} from '@/utils/map/deviceIcon';
import {
    getUnifiedDeviceType,
    generateDeviceId,
    getDeviceName,
    getDeviceLocation,
    getDeviceStatusClass,
    getDeviceStatusText,
    hasValidLocation
} from '@/utils/map/deviceType';

// Props定义
const props = defineProps({
    // 设施数据列表
    facilities: {
        type: Array,
        default: () => [],
    },
    // 监测站数据列表
    monitoringStations: {
        type: Array,
        default: () => [],
    },
    // 当前选中的设备
    selectedDevice: {
        type: Object,
        default: null,
    },
    // 选中的设备类型
    selectedDeviceType: {
        type: String,
        default: 'all',
    },
    // 是否为悬浮面板模式（向后兼容）
    isFloatingMode: {
        type: Boolean,
        default: false,
    },
    // 是否启用悬浮功能
    enableFloating: {
        type: Boolean,
        default: false,
    },
    // 初始面板位置
    initialPosition: {
        type: Object,
        default: () => ({ x: 20, y: 20 }),
    },
    // 初始折叠状态
    initialCollapsed: {
        type: Boolean,
        default: false,
    },
});

// Events定义
const emit = defineEmits([
    "device-select",
    "device-locate",
    "device-type-change",
    // 新增的悬浮面板事件
    "panel-position-change",
    "panel-collapse-change",
    "panel-ready"
]);

// 悬浮面板状态管理
const panelCollapsed = ref(props.initialCollapsed);
const panelHovered = ref(false);
const isDragging = ref(false);
const panelPosition = ref(props.initialPosition);
const dragOffset = ref({ x: 0, y: 0 });
const dragAnimationId = ref(null);
const viewportSize = ref({ width: 0, height: 0 });

// 面板尺寸硬编码（组件专用交互逻辑）
const PANEL_WIDTH = 320;
const PANEL_HEIGHT = 600;
const COLLAPSED_HEIGHT = 48;
const BOUNDARY_DISTANCE = 50;

// 计算面板样式（使用左侧定位）
const panelStyle = computed(() => ({
    left: `${panelPosition.value.x}px`,
    top: `${panelPosition.value.y}px`,
    width: `${PANEL_WIDTH}px`,
    height: panelCollapsed.value ? `${COLLAPSED_HEIGHT}px` : `${PANEL_HEIGHT}px`,
    zIndex: isDragging.value ? 1002 : 1001,
    opacity: isDragging.value ? 1 : (panelHovered.value ? 1 : 0.9)
}));

// 标签页配置
const tabOptions = [
    { name: 'facilities', label: '设备', icon: 'fa-cog' },
    { name: 'monitoring-stations', label: '监测站点', icon: 'fa-chart-line' }
];

// 当前选中的标签页
const activeTab = ref('facilities');

// 设备类型选项（排除监测站点）
const facilityTypeOptions = DEVICE_TYPE_OPTIONS.filter(option => option.value !== 'monitoring_station');

// 当前选中的设备类型（仅在设备标签页有效）
const selectedFacilityType = ref('all');



// 生命周期 - 组件挂载时加载数据
onMounted(() => {
    if (props.enableFloating) {
        loadPanelState();
        updateViewportSize();

        // 添加窗口大小变化监听
        window.addEventListener('resize', updateViewportSize);

        // 通知父组件面板已准备就绪
        emit('panel-ready');
    }
});

// 组件卸载时清理事件监听
onUnmounted(() => {
    if (props.enableFloating) {
        cleanupDragListeners();
        window.removeEventListener('resize', updateViewportSize);
        if (dragAnimationId.value) {
            cancelAnimationFrame(dragAnimationId.value);
        }
    }
});

/**
 * 加载面板状态
 */
const loadPanelState = () => {
    try {
        const savedState = localStorage.getItem('map-panel-state');
        if (savedState) {
            const state = JSON.parse(savedState);
            panelPosition.value = state.position || props.initialPosition;
            panelCollapsed.value = state.collapsed || props.initialCollapsed;
        }
    } catch (error) {
        console.warn('无法加载面板状态:', error);
    }
};

/**
 * 保存面板状态
 */
const savePanelState = () => {
    try {
        const state = {
            position: panelPosition.value,
            collapsed: panelCollapsed.value
        };
        localStorage.setItem('map-panel-state', JSON.stringify(state));

        // 通知父组件状态变化
        emit('panel-position-change', panelPosition.value);
        emit('panel-collapse-change', panelCollapsed.value);
    } catch (error) {
        console.warn('无法保存面板状态:', error);
    }
};

/**
 * 开始拖拽
 */
const startDrag = (event) => {
    if (!props.enableFloating) return;

    event.preventDefault();
    isDragging.value = true;
    panelHovered.value = true; // 拖拽时保持可见

    // 基于左侧坐标系统计算偏移
    const rect = event.currentTarget.getBoundingClientRect();
    dragOffset.value = {
        x: event.clientX - rect.left,
        y: event.clientY - rect.top
    };

    // 添加拖拽事件监听
    document.addEventListener('mousemove', handleDragThrottled);
    document.addEventListener('mouseup', endDrag);
};

/**
 * 节流处理拖拽
 */
const handleDragThrottled = (event) => {
    if (dragAnimationId.value) {
        cancelAnimationFrame(dragAnimationId.value);
    }

    dragAnimationId.value = requestAnimationFrame(() => {
        handleDrag(event);
    });
};

/**
 * 处理拖拽（优化版本）
 */
const handleDrag = (event) => {
    if (!isDragging.value) return;

    event.preventDefault();

    const { width: viewportWidth, height: viewportHeight } = viewportSize.value;
    const panelWidth = PANEL_WIDTH;
    const panelHeight = panelCollapsed.value ? COLLAPSED_HEIGHT : PANEL_HEIGHT;

    // 计算新位置（基于左侧坐标系统）
    let newX = event.clientX - dragOffset.value.x;
    let newY = event.clientY - dragOffset.value.y;

    // 边界限制（确保面板始终在视窗内，但允许边缘操作）
    const minX = -panelWidth + BOUNDARY_DISTANCE; // 左边界：允许大部分超出，保留边缘距离可见
    const maxX = viewportWidth - BOUNDARY_DISTANCE; // 右边界：保留边缘距离可见
    const minY = 0; // 上边界：不能超出
    const maxY = viewportHeight - Math.min(panelHeight, 60); // 下边界：至少保留标题栏可见

    newX = Math.max(minX, Math.min(newX, maxX));
    newY = Math.max(minY, Math.min(newY, maxY));

    panelPosition.value = { x: newX, y: newY };
};

/**
 * 结束拖拽
 */
const endDrag = () => {
    if (isDragging.value) {
        isDragging.value = false;
        panelHovered.value = false; // 恢复悬停状态控制

        // 移除拖拽事件监听
        cleanupDragListeners();

        // 取消待处理的动画帧
        if (dragAnimationId.value) {
            cancelAnimationFrame(dragAnimationId.value);
            dragAnimationId.value = null;
        }

        savePanelState();
    }
};

/**
 * 切换面板折叠状态
 */
const togglePanelCollapse = () => {
    if (!props.enableFloating) return;

    panelCollapsed.value = !panelCollapsed.value;
    savePanelState();
};

/**
 * 更新视窗尺寸
 */
const updateViewportSize = () => {
    viewportSize.value = { width: window.innerWidth, height: window.innerHeight };

    // 当视窗大小变化时，检查面板位置是否需要调整
    const { x, y } = panelPosition.value;
    const panelWidth = PANEL_WIDTH;
    const panelHeight = panelCollapsed.value ? COLLAPSED_HEIGHT : PANEL_HEIGHT;

    // 确保面板不会完全超出新的视窗边界
    const minX = -panelWidth + BOUNDARY_DISTANCE;
    const maxX = viewportSize.value.width - BOUNDARY_DISTANCE;
    const minY = 0;
    const maxY = viewportSize.value.height - Math.min(panelHeight, 60);

    const newX = Math.max(minX, Math.min(x, maxX));
    const newY = Math.max(minY, Math.min(y, maxY));

    if (newX !== x || newY !== y) {
        panelPosition.value = { x: newX, y: newY };
        savePanelState();
    }
};

/**
 * 清理拖拽事件监听
 */
const cleanupDragListeners = () => {
    document.removeEventListener('mousemove', handleDragThrottled);
    document.removeEventListener('mouseup', endDrag);
};

// 设备类型识别已移至统一工具文件 @/utils/deviceTypeUtils.js

// 计算属性 - 当前显示的列表数据（基于标签页和设备类型筛选）
const currentDisplayList = computed(() => {
    // 如果是监测站点标签页，显示所有监测站数据
    if (activeTab.value === 'monitoring-stations') {
        return [...props.monitoringStations];
    }

    // 如果是设备标签页
    if (activeTab.value === 'facilities') {
        // 如果选择全部设备，显示所有设施数据
        if (selectedFacilityType.value === 'all') {
            return [...props.facilities];
        }

        // 根据选择的设备类型进行筛选
        return props.facilities.filter(item => {
            const deviceType = getUnifiedDeviceType(item);
            return deviceType === selectedFacilityType.value;
        });
    }

    return [];
});

/**
 * 获取当前有效坐标数量
 */
const getCurrentValidCount = () => {
    return currentDisplayList.value.filter(item => hasValidLocation(item)).length;
};

/**
 * 处理标签页切换
 */
const handleTabChange = (tabName) => {
    activeTab.value = tabName;

    // 通知父组件标签页变化，传递复合状态信息
    const deviceTypeValue = tabName === 'monitoring-stations' ? 'monitoring_station' : selectedFacilityType.value;
    emit('device-type-change', deviceTypeValue);
};

/**
 * 处理设备类型选择变化（仅设备标签页有效）
 */
const handleFacilityTypeChange = (value) => {
    selectedFacilityType.value = value;

    // 通知父组件设备类型变化
    emit('device-type-change', value);
};

/**
 * 处理列表项点击
 */
const handleListItemClick = (item) => {
    emit('device-select', item);

    // 如果有有效坐标，地图定位到该设备
    if (hasValidLocation(item)) {
        emit('device-locate', {
            longitude: item.longitude,
            latitude: item.latitude,
        });
    } else {
        ElMessage.warning(`${getItemName(item)} 缺少地理位置信息，无法定位`);
    }
};

/**
 * 获取设备ID
 */
const getItemId = (item) => {
    return generateDeviceId(item);
};

/**
 * 获取设备名称
 */
const getItemName = (item) => {
    return getDeviceName(item);
};

/**
 * 获取设备类型显示名称
 */
const getItemType = (item) => {
    const deviceType = getUnifiedDeviceType(item);
    const config = getDeviceIconConfig(item);
    return config ? config.name : (item.typeName || item.type || '未知设备');
};

/**
 * 获取设备图标配置（统一设备类型版本）
 */
const getItemIconConfig = (item) => {
    return getDeviceIconConfig(item);
};

/**
 * 获取设备图标符号（用于显示）
 */
const getItemIconSymbol = (item) => {
    const iconConfig = getItemIconConfig(item);
    return iconConfig.symbol;
};

/**
 * 获取设备状态样式类名
 */
const getItemStatusClass = (item) => {
    return getDeviceStatusClass(item);
};

/**
 * 获取设备状态文本
 */
const getItemStatusText = (item) => {
    return getDeviceStatusText(item);
};

/**
 * 获取设备位置信息
 */
const getItemLocation = (item) => {
    return getDeviceLocation(item);
};
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

// 悬浮面板样式
.floating-panel {
    position: fixed;
    width: var(--panel-width-default);
    background: var(--white-transparent-full);
    backdrop-filter: blur(12px);
    border-radius: var(--border-radius-xl);
    box-shadow: var(--shadow-popup);
    border: var(--border-width-thin) solid var(--white-transparent-base);
    overflow: hidden;
    @include user-select(none);
    transition: height var(--map-panel-transition-duration) var(--map-panel-transition-ease),
        box-shadow var(--map-panel-transition-duration) var(--map-panel-transition-ease),
        transform var(--map-panel-transition-duration) var(--map-panel-transition-ease),
        backdrop-filter var(--map-panel-transition-duration) var(--map-panel-transition-ease);

    &.dragging {
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
    }

    &.collapsed {
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
        backdrop-filter: blur(8px);
        height: var(--map-panel-collapsed-height);
    }

    .floating-panel-header {
        background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-color) 100%);
        color: white;
        padding: 0;
        cursor: move;
        border-bottom: var(--border-width-thin) solid var(--white-transparent-light);
        transition: background var(--map-panel-transition-duration) var(--map-panel-transition-ease);

        &:hover {
            background: linear-gradient(135deg, var(--primary-light) 0%, var(--primary-light) 100%);
        }

        .header-content {
            display: flex;
            align-items: center;
            padding: var(--spacing-medium) var(--spacing-base);

            .drag-handle {
                margin-right: var(--spacing-medium);
                font-size: var(--font-size-base);
                opacity: 0.8;
                cursor: grab;
                transition: opacity var(--map-panel-transition-duration) var(--map-panel-transition-ease);

                &:hover {
                    opacity: 1;
                }

                &:active {
                    cursor: grabbing;
                    opacity: 1;
                }
            }

            .panel-title {
                flex: 1;
                font-weight: var(--font-weight-bold);
                font-size: var(--font-size-base);
                margin: 0;
            }

            .header-actions {
                display: flex;
                gap: var(--spacing-small);

                // CustomButton 样式覆盖以实现半透明白色背景效果
                :deep(.custom-button--circle) {
                    background: var(--white-transparent-base) !important;
                    border: none !important;
                    color: white !important;
                    width: var(--button-size-small) !important;
                    height: var(--button-size-small) !important;
                    min-width: var(--button-size-small) !important;
                    transition: background-color var(--map-panel-transition-duration) var(--map-panel-transition-ease);

                    &:hover:not(.custom-button--disabled):not(.custom-button--loading) {
                        background: var(--white-transparent-medium) !important;
                        border: none !important;
                    }

                    &:active:not(.custom-button--disabled):not(.custom-button--loading) {
                        background: var(--white-transparent-active) !important;
                    }

                    i {
                        color: white !important;
                        font-size: var(--font-size-extra-small);
                    }
                }
            }
        }
    }

    .floating-panel-content {
        height: calc(100% - var(--map-panel-collapsed-height));
        overflow: hidden;
        transition: all var(--map-panel-transition-duration) var(--map-panel-transition-ease);
        max-height: var(--map-panel-max-height);
        opacity: 1;
        transform: translateY(0) scale(1);
        transform-origin: top center;

        &.content-hidden {
            opacity: 0;
            transform: translateY(var(--map-panel-hidden-translate-y)) scale(var(--map-panel-hidden-scale));
            pointer-events: none;
            max-height: 0;
        }

        // 重置FacilityPanel的样式以适应悬浮面板
        .facility-panel {
            background: transparent;
            box-shadow: none;
            border-radius: 0;
            height: 100%;

            .panel-header {
                display: none; // 隐藏原有头部，使用悬浮面板的头部
            }

            .panel-content {
                height: 100%;
                min-height: auto;
            }
        }
    }
}

// 原有的面板样式
.facility-panel {
    height: 100%;
    display: flex;
    flex-direction: column;
    background: var(--bg-primary);
    border-radius: var(--border-radius-large);
    box-shadow: var(--shadow-card);

    .panel-header {
        padding: var(--spacing-base);
        border-bottom: var(--border-width-thin) solid var(--border-light);
        @include flex-start;
        background: var(--bg-secondary);

        .panel-icon {
            font-size: var(--font-size-large);
            margin-right: var(--spacing-small);
            color: var(--primary-color);
        }

        .panel-title {
            font-weight: var(--font-weight-bold);
            color: var(--text-primary);
            font-size: var(--font-size-medium);
        }
    }

    .panel-content {
        flex: 1;
        overflow: hidden;
        min-height: var(--panel-content-min-height);
        padding: var(--spacing-base);
        display: flex;
        flex-direction: column;

        .empty-state {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 250px;
            color: var(--text-secondary);

            .empty-icon {
                font-size: var(--icon-size-xxl);
                margin-bottom: var(--spacing-base);
                color: var(--text-disabled);
            }

            p {
                margin: 0;
                font-size: var(--font-size-base);
            }
        }

        // 标签页内容样式
        .facilities-tab,
        .monitoring-stations-tab {
            .device-type-selector {
                margin-bottom: var(--spacing-base);
                flex-shrink: 0;

                .selector-group {
                    margin-bottom: var(--spacing-medium);

                    .selector-label {
                        display: block;
                        font-size: var(--font-size-base);
                        font-weight: var(--font-weight-medium);
                        color: var(--text-primary);
                        margin-bottom: var(--spacing-small);
                    }
                }

                .selector-stats {
                    font-size: var(--font-size-extra-small);
                    color: var(--text-secondary);
                    text-align: center;
                    padding: var(--spacing-small);
                    background: var(--bg-secondary);
                    border-radius: var(--border-radius-base);

                    .stats-main {
                        margin-bottom: var(--spacing-mini);
                    }

                    .stats-detail {
                        font-size: var(--font-size-mini);
                        color: var(--text-tertiary);
                        font-style: italic;
                    }

                    .invalid-count {
                        color: var(--warning-color);
                    }
                }
            }
        }

        // 监测站点标签页特殊样式
        .monitoring-stations-tab {
            .selector-stats {
                margin-bottom: var(--spacing-base);
                flex-shrink: 0;
            }
        }

        .device-list {
            flex: 1;
            overflow-y: auto;

            .device-items {
                display: flex;
                flex-direction: column;
                gap: var(--spacing-medium);
            }
        }
    }

    .device-item {
        @include flex-start;
        padding: var(--spacing-base);
        border-radius: var(--border-radius-md);
        cursor: pointer;
        transition: background-color var(--transition-duration-fast) var(--transition-timing-function), border-color var(--transition-duration-fast) var(--transition-timing-function);
        border: var(--border-width-thin) solid var(--border-light);
        box-shadow: var(--shadow-card);

        &:hover {
            background: var(--bg-disabled);
            border-color: var(--border-color);
            box-shadow: var(--shadow-card-hover);
        }

        &.active {
            background: var(--primary-bg-light);
            border-color: var(--primary-color);
            box-shadow: var(--shadow-card-hover);
        }

        &.no-location {
            opacity: var(--disabled-opacity);

            .device-location.invalid {
                color: var(--warning-color);
                font-style: italic;
            }
        }

        .device-icon {
            width: 40px;
            height: 40px;
            border-radius: var(--border-radius-md);
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: var(--spacing-medium);
            box-sizing: border-box;

            .icon-symbol {
                font-size: var(--font-size-large);
                font-weight: var(--font-weight-bold);
                line-height: 1;
                user-select: none;
            }
        }

        .device-info {
            flex: 1;
            min-width: 0;

            .device-name {
                font-weight: var(--font-weight-bold);
                color: var(--text-primary);
                margin-bottom: var(--spacing-mini);
                @include text-ellipsis;
            }

            .device-type {
                font-size: var(--font-size-extra-small);
                color: var(--text-primary);
                margin-bottom: var(--spacing-micro);
            }

            .device-status {
                font-size: var(--font-size-extra-small);
                padding: var(--spacing-micro) var(--spacing-xs);
                border-radius: var(--border-radius-small);
                font-weight: var(--font-weight-medium);
                margin-bottom: var(--spacing-micro);

                &.status-normal,
                &.status-online {
                    background: var(--status-normal-bg);
                    color: var(--primary-color);
                }

                &.status-offline {
                    background: var(--bg-secondary);
                    color: var(--text-tertiary);
                }

                &.status-maintenance {
                    background: var(--warning-bg-color);
                    color: var(--warning-color);
                }

                &.status-fault {
                    background: var(--danger-bg-light);
                    color: var(--danger-color);
                }

                &.status-default {
                    background: var(--bg-secondary);
                    color: var(--text-tertiary);
                }
            }

            .device-location {
                font-size: var(--font-size-mini);
                color: var(--text-tertiary);
                display: flex;
                align-items: center;

                .location-icon,
                .warning-icon {
                    margin-right: var(--spacing-mini);
                    font-size: var(--font-size-micro);
                }

                &.invalid {
                    color: var(--warning-color);
                    font-style: italic;
                }
            }
        }
    }

    // 悬浮模式样式优化
    &.floating-mode {
        background: transparent;
        box-shadow: none;
        border-radius: 0;

        .panel-content {
            height: 100%;
            min-height: auto;
            padding: var(--spacing-medium);
        }

        .device-item {
            padding: var(--spacing-medium);
            margin-bottom: var(--spacing-small);
            background: var(--white-transparent-strong);
            backdrop-filter: blur(8px);

            &:hover {
                background: var(--bg-disabled);
            }

            &.active {
                background: var(--primary-bg-light);
            }
        }

        .selector-stats {
            background: var(--map-panel-stats-bg);
            backdrop-filter: blur(8px);
            padding: var(--spacing-small);
            border-radius: var(--border-radius-base);
        }
    }
}

// 响应式设计
@include respond-to(sm) {
    .facility-panel {
        .panel-content {
            padding: var(--spacing-medium);

            .device-item {
                padding: var(--spacing-medium);

                .device-info .device-name {
                    font-size: var(--font-size-small);
                }
            }
        }
    }
}
</style>