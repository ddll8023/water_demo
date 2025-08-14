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
@use "@/assets/styles/index.scss" as *;

/* ===================================
 * 基础样式定义
 * =================================== */
.custom-radio-group {
  display: inline-flex;
  font-size: var(--font-size-base);
  line-height: 1;

  /* ===================================
   * 单选项样式
   * =================================== */
  .radio-item {
    position: relative;
    @include flex-start;
    cursor: pointer;
    @include user-select(none);

    &:not(:last-child) {
      margin-right: var(--spacing-medium);
    }

    .radio-input {
      position: absolute;
      opacity: 0;
      width: 0;
      height: 0;
      margin: 0;
    }

    .radio-label {
      @include flex-start;
      cursor: pointer;

      .radio-indicator {
        position: relative;
        display: inline-block;
        width: 14px;
        height: 14px;
        border: 1px solid var(--border-color);
        border-radius: var(--border-radius-round);
        background-color: var(--bg-primary);
        transition: var(--transition-base);

        .radio-inner {
          position: absolute;
          top: 50%;
          left: 50%;
          width: 6px;
          height: 6px;
          background-color: var(--primary-color);
          border-radius: var(--border-radius-round);
          transform: translate(-50%, -50%) scale(0);
          transition: var(--transition-transform);
        }
      }

      .radio-text {
        margin-left: var(--spacing-small);
        color: var(--text-secondary);
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
        border-color: var(--primary-color);
        background-color: var(--bg-primary);

        .radio-inner {
          transform: translate(-50%, -50%) scale(1);
        }
      }

      .radio-text {
        color: var(--primary-color);
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
        background-color: var(--bg-disabled);
        border-color: var(--el-border-color-light);
      }

      &.is-checked {
        .radio-indicator {
          background-color: var(--bg-disabled);
          border-color: var(--el-border-color-light);

          .radio-inner {
            background-color: var(--text-disabled);
          }
        }
      }
    }

    // 边框样式
    &.is-bordered {
      padding: var(--spacing-small) var(--spacing-medium);
      border: 1px solid var(--border-color);
      border-radius: var(--border-radius-base);
      margin-right: var(--spacing-small);

      &.is-checked {
        border-color: var(--primary-color);
        background-color: var(--primary-bg-light);
      }

      &.is-disabled {
        border-color: var(--el-border-color-light);
        background-color: var(--bg-disabled);
      }
    }

    // 悬停效果
    &:hover:not(.is-disabled) {
      .radio-indicator {
        border-color: var(--primary-color);
      }
    }

    // 聚焦效果
    .radio-input:focus+.radio-label .radio-indicator {
      box-shadow: 0 0 0 2px var(--primary-transparent-light);
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
      margin-bottom: var(--spacing-medium);

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
    font-size: var(--font-size-medium);

    .radio-item {
      &:not(:last-child) {
        margin-right: var(--spacing-base);
      }

      .radio-indicator {
        width: 16px;
        height: 16px;

        .radio-inner {
          width: 8px;
          height: 8px;
        }
      }
    }

    &.custom-radio-group--vertical .radio-item {
      margin-bottom: var(--spacing-base);
    }
  }

  &--small {
    font-size: var(--font-size-extra-small);

    .radio-item {
      &:not(:last-child) {
        margin-right: var(--spacing-small);
      }

      .radio-indicator {
        width: 12px;
        height: 12px;

        .radio-inner {
          width: 4px;
          height: 4px;
        }
      }
    }

    &.custom-radio-group--vertical .radio-item {
      margin-bottom: var(--spacing-small);
    }
  }

  /* ===================================
   * 验证状态样式
   * =================================== */
  &.is-error {
    .radio-item {
      .radio-indicator {
        border-color: var(--el-color-danger);
      }

      &.is-checked {
        .radio-indicator {
          border-color: var(--el-color-danger);
        }

        .radio-text {
          color: var(--el-color-danger);
        }
      }

      &:hover:not(.is-disabled) {
        .radio-indicator {
          border-color: var(--el-color-danger);
        }
      }
    }
  }

  &.is-warning {
    .radio-item {
      .radio-indicator {
        border-color: var(--el-color-warning);
      }

      &.is-checked {
        .radio-indicator {
          border-color: var(--el-color-warning);
        }

        .radio-text {
          color: var(--el-color-warning);
        }
      }

      &:hover:not(.is-disabled) {
        .radio-indicator {
          border-color: var(--el-color-warning);
        }
      }
    }
  }

  &.is-success {
    .radio-item {
      .radio-indicator {
        border-color: var(--el-color-success);
      }

      &.is-checked {
        .radio-indicator {
          border-color: var(--el-color-success);
        }

        .radio-text {
          color: var(--el-color-success);
        }
      }

      &:hover:not(.is-disabled) {
        .radio-indicator {
          border-color: var(--el-color-success);
        }
      }
    }
  }
}

/* ===================================
 * 响应式设计
 * =================================== */
@include respond-to(sm) {
  .custom-radio-group {
    &--horizontal {
      flex-direction: column;

      .radio-item {
        margin-right: 0;
        margin-bottom: var(--spacing-small);

        &:last-child {
          margin-bottom: 0;
        }
      }
    }
  }
}
</style>
