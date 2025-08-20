<!-- 
  通用对话框组件 - 封装Element Plus的el-dialog
  提供统一的样式、动画效果和交互行为
  支持标题、内容、底部按钮区域的自定义
-->
<template>
    <el-dialog v-model="dialogVisible" :title="title" :width="width" :top="top" :modal="modal"
        :append-to-body="appendToBody" :lock-scroll="lockScroll" :custom-class="customClass"
        :close-on-click-modal="closeOnClickModal" :close-on-press-escape="closeOnPressEscape" :show-close="showClose"
        :before-close="handleBeforeClose" @close="handleClose" destroy-on-close>
        <!-- 默认内容插槽 -->
        <slot></slot>

        <!-- 自定义页脚插槽 -->
        <template #footer>
            <slot name="footer">
                <div class="custom-dialog__footer">
                    <CustomButton v-if="showCancelButton" @click="handleCancel">
                        {{ cancelButtonText }}
                    </CustomButton>
                    <CustomButton v-if="showConfirmButton" type="primary" :loading="loading" @click="handleConfirm">
                        {{ confirmButtonText }}
                    </CustomButton>
                </div>
            </slot>
        </template>
    </el-dialog>
</template>

<script setup>
import { computed } from 'vue';
import CustomButton from './CustomButton.vue';

/**
 * ----------------------------------------
 * 组件属性定义
 * ----------------------------------------
 */
const props = defineProps({
    // 对话框可见性，用于v-model绑定
    visible: {
        type: Boolean,
        default: false
    },
    // 对话框标题
    title: {
        type: String,
        default: '对话框'
    },
    // 对话框宽度
    width: {
        type: String,
        default: '500px'
    },
    // 对话框距离顶部的距离
    top: {
        type: String,
        default: '15vh'
    },
    // 是否需要遮罩层
    modal: {
        type: Boolean,
        default: true
    },
    // 是否将对话框插入至body元素
    appendToBody: {
        type: Boolean,
        default: true
    },
    // 是否在显示对话框时将body滚动锁定
    lockScroll: {
        type: Boolean,
        default: true
    },
    // 自定义类名
    customClass: {
        type: String,
        default: ''
    },
    // 是否可以通过点击遮罩层关闭对话框
    closeOnClickModal: {
        type: Boolean,
        default: false
    },
    // 是否可以通过按下ESC键关闭对话框
    closeOnPressEscape: {
        type: Boolean,
        default: true
    },
    // 是否显示关闭按钮
    showClose: {
        type: Boolean,
        default: true
    },
    // 关闭前的回调函数
    beforeClose: {
        type: Function,
        default: null
    },
    // 是否显示取消按钮
    showCancelButton: {
        type: Boolean,
        default: true
    },
    // 是否显示确认按钮
    showConfirmButton: {
        type: Boolean,
        default: true
    },
    // 取消按钮文本
    cancelButtonText: {
        type: String,
        default: '取消'
    },
    // 确认按钮文本
    confirmButtonText: {
        type: String,
        default: '确定'
    },
    // 确认按钮加载状态
    loading: {
        type: Boolean,
        default: false
    }
});

/**
 * ----------------------------------------
 * 事件定义
 * ----------------------------------------
 */
const emit = defineEmits([
    'update:visible',
    'cancel',
    'confirm'
]);

/**
 * ----------------------------------------
 * 组件状态
 * ----------------------------------------
 */
// 对话框可见性的计算属性，用于v-model绑定
const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value)
});

/**
 * ----------------------------------------
 * 事件处理方法
 * ----------------------------------------
 */
// 处理beforeClose钩子
const handleBeforeClose = (done) => {
    if (props.beforeClose) {
        props.beforeClose(done);
    } else {
        done();
    }
};

// 处理取消按钮点击事件
const handleCancel = () => {
    emit('cancel');
    emit('update:visible', false);
};

// 处理确认按钮点击事件
const handleConfirm = () => {
    emit('confirm');
};
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

.custom-dialog__footer {
    @include flex-end;
    gap: var(--spacing-medium);
    padding-top: var(--spacing-sm);
}

:deep(.el-dialog) {
    border-radius: var(--border-radius-large);
    overflow: hidden;
    box-shadow: var(--shadow-card-hover);

    .el-dialog__header {
        padding: var(--spacing-large);
        border-bottom: 1px solid var(--border-light);
        margin: 0;
    }

    .el-dialog__title {
        font-size: var(--font-size-medium);
        font-weight: var(--font-weight-bold);
        color: var(--text-primary);
    }

    .el-dialog__headerbtn {
        top: var(--spacing-large);
        right: var(--spacing-large);
    }

    .el-dialog__body {
        padding: var(--spacing-extra-large);
        color: var(--text-secondary);
        font-size: var(--font-size-base);
    }

    .el-dialog__footer {
        padding: var(--spacing-sm) var(--spacing-large) var(--spacing-large);
        border-top: none;
    }
}
</style>