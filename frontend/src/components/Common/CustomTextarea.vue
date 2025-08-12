<!-- 自定义多行文本输入组件 - 替换Element UI的el-input type="textarea" -->
<template>
  <div class="custom-textarea" :class="textareaClasses">
    <div class="textarea-wrapper">
      <textarea ref="textareaRef" v-model="textareaValue" :placeholder="placeholder" :disabled="disabled"
        :readonly="readonly" :maxlength="maxlength" :minlength="minlength" :rows="rows" :cols="cols" :name="name"
        :id="textareaId" :style="textareaStyle" class="textarea-inner" @input="handleInput" @change="handleChange"
        @focus="handleFocus" @blur="handleBlur" @keyup="handleKeyup" @keydown="handleKeydown"
        @keypress="handleKeypress"></textarea>

      <!-- 清除按钮 -->
      <div v-if="clearable && textareaValue && !disabled && !readonly" class="clear-button" @click="handleClear">
        <i class="fa fa-times"></i>
      </div>
    </div>

    <!-- 字数统计 -->
    <div v-if="showWordLimit && maxlength" class="word-limit">
      {{ textareaValue.length }} / {{ maxlength }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted } from 'vue'

// ========================================
// 组件属性定义
// ========================================
const props = defineProps({
  // v-model绑定值
  modelValue: {
    type: [String, Number],
    default: ''
  },
  // 占位文字
  placeholder: {
    type: String,
    default: ''
  },
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  },
  // 是否只读
  readonly: {
    type: Boolean,
    default: false
  },
  // 是否可清空
  clearable: {
    type: Boolean,
    default: false
  },
  // 最大长度
  maxlength: {
    type: [String, Number],
    default: null
  },
  // 最小长度
  minlength: {
    type: [String, Number],
    default: null
  },
  // 是否显示字数统计
  showWordLimit: {
    type: Boolean,
    default: false
  },
  // 行数
  rows: {
    type: [String, Number],
    default: 3
  },
  // 列数
  cols: {
    type: [String, Number],
    default: null
  },
  // 是否自适应高度
  autosize: {
    type: [Boolean, Object],
    default: false
  },
  // 尺寸
  size: {
    type: String,
    default: 'default',
    validator: (value) => ['large', 'default', 'small'].includes(value)
  },
  // 名称
  name: {
    type: String,
    default: ''
  },
  // ID
  id: {
    type: String,
    default: ''
  },
  // 验证状态
  validateState: {
    type: String,
    default: '',
    validator: (value) => ['', 'success', 'warning', 'error'].includes(value)
  },
  // 自定义样式
  style: {
    type: Object,
    default: () => ({})
  }
})

// ========================================
// 事件定义
// ========================================
const emit = defineEmits([
  'update:modelValue',
  'input',
  'change',
  'focus',
  'blur',
  'clear',
  'keyup',
  'keydown',
  'keypress'
])

// ========================================
// 响应式状态
// ========================================
const textareaRef = ref()
const focused = ref(false)
const textareaStyle = ref({})

// ========================================
// 计算属性
// ========================================
const textareaValue = computed({
  get() {
    return props.modelValue
  },
  set(value) {
    emit('update:modelValue', value)
  }
})

const textareaId = computed(() => {
  return props.id || `custom-textarea-${Math.random().toString(36).substr(2, 9)}`
})

const textareaClasses = computed(() => {
  return [
    `custom-textarea--${props.size}`,
    {
      'is-disabled': props.disabled,
      'is-readonly': props.readonly,
      'is-focused': focused.value,
      'is-autosize': props.autosize,
      [`is-${props.validateState}`]: props.validateState
    }
  ]
})

// ========================================
// 自适应高度处理
// ========================================
/**
 * 调整文本域自适应高度
 */
const resizeTextarea = () => {
  if (!props.autosize || !textareaRef.value) return

  const textarea = textareaRef.value
  const { minRows = 1, maxRows = null } = typeof props.autosize === 'object' ? props.autosize : {}

  // 重置高度以获取正确的scrollHeight
  textarea.style.height = 'auto'

  // 计算行高
  const computedStyle = window.getComputedStyle(textarea)
  const lineHeight = parseInt(computedStyle.lineHeight) || parseInt(computedStyle.fontSize) * 1.2
  const paddingTop = parseInt(computedStyle.paddingTop)
  const paddingBottom = parseInt(computedStyle.paddingBottom)
  const borderTop = parseInt(computedStyle.borderTopWidth)
  const borderBottom = parseInt(computedStyle.borderBottomWidth)

  // 计算内容高度
  const contentHeight = textarea.scrollHeight - paddingTop - paddingBottom
  const minHeight = lineHeight * minRows + paddingTop + paddingBottom + borderTop + borderBottom

  let height = Math.max(contentHeight + paddingTop + paddingBottom + borderTop + borderBottom, minHeight)

  // 应用最大行数限制
  if (maxRows && maxRows > 0) {
    const maxHeight = lineHeight * maxRows + paddingTop + paddingBottom + borderTop + borderBottom
    height = Math.min(height, maxHeight)
  }

  textareaStyle.value = {
    ...props.style,
    height: `${height}px`,
    overflowY: height >= (maxRows ? lineHeight * maxRows + paddingTop + paddingBottom + borderTop + borderBottom : Infinity) ? 'auto' : 'hidden'
  }
}

// ========================================
// 事件处理函数
// ========================================
/**
 * 处理输入事件
 * @param {Event} event 输入事件对象
 */
const handleInput = (event) => {
  const value = event.target.value
  emit('update:modelValue', value)
  emit('input', value)

  // 自适应高度
  if (props.autosize) {
    nextTick(() => {
      resizeTextarea()
    })
  }
}

/**
 * 处理值变化事件
 * @param {Event} event 值变化事件对象
 */
const handleChange = (event) => {
  emit('change', event.target.value)
}

/**
 * 处理获取焦点事件
 * @param {Event} event 焦点事件对象
 */
const handleFocus = (event) => {
  focused.value = true
  emit('focus', event)
}

/**
 * 处理失去焦点事件
 * @param {Event} event 焦点事件对象
 */
const handleBlur = (event) => {
  focused.value = false
  emit('blur', event)
}

/**
 * 处理清空内容事件
 */
const handleClear = () => {
  emit('update:modelValue', '')
  emit('clear')
  emit('input', '')
  emit('change', '')
  nextTick(() => {
    textareaRef.value?.focus()
    if (props.autosize) {
      resizeTextarea()
    }
  })
}

/**
 * 处理键盘按键释放事件
 * @param {KeyboardEvent} event 键盘事件对象
 */
const handleKeyup = (event) => {
  emit('keyup', event)
}

/**
 * 处理键盘按下事件
 * @param {KeyboardEvent} event 键盘事件对象
 */
const handleKeydown = (event) => {
  emit('keydown', event)
}

/**
 * 处理键盘按键事件
 * @param {KeyboardEvent} event 键盘事件对象
 */
const handleKeypress = (event) => {
  emit('keypress', event)
}

// ========================================
// 公开方法
// ========================================
/**
 * 获取输入焦点
 */
const focus = () => {
  textareaRef.value?.focus()
}

/**
 * 失去输入焦点
 */
const blur = () => {
  textareaRef.value?.blur()
}

/**
 * 选择所有文本内容
 */
const select = () => {
  textareaRef.value?.select()
}

/**
 * 暴露给父组件的方法
 */
defineExpose({
  focus,
  blur,
  select,
  textareaRef
})

// ========================================
// 监听器与生命周期钩子
// ========================================
// 监听值变化，自适应高度
watch(() => props.modelValue, () => {
  if (props.autosize) {
    nextTick(() => {
      resizeTextarea()
    })
  }
})

// 组件挂载后初始化自适应高度
onMounted(() => {
  if (props.autosize) {
    nextTick(() => {
      resizeTextarea()
    })
  }
})
</script>

<style scoped lang="scss">
// ========================================
// 组件样式
// ========================================
.custom-textarea {
  position: relative;
  width: 100%;
  font-size: 14px;

  .textarea-wrapper {
    position: relative;
    display: flex;
    background: #FFFFFF;
    border: 1px solid var(--el-border-color);
    border-radius: 4px;
    transition: all 0.3s ease;

    &:hover {
      border-color: var(--el-color-primary-light-7);
    }

    .textarea-inner {
      flex: 1;
      padding: 8px 12px;
      border: none;
      outline: none;
      background: transparent;
      color: var(--el-text-color-primary);
      font-size: inherit;
      line-height: 1.5;
      font-family: inherit;
      resize: vertical;

      &::placeholder {
        color: var(--el-text-color-placeholder);
      }

      &:disabled {
        color: var(--el-text-color-disabled);
        cursor: not-allowed;
        resize: none;
      }

      &:readonly {
        resize: none;
      }
    }

    .clear-button {
      position: absolute;
      top: 8px;
      right: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      width: 20px;
      height: 20px;
      color: var(--el-text-color-regular);
      cursor: pointer;
      border-radius: 2px;
      transition: all 0.3s ease;
      background: rgba(255, 255, 255, 0.8);

      &:hover {
        color: var(--el-color-primary);
        background: var(--el-color-primary-light-9);
      }

      .fa {
        font-size: 12px;
      }
    }
  }

  .word-limit {
    margin-top: 4px;
    text-align: right;
    font-size: 12px;
    color: var(--el-text-color-placeholder);
  }

  // 尺寸变体
  &.custom-textarea--large {
    font-size: 16px;

    .textarea-wrapper .textarea-inner {
      padding: 12px 16px;
    }
  }

  &.custom-textarea--small {
    font-size: 12px;

    .textarea-wrapper .textarea-inner {
      padding: 6px 8px;
    }
  }

  // 自适应高度
  &.is-autosize .textarea-wrapper .textarea-inner {
    resize: none;
  }

  // 状态样式
  &.is-focused .textarea-wrapper {
    border-color: var(--el-color-primary);
    box-shadow: 0 0 0 2px var(--el-color-primary-light-9);
  }

  &.is-disabled .textarea-wrapper {
    background: var(--el-fill-color-light);
    border-color: var(--el-border-color-lighter);
    cursor: not-allowed;
  }

  &.is-readonly .textarea-wrapper {
    background: var(--el-fill-color-light);
  }

  // 验证状态
  &.is-error .textarea-wrapper {
    border-color: var(--el-color-danger);

    &:focus-within {
      border-color: var(--el-color-danger);
      box-shadow: 0 0 0 2px var(--el-color-danger-light-9);
    }
  }

  &.is-success .textarea-wrapper {
    border-color: var(--el-color-success);
  }

  &.is-warning .textarea-wrapper {
    border-color: var(--el-color-warning);
  }
}
</style>
