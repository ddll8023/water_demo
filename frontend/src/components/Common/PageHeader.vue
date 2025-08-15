<template>
    <div class="page-header">
        <div class="header-main">
            <div class="header-content">
                <h2 class="page-title">
                    <i v-if="icon" :class="`fa ${icon}`"></i>
                    {{ title }}
                </h2>
                <p v-if="description" class="page-description">{{ description }}</p>
            </div>
            <div class="header-actions">
                <slot name="actions"></slot>
            </div>
        </div>
    </div>
</template>

<script setup>
// 定义组件接收的属性
defineProps({
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
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

// 页面头部样式
.page-header {
    background: var(--bg-primary);
    padding: var(--spacing-large);
    margin-bottom: var(--spacing-large);
    border-radius: var(--border-radius-large);
    box-shadow: var(--shadow-card);

    .header-main {
        @include flex-between;
        gap: var(--spacing-medium);
        flex-wrap: wrap;
    }

    .header-content {
        .page-title {
            color: var(--text-primary);
            font-size: var(--font-size-extra-large);
            font-weight: var(--font-weight-bold);
            margin: var(--spacing-small) 0;
            @include flex-start;

            .fa {
                color: var(--primary-color);
                margin-right: var(--spacing-medium);
                font-size: var(--icon-size-2xl);
            }
        }

        .page-description {
            color: var(--text-secondary);
            font-size: var(--font-size-base);
            margin: 0;
        }
    }

    .header-actions {
        @include flex-start;
        gap: var(--spacing-small);
        margin-left: auto;
        flex-wrap: wrap;
    }
}
</style>