<!-- 
  通用对话框组件 - 封装Element Plus的el-dialog
  提供统一的样式、动画效果和交互行为
  支持标题、内容、底部按钮区域的自定义
-->
<template>
    <el-dialog v-model="dialogVisible" :title="title" :width="width" :top="top" :modal="modal"
        :append-to-body="appendToBody" :lock-scroll="lockScroll" :custom-class="customClass"
        :close-on-click-modal="closeOnClickModal" :close-on-press-escape="closeOnPressEscape" :show-close="showClose"
        :before-close="handleBeforeClose" :draggable="draggable" @open="handleOpen" @opened="handleOpened"
        @close="handleClose" @closed="handleClosed" destroy-on-close>
        <!-- 默认内容插槽 -->
        <slot></slot>

        <!-- 自定义页脚插槽 -->
        <template #footer>
            <slot name="footer">
                <div class="dialog-footer">
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
import { computed, ref, watch } from 'vue';
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
    // 是否可拖拽
    draggable: {
        type: Boolean,
        default: false
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
    'open',
    'opened',
    'close',
    'closed',
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
// 对话框打开前触发
const handleOpen = () => {
    emit('open');
};

// 对话框打开动画结束后触发
const handleOpened = () => {
    emit('opened');
};

// 对话框关闭前触发
const handleClose = () => {
    emit('close');
};

// 对话框关闭动画结束后触发
const handleClosed = () => {
    emit('closed');
};

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
    // 由父组件控制对话框的关闭，不再自动关闭
    // 注释掉原代码，让父组件完全控制关闭行为
    // if (!props.loading) {
    //     emit('update:visible', false);
    // }
};
</script>

<style scoped lang="scss">
.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding-top: 10px;
}

:deep(.el-dialog) {
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);

    .el-dialog__header {
        padding: 20px;
        border-bottom: 1px solid #f0f0f0;
        margin: 0;
    }

    .el-dialog__title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
    }

    .el-dialog__headerbtn {
        top: 20px;
        right: 20px;
    }

    .el-dialog__body {
        padding: 24px;
        color: #606266;
        font-size: 14px;
    }

    .el-dialog__footer {
        padding: 10px 20px 20px;
        border-top: none;
    }
}
</style>