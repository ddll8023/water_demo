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
.tab-section {
    background: var(--bg-primary);
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, .1);
    border: 1px solid var(--neutral-dark);

    .custom-tabs {
        padding: 0 20px;

        .tab-label {
            display: flex;
            align-items: center;
            gap: 8px;
            color: var(--text-secondary);
            transition: color 0.15s ease;

            .fa {
                font-size: 14px;
            }
        }

        :deep(.el-tabs__item.is-active) .tab-label {
            color: var(--primary-color);
            font-weight: 500;
        }
    }
}

@media (max-width: 768px) {
    .tab-section {
        .custom-tabs {
            padding: 0 10px;

            :deep(.el-tabs__nav-wrap) {
                padding: 0 5px;
            }
        }
    }
}
</style>