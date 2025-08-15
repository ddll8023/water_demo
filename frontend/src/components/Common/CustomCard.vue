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


    // 当没有头部时，内容区域保持顶部圆角
    &:not(.has-header) .custom-card-content {
        border-radius: var(--border-radius-large) var(--border-radius-large) 0 0;
    }

    /**
     * 内边距变体
     */
    &.padding-none {
        .custom-card-header {
            padding: 0;
        }

        .custom-card-content {
            padding: 0;
        }
    }

    &.padding-small {
        .custom-card-header {
            padding: var(--spacing-small);
        }

        .custom-card-content {
            padding: var(--spacing-small);
        }
    }

    &.padding-normal {
        .custom-card-header {
            padding: var(--padding-responsive-header);
        }

        .custom-card-content {
            padding: var(--padding-responsive-content);
        }
    }

    &.padding-large {
        .custom-card-header {
            padding: var(--spacing-large);
        }

        .custom-card-content {
            padding: var(--spacing-large);
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

        &.padding-normal {

            .custom-card-header,
            .custom-card-content {
                padding: var(--spacing-small);
            }
        }
    }
}
</style>