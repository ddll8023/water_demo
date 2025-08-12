<!-- 自定义输入框组件 - 替换Element UI的el-input -->
<template>
  <div class="custom-input" :class="inputClasses">
    <div class="input-wrapper">
      <!-- 前置内容 -->
      <div v-if="$slots.prefix || prefixIcon" class="input-prefix">
        <slot name="prefix">
          <i v-if="prefixIcon" class="fa prefix-icon" :class="prefixIcon"></i>
        </slot>
      </div>

      <!-- 主输入框 -->
      <input ref="inputRef" v-model="inputValue" :type="actualType" :placeholder="placeholder" :disabled="disabled"
        :readonly="readonly" :maxlength="maxlength" :minlength="minlength" :autocomplete="autocomplete" :name="name"
        :id="inputId" class="input-inner" @input="handleInput" @change="handleChange" @focus="handleFocus"
        @blur="handleBlur" @keyup="handleKeyup" @keydown="handleKeydown" @keypress="handleKeypress" />

      <!-- 密码显示切换按钮 -->
      <div v-if="showPassword && type === 'password'" class="password-toggle" @click="togglePasswordVisibility">
        <i class="fa" :class="passwordVisible ? 'fa-eye-slash' : 'fa-eye'"></i>
      </div>

      <!-- 清除按钮 -->
      <div v-if="clearable && inputValue && !disabled && !readonly" class="clear-button" @click="handleClear">
        <i class="fa fa-times"></i>
      </div>

      <!-- 后置内容 -->
      <div v-if="$slots.suffix || suffixIcon" class="input-suffix">
        <slot name="suffix">
          <i v-if="suffixIcon" class="fa suffix-icon" :class="suffixIcon"></i>
        </slot>
      </div>
    </div>

    <!-- 字数统计 -->
    <div v-if="showWordLimit && maxlength" class="word-limit">
      {{ inputValue.length }} / {{ maxlength }}
    </div>
  </div>
</template>

<script setup>
/**
 * ===============================
 * 导入模块
 * ===============================
 */
import { ref, computed, watch, nextTick } from 'vue'

/**
 * ===============================
 * Props 定义
 * ===============================
 */
const props = defineProps({
  // v-model绑定值
  modelValue: {
    type: [String, Number],
    default: ''
  },
  // 输入框类型
  type: {
    type: String,
    default: 'text',
    validator: (value) => ['text', 'password', 'email', 'number', 'tel', 'url', 'search'].includes(value)
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
  // 是否显示密码切换按钮
  showPassword: {
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
  // 前置图标
  prefixIcon: {
    type: String,
    default: ''
  },
  // 后置图标
  suffixIcon: {
    type: String,
    default: ''
  },
  // 尺寸
  size: {
    type: String,
    default: 'default',
    validator: (value) => ['large', 'default', 'small'].includes(value)
  },
  // 自动完成
  autocomplete: {
    type: String,
    default: 'off'
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
 * ===============================
 * Emits 定义
 * ===============================
 */
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

/**
 * ===============================
 * 响应式数据
 * ===============================
 */
const inputRef = ref()
const passwordVisible = ref(false)
const focused = ref(false)

/**
 * ===============================
 * 计算属性
 * ===============================
 */
// 输入框的值（双向绑定）
const inputValue = computed({
  get() {
    return props.modelValue
  },
  set(value) {
    emit('update:modelValue', value)
  }
})

// 实际显示的输入框类型（处理密码显示切换）
const actualType = computed(() => {
  if (props.type === 'password' && props.showPassword) {
    return passwordVisible.value ? 'text' : 'password'
  }
  return props.type
})

// 输入框ID
const inputId = computed(() => {
  return props.id || `custom-input-${Math.random().toString(36).substr(2, 9)}`
})

// 输入框样式类
const inputClasses = computed(() => {
  return [
    `custom-input--${props.size}`,
    {
      'is-disabled': props.disabled,
      'is-readonly': props.readonly,
      'is-focused': focused.value,
      'has-prefix': props.prefixIcon || !!props.$slots?.prefix,
      'has-suffix': props.suffixIcon || !!props.$slots?.suffix || props.clearable || (props.showPassword && props.type === 'password'),
      [`is-${props.validateState}`]: props.validateState
    }
  ]
})

/**
 * ===============================
 * 输入事件处理
 * ===============================
 */
// 处理输入事件
const handleInput = (event) => {
  const value = event.target.value
  emit('update:modelValue', value)
  emit('input', value)
}

// 处理值变化事件
const handleChange = (event) => {
  emit('change', event.target.value)
}

/**
 * ===============================
 * 焦点事件处理
 * ===============================
 */
// 处理获取焦点事件
const handleFocus = (event) => {
  focused.value = true
  emit('focus', event)
}

// 处理失去焦点事件
const handleBlur = (event) => {
  focused.value = false
  emit('blur', event)
}

/**
 * ===============================
 * 键盘事件处理
 * ===============================
 */
// 处理键盘按键释放
const handleKeyup = (event) => {
  emit('keyup', event)
}

// 处理键盘按键按下
const handleKeydown = (event) => {
  emit('keydown', event)
}

// 处理键盘按键按下并释放
const handleKeypress = (event) => {
  emit('keypress', event)
}

/**
 * ===============================
 * 功能方法
 * ===============================
 */
// 清空输入框内容
const handleClear = () => {
  emit('update:modelValue', '')
  emit('clear')
  emit('input', '')
  emit('change', '')
  nextTick(() => {
    inputRef.value?.focus()
  })
}

// 切换密码显示/隐藏
const togglePasswordVisibility = () => {
  passwordVisible.value = !passwordVisible.value
  nextTick(() => {
    inputRef.value?.focus()
  })
}

/**
 * ===============================
 * 暴露方法
 * ===============================
 */
// 聚焦输入框
const focus = () => {
  inputRef.value?.focus()
}

// 取消输入框焦点
const blur = () => {
  inputRef.value?.blur()
}

// 选中输入框内容
const select = () => {
  inputRef.value?.select()
}

// 暴露方法给父组件
defineExpose({
  focus,
  blur,
  select,
  inputRef
})
</script>

<style scoped lang="scss">
/**
 * ===============================
 * 组件基础样式
 * ===============================
 */
.custom-input {
  position: relative;
  width: 100%;
  font-size: 14px;

  /**
   * ===============================
   * 输入框布局
   * ===============================
   */
  .input-wrapper {
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

    // 输入框
    .input-inner {
      flex: 1;
      padding: 8px 12px;
      border: none;
      outline: none;
      background: transparent;
      color: var(--el-text-color-primary);
      font-size: inherit;
      line-height: 1.5;
      min-width: 0; // 防止文本溢出

      &::placeholder {
        color: var(--el-text-color-placeholder);
        text-overflow: ellipsis; // 占位符文本过长时显示省略号
        white-space: nowrap;
        overflow: hidden;
      }

      &:disabled {
        color: var(--el-text-color-disabled);
        cursor: not-allowed;
      }
    }

    // 前缀和后缀
    .input-prefix,
    .input-suffix {
      display: flex;
      align-items: center;
      color: var(--el-text-color-regular);

      .fa {
        font-size: 14px;
      }
    }

    .input-prefix {
      padding-left: 12px;
      padding-right: 8px;
    }

    .input-suffix {
      padding-left: 8px;
      padding-right: 12px;
    }

    // 密码切换和清除按钮
    .password-toggle,
    .clear-button {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 20px;
      height: 20px;
      margin-right: 8px;
      color: var(--el-text-color-regular);
      cursor: pointer;
      border-radius: 2px;
      transition: all 0.3s ease;
      flex-shrink: 0; // 防止按钮被压缩

      &:hover {
        color: var(--el-color-primary);
        background: var(--el-color-primary-light-9);
      }

      .fa {
        font-size: 12px;
      }
    }
  }

  // 字数限制显示
  .word-limit {
    margin-top: 4px;
    text-align: right;
    font-size: 12px;
    color: var(--el-text-color-placeholder);
  }

  /**
   * ===============================
   * 尺寸变体
   * ===============================
   */
  &.custom-input--large {
    font-size: 16px;

    .input-wrapper .input-inner {
      padding: 12px 16px;
    }
  }

  &.custom-input--small {
    font-size: 12px;

    .input-wrapper .input-inner {
      padding: 6px 8px;
    }
  }

  /**
   * ===============================
   * 状态样式
   * ===============================
   */
  // 聚焦状态
  &.is-focused .input-wrapper {
    border-color: var(--el-color-primary);
    box-shadow: 0 0 0 2px var(--el-color-primary-light-9);
  }

  // 禁用状态
  &.is-disabled .input-wrapper {
    background: var(--el-fill-color-light);
    border-color: var(--el-border-color-lighter);
    cursor: not-allowed;
  }

  // 只读状态
  &.is-readonly .input-wrapper {
    background: var(--el-fill-color-light);
  }

  // 验证状态
  &.is-error .input-wrapper {
    border-color: var(--el-color-danger);

    &:focus-within {
      border-color: var(--el-color-danger);
      box-shadow: 0 0 0 2px var(--el-color-danger-light-9);
    }
  }

  &.is-success .input-wrapper {
    border-color: var(--el-color-success);
  }

  &.is-warning .input-wrapper {
    border-color: var(--el-color-warning);
  }

  /**
   * ===============================
   * 前后缀调整
   * ===============================
   */
  // 有前缀时调整左边距
  &.has-prefix .input-wrapper .input-inner {
    padding-left: 0;
  }

  // 有后缀时调整右边距
  &.has-suffix .input-wrapper .input-inner {
    padding-right: 0;
  }

  // 确保有清除按钮或密码切换按钮时，输入框有足够的右边距
  &.has-suffix .input-wrapper {
    .input-inner {
      padding-right: 4px; // 给按钮留出空间
    }
  }
}
</style>
