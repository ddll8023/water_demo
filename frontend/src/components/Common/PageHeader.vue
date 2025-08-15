<template>
    <CustomCard shadow="always" padding="normal" :hide-header-border="true" class="page-header">
        <template #header>
            <div class="header-content">
                <h2 class="page-title">
                    <i v-if="icon" :class="iconClass"></i>
                    {{ title }}
                </h2>
                <p v-if="description" class="page-description">{{ description }}</p>
            </div>
        </template>
        <template #extra>
            <div class="header-actions">
                <slot name="actions"></slot>
            </div>
        </template>
    </CustomCard>
</template>

<script setup>
import { computed } from 'vue'
import CustomCard from './CustomCard.vue'

// 定义组件接收的属性
const props = defineProps({
    // 页面标题
    title: {
        type: String,
        required: true
    },
    // 图标类名（不含fa前缀）
    icon: {
        type: String,
        default: ''
    },
    // 页面描述
    description: {
        type: String,
        default: ''
    }
})

// 计算图标类名，规范化处理
const iconClass = computed(() => {
    if (!props.icon) return ''

    // 如果已经包含fa前缀，直接返回
    if (props.icon.startsWith('fa ') || props.icon.startsWith('fa-')) {
        return `fa ${props.icon}`
    }

    // 否则添加fa前缀
    return `fa ${props.icon}`
})
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

// 页面头部样式 - 仅保留标题和描述的专有样式
.page-header {
    margin-bottom: var(--spacing-large);

    .header-content {
        .page-title {
            color: var(--text-primary);
            font-size: var(--font-size-extra-large);
            font-weight: var(--font-weight-bold);
            margin: 0;
            @include flex-start;
            align-items: center;

            .fa {
                color: var(--primary-color);
                margin-right: var(--spacing-medium);
                font-size: var(--icon-size-xl);
            }
        }

        .page-description {
            color: var(--text-secondary);
            font-size: var(--font-size-base);
            margin: var(--spacing-small) 0 0 0;
        }
    }

    .header-actions {
        @include flex-start;
        gap: var(--spacing-small);
        flex-wrap: wrap;
    }
}

// 响应式设计
@include respond-to(sm) {
    .page-header {
        margin-bottom: var(--spacing-medium);

        .header-content {
            .page-title {
                font-size: var(--font-size-large);

                .fa {
                    font-size: var(--icon-size-md);
                    margin-right: var(--spacing-small);
                }
            }

            .page-description {
                font-size: var(--font-size-sm);
            }
        }
    }
}
</style>