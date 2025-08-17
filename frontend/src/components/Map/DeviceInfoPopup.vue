<template>
    <div v-if="visible" class="device-popup" :class="`device-popup--${popupDirection}`" :style="popupStyle" @click.stop>
        <!-- 弹窗内容 -->
        <div class="device-popup__content">
            <!-- 设备标题 -->
            <div class="device-popup__header">
                <div class="device-popup__title">
                    <div class="device-popup__icon" :style="{ color: deviceIcon.color }">
                        {{ deviceIcon.symbol }}
                    </div>
                    <span class="device-popup__name">{{ deviceName }}</span>
                </div>
                <CustomButton type="secondary" shape="circle" iconOnly @click="handleClose" title="关闭">
                    <i class="fa fa-times"></i>
                </CustomButton>
            </div>

            <!-- 设备信息 -->
            <div class="device-popup__body">
                <div class="device-popup__info">
                    <div class="device-popup__row" v-if="deviceType">
                        <span class="device-popup__label">类型</span>
                        <span class="device-popup__value">{{ deviceTypeText }}</span>
                    </div>

                    <div class="device-popup__row" v-if="deviceLocation">
                        <span class="device-popup__label">位置</span>
                        <span class="device-popup__value">{{ deviceLocation }}</span>
                    </div>

                    <!-- 监测站特有信息 -->
                    <template v-if="isMonitoringStation">
                        <div class="device-popup__row" v-if="device.monitoringItem">
                            <span class="device-popup__label">监测项目</span>
                            <span class="device-popup__value">{{ device.monitoringItem }}</span>
                        </div>
                        <div class="device-popup__row" v-if="device.latestValue !== undefined">
                            <span class="device-popup__label">最新数据</span>
                            <span class="device-popup__value device-popup__value--data">
                                {{ device.latestValue }} {{ device.unit || '' }}
                            </span>
                        </div>
                        <div class="device-popup__row" v-if="device.updateTime">
                            <span class="device-popup__label">更新时间</span>
                            <span class="device-popup__value device-popup__value--time">
                                {{ formatTime(device.updateTime) }}
                            </span>
                        </div>
                        <div class="device-popup__row" v-if="deviceStatus">
                            <span class="device-popup__label">状态</span>
                            <span class="device-popup__value"
                                :class="`device-popup__value--status-${deviceStatus?.toLowerCase()}`">
                                {{ getDictLabel(deviceStatus) }}
                            </span>
                        </div>
                    </template>

                    <!-- 设施特有信息 -->
                    <template v-else>
                        <div class="device-popup__row" v-if="device.capacity">
                            <span class="device-popup__label">容量</span>
                            <span class="device-popup__value">{{ device.capacity }}</span>
                        </div>
                        <div class="device-popup__row" v-if="deviceStatus">
                            <span class="device-popup__label">状态</span>
                            <span class="device-popup__value"
                                :class="`device-popup__value--status-${deviceStatus?.toLowerCase()}`">
                                {{ getDictLabel(deviceStatus) }}
                            </span>
                        </div>
                    </template>
                </div>

                <!-- 操作按钮 -->
                <div class="device-popup__actions" v-if="showActions">
                    <CustomButton type="primary" @click="handleViewDetail">
                        <i class="fa fa-eye"></i>
                        查看详情
                    </CustomButton>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue';
import { FACILITY_TYPE_CONFIG, MONITORING_ITEM_CONFIG, DEFAULT_DEVICE_ICON } from '@/utils/map/deviceIcon';
import CustomButton from '@/components/Common/CustomButton.vue';

// Props定义
const props = defineProps({
    // 弹窗显示状态
    visible: {
        type: Boolean,
        default: false
    },
    // 设备数据
    device: {
        type: Object,
        default: () => ({})
    },
    // 弹窗位置（像素坐标）
    position: {
        type: Object,
        default: () => ({ x: 0, y: 0 })
    },
    // 是否显示操作按钮
    showActions: {
        type: Boolean,
        default: true
    },
    // 地图容器尺寸（用于边界检测）
    containerSize: {
        type: Object,
        default: () => ({ width: 0, height: 0 })
    },
    // Marker尺寸信息
    markerSize: {
        type: Number,
        default: 16
    },
    // Marker边框宽度
    markerBorderWidth: {
        type: Number,
        default: 1
    },
    // 设备状态字典选项
    deviceStatusOptions: {
        type: Array,
        default: () => []
    }
});

// Events定义
const emit = defineEmits([
    'close',
    'view-detail'
]);

// 获取CSS变量值的工具函数
const getCSSVariableValue = (variableName) => {
    if (typeof window === 'undefined') return 0;
    const value = getComputedStyle(document.documentElement).getPropertyValue(variableName).trim();
    return parseFloat(value) || 0;
};

// 弹窗尺寸常量
const getPopupDimensions = () => ({
    POPUP_WIDTH: 280,
    POPUP_HEIGHT: 200,
    MARGIN: getCSSVariableValue('--spacing-base'),
    GAP: getCSSVariableValue('--spacing-small'),
    BORDER_WIDTH: getCSSVariableValue('--border-width-base'),
    OFFSET: getCSSVariableValue('--spacing-15')
});

// 计算设备名称
const deviceName = computed(() => {
    if (!props.device) return '未知设备';
    return props.device.stationName || props.device.name || '未知设备';
});

// 计算设备类型
const deviceType = computed(() => {
    if (!props.device) return '';
    return props.device.type || '';
});

// 计算设备类型文本
const deviceTypeText = computed(() => {
    const type = deviceType.value;
    if (!type) return '';

    // 监测站类型映射
    const monitoringTypeMap = {
        'water_level': '水位监测站',
        'flow': '流量监测站',
        'rainfall': '降雨监测站',
        'water_quality': '水质监测站',
        'water_condition': '水情监测站',
        'reservoir': '水库监测站'
    };

    // 设施类型映射
    const facilityTypeMap = {
        'pumping_station': '泵站',
        'water_plant': '水厂',
        'reservoir': '水库',
        'monitoring_station': '监测站',
        'pipeline': '管道',
        'gate': '闸门',
        'dam': '大坝',
        'canal': '渠道'
    };

    return monitoringTypeMap[type] || facilityTypeMap[type] || type;
});

// 计算设备位置信息
const deviceLocation = computed(() => {
    if (!props.device) return '';
    const { longitude, latitude } = props.device;
    if (!longitude || !latitude) return '';
    return `${longitude.toFixed(4)}, ${latitude.toFixed(4)}`;
});

// 判断是否为监测站
const isMonitoringStation = computed(() => {
    const hasStationFields = !!(props.device.stationName || props.device.stationId || props.device.monitoringItem);

    // 调试信息：输出设备分类结果
    console.log('设备分类判断:', {
        device: props.device,
        hasStationFields,
        stationName: props.device.stationName,
        stationId: props.device.stationId,
        monitoringItem: props.device.monitoringItem,
        type: props.device.type,
        status: props.device.status
    });

    return hasStationFields;
});

// 获取设备状态（兼容多种字段名）
const deviceStatus = computed(() => {
    const device = props.device;
    // 检查多种可能的状态字段名
    return device.status || device.deviceStatus || device.runStatus || device.operationStatus || null;
});

// 计算设备图标配置
const deviceIcon = computed(() => {
    if (!props.device) return DEFAULT_DEVICE_ICON;

    // 监测站图标
    if (isMonitoringStation.value && props.device.monitoringItem) {
        return MONITORING_ITEM_CONFIG[props.device.monitoringItem] || DEFAULT_DEVICE_ICON;
    }

    // 设施图标
    if (props.device.type) {
        return FACILITY_TYPE_CONFIG[props.device.type] || DEFAULT_DEVICE_ICON;
    }

    return DEFAULT_DEVICE_ICON;
});

// 计算弹窗位置样式
const popupStyle = computed(() => {
    const { x, y } = props.position;
    const { width: containerWidth, height: containerHeight } = props.containerSize;
    const dimensions = getPopupDimensions();
    const { POPUP_WIDTH, POPUP_HEIGHT, MARGIN, GAP, BORDER_WIDTH, OFFSET } = dimensions;

    // 如果容器尺寸无效，使用默认位置
    if (!containerWidth || !containerHeight || containerWidth <= 0 || containerHeight <= 0) {
        console.warn('容器尺寸无效，使用默认位置');
        return {
            left: `${x + 20}px`,
            top: `${y - POPUP_HEIGHT / 2}px`
        };
    }

    // 计算marker的半径
    const markerRadius = props.markerSize / 2;

    // 有效半径：包含图标描边宽度
    const effectiveRadius = markerRadius + (props.markerBorderWidth || 1);

    // 默认：弹窗在图标右侧边缘外 GAP，并加上自身边框宽度
    let left = x + effectiveRadius + GAP + BORDER_WIDTH + OFFSET;
    let top = y - POPUP_HEIGHT / 2;

    // 边界检测：右侧空间不足则放到左侧，同样考虑自身边框
    if (left + POPUP_WIDTH + MARGIN > containerWidth) {
        left = x - effectiveRadius - GAP - POPUP_WIDTH - BORDER_WIDTH;
    }

    if (top < MARGIN) {
        top = MARGIN;
    } else if (top + POPUP_HEIGHT + MARGIN > containerHeight) {
        top = containerHeight - POPUP_HEIGHT - MARGIN;
    }

    return {
        left: `${left}px`,
        top: `${top}px`
    };
});

// 计算弹窗朝向（用于切换对话箭头位置）
const popupDirection = computed(() => {
    const { x } = props.position;
    const { width: containerWidth } = props.containerSize;

    if (!containerWidth || containerWidth <= 0) {
        return 'right';
    }

    const dimensions = getPopupDimensions();
    const { POPUP_WIDTH, MARGIN, GAP, BORDER_WIDTH, OFFSET } = dimensions;

    const markerRadius = props.markerSize / 2;
    const effectiveRadius = markerRadius + (props.markerBorderWidth || 1);

    const defaultLeft = x + effectiveRadius + GAP + BORDER_WIDTH + OFFSET;
    if (defaultLeft + POPUP_WIDTH + MARGIN > containerWidth) {
        return 'left';
    }
    return 'right';
});

// 格式化时间
const formatTime = (timeStr) => {
    if (!timeStr) return '';
    try {
        const date = new Date(timeStr);
        return date.toLocaleString('zh-CN', {
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    } catch {
        return timeStr;
    }
};

// 获取字典标签
const getDictLabel = (value) => {
    if (!value || !props.deviceStatusOptions.length) {
        return value || '';
    }

    // 精确匹配
    let dictItem = props.deviceStatusOptions.find(item =>
        String(item.value) === String(value)
    );

    // 如果精确匹配失败，尝试忽略大小写匹配
    if (!dictItem) {
        dictItem = props.deviceStatusOptions.find(item =>
            String(item.value).toLowerCase() === String(value).toLowerCase()
        );
    }

    // 如果仍然匹配失败，尝试常见状态值映射
    if (!dictItem) {
        const statusMappings = {
            'NORMAL': 'normal',
            'FAULT': 'fault',
            'ERROR': 'fault',
            'MAINTENANCE': 'maintenance',
            'REPAIR': 'maintenance'
        };

        const mappedValue = statusMappings[String(value).toUpperCase()];
        if (mappedValue) {
            dictItem = props.deviceStatusOptions.find(item =>
                String(item.value) === mappedValue
            );
        }
    }

    return dictItem ? dictItem.label : value;
};

// 事件处理函数
const handleClose = () => {
    emit('close');
};

const handleViewDetail = () => {
    emit('view-detail', props.device);
};
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

.device-popup {
    position: absolute;
    width: 280px;
    min-height: 160px;
    background: var(--glass-panel-bg);
    backdrop-filter: blur(18px);
    -webkit-backdrop-filter: blur(18px);
    border: 1px solid var(--glass-panel-border);
    border-radius: var(--border-radius-large);
    box-shadow: var(--shadow-popup);
    z-index: var(--z-index-popover);
    animation: glassPopupFadeIn var(--transition-duration-fast) cubic-bezier(0.25, 0.46, 0.45, 0.94);
    transform-origin: left center;
    pointer-events: auto;

    // 毛玻璃效果增强
    &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(135deg,
                rgba(255, 255, 255, 0.08) 0%,
                rgba(255, 255, 255, 0.04) 100%);
        border-radius: inherit;
        pointer-events: none;
    }

    // 朝向：左侧展开时修正动画锚点
    &.device-popup--left {
        transform-origin: right center;
    }

    &__content {
        position: relative;
        z-index: 1;
        height: 100%;
        display: flex;
        flex-direction: column;
    }

    &__header {
        @include flex-between;
        padding: var(--spacing-base);
        border-bottom: 1px solid var(--black-transparent-thin);
        background: rgba(255, 255, 255, 0.06);
        border-radius: var(--border-radius-large) var(--border-radius-large) 0 0;

        .custom-button--circle {
            margin-left: var(--spacing-small);
        }
    }

    &__title {
        @include flex-start;
        flex: 1;
        min-width: 0;
    }

    &__icon {
        @include flex-center;
        display: flex;
        width: var(--icon-size-xl);
        height: var(--icon-size-xl);
        border-radius: 50%;
        background: var(--white-transparent-full);
        font-size: var(--font-size-lg);
        font-weight: var(--font-weight-bold);
        font-style: normal;
        line-height: 1;
        margin-right: var(--spacing-small);
        flex-shrink: 0;
        border: 1px solid currentColor;
    }

    &__name {
        font-weight: var(--font-weight-bold);
        color: var(--text-primary);
        font-size: var(--font-size-base);
        line-height: 1.4;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }



    &__body {
        flex: 1;
        padding: var(--spacing-base);
        display: flex;
        flex-direction: column;
        gap: var(--spacing-base);
    }

    &__info {
        display: flex;
        flex-direction: column;
        gap: var(--spacing-small);
    }

    &__row {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        line-height: 1.4;
        gap: var(--spacing-small);
    }

    &__label {
        font-size: var(--font-size-small);
        color: var(--text-secondary);
        font-weight: var(--font-weight-medium);
        flex-shrink: 0;
        min-width: 60px;
    }

    &__value {
        font-size: var(--font-size-small);
        color: var(--text-primary);
        text-align: right;
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;

        &--data {
            font-weight: var(--font-weight-bold);
            color: var(--primary-color);
        }

        &--time {
            color: var(--text-tertiary);
            font-size: var(--font-size-extra-small);
        }

        &--status-normal {
            color: var(--success-color);
        }

        &--status-fault {
            color: var(--danger-color);
        }

        &--status-error {
            color: var(--danger-color);
        }

        &--status-maintenance {
            color: var(--warning-color);
        }

        &--status-repair {
            color: var(--warning-color);
        }
    }

    &__actions {
        display: flex;
        gap: var(--spacing-small);
        margin-top: auto;
    }
}

// 响应式适配
@include respond-to(md) {
    .device-popup {
        width: 260px;
        min-height: 140px;

        &__header {
            padding: var(--spacing-medium);
        }

        &__body {
            padding: var(--spacing-medium);
        }

        &__icon {
            width: var(--icon-size-lg);
            height: var(--icon-size-lg);
            font-size: var(--font-size-small);
            font-style: normal;
            line-height: 1;
        }
    }
}

@include respond-to(sm) {
    .device-popup {
        width: 240px;
        min-height: 120px;

        &__header {
            padding: var(--spacing-small);
        }

        &__body {
            padding: var(--spacing-small);
        }

        &__name {
            font-size: var(--font-size-small);
        }

        &__actions {
            flex-direction: column;
        }
    }
}
</style>