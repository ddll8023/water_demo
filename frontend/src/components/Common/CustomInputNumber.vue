<!-- 自定义数字输入框组件 - 替换Element UI的el-input-number -->
<template>
  <div class="custom-input-number" :class="inputClasses">
    <div class="input-number-wrapper">
      <!-- 减少按钮 -->
      <button type="button" v-if="controls" class="input-number-decrease" :class="{ 'is-disabled': decreaseDisabled }"
        :disabled="decreaseDisabled" @click="decrease">
        <i class="fa fa-minus"></i>
      </button>

      <!-- 数字输入框 -->
      <div class="input-number-input">
        <input ref="inputRef" v-model="displayValue" type="text" :placeholder="placeholder" :disabled="disabled"
          :readonly="readonly" :name="name" :id="inputId" class="input-inner" @change="handleChange"
          @input="handleInput" @keydown="handleKeydown" />
      </div>

      <!-- 增加按钮 -->
      <button type="button" v-if="controls" class="input-number-increase" :class="{ 'is-disabled': increaseDisabled }"
        :disabled="increaseDisabled" @click="increase">
        <i class="fa fa-plus"></i>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

// ===============================
// 属性和事件定义
// ===============================

/**
 * 组件属性定义
 */
const props = defineProps({
  // v-model绑定值
  modelValue: {
    type: [Number, String],
    default: 0
  },
  // 最小值
  min: {
    type: Number,
    default: -Infinity
  },
  // 最大值
  max: {
    type: Number,
    default: Infinity
  },
  // 步长
  step: {
    type: Number,
    default: 1
  },
  // 精度
  precision: {
    type: Number,
    default: null
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
  // 是否显示控制按钮
  controls: {
    type: Boolean,
    default: true
  },
  // 控制按钮位置
  controlsPosition: {
    type: String,
    default: 'right',
    validator: (value) => ['right', 'both'].includes(value)
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
  }
})

/**
 * 组件事件定义
 */
const emit = defineEmits([
  'update:modelValue'
])

// ===============================
// 响应式数据定义
// ===============================

/**
 * 基础响应式数据
 */
const inputRef = ref()         // 输入框引用

// ===============================
// 计算属性
// ===============================

/**
 * 数值转换与计算
 */
const numericValue = computed(() => {
  const value = props.modelValue
  if (value === '' || value === null || value === undefined || isNaN(value)) {
    return null
  }
  return Number(value)
})

const displayValue = computed({
  get() {
    if (numericValue.value === null) {
      return ''
    }

    if (props.precision !== null) {
      return numericValue.value.toFixed(props.precision)
    }

    return String(numericValue.value)
  },
  set(value) {
    updateValue(value)
  }
})

/**
 * 样式与状态计算
 */
const inputId = computed(() => {
  return props.id || `custom-input-number-${Math.random().toString(36).substr(2, 9)}`
})

const inputClasses = computed(() => {
  return [
    `custom-input-number--${props.size}`,
    `custom-input-number--${props.controlsPosition}`,
    {
      'is-disabled': props.disabled,
      'is-readonly': props.readonly,
      'is-without-controls': !props.controls,
      [`is-${props.validateState}`]: props.validateState
    }
  ]
})

/**
 * 按钮状态控制
 */
const decreaseDisabled = computed(() => {
  return props.disabled || props.readonly || (numericValue.value !== null && numericValue.value <= props.min)
})

const increaseDisabled = computed(() => {
  return props.disabled || props.readonly || (numericValue.value !== null && numericValue.value >= props.max)
})

// ===============================
// 方法定义
// ===============================

/**
 * 值处理方法
 */
const formatValue = (value) => {
  if (value === '' || value === null || value === undefined || isNaN(value)) {
    return null
  }

  let num = Number(value)

  // 应用精度
  if (props.precision !== null) {
    num = Number(num.toFixed(props.precision))
  }

  // 应用范围限制
  if (num < props.min) {
    num = props.min
  }
  if (num > props.max) {
    num = props.max
  }

  return num
}

/**
 * 过滤输入内容，只保留数字、小数点和负号
 */
const filterNumericInput = (value) => {
  if (!value) return ''

  // 如果最小值大于等于0，移除负号
  const allowNegative = props.min < 0
  const regex = allowNegative ? /[^0-9.-]/g : /[^0-9.]/g

  // 移除所有非数字、非小数点、非负号的字符
  let filtered = value.replace(regex, '')

  // 确保只有一个小数点
  const decimalIndex = filtered.indexOf('.')
  if (decimalIndex !== -1) {
    filtered = filtered.substring(0, decimalIndex + 1) +
      filtered.substring(decimalIndex + 1).replace(/\./g, '')
  }

  // 确保负号只在开头（仅在允许负数时）
  if (allowNegative && filtered.includes('-')) {
    const negativeSign = filtered.charAt(0) === '-' ? '-' : ''
    filtered = negativeSign + filtered.replace(/-/g, '')
  }

  return filtered
}

const updateValue = (value) => {
  const formattedValue = formatValue(value)
  emit('update:modelValue', formattedValue)
}

/**
 * 事件处理方法
 */
const handleChange = (event) => {
  const value = event.target.value
  updateValue(value)
}

/**
 * 输入事件处理 - 实时验证输入内容
 */
const handleInput = (event) => {
  const value = event.target.value
  const filteredValue = filterNumericInput(value)

  // 如果输入内容被过滤，更新输入框显示值
  if (filteredValue !== value) {
    event.target.value = filteredValue
    displayValue.value = filteredValue
  }
}

/**
 * 按键事件处理 - 阻止非法字符输入
 */
const handleKeydown = (event) => {
  // 允许的功能键
  const allowedKeys = [
    'Backspace', 'Delete', 'Tab', 'Escape', 'Enter',
    'ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown',
    'Home', 'End'
  ]

  // 允许Ctrl+A, Ctrl+C, Ctrl+V, Ctrl+X
  if (event.ctrlKey && ['a', 'c', 'v', 'x'].includes(event.key.toLowerCase())) {
    return
  }

  // 允许功能键
  if (allowedKeys.includes(event.key)) {
    return
  }

  // 允许数字
  if (event.key >= '0' && event.key <= '9') {
    return
  }

  // 允许小数点（只能输入一个）
  if (event.key === '.') {
    const currentValue = event.target.value
    if (currentValue.includes('.')) {
      event.preventDefault()
      return
    }
    return
  }

  // 允许负号（只能在开头，且min值允许负数）
  if (event.key === '-') {
    // 如果最小值大于等于0，不允许输入负号
    if (props.min >= 0) {
      event.preventDefault()
      return
    }

    const currentValue = event.target.value
    const cursorPosition = event.target.selectionStart
    if (cursorPosition === 0 && !currentValue.includes('-')) {
      return
    }
    event.preventDefault()
    return
  }

  // 阻止其他所有字符
  event.preventDefault()
}

/**
 * 数值操作方法
 */
const increase = () => {
  if (increaseDisabled.value) return

  const currentValue = numericValue.value || 0
  const newValue = currentValue + props.step
  updateValue(newValue)
}

const decrease = () => {
  if (decreaseDisabled.value) return

  const currentValue = numericValue.value || 0
  const newValue = currentValue - props.step
  updateValue(newValue)
}

// ===============================
// 监听器
// ===============================

/**
 * 外部值变化监听
 */
watch(() => props.modelValue, () => {
  // 监听外部值变化以更新显示
})
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

/**
 * 自定义数字输入框基础样式
 */
.custom-input-number {
  position: relative;
  width: inherit; // 继承父容器宽度
  font-size: var(--font-size-base);

  /**
   * 输入框容器
   */
  .input-number-wrapper {
    position: relative;
    display: flex;
    align-items: center;
    background: var(--bg-primary);
    border: 1px solid var(--border-color);
    border-radius: var(--border-radius-base);
    transition: var(--transition-base);

    &:hover {
      border-color: var(--primary-light);
    }

    /**
     * 输入框区域
     */
    .input-number-input {
      flex: 1;

      .input-inner {
        width: 100%;
        padding: var(--spacing-small) var(--spacing-medium);
        border: none;
        outline: none;
        background: transparent;
        color: var(--text-primary);
        font-size: inherit;
        line-height: var(--line-height-base);
        text-align: center;

        &::placeholder {
          color: var(--text-placeholder);
        }

        &:disabled {
          color: var(--text-disabled);
          cursor: not-allowed;
        }
      }
    }

    /**
     * 控制按钮样式
     */
    .input-number-decrease,
    .input-number-increase {
      @include flex-center;
      width: var(--button-standard-size);
      height: 100%;
      border: none;
      background: transparent;
      color: var(--text-secondary);
      cursor: pointer;
      transition: var(--transition-base);

      &:hover:not(.is-disabled) {
        color: var(--primary-color);
        background: var(--primary-bg-light);
      }

      &.is-disabled {
        color: var(--text-disabled);
        cursor: not-allowed;
      }

      .fa {
        font-size: var(--font-size-extra-small);
      }
    }

    .input-number-decrease {
      border-right: 1px solid var(--border-light);
      border-radius: var(--border-radius-base) 0 0 var(--border-radius-base);
    }

    .input-number-increase {
      border-left: 1px solid var(--border-light);
      border-radius: 0 var(--border-radius-base) var(--border-radius-base) 0;
    }
  }

  /**
   * 尺寸变体样式
   */
  &.custom-input-number--large {
    font-size: var(--font-size-medium);

    .input-number-wrapper {
      .input-number-input .input-inner {
        padding: var(--spacing-medium) var(--spacing-base);
      }

      .input-number-decrease,
      .input-number-increase {
        width: var(--icon-size-xl);
      }
    }
  }

  &.custom-input-number--small {
    font-size: var(--font-size-extra-small);

    .input-number-wrapper {
      .input-number-input .input-inner {
        padding: var(--spacing-xs) var(--spacing-small);
      }

      .input-number-decrease,
      .input-number-increase {
        width: var(--icon-size-lg);
      }
    }
  }

  /**
   * 控制按钮显示状态
   */
  &.is-without-controls {
    .input-number-wrapper {
      .input-number-input .input-inner {
        text-align: left;
      }
    }
  }

  /**
   * 组件状态样式
   */
  &.is-disabled .input-number-wrapper {
    background: var(--bg-disabled);
    border-color: var(--border-light);
    cursor: not-allowed;
  }

  &.is-readonly .input-number-wrapper {
    background: var(--bg-disabled);
  }

  /**
   * 验证状态样式
   */
  &.is-error .input-number-wrapper {
    border-color: var(--danger-color);

    &:focus-within {
      border-color: var(--danger-color);
      box-shadow: var(--focus-shadow-offset) var(--danger-bg-light);
    }
  }

  &.is-success .input-number-wrapper {
    border-color: var(--success-color);
  }

  &.is-warning .input-number-wrapper {
    border-color: var(--warning-color);
  }
}
</style>
