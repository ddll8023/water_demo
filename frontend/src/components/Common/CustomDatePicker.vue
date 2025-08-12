<!--
  ===========================
  自定义日期选择器组件
  ===========================
  基于Element Plus的el-date-picker封装，提供更稳定的日期处理
  确保无效日期的安全处理，支持所有Element Plus日期选择器的属性和事件
-->
<template>
  <!-- 当值为 null 时，使用 key 强制重新渲染组件 -->
  <el-date-picker :key="componentKey" :model-value="safeModelValue" @update:model-value="handleUpdateModelValue"
    :type="type" :placeholder="placeholder" :start-placeholder="startPlaceholder" :end-placeholder="endPlaceholder"
    :range-separator="rangeSeparator" :disabled="disabled" :clearable="clearable" :format="format"
    :value-format="valueFormat" :size="size" :readonly="readonly" :editable="editable" :prefix-icon="prefixIcon"
    :clear-icon="clearIcon" :validate-event="validateEvent" :disabled-date="disabledDate" :shortcuts="shortcuts"
    :default-value="null" :default-time="defaultTime" :teleported="teleported" :popper-class="popperClass"
    :popper-options="popperOptions" @change="handleChange" @blur="handleBlur" @focus="handleFocus"
    @calendar-change="handleCalendarChange" @panel-change="handlePanelChange" @visible-change="handleVisibleChange"
    style="width: 100%" />
</template>

<script setup>
import { computed } from 'vue'

/**
 * ===========================
 * 组件属性定义
 * ===========================
 * 日期选择器组件的所有可配置属性
 */
const props = defineProps({
  // v-model绑定值
  modelValue: {
    type: [String, Array, Date],
    default: null
  },
  // 日期选择器类型
  type: {
    type: String,
    default: 'date',
    validator: (value) => ['date', 'datetime', 'daterange', 'datetimerange', 'month', 'year', 'dates', 'week'].includes(value)
  },
  // 占位文字
  placeholder: {
    type: String,
    default: ''
  },
  // 开始日期占位文字（范围选择）
  startPlaceholder: {
    type: String,
    default: '开始日期'
  },
  // 结束日期占位文字（范围选择）
  endPlaceholder: {
    type: String,
    default: '结束日期'
  },
  // 范围分隔符
  rangeSeparator: {
    type: String,
    default: '至'
  },
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  },
  // 是否可清空
  clearable: {
    type: Boolean,
    default: true
  },
  // 日期格式
  format: {
    type: String,
    default: 'YYYY-MM-DD'
  },
  // 值格式
  valueFormat: {
    type: String,
    default: 'YYYY-MM-DD'
  },
  // 尺寸
  size: {
    type: String,
    default: 'default',
    validator: (value) => ['large', 'default', 'small'].includes(value)
  },
  // 是否只读
  readonly: {
    type: Boolean,
    default: false
  },
  // 是否可编辑
  editable: {
    type: Boolean,
    default: true
  },
  // 前缀图标
  prefixIcon: {
    type: String,
    default: ''
  },
  // 清除图标
  clearIcon: {
    type: String,
    default: ''
  },
  // 是否触发表单验证
  validateEvent: {
    type: Boolean,
    default: true
  },
  // 禁用日期函数
  disabledDate: {
    type: Function,
    default: null
  },
  // 快捷选项
  shortcuts: {
    type: Array,
    default: () => []
  },
  // 默认值
  defaultValue: {
    type: [Date, Array],
    default: null
  },
  // 默认时间
  defaultTime: {
    type: [Date, Array],
    default: null
  },
  // 是否将弹出框插入到body
  teleported: {
    type: Boolean,
    default: true
  },
  // 弹出框类名
  popperClass: {
    type: String,
    default: ''
  },
  // 弹出框选项
  popperOptions: {
    type: Object,
    default: () => ({})
  }
})

/**
 * ===========================
 * 组件事件定义
 * ===========================
 * 定义所有可触发的事件
 */
const emit = defineEmits([
  'update:modelValue',
  'change',
  'blur',
  'focus',
  'calendar-change',
  'panel-change',
  'visible-change'
])

/**
 * ===========================
 * 计算属性
 * ===========================
 * 处理组件的响应式数据
 */
// 安全的 modelValue - 确保日期值有效
const safeModelValue = computed(() => {
  // 如果是 null 或 undefined，返回空字符串
  if (props.modelValue === null || props.modelValue === undefined) {
    return ''
  }

  // 如果是字符串且包含 Invalid 或 NaN，返回空字符串
  if (typeof props.modelValue === 'string' &&
    (props.modelValue.includes('Invalid') || props.modelValue.includes('NaN'))) {
    return ''
  }

  // 如果是 Date 对象且无效，返回空字符串
  if (props.modelValue instanceof Date && isNaN(props.modelValue.getTime())) {
    return ''
  }

  return props.modelValue
})

// 组件 key - 用于强制重新渲染
const componentKey = computed(() => {
  // 当值为 null 时，使用特殊的 key 来强制重新渲染
  if (props.modelValue === null || props.modelValue === undefined) {
    return 'empty-date-picker'
  }
  return 'date-picker-with-value'
})

/**
 * ===========================
 * 事件处理函数
 * ===========================
 * 处理用户交互事件
 */
// 更新模型值
const handleUpdateModelValue = (value) => {
  // 防止传递 Invalid Date 或其他无效值
  if (value === 'Invalid Date' ||
    (value instanceof Date && isNaN(value.getTime())) ||
    (typeof value === 'string' && (value.includes('Invalid') || value.includes('NaN')))) {
    // 如果当前 modelValue 是 null，保持 null 状态
    if (props.modelValue === null) {
      return
    }
    // 如果当前有有效值，清空为 null
    emit('update:modelValue', null)
    return
  }

  emit('update:modelValue', value)
}

// 值变化事件
const handleChange = (value) => {
  // 防止传递 Invalid Date 或其他无效值
  if (value === 'Invalid Date' ||
    (value instanceof Date && isNaN(value.getTime())) ||
    (typeof value === 'string' && (value.includes('Invalid') || value.includes('NaN')))) {
    return
  }

  emit('change', value)
}

// 失去焦点事件
const handleBlur = (event) => {
  emit('blur', event)
}

// 获得焦点事件
const handleFocus = (event) => {
  emit('focus', event)
}

// 日历变化事件
const handleCalendarChange = (value) => {
  emit('calendar-change', value)
}

// 面板变化事件
const handlePanelChange = (value, mode, view) => {
  emit('panel-change', value, mode, view)
}

// 可见性变化事件
const handleVisibleChange = (visible) => {
  emit('visible-change', visible)
}

/**
 * ===========================
 * 组件导出方法
 * ===========================
 * 暴露给父组件的方法
 */
defineExpose({
  focus: () => {
    // Element Plus的日期选择器会自动处理focus
  },
  blur: () => {
    // Element Plus的日期选择器会自动处理blur
  }
})
</script>

<style scoped lang="scss">
/**
 * ===========================
 * 组件样式定义
 * ===========================
 * 自定义日期选择器的样式
 */
// 目前使用Element Plus的默认样式
// 如有需要，可在此处添加自定义样式</style>
