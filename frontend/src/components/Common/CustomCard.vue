<template>
    <div class="custom-card" :class="cardClasses">
        <!-- 卡片头部 -->
        <div v-if="hasHeader" class="custom-card-header">
            <div class="header-content">
                <slot name="header">
                    <span v-if="title" class="card-title">{{ title }}</span>
                </slot>
            </div>
            <div v-if="slots.extra" class="header-extra">
                <slot name="extra"></slot>
            </div>
        </div>

        <!-- 卡片内容 -->
        <div class="custom-card-content" :style="contentStyle" v-if="hasContent">
            <slot></slot>
        </div>
    </div>
</template>

<script setup>
import { computed, useSlots } from 'vue'

const slots = useSlots()

/**
 * 通用卡片组件属性
 */
const props = defineProps({
    // 卡片标题
    title: {
        type: String,
        default: ''
    },
    // 是否显示阴影
    shadow: {
        type: [String, Boolean],
        default: 'hover', // 'always', 'hover', 'never', false
        validator: (value) => ['always', 'hover', 'never', false, true].includes(value)
    },
    // 是否显示边框
    bordered: {
        type: Boolean,
        default: true
    },
    // 是否支持悬停效果
    hoverable: {
        type: Boolean,
        default: false
    },
    // 自定义内容高度
    contentHeight: {
        type: String,
        default: ''
    },
    // 自定义内边距
    padding: {
        type: String,
        default: 'normal' // 'none', 'small', 'normal', 'large'
    },
    // 是否隐藏头部边框
    hideHeaderBorder: {
        type: Boolean,
        default: false
    }
})

/**
 * 计算卡片CSS类名
 */
const cardClasses = computed(() => {
    const classes = []

    // 阴影类型
    if (props.shadow === true || props.shadow === 'always') {
        classes.push('shadow-always')
    } else if (props.shadow === 'hover') {
        classes.push('shadow-hover')
    }

    // 边框
    if (props.bordered) {
        classes.push('bordered')
    }

    // 悬停效果
    if (props.hoverable) {
        classes.push('hoverable')
    }

    // 头部状态
    if (hasHeader.value) {
        classes.push('has-header')
    }

    // 隐藏头部边框
    if (props.hideHeaderBorder) {
        classes.push('hide-header-border')
    }

    // 内边距
    classes.push(`padding-${props.padding}`)

    return classes
})

/**
 * 计算内容区域样式
 */
const contentStyle = computed(() => {
    const styles = {}
    if (props.contentHeight) {
        styles.height = props.contentHeight
    }
    return styles
})

/**
 * 判断是否有头部内容
 */
const hasHeader = computed(() => {
    return props.title || !!slots.header || !!slots.extra
})

/**
 * 判断是否有内容区域
 */
const hasContent = computed(() => {
    return !!slots.default
})
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

/**
 * 通用卡片组件样式
 */
.custom-card {
    background: var(--bg-primary);
    border-radius: var(--border-radius-large);
    overflow: hidden;
    transition: var(--transition-base);
    display: flex;
    flex-direction: column;

    // 边框样式
    &.bordered {
        border: 1px solid var(--black-transparent-light);
    }

    // 阴影样式
    &.shadow-always {
        box-shadow: var(--shadow-card);
    }

    &.shadow-hover {
        &:hover {
            box-shadow: var(--shadow-card);
        }
    }

    // 悬停效果
    &.hoverable {
        cursor: pointer;

        &:hover {
            transform: translateY(-2px);
            box-shadow: var(--shadow-card-hover);
        }
    }

    /**
     * 卡片头部
     */
    .custom-card-header {
        @include flex-between;
        background: var(--bg-primary);

        .header-content {
            flex: 1;

            .card-title {
                font-size: var(--font-size-lg);
                font-weight: var(--font-weight-semibold);
                color: var(--text-primary);
                margin: 0;
            }
        }

        .header-extra {
            flex-shrink: 0;
        }
    }

    // 显示头部边框的情况（默认行为）
    &:not(.hide-header-border) .custom-card-header {
        border-bottom: 1px solid var(--border-light);
    }

    .custom-card-content {
        flex: 1;
        overflow: auto;
        display: flex;
        flex-direction: column;
        position: relative;
        min-height: 0; // 防止 flex 子项溢出

        // 统一滚动条样式
        @include custom-scrollbar();

        // 优化嵌套组件布局
        >*:not(.el-tabs) {
            flex-shrink: 0;
        }

        // 为常见嵌套组件提供更好的适配
        :deep(.el-tree) {
            background: transparent;
        }

        :deep(.el-tabs) {
            height: 100%;

            .el-tabs__content {
                flex: 1;
                overflow: auto;
                @include custom-scrollbar();
            }
        }

        :deep(.el-table) {
            .el-table__body-wrapper {
                @include custom-scrollbar();
            }
        }
    }

    // 当没有头部时，内容区域保持顶部圆角
    &:not(.has-header) .custom-card-content {
        border-radius: var(--border-radius-large) var(--border-radius-large) 0 0;
    }

    /**
     * 内边距变体
     */
    &.padding-none {

        .custom-card-header,
        .custom-card-content {
            padding: 0;
        }
    }

    &.padding-small {

        .custom-card-header,
        .custom-card-content {
            padding: var(--spacing-small);
        }

        .custom-card-content {
            gap: var(--spacing-xs);
        }
    }

    &.padding-normal {

        .custom-card-header,
        .custom-card-content {
            padding: var(--padding-responsive-header);
        }

        .custom-card-content {
            gap: var(--spacing-small);
        }
    }

    &.padding-large {

        .custom-card-header,
        .custom-card-content {
            padding: var(--spacing-large);
        }

        .custom-card-content {
            gap: var(--spacing-medium);
        }
    }

    /**
     * 响应式设计
     */
    @include respond-to(sm) {
        border-radius: var(--border-radius-base);

        .custom-card-header {
            flex-direction: column;
            align-items: flex-start;
            gap: var(--spacing-small);

            .header-extra {
                width: 100%;
                @include flex-end;
            }
        }

        .custom-card-content {
            // 提升小屏幕下的内容密度
            min-height: 0;

            // 嵌套组件在小屏幕下的优化
            :deep(.el-tabs) {
                .el-tabs__nav-wrap {
                    padding: 0 var(--spacing-xs);
                }
            }

            :deep(.el-tree) {
                font-size: var(--font-size-sm);

                .el-tree-node__content {
                    height: var(--button-standard-size);
                    padding: 0 var(--spacing-xs);
                }
            }

            :deep(.el-table) {
                font-size: var(--font-size-sm);

                .el-table__header th,
                .el-table__body td {
                    padding: var(--spacing-xs);
                }
            }
        }

        &.padding-normal {

            .custom-card-header,
            .custom-card-content {
                padding: var(--spacing-xs);
            }
        }

        // 进一步优化极小屏幕
        @include respond-to(xs) {
            .custom-card-content {
                :deep(.el-tabs__nav-wrap) {
                    padding: 0;
                }

                :deep(.el-tree-node__content) {
                    height: var(--button-size-small);
                    font-size: var(--font-size-extra-small);
                }
            }

            &.padding-normal {

                .custom-card-header,
                .custom-card-content {
                    padding: var(--spacing-xs);
                }
            }
        }
    }
}
</style>