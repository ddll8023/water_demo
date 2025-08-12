<!-- 自定义单选框组组件 - 替换Element UI的el-radio-group -->
<template>
  <div class="custom-radio-group" :class="radioGroupClasses" role="radiogroup">
    <div v-for="(option, index) in options" :key="getOptionValue(option)" class="radio-item"
      :class="getRadioItemClasses(option)" @click="handleOptionClick(option)">
      <input :id="getRadioId(option, index)" :ref="el => setRadioRef(el, index)" v-model="radioValue" type="radio"
        :value="getOptionValue(option)" :name="radioName" :disabled="disabled || getOptionDisabled(option)"
        class="radio-input" @change="handleChange" @focus="handleFocus" @blur="handleBlur" />
      <label :for="getRadioId(option, index)" class="radio-label">
        <span class="radio-indicator">
          <span class="radio-inner"></span>
        </span>
        <span class="radio-text">
          <slot name="option" :option="option" :index="index">
            {{ getOptionLabel(option) }}
          </slot>
        </span>
      </label>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'

// ===================================
// Props 和 Emits 定义
// ===================================

const props = defineProps({
  // v-model绑定值
  modelValue: {
    type: [String, Number, Boolean],
    default: ''
  },
  // 选项数据
  options: {
    type: Array,
    default: () => []
  },
  // 选项值字段名
  valueKey: {
    type: String,
    default: 'value'
  },
  // 选项标签字段名
  labelKey: {
    type: String,
    default: 'label'
  },
  // 选项禁用字段名
  disabledKey: {
    type: String,
    default: 'disabled'
  },
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  },
  // 尺寸
  size: {
    type: String,
    default: 'default',
    validator: (value) => ['large', 'default', 'small'].includes(value)
  },
  // 排列方向
  direction: {
    type: String,
    default: 'horizontal',
    validator: (value) => ['horizontal', 'vertical'].includes(value)
  },
  // 是否显示边框
  border: {
    type: Boolean,
    default: false
  },
  // 名称
  name: {
    type: String,
    default: ''
  },
  // 验证状态
  validateState: {
    type: String,
    default: '',
    validator: (value) => ['', 'success', 'warning', 'error'].includes(value)
  },
  // 文字颜色
  textColor: {
    type: String,
    default: ''
  },
  // 填充颜色
  fill: {
    type: String,
    default: ''
  }
})

// 事件定义
const emit = defineEmits([
  'update:modelValue',
  'change',
  'focus',
  'blur'
])

// ===================================
// 响应式数据和计算属性
// ===================================

// 响应式数据
const radioRefs = ref([])
const focused = ref(false)

// v-model计算属性
const radioValue = computed({
  get() {
    return props.modelValue
  },
  set(value) {
    emit('update:modelValue', value)
  }
})

// 计算单选组名称
const radioName = computed(() => {
  return props.name || `custom-radio-group-${Math.random().toString(36).substr(2, 9)}`
})

// 计算单选组类名
const radioGroupClasses = computed(() => {
  return [
    `custom-radio-group--${props.size}`,
    `custom-radio-group--${props.direction}`,
    {
      'is-disabled': props.disabled,
      'is-focused': focused.value,
      'has-border': props.border,
      [`is-${props.validateState}`]: props.validateState
    }
  ]
})

// 计算单选组样式
const radioGroupStyle = computed(() => {
  const style = {}

  if (props.textColor) {
    style['--radio-text-color'] = props.textColor
  }

  if (props.fill) {
    style['--radio-fill-color'] = props.fill
  }

  return style
})

// ===================================
// 选项处理方法
// ===================================

// 获取选项值
const getOptionValue = (option) => {
  if (typeof option === 'object' && option !== null) {
    return option[props.valueKey]
  }
  return option
}

// 获取选项标签
const getOptionLabel = (option) => {
  if (typeof option === 'object' && option !== null) {
    return option[props.labelKey] || option[props.valueKey]
  }
  return option
}

// 获取选项是否禁用
const getOptionDisabled = (option) => {
  if (typeof option === 'object' && option !== null) {
    return option[props.disabledKey] || false
  }
  return false
}

// 生成单选项ID
const getRadioId = (option, index) => {
  return `${radioName.value}-${index}`
}

// 获取单选项类名
const getRadioItemClasses = (option) => {
  const value = getOptionValue(option)
  return {
    'is-checked': radioValue.value === value,
    'is-disabled': props.disabled || getOptionDisabled(option),
    'is-bordered': props.border
  }
}

// 设置单选框引用
const setRadioRef = (el, index) => {
  if (el) {
    radioRefs.value[index] = el
  }
}

// ===================================
// 事件处理方法
// ===================================

// 处理选项点击
const handleOptionClick = (option) => {
  if (props.disabled || getOptionDisabled(option)) {
    return
  }

  const value = getOptionValue(option)
  radioValue.value = value
  emit('change', value)
}

// 处理值变化
const handleChange = (event) => {
  const value = event.target.value
  // 尝试转换为原始类型
  let convertedValue = value

  // 如果原始值是数字，尝试转换
  if (typeof props.modelValue === 'number') {
    const numValue = Number(value)
    if (!isNaN(numValue)) {
      convertedValue = numValue
    }
  }
  // 如果原始值是布尔值，尝试转换
  else if (typeof props.modelValue === 'boolean') {
    convertedValue = value === 'true'
  }

  emit('update:modelValue', convertedValue)
  emit('change', convertedValue)
}

// 处理获得焦点
const handleFocus = (event) => {
  focused.value = true
  emit('focus', event)
}

// 处理失去焦点
const handleBlur = (event) => {
  focused.value = false
  emit('blur', event)
}

// ===================================
// 组件公开方法
// ===================================

// 聚焦方法
const focus = () => {
  const checkedRadio = radioRefs.value.find(radio => radio && radio.checked)
  if (checkedRadio) {
    checkedRadio.focus()
  } else if (radioRefs.value[0]) {
    radioRefs.value[0].focus()
  }
}

// 取消焦点方法
const blur = () => {
  const focusedRadio = radioRefs.value.find(radio => radio && radio === document.activeElement)
  if (focusedRadio) {
    focusedRadio.blur()
  }
}

// 向父组件暴露方法
defineExpose({
  focus,
  blur,
  radioRefs
})

// ===================================
// 监听器
// ===================================

// 监听选项变化
watch(() => props.options, () => {
  // 清空旧的引用
  radioRefs.value = []
}, { immediate: true })
</script>

<style scoped lang="scss">
/* ===================================
 * 基础样式定义
 * =================================== */
.custom-radio-group {
  display: inline-flex;
  font-size: 14px;
  line-height: 1;

  // CSS变量定义
  --radio-size: 14px;
  --radio-inner-size: 6px;
  --radio-color: #409eff;
  --radio-border-color: #dcdfe6;
  --radio-text-color: var(--el-text-color-regular);
  --radio-fill-color: #409eff;
  --radio-gap: 12px;

  /* ===================================
   * 单选项样式
   * =================================== */
  .radio-item {
    position: relative;
    display: inline-flex;
    align-items: center;
    cursor: pointer;
    user-select: none;

    &:not(:last-child) {
      margin-right: var(--radio-gap);
    }

    .radio-input {
      position: absolute;
      opacity: 0;
      width: 0;
      height: 0;
      margin: 0;
    }

    .radio-label {
      display: inline-flex;
      align-items: center;
      cursor: pointer;

      .radio-indicator {
        position: relative;
        display: inline-block;
        width: var(--radio-size);
        height: var(--radio-size);
        border: 1px solid var(--radio-border-color);
        border-radius: 50%;
        background-color: #ffffff;
        transition: all 0.3s;

        .radio-inner {
          position: absolute;
          top: 50%;
          left: 50%;
          width: var(--radio-inner-size);
          height: var(--radio-inner-size);
          background-color: var(--radio-fill-color);
          border-radius: 50%;
          transform: translate(-50%, -50%) scale(0);
          transition: transform 0.15s ease-in;
        }
      }

      .radio-text {
        margin-left: 8px;
        color: var(--radio-text-color);
        font-size: inherit;
        line-height: 1;
      }
    }

    /* ===================================
     * 状态样式
     * =================================== */
    // 选中状态
    &.is-checked {
      .radio-indicator {
        border-color: var(--radio-color);
        background-color: #ffffff;

        .radio-inner {
          transform: translate(-50%, -50%) scale(1);
        }
      }

      .radio-text {
        color: var(--radio-color);
      }
    }

    // 禁用状态
    &.is-disabled {
      cursor: not-allowed;
      opacity: 0.6;

      .radio-label {
        cursor: not-allowed;
      }

      .radio-indicator {
        background-color: #f5f7fa;
        border-color: #e4e7ed;
      }

      &.is-checked {
        .radio-indicator {
          background-color: #f5f7fa;
          border-color: #e4e7ed;

          .radio-inner {
            background-color: #c0c4cc;
          }
        }
      }
    }

    // 边框样式
    &.is-bordered {
      padding: 8px 12px;
      border: 1px solid var(--radio-border-color);
      border-radius: 4px;
      margin-right: 8px;

      &.is-checked {
        border-color: var(--radio-color);
        background-color: rgba(64, 158, 255, 0.1);
      }

      &.is-disabled {
        border-color: #e4e7ed;
        background-color: #f5f7fa;
      }
    }

    // 悬停效果
    &:hover:not(.is-disabled) {
      .radio-indicator {
        border-color: var(--radio-color);
      }
    }

    // 聚焦效果
    .radio-input:focus+.radio-label .radio-indicator {
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
    }
  }

  /* ===================================
   * 布局变体样式
   * =================================== */
  // 垂直排列
  &--vertical {
    flex-direction: column;
    align-items: flex-start;

    .radio-item {
      margin-right: 0;
      margin-bottom: var(--radio-gap);

      &:last-child {
        margin-bottom: 0;
      }
    }
  }

  // 水平排列
  &--horizontal {
    flex-direction: row;
    flex-wrap: wrap;
  }

  /* ===================================
   * 尺寸变体样式
   * =================================== */
  &--large {
    --radio-size: 16px;
    --radio-inner-size: 8px;
    --radio-gap: 16px;
    font-size: 16px;
  }

  &--small {
    --radio-size: 12px;
    --radio-inner-size: 4px;
    --radio-gap: 8px;
    font-size: 12px;
  }

  /* ===================================
   * 验证状态样式
   * =================================== */
  &.is-error {
    --radio-border-color: var(--el-color-danger);
    --radio-color: var(--el-color-danger);
  }

  &.is-warning {
    --radio-border-color: var(--el-color-warning);
    --radio-color: var(--el-color-warning);
  }

  &.is-success {
    --radio-border-color: var(--el-color-success);
    --radio-color: var(--el-color-success);
  }
}

/* ===================================
 * 响应式设计
 * =================================== */
@media (max-width: 768px) {
  .custom-radio-group {
    &--horizontal {
      flex-direction: column;

      .radio-item {
        margin-right: 0;
        margin-bottom: 8px;

        &:last-child {
          margin-bottom: 0;
        }
      }
    }
  }
}
</style>
