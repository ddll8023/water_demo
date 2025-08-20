<!-- 自定义按钮组件 - 实现index.html的按钮视觉效果 -->
<template>
  <button :class="buttonClasses" :disabled="disabled || loading" @click="handleClick" :type="nativeType">
    <!-- 加载图标 -->
    <i v-if="loading" class="fa fa-spinner fa-spin custom-button__loading-icon"></i>

    <!-- 默认插槽内容 -->
    <span v-if="$slots.default" :class="{ 'custom-button__content--with-loading': loading }">
      <slot></slot>
    </span>
  </button>
</template>

<script setup>
import { computed } from 'vue'

/**
 * ----------------------------------------
 * 组件属性定义
 * ----------------------------------------
 */
// Props定义
const props = defineProps({
  // 按钮类型
  type: {
    type: String,
    default: 'secondary',
    validator: (value) => ['primary', 'secondary', 'danger', 'text'].includes(value)
  },
  // 按钮尺寸
  size: {
    type: String,
    default: 'default',
    validator: (value) => ['large', 'default', 'small'].includes(value)
  },
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  },
  // 是否加载中
  loading: {
    type: Boolean,
    default: false
  },
  // 原生type属性
  nativeType: {
    type: String,
    default: 'button',
    validator: (value) => ['button', 'submit', 'reset'].includes(value)
  },
  // 文本按钮的颜色变体（仅在type="text"时有效）
  textType: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'danger'].includes(value)
  },
  // 按钮形状
  shape: {
    type: String,
    default: 'default',
    validator: (value) => ['default', 'circle'].includes(value)
  },
  // 是否为纯图标按钮
  iconOnly: {
    type: Boolean,
    default: false
  }
})

// Emits定义
const emit = defineEmits(['click'])

/**
 * ----------------------------------------
 * 计算属性
 * ----------------------------------------
 */
// 计算按钮样式类
const buttonClasses = computed(() => {
  const classes = ['custom-button']

  // 按钮类型
  if (props.type === 'text') {
    classes.push('custom-button--text')
    classes.push(`custom-button--text-${props.textType}`)
  } else {
    classes.push(`custom-button--${props.type}`)
  }

  // 按钮尺寸
  classes.push(`custom-button--${props.size}`)

  // 按钮形状
  if (props.shape === 'circle') {
    classes.push('custom-button--circle')
  }

  // 纯图标按钮
  if (props.iconOnly) {
    classes.push('custom-button--icon-only')
  }

  // 状态类
  if (props.disabled) {
    classes.push('custom-button--disabled')
  }
  if (props.loading) {
    classes.push('custom-button--loading')
  }

  return classes
})

/**
 * ----------------------------------------
 * 事件处理方法
 * ----------------------------------------
 */
// 点击事件处理
const handleClick = (event) => {
  if (!props.disabled && !props.loading) {
    emit('click', event)
  }
}
</script>

<style lang="scss" scoped>
@use "@/assets/styles/index.scss" as *;

/**
 * ----------------------------------------
 * 按钮基础样式
 * ----------------------------------------
 */
.custom-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: var(--border-radius-md);
  font-family: inherit;
  font-weight: var(--font-weight-normal);
  text-align: center;
  white-space: nowrap;
  cursor: pointer;
  outline: none;
  transition: var(--transition-fast);
  user-select: none;
  vertical-align: middle;

  // 禁用默认样式
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;

  // 图标间距
  i {
    margin-right: var(--spacing-xs);

    &:last-child {
      margin-right: 0;
    }
  }

  // 只有图标时的样式
  &:not(:has(.custom-button__content--with-loading)) i:only-child {
    margin-right: 0;
  }
}

/**
 * ----------------------------------------
 * 按钮类型样式
 * ----------------------------------------
 */
// 主要按钮样式
.custom-button--primary {
  background-color: var(--primary-color);
  color: var(--text-on-dark);
  border: var(--border-width-base) solid var(--primary-color);

  &:hover:not(.custom-button--disabled):not(.custom-button--loading) {
    background-color: var(--primary-dark);
    border-color: var(--primary-dark);
  }

  &:active:not(.custom-button--disabled):not(.custom-button--loading) {
    background-color: var(--primary-dark);
    border-color: var(--primary-dark);
    transform: translateY(var(--hover-transform-y-small));
  }
}

// 次要按钮样式
.custom-button--secondary {
  background-color: var(--bg-primary);
  color: var(--text-primary);
  border: var(--border-width-base) solid var(--border-color);

  &:hover:not(.custom-button--disabled):not(.custom-button--loading) {
    background-color: var(--bg-tertiary);
    border-color: var(--border-color);
  }

  &:active:not(.custom-button--disabled):not(.custom-button--loading) {
    background-color: var(--bg-secondary);
    transform: translateY(var(--hover-transform-y-small));
  }
}

// 危险按钮样式
.custom-button--danger {
  background-color: var(--danger-color);
  color: var(--text-on-dark);
  border: var(--border-width-base) solid var(--danger-color);

  &:hover:not(.custom-button--disabled):not(.custom-button--loading) {
    background-color: var(--danger-dark);
    border-color: var(--danger-dark);
  }

  &:active:not(.custom-button--disabled):not(.custom-button--loading) {
    background-color: var(--danger-dark);
    border-color: var(--danger-dark);
    transform: translateY(var(--hover-transform-y-small));
  }
}

// 文本按钮样式
.custom-button--text {
  background: none;
  border: none;
  padding: var(--padding-button-text);

  &.custom-button--text-primary {
    color: var(--primary-color);

    &:hover:not(.custom-button--disabled):not(.custom-button--loading) {
      color: var(--primary-dark);
    }
  }

  &.custom-button--text-danger {
    color: var(--danger-color);

    &:hover:not(.custom-button--disabled):not(.custom-button--loading) {
      color: var(--danger-dark);
    }
  }

  &:active:not(.custom-button--disabled):not(.custom-button--loading) {
    transform: translateY(var(--hover-transform-y-small));
  }
}

/**
 * ----------------------------------------
 * 按钮尺寸样式
 * ----------------------------------------
 */
// 大尺寸按钮
.custom-button--large {
  padding: var(--spacing-medium) var(--spacing-large);
  font-size: var(--font-size-medium-button);
  min-height: var(--button-height-large);

  &.custom-button--text {
    padding: var(--spacing-small) var(--spacing-medium);
  }
}

// 默认尺寸按钮
.custom-button--default {
  padding: var(--spacing-small) var(--spacing-base);
  font-size: var(--font-size-base);
  min-height: var(--form-item-height);

  &.custom-button--text {
    padding: var(--padding-button-text);
  }
}

// 小尺寸按钮
.custom-button--small {
  padding: var(--spacing-mini) var(--spacing-medium);
  font-size: var(--font-size-sm);
  min-height: var(--button-size-small);

  &.custom-button--text {
    padding: var(--padding-button-text);
  }
}

/**
 * ----------------------------------------
 * 按钮形状样式
 * ----------------------------------------
 */
// 圆形按钮
.custom-button--circle {
  border-radius: var(--border-radius-round);
  width: var(--icon-size-2xl);
  height: var(--icon-size-2xl);
  min-width: var(--icon-size-2xl);
  padding: 0;
  flex-shrink: 0;
  border: none;

  &:hover:not(.custom-button--disabled):not(.custom-button--loading) {
    border: none;
  }

  &.custom-button--large {
    width: calc(var(--icon-size-2xl) + var(--spacing-small));
    height: calc(var(--icon-size-2xl) + var(--spacing-small));
    min-width: calc(var(--icon-size-2xl) + var(--spacing-small));
  }

  &.custom-button--small {
    width: calc(var(--icon-size-2xl) - var(--spacing-small));
    height: calc(var(--icon-size-2xl) - var(--spacing-small));
    min-width: calc(var(--icon-size-2xl) - var(--spacing-small));
  }
}

// 纯图标按钮
.custom-button--icon-only {
  i {
    margin-right: 0;
    font-size: var(--font-size-extra-small);
  }
}

/**
 * ----------------------------------------
 * 按钮状态样式
 * ----------------------------------------
 */
// 禁用状态样式
.custom-button--disabled {
  opacity: var(--disabled-opacity);
  cursor: not-allowed;

  &:hover,
  &:active {
    transform: none !important;
  }
}

// 加载状态样式
.custom-button--loading {
  cursor: not-allowed;

  .custom-button__content--with-loading {
    opacity: var(--disabled-opacity);
  }
}

/**
 * ----------------------------------------
 * 加载动画样式
 * ----------------------------------------
 */
// 加载图标样式
.custom-button__loading-icon {
  margin-right: var(--spacing-xs);
  animation: loading-spin 1s linear infinite;
}

/**
 * ----------------------------------------
 * 响应式设计
 * ----------------------------------------
 */
@include respond-to(sm) {
  .custom-button--large {
    padding: var(--spacing-sm) var(--spacing-base);
    font-size: var(--font-size-medium);
    min-height: var(--button-height-large);
  }

  .custom-button--default {
    padding: 7px var(--spacing-small);
    font-size: var(--font-size-sm);
    min-height: 34px;
  }

  .custom-button--small {
    padding: var(--spacing-mini) var(--spacing-sm);
    font-size: var(--font-size-sm);
    min-height: 26px;
  }
}
</style>
