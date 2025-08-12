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
  // 激活时的颜色
  activeColor: {
    type: String,
    default: '#409eff'
  },
  // 非激活时的颜色
  inactiveColor: {
    type: String,
    default: '#dcdfe6'
  },
  // 开关的宽度
  width: {
    type: [String, Number],
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

/**
 * 开关样式计算属性
 * 动态设置CSS变量
 */
const switchStyle = computed(() => {
  const style = {}

  if (props.width) {
    style['--switch-width'] = typeof props.width === 'number' ? `${props.width}px` : props.width
  }

  if (props.activeColor) {
    style['--switch-active-color'] = props.activeColor
  }

  if (props.inactiveColor) {
    style['--switch-inactive-color'] = props.inactiveColor
  }

  return style
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
/* ===========================
 * 开关组件基础样式
 * =========================== */
.custom-switch {
  position: relative;
  display: inline-flex;
  align-items: center;
  font-size: 14px;
  line-height: 20px;
  vertical-align: middle;

  // CSS变量定义
  --switch-width: 40px;
  --switch-height: 20px;
  --switch-active-color: #409eff;
  --switch-inactive-color: #dcdfe6;
  --switch-thumb-size: 16px;
  --switch-padding: 2px;

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
    display: inline-flex;
    align-items: center;
    cursor: pointer;
    user-select: none;

    .switch-track {
      position: relative;
      display: inline-block;
      width: var(--switch-width);
      height: var(--switch-height);
      background-color: var(--switch-inactive-color);
      border-radius: calc(var(--switch-height) / 2);
      transition: all 0.3s;

      .switch-thumb {
        position: absolute;
        top: var(--switch-padding);
        left: var(--switch-padding);
        width: var(--switch-thumb-size);
        height: var(--switch-thumb-size);
        background-color: #ffffff;
        border-radius: 50%;
        transition: all 0.3s;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12);
      }
    }

    /* ===========================
     * 文本标签样式
     * =========================== */
    .switch-text {
      margin: 0 8px;
      font-size: 14px;
      color: var(--el-text-color-regular);
      transition: color 0.3s;

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
      font-size: 12px;
      color: #ffffff;
      pointer-events: none;

      .switch-inline-prompt--active,
      .switch-inline-prompt--inactive {
        position: absolute;
        white-space: nowrap;
        transition: opacity 0.3s;
      }

      .switch-inline-prompt--active {
        left: 6px;
        opacity: 0;
      }

      .switch-inline-prompt--inactive {
        right: 6px;
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
      background-color: var(--switch-active-color);

      .switch-thumb {
        transform: translateX(calc(var(--switch-width) - var(--switch-thumb-size) - var(--switch-padding) * 2));
      }
    }

    .switch-text--active {
      color: var(--switch-active-color);
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
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
    }
  }

  /* ===========================
   * 尺寸变体样式
   * =========================== */
  &--large {
    --switch-width: 50px;
    --switch-height: 24px;
    --switch-thumb-size: 20px;
    font-size: 16px;
  }

  &--small {
    --switch-width: 30px;
    --switch-height: 16px;
    --switch-thumb-size: 12px;
    font-size: 12px;
  }

  /* ===========================
   * 验证状态样式
   * =========================== */
  &.is-error {
    .switch-track {
      border: 1px solid var(--el-color-danger);
    }
  }

  &.is-warning {
    .switch-track {
      border: 1px solid var(--el-color-warning);
    }
  }

  &.is-success {
    .switch-track {
      border: 1px solid var(--el-color-success);
    }
  }
}

/* ===========================
 * 响应式设计
 * =========================== */
@media (max-width: 768px) {
  .custom-switch {
    .switch-text {
      font-size: 12px;
      margin: 0 6px;
    }
  }
}
</style>
