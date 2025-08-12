<!-- 自定义数字输入框组件 - 替换Element UI的el-input-number -->
<template>
  <div class="custom-input-number" :class="inputClasses">
    <div class="input-number-wrapper">
      <!-- 减少按钮 -->
      <button type="button" v-if="controls" class="input-number-decrease" :class="{ 'is-disabled': decreaseDisabled }"
        :disabled="decreaseDisabled" @click="decrease" @mousedown="startRepeat('decrease')" @mouseup="stopRepeat"
        @mouseleave="stopRepeat">
        <i class="fa fa-minus"></i>
      </button>

      <!-- 数字输入框 -->
      <div class="input-number-input">
        <input ref="inputRef" v-model="displayValue" type="text" :placeholder="placeholder" :disabled="disabled"
          :readonly="readonly" :name="name" :id="inputId" class="input-inner" @input="handleInput"
          @change="handleChange" @focus="handleFocus" @blur="handleBlur" @keyup="handleKeyup"
          @keydown="handleKeydown" />
      </div>

      <!-- 增加按钮 -->
      <button type="button" v-if="controls" class="input-number-increase" :class="{ 'is-disabled': increaseDisabled }"
        :disabled="increaseDisabled" @click="increase" @mousedown="startRepeat('increase')" @mouseup="stopRepeat"
        @mouseleave="stopRepeat">
        <i class="fa fa-plus"></i>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'

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
  'update:modelValue',
  'input',
  'change',
  'focus',
  'blur',
  'keyup',
  'keydown'
])

// ===============================
// 响应式数据定义
// ===============================

/**
 * 基础响应式数据
 */
const inputRef = ref()         // 输入框引用
const focused = ref(false)     // 焦点状态
const repeatTimer = ref(null)  // 重复操作计时器
const userInput = ref(null)    // 用户输入值

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
    if (userInput.value !== null) {
      return userInput.value
    }

    if (numericValue.value === null) {
      return ''
    }

    if (props.precision !== null) {
      return numericValue.value.toFixed(props.precision)
    }

    return String(numericValue.value)
  },
  set(value) {
    userInput.value = value
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
      'is-focused': focused.value,
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

const updateValue = (value) => {
  const formattedValue = formatValue(value)
  emit('update:modelValue', formattedValue)
  userInput.value = null
}

/**
 * 事件处理方法
 */
const handleInput = (event) => {
  const value = event.target.value
  userInput.value = value

  // 实时验证输入
  if (value === '' || value === '-') {
    return
  }

  const num = Number(value)
  if (!isNaN(num)) {
    emit('input', num)
  }
}

const handleChange = (event) => {
  const value = event.target.value
  updateValue(value)
  emit('change', numericValue.value)
}

const handleFocus = (event) => {
  focused.value = true
  emit('focus', event)
}

const handleBlur = (event) => {
  focused.value = false

  // 失焦时格式化值
  if (userInput.value !== null) {
    updateValue(userInput.value)
  }

  emit('blur', event)
}

const handleKeyup = (event) => {
  emit('keyup', event)
}

const handleKeydown = (event) => {
  // 支持上下箭头键
  if (event.key === 'ArrowUp') {
    event.preventDefault()
    increase()
  } else if (event.key === 'ArrowDown') {
    event.preventDefault()
    decrease()
  }

  emit('keydown', event)
}

/**
 * 数值操作方法
 */
const increase = () => {
  if (increaseDisabled.value) return

  const currentValue = numericValue.value || 0
  const newValue = currentValue + props.step
  updateValue(newValue)
  emit('change', numericValue.value)
}

const decrease = () => {
  if (decreaseDisabled.value) return

  const currentValue = numericValue.value || 0
  const newValue = currentValue - props.step
  updateValue(newValue)
  emit('change', numericValue.value)
}

/**
 * 重复操作控制
 */
const startRepeat = (action) => {
  if (repeatTimer.value) return

  repeatTimer.value = setTimeout(() => {
    repeatTimer.value = setInterval(() => {
      if (action === 'increase') {
        increase()
      } else {
        decrease()
      }
    }, 100)
  }, 500)
}

const stopRepeat = () => {
  if (repeatTimer.value) {
    clearTimeout(repeatTimer.value)
    clearInterval(repeatTimer.value)
    repeatTimer.value = null
  }
}

// ===============================
// 对外暴露方法
// ===============================

/**
 * 组件公共方法
 */
const focus = () => {
  inputRef.value?.focus()
}

const blur = () => {
  inputRef.value?.blur()
}

const select = () => {
  inputRef.value?.select()
}

defineExpose({
  focus,
  blur,
  select,
  inputRef
})

// ===============================
// 监听器
// ===============================

/**
 * 外部值变化监听
 */
watch(() => props.modelValue, () => {
  userInput.value = null
})
</script>

<style scoped lang="scss">
/**
 * 自定义数字输入框基础样式
 */
.custom-input-number {
  position: relative;
  width: 100%;
  font-size: 14px;

  /**
   * 输入框容器
   */
  .input-number-wrapper {
    position: relative;
    display: flex;
    align-items: center;
    background: #FFFFFF;
    border: 1px solid var(--el-border-color);
    border-radius: 4px;
    transition: all 0.3s ease;

    &:hover {
      border-color: var(--el-color-primary-light-7);
    }

    /**
     * 输入框区域
     */
    .input-number-input {
      flex: 1;

      .input-inner {
        width: 100%;
        padding: 8px 12px;
        border: none;
        outline: none;
        background: transparent;
        color: var(--el-text-color-primary);
        font-size: inherit;
        line-height: 1.5;
        text-align: center;

        &::placeholder {
          color: var(--el-text-color-placeholder);
        }

        &:disabled {
          color: var(--el-text-color-disabled);
          cursor: not-allowed;
        }
      }
    }

    /**
     * 控制按钮样式
     */
    .input-number-decrease,
    .input-number-increase {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 32px;
      height: 100%;
      border: none;
      background: transparent;
      color: var(--el-text-color-regular);
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover:not(.is-disabled) {
        color: var(--el-color-primary);
        background: var(--el-color-primary-light-9);
      }

      &.is-disabled {
        color: var(--el-text-color-disabled);
        cursor: not-allowed;
      }

      .fa {
        font-size: 12px;
      }
    }

    .input-number-decrease {
      border-right: 1px solid var(--el-border-color-lighter);
      border-radius: 4px 0 0 4px;
    }

    .input-number-increase {
      border-left: 1px solid var(--el-border-color-lighter);
      border-radius: 0 4px 4px 0;
    }
  }

  /**
   * 尺寸变体样式
   */
  &.custom-input-number--large {
    font-size: 16px;

    .input-number-wrapper {
      .input-number-input .input-inner {
        padding: 12px 16px;
      }

      .input-number-decrease,
      .input-number-increase {
        width: 40px;
      }
    }
  }

  &.custom-input-number--small {
    font-size: 12px;

    .input-number-wrapper {
      .input-number-input .input-inner {
        padding: 6px 8px;
      }

      .input-number-decrease,
      .input-number-increase {
        width: 24px;
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
  &.is-focused .input-number-wrapper {
    border-color: var(--el-color-primary);
    box-shadow: 0 0 0 2px var(--el-color-primary-light-9);
  }

  &.is-disabled .input-number-wrapper {
    background: var(--el-fill-color-light);
    border-color: var(--el-border-color-lighter);
    cursor: not-allowed;
  }

  &.is-readonly .input-number-wrapper {
    background: var(--el-fill-color-light);
  }

  /**
   * 验证状态样式
   */
  &.is-error .input-number-wrapper {
    border-color: var(--el-color-danger);

    &:focus-within {
      border-color: var(--el-color-danger);
      box-shadow: 0 0 0 2px var(--el-color-danger-light-9);
    }
  }

  &.is-success .input-number-wrapper {
    border-color: var(--el-color-success);
  }

  &.is-warning .input-number-wrapper {
    border-color: var(--el-color-warning);
  }
}
</style>
