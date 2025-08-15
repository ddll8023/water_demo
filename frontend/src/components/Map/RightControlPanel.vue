<template>
    <div class="right-control-panel" :class="{ collapsed: !panelVisible }">
        <div class="panel-header" @click="togglePanel">
            <!-- 展开状态显示 -->
            <template v-if="panelVisible">
                <i class="fa fa-list-ul header-icon"></i>
                <span class="header-title">图例说明</span>
                <i class="fa fa-angle-down toggle-icon" :class="{ expanded: panelVisible }"></i>
            </template>
            <!-- 收缩状态显示 -->
            <template v-else>
                <i class="fa fa-list-ul collapsed-icon" title="点击展开图例说明"></i>
            </template>
        </div>

        <div class="panel-content" :class="{ 'content-hidden': !panelVisible }">
            <!-- 工程站点控制 -->
            <div class="control-group">
                <div class="group-title">工程站点</div>
                <div class="legend-options">
                    <div class="legend-item" v-for="item in legendConfig.engineeringSites" :key="item.label">
                        <i class="legend-icon" :class="[item.icon, item.iconClass]"></i>
                        <span>{{ item.label }}</span>
                    </div>
                </div>
            </div>

            <!-- 预警站点图例 -->
            <div class="control-group">
                <div class="group-title">预警站点</div>
                <div class="legend-options">
                    <div class="legend-item" v-for="item in legendConfig.warningSites" :key="item.label">
                        <i class="legend-icon" :class="[item.icon, item.iconClass]"></i>
                        <span>{{ item.label }}</span>
                    </div>
                </div>
            </div>

            <!-- 供水管线图例 -->
            <div class="control-group">
                <div class="group-title">供水管线</div>
                <div class="legend-options">
                    <div class="legend-item" v-for="item in legendConfig.waterPipelines" :key="item.label">
                        <span :class="item.class"></span>
                        <span>{{ item.label }}</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue';

// Props定义
const props = defineProps({
    // 初始面板显示状态
    initialVisible: {
        type: Boolean,
        default: true
    }
});

// Events定义
const emit = defineEmits([
    'panel-toggle'
]);

// 面板显示状态
const panelVisible = ref(props.initialVisible);

// 图例配置数据
const legendConfig = reactive({
    // 工程站点图例
    engineeringSites: [
        {
            icon: 'fa fa-tint',
            iconClass: 'icon-reservoir',
            label: '两河口水库'
        },
        {
            icon: 'fa fa-tachometer',
            iconClass: 'icon-monitoring-station',
            label: '流量站'
        },
        {
            icon: 'fa fa-building',
            iconClass: 'icon-water-plant',
            label: '水厂'
        },
        {
            icon: 'fa fa-ship',
            iconClass: 'icon-floating-boat',
            label: '浮舟'
        },
        {
            icon: 'fa fa-compress',
            iconClass: 'icon-pumping-station',
            label: '加压站'
        },
        {
            icon: 'fa fa-play-circle',
            iconClass: 'icon-pump-open',
            label: '泵打开'
        },
        {
            icon: 'fa fa-stop-circle',
            iconClass: 'icon-pump-closed',
            label: '泵关闭'
        }
    ],
    // 预警站点图例
    warningSites: [
        {
            icon: 'fa fa-exclamation-triangle',
            iconClass: 'icon-warning-general',
            label: '一般预警'
        },
        {
            icon: 'fa fa-exclamation-circle',
            iconClass: 'icon-warning-serious',
            label: '严重预警'
        }
    ],
    // 供水管线图例
    waterPipelines: [
        {
            class: 'pipeline-legend main-pipeline',
            label: '供水干管'
        },
        {
            class: 'pipeline-legend branch-pipeline',
            label: '供水支管'
        }
    ]
});

/**
 * 切换面板显示状态
 */
const togglePanel = () => {
    panelVisible.value = !panelVisible.value;
    emit('panel-toggle', panelVisible.value);
};

// 暴露给父组件的方法
defineExpose({
    togglePanel
});
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

.right-control-panel {
    position: absolute;
    top: 80px;
    right: var(--spacing-base);
    background: var(--white-transparent-full);
    backdrop-filter: blur(12px);
    border-radius: var(--border-radius-xl);
    box-shadow: 0 8px 32px var(--black-transparent-medium);
    border: var(--border-width-thin) solid var(--white-transparent-base);
    z-index: var(--z-index-dropdown);
    min-width: var(--panel-width-default);
    max-width: 400px;
    overflow: hidden;
    transition: all var(--map-panel-transition-duration) var(--map-panel-transition-ease);
    animation: glassPanelFadeIn var(--map-panel-transition-duration) var(--map-panel-transition-ease);
    will-change: transform, opacity, box-shadow;
    transform-origin: top right;

    &:hover {
        box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
        backdrop-filter: blur(16px);
    }

    &.collapsed {
        min-width: auto;
        width: var(--map-panel-collapsed-height);
        height: var(--map-panel-collapsed-height);
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
        backdrop-filter: blur(8px);
        transform: scale(var(--map-panel-hidden-scale));

        .panel-header {
            background: rgba(248, 249, 250, 0.95);
            backdrop-filter: blur(12px);
            justify-content: center;

            .collapsed-icon {
                margin: 0;
                font-size: 24px;
                color: var(--primary-color);
                transition: all var(--map-panel-transition-duration) var(--map-panel-transition-ease);

                &:hover {
                    color: var(--primary-light);
                    transform: scale(1.15) rotate(5deg);
                }
            }
        }

        .panel-content {
            opacity: 0;
            transform: translateY(var(--map-panel-hidden-translate-y)) scale(var(--map-panel-hidden-scale));
            pointer-events: none;
            max-height: 0;
        }
    }

    .panel-header {
        display: flex;
        align-items: center;
        padding: var(--padding-panel-header);
        background: var(--map-panel-stats-bg);
        backdrop-filter: blur(8px);
        border-bottom: var(--border-width-thin) solid rgba(222, 226, 230, 0.6);
        cursor: pointer;
        position: relative;
        transition: all var(--map-panel-transition-duration) var(--map-panel-transition-ease);

        &:hover {
            background: rgba(240, 242, 245, 0.9);
            backdrop-filter: blur(12px);
        }

        &:active {
            transform: scale(0.98);
        }

        .header-icon {
            color: var(--primary-color);
            font-size: var(--font-size-medium);
            margin-right: var(--spacing-sm);
            transition: all var(--map-panel-transition-duration) var(--map-panel-transition-ease);
            transform-origin: center;
        }

        .header-title {
            flex: 1;
            font-weight: 600;
            color: var(--text-primary);
            font-size: var(--font-size-base);
            transition: all var(--map-panel-transition-duration) var(--map-panel-transition-ease);

            &.collapsed-title {
                opacity: 0;
                transform: translateX(-10px);
                transition: all var(--map-panel-transition-duration) var(--map-panel-transition-ease) 0.1s;
            }
        }

        .toggle-icon {
            color: var(--text-secondary);
            font-size: var(--font-size-base);
            margin-left: 8px;
            transition: all var(--map-panel-transition-duration) var(--map-panel-transition-ease);
            transform-origin: center;

            &.expanded {
                transform: rotate(180deg);
                color: var(--primary-color);
            }

            &:hover {
                color: var(--primary-color);
                transform: scale(1.1);
            }
        }
    }

    .panel-content {
        padding: var(--spacing-base);
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
            padding: 0 16px;
        }

        .control-group {
            margin-bottom: var(--spacing-large);
            animation: controlGroupFadeIn 0.6s cubic-bezier(0.4, 0, 0.2, 1);

            &:nth-child(1) {
                animation-delay: 0.1s;
            }

            &:nth-child(2) {
                animation-delay: 0.2s;
            }

            &:nth-child(3) {
                animation-delay: 0.3s;
            }

            &:last-child {
                margin-bottom: 0;
            }

            .group-title {
                font-size: var(--font-size-small);
                color: var(--text-secondary);
                font-weight: 600;
                margin-bottom: var(--spacing-medium);
                text-transform: uppercase;
                letter-spacing: 0.8px;
                position: relative;
                padding-left: 8px;

                &::before {
                    content: '';
                    position: absolute;
                    left: 0;
                    top: 50%;
                    transform: translateY(-50%);
                    width: 3px;
                    height: 12px;
                    background: linear-gradient(135deg, var(--primary-color), var(--primary-lighter));
                    border-radius: var(--border-radius-small);
                }
            }

            .legend-options {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: var(--spacing-sm);

                .legend-item {
                    display: flex;
                    align-items: center;
                    padding: var(--padding-legend-item);
                    background: var(--white-transparent-strong-90);
                    backdrop-filter: blur(8px);
                    border: var(--border-width-thin) solid var(--white-transparent-medium);
                    border-radius: var(--border-radius-large);
                    box-shadow: var(--shadow-card);
                    font-size: var(--font-size-extra-small);
                    transition: all var(--map-panel-transition-duration) var(--map-panel-transition-ease);
                    cursor: pointer;
                    position: relative;
                    overflow: hidden;

                    &::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%;
                        width: 100%;
                        height: 100%;
                        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
                        transition: left var(--map-panel-transition-duration) var(--map-panel-transition-ease);
                    }

                    &:hover {
                        background: rgba(248, 250, 252, 0.95);
                        border-color: rgba(64, 158, 255, 0.2);
                        box-shadow: 0 4px 16px var(--black-transparent-light-10);
                        transform: translateY(-2px);

                        &::before {
                            left: 100%;
                        }
                    }

                    .legend-icon {
                        font-size: var(--font-size-medium);
                        margin-right: var(--spacing-sm);
                        width: 18px;
                        text-align: center;
                        transition: transform var(--map-panel-transition-duration) var(--map-panel-transition-ease);

                        &.fa {
                            font-family: FontAwesome;
                        }
                    }

                    &:hover .legend-icon {
                        transform: scale(1.1);
                    }

                    span:last-child {
                        color: var(--text-primary);
                        font-weight: 500;
                        font-size: var(--font-size-extra-small);
                        line-height: 1.3;
                        transition: color var(--map-panel-transition-duration) var(--map-panel-transition-ease);
                    }

                    &:hover span:last-child {
                        color: var(--primary-color);
                    }
                }

                // 管线图例样式
                .pipeline-legend {
                    width: 16px;
                    height: 3px;
                    border-radius: var(--border-radius-small);
                    margin-right: var(--spacing-small);
                    transition: all var(--map-panel-transition-duration) var(--map-panel-transition-ease);

                    &.main-pipeline {
                        background: linear-gradient(90deg, #1890ff, #40a9ff);
                        height: 4px;
                    }

                    &.branch-pipeline {
                        background: linear-gradient(90deg, #40a9ff, #69c0ff);
                        height: 3px;
                    }
                }

                .legend-item:hover .pipeline-legend {
                    transform: scaleX(1.2);
                    box-shadow: var(--shadow-card-hover);
                }
            }
        }
    }

    @keyframes controlGroupFadeIn {
        0% {
            opacity: 0;
            transform: translateY(10px);
        }

        100% {
            opacity: 1;
            transform: translateY(0);
        }
    }
}

// 响应式设计优化
@media (max-width: 768px) {
    .right-control-panel {
        min-width: 280px;
        max-width: var(--panel-width-default);
        right: 8px;

        &.collapsed {
            min-width: 160px;
        }

        .panel-content {
            padding: var(--spacing-medium);

            .control-group .legend-options {
                grid-template-columns: 1fr;
                gap: var(--spacing-small);

                .legend-item {
                    padding: var(--padding-legend-item-mobile);
                    font-size: var(--font-size-mini);
                }
            }
        }
    }
}

// 图例图标颜色定义（使用业务变量）
.icon-reservoir {
    color: var(--facility-reservoir-color) !important;
}

.icon-monitoring-station {
    color: var(--facility-monitoring-station-color) !important;
}

.icon-pump-open {
    color: var(--pump-status-open-color) !important;
}

.icon-water-plant {
    color: var(--facility-water-plant-color) !important;
}

.icon-floating-boat {
    color: var(--facility-village-color) !important;
}

.icon-pumping-station {
    color: var(--facility-pumping-station-color) !important;
}

.icon-pump-closed {
    color: var(--pump-status-closed-color) !important;
}

.icon-warning-general {
    color: var(--warning-level-general-color) !important;
}

.icon-warning-serious {
    color: var(--warning-level-serious-color) !important;
}

// 性能优化
@media (prefers-reduced-motion: reduce) {

    .right-control-panel,
    .right-control-panel * {
        animation-duration: 0.01ms !important;
        animation-iteration-count: 1 !important;
        transition-duration: 0.01ms !important;
    }
}
</style>