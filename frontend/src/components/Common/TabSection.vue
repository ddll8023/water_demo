<template>
    <div class="tab-section">
        <el-tabs :model-value="modelValue" class="custom-tabs" @update:model-value="handleTabChange">
            <el-tab-pane v-for="tab in tabs" :key="tab.name" :name="tab.name">
                <template #label>
                    <span class="tab-label">
                        <i class="fa" :class="tab.icon"></i>
                        {{ tab.label }}
                    </span>
                </template>
            </el-tab-pane>
        </el-tabs>
    </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
    /**
     * 当前选中的标签页值，用于v-model绑定
     */
    modelValue: {
        type: String,
        required: true
    },
    /**
     * 选项卡配置数组
     * @example 
     * [
     *   { name: 'tab1', label: '标签1', icon: 'fa-home' },
     *   { name: 'tab2', label: '标签2', icon: 'fa-user' }
     * ]
     */
    tabs: {
        type: Array,
        required: true,
        validator: (value) => {
            return value.every(tab => tab.name && tab.label && tab.icon)
        }
    }
})

const emit = defineEmits(['update:modelValue', 'tab-change'])

/**
 * 标签页切换事件处理
 * @param {String} tabName - 被选中的标签页名称
 */
const handleTabChange = (tabName) => {
    emit('update:modelValue', tabName)
    emit('tab-change', tabName)
}
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

.tab-section {
    background: var(--bg-primary);
    border-radius: var(--border-radius-large);
    box-shadow: var(--shadow-card);
    border: 1px solid var(--border-light);

    .custom-tabs {
        padding: 0 var(--spacing-large);

        .tab-label {
            @include flex-start;
            gap: var(--spacing-small);
            color: var(--text-secondary);
            transition: color var(--transition-duration-fast) var(--transition-timing-function);

            .fa {
                font-size: var(--font-size-base);
            }
        }

        :deep(.el-tabs__item.is-active) .tab-label {
            color: var(--primary-color);
            font-weight: var(--font-weight-medium);
        }
    }
}

@include respond-to(sm) {
    .tab-section {
        .custom-tabs {
            padding: 0 var(--spacing-sm);

            :deep(.el-tabs__nav-wrap) {
                padding: 0 var(--spacing-xs);
            }
        }
    }
}
</style>