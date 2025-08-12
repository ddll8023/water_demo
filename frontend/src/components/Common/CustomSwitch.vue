<!-- 自定义开关组件 - 替换Element UI的el-switch -->
<template>
  <div class="custom-switch" :class="switchClasses">
    <input :id="switchId" ref="inputRef" v-model="switchValue" type="checkbox" class="switch-input" :disabled="disabled"
      :name="name" @change="handleChange" @focus="handleFocus" @blur="handleBlur" />
    <label :for="switchId" class="switch-label">
      <span class="switch-track">
        <span class="switch-thumb"></span>
      </span>

      <!-- 激活状态文本 -->
      <span v-if="activeText && !inlinePrompt" class="switch-text switch-text--active">
        {{ activeText }}
      </span>

      <!-- 非激活状态文本 -->
      <span v-if="inactiveText && !inlinePrompt" class="switch-text switch-text--inactive">
        {{ inactiveText }}
      </span>

      <!-- 内联提示文本 -->
      <span v-if="inlinePrompt" class="switch-inline-prompt">
        <span class="switch-inline-prompt--active">{{ activeText }}</span>
        <span class="switch-inline-prompt--inactive">{{ inactiveText }}</span>
      </span>
    </label>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

// ===========================
// 组件属性与数据定义
// ===========================

/**
 * Props定义
 */
const props = defineProps({
  // v-model绑定值
  modelValue: {
    type: [Boolean, String, Number],
    default: false
  },
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  },
  // 激活时的值
  activeValue: {
    type: [Boolean, String, Number],
    default: true
  },
  // 非激活时的值
  inactiveValue: {
    type: [Boolean, String, Number],
    default: false
  },
  // 激活时的文字描述
  activeText: {
    type: String,
    default: ''
  },
  // 非激活时的文字描述
  inactiveText: {
    type: String,
    default: ''
  },

  // 是否显示内联文字提示
  inlinePrompt: {
    type: Boolean,
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
  // 是否在切换前进行确认
  beforeChange: {
    type: Function,
    default: null
  }
})

/**
 * Emits定义
 */
const emit = defineEmits([
  'update:modelValue',
  'change',
  'focus',
  'blur'
])

/**
 * 响应式数据
 */
const inputRef = ref()
const focused = ref(false)

// ===========================
// 计算属性
// ===========================

/**
 * 开关值计算属性
 * 处理v-model双向绑定
 */
const switchValue = computed({
  get() {
    return props.modelValue === props.activeValue
  },
  set(value) {
    const newValue = value ? props.activeValue : props.inactiveValue
    emit('update:modelValue', newValue)
  }
})

/**
 * 开关ID计算属性
 * 如未指定则生成随机ID
 */
const switchId = computed(() => {
  return props.id || `custom-switch-${Math.random().toString(36).substr(2, 9)}`
})

/**
 * 开关CSS类计算属性
 * 基于组件状态动态添加类名
 */
const switchClasses = computed(() => {
  return [
    `custom-switch--${props.size}`,
    {
      'is-disabled': props.disabled,
      'is-checked': switchValue.value,
      'is-focused': focused.value,
      'has-text': props.activeText || props.inactiveText,
      'is-inline-prompt': props.inlinePrompt,
      [`is-${props.validateState}`]: props.validateState
    }
  ]
})



// ===========================
// 事件处理方法
// ===========================

/**
 * 处理开关状态变化
 * 支持beforeChange钩子进行变更确认
 */
const handleChange = async (event) => {
  const newValue = event.target.checked ? props.activeValue : props.inactiveValue

  // 如果有beforeChange函数，先执行确认
  if (props.beforeChange) {
    try {
      const result = await props.beforeChange()
      if (result === false) {
        // 阻止切换，恢复原状态
        event.target.checked = switchValue.value
        return
      }
    } catch (error) {
      // 如果beforeChange抛出异常，阻止切换
      event.target.checked = switchValue.value
      return
    }
  }

  emit('update:modelValue', newValue)
  emit('change', newValue)
}

/**
 * 处理获取焦点事件
 */
const handleFocus = (event) => {
  focused.value = true
  emit('focus', event)
}

/**
 * 处理失去焦点事件
 */
const handleBlur = (event) => {
  focused.value = false
  emit('blur', event)
}

// ===========================
// 公开方法
// ===========================

/**
 * 聚焦方法
 */
const focus = () => {
  inputRef.value?.focus()
}

/**
 * 失焦方法
 */
const blur = () => {
  inputRef.value?.blur()
}

// 暴露方法给父组件
defineExpose({
  focus,
  blur,
  inputRef
})

// ===========================
// 监听与生命周期
// ===========================

/**
 * 监听modelValue变化
 * 确保值符合activeValue或inactiveValue
 */
watch(() => props.modelValue, (newValue) => {
  // 确保值的一致性
  if (newValue !== props.activeValue && newValue !== props.inactiveValue) {
    console.warn(`CustomSwitch: modelValue should be either activeValue(${props.activeValue}) or inactiveValue(${props.inactiveValue})`)
  }
})
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

/* ===========================
 * 开关组件基础样式
 * =========================== */
.custom-switch {
  position: relative;
  @include flex-start;
  font-size: var(--font-size-base);
  line-height: var(--spacing-large);
  vertical-align: middle;

  .switch-input {
    position: absolute;
    opacity: 0;
    width: 0;
    height: 0;
    margin: 0;
  }

  /* ===========================
   * 开关轨道与滑块样式
   * =========================== */
  .switch-label {
    position: relative;
    @include flex-start;
    cursor: pointer;
    @include user-select(none);

    .switch-track {
      position: relative;
      display: inline-block;
      width: var(--form-item-height);
      height: var(--spacing-large);
      background-color: var(--border-color);
      border-radius: var(--border-radius-round);
      transition: var(--transition-base);

      .switch-thumb {
        position: absolute;
        top: var(--spacing-micro);
        left: var(--spacing-micro);
        width: var(--icon-size-md);
        height: var(--icon-size-md);
        background-color: var(--bg-primary);
        border-radius: var(--border-radius-round);
        transition: var(--transition-base);
        box-shadow: var(--box-shadow-light);
      }
    }

    /* ===========================
     * 文本标签样式
     * =========================== */
    .switch-text {
      margin: 0 var(--spacing-small);
      font-size: var(--font-size-base);
      color: var(--text-secondary);
      transition: var(--transition-opacity);

      &--active {
        order: -1;
      }

      &--inactive {
        order: 1;
      }
    }

    /* ===========================
     * 内联提示文本样式
     * =========================== */
    .switch-inline-prompt {
      position: absolute;
      top: 50%;
      transform: translateY(-50%);
      font-size: var(--font-size-extra-small);
      color: var(--bg-primary);
      pointer-events: none;

      .switch-inline-prompt--active,
      .switch-inline-prompt--inactive {
        position: absolute;
        white-space: nowrap;
        transition: var(--transition-opacity);
      }

      .switch-inline-prompt--active {
        left: var(--spacing-xs);
        opacity: 0;
      }

      .switch-inline-prompt--inactive {
        right: var(--spacing-xs);
        opacity: 1;
      }
    }
  }

  /* ===========================
   * 组件状态样式
   * =========================== */

  // 选中状态
  &.is-checked {
    .switch-track {
      background-color: var(--primary-color);

      .switch-thumb {
        transform: translateX(calc(var(--form-item-height) - var(--icon-size-md) - var(--spacing-micro) * 2));
      }
    }

    .switch-text--active {
      color: var(--primary-color);
    }

    .switch-inline-prompt {
      .switch-inline-prompt--active {
        opacity: 1;
      }

      .switch-inline-prompt--inactive {
        opacity: 0;
      }
    }
  }

  // 禁用状态
  &.is-disabled {
    opacity: 0.6;
    cursor: not-allowed;

    .switch-label {
      cursor: not-allowed;
    }
  }

  // 聚焦状态
  &.is-focused {
    .switch-track {
      box-shadow: 0 0 0 var(--spacing-micro) var(--primary-transparent-light);
    }
  }

  /* ===========================
   * 尺寸变体样式
   * =========================== */
  &--large {
    font-size: var(--font-size-medium);

    .switch-track {
      width: calc(var(--form-item-height) + var(--spacing-sm));
      height: var(--icon-size-xl);

      .switch-thumb {
        width: var(--icon-size-lg);
        height: var(--icon-size-lg);
      }
    }

    &.is-checked .switch-track .switch-thumb {
      transform: translateX(calc(var(--form-item-height) + var(--spacing-sm) - var(--icon-size-lg) - var(--spacing-micro) * 2));
    }
  }

  &--small {
    font-size: var(--font-size-extra-small);

    .switch-track {
      width: calc(var(--form-item-height) - var(--spacing-sm));
      height: var(--icon-size-md);

      .switch-thumb {
        width: var(--icon-size-xs);
        height: var(--icon-size-xs);
      }
    }

    &.is-checked .switch-track .switch-thumb {
      transform: translateX(calc(var(--form-item-height) - var(--spacing-sm) - var(--icon-size-xs) - var(--spacing-micro) * 2));
    }
  }

  /* ===========================
   * 验证状态样式
   * =========================== */
  &.is-error {
    .switch-track {
      border: var(--spacing-1) solid var(--danger-color);
    }
  }

  &.is-warning {
    .switch-track {
      border: var(--spacing-1) solid var(--warning-color);
    }
  }

  &.is-success {
    .switch-track {
      border: var(--spacing-1) solid var(--success-color);
    }
  }
}

/* ===========================
 * 响应式设计
 * =========================== */
.custom-switch {
  @include respond-to(sm) {
    .switch-text {
      font-size: var(--font-size-extra-small);
      margin: 0 var(--spacing-xs);
    }
  }
}
</style>
