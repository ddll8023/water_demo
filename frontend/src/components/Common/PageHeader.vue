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
// 页面头部样式
.page-header {
    background: var(--bg-primary);
    padding: 20px;
    margin-bottom: 20px;
    border-radius: 8px;
    box-shadow: var(--shadow-card);

    .header-main {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 12px;
        flex-wrap: wrap;
    }

    .header-content {
        .page-title {
            color: var(--text-primary);
            font-size: 24px;
            font-weight: 600;
            margin-bottom: 8px;
            display: flex;
            align-items: center;

            .fa {
                color: var(--primary-color);
                margin-right: 12px;
                font-size: 28px;
            }
        }

        .page-description {
            color: var(--text-secondary);
            font-size: 14px;
            margin: 0;
        }
    }

    .header-actions {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-left: auto;
        flex-wrap: wrap;
    }
}
</style>