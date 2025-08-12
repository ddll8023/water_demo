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
                <i class="fa fa-list-ul header-icon collapsed-icon" title="点击展开图例说明"></i>
                <span class="header-title collapsed-title">图例</span>
                <i class="fa fa-angle-down toggle-icon" :class="{ expanded: panelVisible }"></i>
            </template>
        </div>

        <div class="panel-content" :class="{ 'content-hidden': !panelVisible }">
            <!-- 工程站点控制 -->
            <div class="control-group">
                <div class="group-title">工程站点</div>
                <div class="legend-options">
                    <div class="legend-item">
                        <i class="legend-icon fa fa-tint" style="color: #1890ff;"></i>
                        <span>两河口水库</span>
                    </div>
                    <div class="legend-item">
                        <i class="legend-icon fa fa-tachometer" style="color: #52c41a;"></i>
                        <span>流量站</span>
                    </div>
                    <div class="legend-item">
                        <i class="legend-icon fa fa-building" style="color: #722ed1;"></i>
                        <span>水厂</span>
                    </div>
                    <div class="legend-item">
                        <i class="legend-icon fa fa-ship" style="color: #13c2c2;"></i>
                        <span>浮舟</span>
                    </div>
                    <div class="legend-item">
                        <i class="legend-icon fa fa-compress" style="color: #fa8c16;"></i>
                        <span>加压站</span>
                    </div>
                    <div class="legend-item">
                        <i class="legend-icon fa fa-play-circle" style="color: #52c41a;"></i>
                        <span>泵打开</span>
                    </div>
                    <div class="legend-item">
                        <i class="legend-icon fa fa-stop-circle" style="color: #f5222d;"></i>
                        <span>泵关闭</span>
                    </div>
                </div>
            </div>

            <!-- 预警站点图例 -->
            <div class="control-group">
                <div class="group-title">预警站点</div>
                <div class="legend-options">
                    <div class="legend-item">
                        <i class="legend-icon fa fa-exclamation-triangle" style="color: #faad14;"></i>
                        <span>一般预警</span>
                    </div>
                    <div class="legend-item">
                        <i class="legend-icon fa fa-exclamation-circle" style="color: #f5222d;"></i>
                        <span>严重预警</span>
                    </div>
                </div>
            </div>

            <!-- 供水管线图例 -->
            <div class="control-group">
                <div class="group-title">供水管线</div>
                <div class="legend-options">
                    <div class="legend-item">
                        <span class="pipeline-legend main-pipeline"></span>
                        <span>供水干管</span>
                    </div>
                    <div class="legend-item">
                        <span class="pipeline-legend branch-pipeline"></span>
                        <span>供水支管</span>
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
    right: 16px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(12px);
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
    border: 1px solid rgba(255, 255, 255, 0.2);
    z-index: 1000;
    min-width: 320px;
    max-width: 400px;
    overflow: hidden;
    transition: all var(--panel-transition-duration) var(--panel-transition-ease);
    animation: panelFadeIn var(--panel-transition-duration) var(--panel-transition-ease);
    will-change: transform, opacity, box-shadow;

    &:hover {
        box-shadow: 0 12px 48px rgba(0, 0, 0, 0.2);
        backdrop-filter: blur(16px);
    }

    &.collapsed {
        min-width: 200px;
        height: var(--panel-collapsed-height);
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
        backdrop-filter: blur(8px);

        .panel-header {
            background: rgba(248, 249, 250, 0.95);
            backdrop-filter: blur(10px);

            .header-icon {
                color: #409eff;
                transition: all var(--panel-transition-duration) var(--panel-transition-ease);
                transform: scale(1.1);

                &:hover {
                    color: #66b1ff;
                    transform: scale(1.2);
                }
            }

            .header-title.collapsed-title {
                opacity: 1;
                transform: translateX(0);
            }

            .toggle-icon {
                opacity: 0.7;
                transform: rotate(0deg) scale(0.9);

                &:hover {
                    opacity: 1;
                    transform: rotate(0deg) scale(1);
                }
            }
        }

        .panel-content {
            opacity: 0;
            transform: translateY(var(--panel-hidden-translate-y)) scale(var(--panel-hidden-scale));
            pointer-events: none;
            max-height: 0;
        }
    }

    .panel-header {
        display: flex;
        align-items: center;
        padding: 12px 16px;
        background: rgba(248, 249, 250, 0.8);
        backdrop-filter: blur(8px);
        border-bottom: 1px solid rgba(222, 226, 230, 0.6);
        cursor: pointer;
        position: relative;
        transition: all var(--panel-transition-duration) var(--panel-transition-ease);

        &:hover {
            background: rgba(240, 242, 245, 0.9);
            backdrop-filter: blur(12px);
        }

        &:active {
            transform: scale(0.98);
        }

        .header-icon {
            color: #409eff;
            font-size: 16px;
            margin-right: 10px;
            transition: all var(--panel-transition-duration) var(--panel-transition-ease);
            transform-origin: center;
        }

        .header-title {
            flex: 1;
            font-weight: 600;
            color: #303133;
            font-size: 14px;
            transition: all var(--panel-transition-duration) var(--panel-transition-ease);

            &.collapsed-title {
                opacity: 0;
                transform: translateX(-10px);
                transition: all var(--panel-transition-duration) var(--panel-transition-ease) 0.1s;
            }
        }

        .toggle-icon {
            color: #606266;
            font-size: 14px;
            margin-left: 8px;
            transition: all var(--panel-transition-duration) var(--panel-transition-ease);
            transform-origin: center;

            &.expanded {
                transform: rotate(180deg);
                color: #409eff;
            }

            &:hover {
                color: #409eff;
                transform: scale(1.1);
            }
        }
    }

    .panel-content {
        padding: 16px;
        transition: all var(--panel-transition-duration) var(--panel-transition-ease);
        max-height: 2000px;
        opacity: 1;
        transform: translateY(0) scale(1);
        transform-origin: top center;

        &.content-hidden {
            opacity: 0;
            transform: translateY(var(--panel-hidden-translate-y)) scale(var(--panel-hidden-scale));
            pointer-events: none;
            max-height: 0;
            padding: 0 16px;
        }

        .control-group {
            margin-bottom: 20px;
            animation: groupFadeIn 0.6s cubic-bezier(0.4, 0, 0.2, 1);

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
                font-size: 13px;
                color: #606266;
                font-weight: 600;
                margin-bottom: 12px;
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
                    background: linear-gradient(135deg, #409eff, #66b1ff);
                    border-radius: 2px;
                }
            }

            .legend-options {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 10px;

                .legend-item {
                    display: flex;
                    align-items: center;
                    padding: 10px 12px;
                    background: rgba(255, 255, 255, 0.9);
                    backdrop-filter: blur(8px);
                    border: 1px solid rgba(255, 255, 255, 0.3);
                    border-radius: 8px;
                    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
                    font-size: 12px;
                    transition: all var(--panel-transition-duration) var(--panel-transition-ease);
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
                        transition: left var(--panel-transition-duration) var(--panel-transition-ease);
                    }

                    &:hover {
                        background: rgba(248, 250, 252, 0.95);
                        border-color: rgba(64, 158, 255, 0.2);
                        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
                        transform: translateY(-2px);

                        &::before {
                            left: 100%;
                        }
                    }

                    .legend-icon {
                        font-size: 16px;
                        margin-right: 10px;
                        width: 18px;
                        text-align: center;
                        transition: transform var(--panel-transition-duration) var(--panel-transition-ease);

                        &.fa {
                            font-family: FontAwesome;
                        }
                    }

                    &:hover .legend-icon {
                        transform: scale(1.1);
                    }

                    span:last-child {
                        color: #303133;
                        font-weight: 500;
                        font-size: 12px;
                        line-height: 1.3;
                        transition: color var(--panel-transition-duration) var(--panel-transition-ease);
                    }

                    &:hover span:last-child {
                        color: #409eff;
                    }
                }

                // 管线图例样式
                .pipeline-legend {
                    width: 16px;
                    height: 3px;
                    border-radius: 2px;
                    margin-right: 8px;
                    transition: all var(--panel-transition-duration) var(--panel-transition-ease);

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
                    box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
                }
            }
        }
    }

    @keyframes panelFadeIn {
        0% {
            opacity: 0;
            transform: translateY(var(--panel-hidden-translate-y)) translateZ(0) scale(var(--panel-hidden-scale));
            backdrop-filter: blur(0px);
        }

        100% {
            opacity: 1;
            transform: translateY(0) translateZ(0) scale(1);
            backdrop-filter: blur(12px);
        }
    }

    @keyframes groupFadeIn {
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
        max-width: 320px;
        right: 8px;

        &.collapsed {
            min-width: 160px;
        }

        .panel-content {
            padding: 12px;

            .control-group .legend-options {
                grid-template-columns: 1fr;
                gap: 8px;

                .legend-item {
                    padding: 8px 10px;
                    font-size: 11px;
                }
            }
        }
    }
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