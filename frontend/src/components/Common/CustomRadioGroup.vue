<!-- 自定义单选框组组件 - 替换Element UI的el-radio-group -->
<template>
  <div class="custom-radio-group" :class="radioGroupClasses" role="radiogroup">
    <div v-for="(option, index) in options" :key="getOptionValue(option)" class="radio-item"
      :class="getRadioItemClasses(option)" @click="handleOptionClick(option)">
      <input :id="getRadioId(option, index)" v-model="radioValue" type="radio" :value="getOptionValue(option)"
        :name="radioName" :disabled="disabled || getOptionDisabled(option)" class="radio-input"
        @change="handleChange" />
      <label :for="getRadioId(option, index)" class="radio-label">
        <span class="radio-indicator">
          <span class="radio-inner"></span>
        </span>
        <span class="radio-text">
          {{ getOptionLabel(option) }}
        </span>
      </label>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

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
  }
})

// 事件定义
const emit = defineEmits([
  'update:modelValue'
])

// ===================================
// 响应式数据和计算属性
// ===================================

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
  return `custom-radio-group-${Math.random().toString(36).substr(2, 9)}`
})

// 计算单选组类名
const radioGroupClasses = computed(() => {
  return [
    {
      'is-disabled': props.disabled
    }
  ]
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
    'is-disabled': props.disabled || getOptionDisabled(option)
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
}
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
      opacity: var(--disabled-opacity);

      .radio-label {
        cursor: not-allowed;
      }

      .radio-indicator {
        background-color: var(--bg-disabled);
        border-color: var(--border-light);
      }

      &.is-checked {
        .radio-indicator {
          background-color: var(--bg-disabled);
          border-color: var(--border-light);

          .radio-inner {
            background-color: var(--text-disabled);
          }
        }
      }
    }

    // 悬停效果
    &:hover:not(.is-disabled) {
      .radio-indicator {
        border-color: var(--primary-color);
      }
    }
  }
}
</style>
