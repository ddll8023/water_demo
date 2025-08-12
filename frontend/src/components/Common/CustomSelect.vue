<!-- 自定义选择器组件 - 用于替换Element UI的远程搜索选择器 -->
<template>
  <div class="custom-select" :class="selectClasses">
    <!-- 选择器输入框 -->
    <div class="select-input-wrapper" @click="toggleDropdown">
      <div class="select-input">
        <!-- 单选显示 -->
        <div v-if="!multiple && selectedOption" class="single-value" :title="selectedOption[labelKey]">
          <slot name="option" :option="selectedOption">
            {{ selectedOption[labelKey] }}
          </slot>
        </div>

        <!-- 多选显示 -->
        <div v-else-if="multiple && selectedOptions.length > 0" class="multiple-values">
          <div v-for="option in selectedOptions" :key="option[valueKey]" class="selected-tag">
            <slot name="tag" :option="option">
              <span class="tag-text" :title="option[labelKey]">{{ option[labelKey] }}</span>
              <i class="fa fa-times tag-close" @click.stop="removeOption(option)"></i>
            </slot>
          </div>
        </div>

        <!-- 占位符 -->
        <div v-else class="placeholder">
          {{ placeholder }}
        </div>

        <!-- 搜索输入框 -->
        <input v-if="filterable && dropdownVisible" ref="searchInput" v-model="searchKeyword" class="search-input"
          :placeholder="searchPlaceholder" @input="handleSearch" @keydown.enter.prevent="selectFirstOption"
          @keydown.esc="closeDropdown" />
      </div>

      <!-- 右侧图标 -->
      <div class="select-suffix">
        <i v-if="clearable && hasValue && !disabled" class="fa fa-times clear-icon" @click.stop="handleClear"></i>
        <i class="fa fa-chevron-down dropdown-icon" :class="{ 'is-reverse': dropdownVisible }"></i>
      </div>
    </div>

    <!-- 下拉选项 -->
    <transition name="dropdown">
      <div v-if="dropdownVisible" class="select-dropdown">
        <!-- 头部插槽 -->
        <div v-if="$slots.header" class="dropdown-header">
          <slot name="header"></slot>
        </div>

        <!-- 选项列表 -->
        <div class="options-list">
          <div v-for="option in filteredOptions" :key="option[valueKey]" class="select-option" :class="{
            'is-selected': isSelected(option),
            'is-disabled': option.disabled
          }" @click="selectOption(option)">
            <slot name="option" :option="option">
              {{ option[labelKey] }}
            </slot>

            <!-- 多选勾选图标 -->
            <i v-if="multiple && isSelected(option)" class="fa fa-check option-check"></i>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-option">
            <i class="fa fa-spinner fa-spin"></i>
            <span>加载中...</span>
          </div>

          <!-- 空数据 -->
          <div v-else-if="filteredOptions.length === 0" class="empty-option">
            <slot name="empty">
              <i class="fa fa-inbox"></i>
              <span>{{ emptyText }}</span>
            </slot>
          </div>
        </div>

        <!-- 底部插槽 -->
        <div v-if="$slots.footer" class="dropdown-footer">
          <slot name="footer"></slot>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted, onUnmounted } from 'vue'

// ===== 组件属性定义 =====
const props = defineProps({
  // 选中值
  modelValue: {
    type: [String, Number, Array],
    default: null
  },
  // 选项数据
  options: {
    type: Array,
    default: () => []
  },
  // 值字段名
  valueKey: {
    type: String,
    default: 'value'
  },
  // 标签字段名
  labelKey: {
    type: String,
    default: 'label'
  },
  // 占位符
  placeholder: {
    type: String,
    default: '请选择'
  },
  // 搜索占位符
  searchPlaceholder: {
    type: String,
    default: '请输入关键词搜索'
  },
  // 是否多选
  multiple: {
    type: Boolean,
    default: false
  },
  // 是否可搜索
  filterable: {
    type: Boolean,
    default: false
  },
  // 是否远程搜索
  remote: {
    type: Boolean,
    default: false
  },
  // 远程搜索方法
  remoteMethod: {
    type: Function,
    default: null
  },
  // 是否可清除
  clearable: {
    type: Boolean,
    default: false
  },
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  },
  // 加载状态
  loading: {
    type: Boolean,
    default: false
  },
  // 空数据文本
  emptyText: {
    type: String,
    default: '暂无数据'
  },
  // 尺寸
  size: {
    type: String,
    default: 'default',
    validator: (value) => ['large', 'default', 'small'].includes(value)
  }
})

// ===== 组件事件定义 =====
const emit = defineEmits([
  'update:modelValue',
  'change',
  'clear',
  'visible-change',
  'remove-tag'
])

// ===== 响应式状态 =====
const dropdownVisible = ref(false)
const searchKeyword = ref('')
const searchInput = ref()

// ===== 计算属性 =====
// 单选选中项
const selectedOption = computed(() => {
  if (props.multiple) return null
  return props.options.find(option => option[props.valueKey] === props.modelValue)
})

// 多选选中项数组
const selectedOptions = computed(() => {
  if (!props.multiple) return []
  const values = Array.isArray(props.modelValue) ? props.modelValue : []
  return props.options.filter(option => values.includes(option[props.valueKey]))
})

// 是否有选中值
const hasValue = computed(() => {
  if (props.multiple) {
    return Array.isArray(props.modelValue) && props.modelValue.length > 0
  }
  return props.modelValue !== null && props.modelValue !== undefined && props.modelValue !== ''
})

// 选择器样式类
const selectClasses = computed(() => {
  return [
    `custom-select--${props.size}`,
    {
      'is-multiple': props.multiple,
      'is-disabled': props.disabled,
      'is-focused': dropdownVisible.value
    }
  ]
})

// 过滤后的选项列表
const filteredOptions = computed(() => {
  if (props.remote) {
    return props.options
  }

  if (!props.filterable || !searchKeyword.value) {
    return props.options
  }

  const keyword = searchKeyword.value.toLowerCase()
  return props.options.filter(option =>
    option[props.labelKey].toLowerCase().includes(keyword)
  )
})

// ===== 下拉框控制 =====
/**
 * 切换下拉框显示状态
 */
const toggleDropdown = () => {
  if (props.disabled) return

  dropdownVisible.value = !dropdownVisible.value
  emit('visible-change', dropdownVisible.value)

  if (dropdownVisible.value && props.filterable) {
    nextTick(() => {
      searchInput.value?.focus()
    })
  }
}

/**
 * 关闭下拉框
 */
const closeDropdown = () => {
  dropdownVisible.value = false
  searchKeyword.value = ''
  emit('visible-change', false)
}

/**
 * 处理点击外部事件
 */
const handleClickOutside = (event) => {
  const selectElement = event.target.closest('.custom-select')
  if (!selectElement && dropdownVisible.value) {
    closeDropdown()
  }
}

// ===== 选项操作 =====
/**
 * 选择选项
 */
const selectOption = (option) => {
  if (option.disabled) return

  if (props.multiple) {
    const values = Array.isArray(props.modelValue) ? [...props.modelValue] : []
    const index = values.indexOf(option[props.valueKey])

    if (index > -1) {
      values.splice(index, 1)
    } else {
      values.push(option[props.valueKey])
    }

    emit('update:modelValue', values)
    emit('change', values, option)
  } else {
    emit('update:modelValue', option[props.valueKey])
    emit('change', option[props.valueKey], option)
    closeDropdown()
  }
}

/**
 * 移除选项
 */
const removeOption = (option) => {
  if (props.multiple) {
    const values = Array.isArray(props.modelValue) ? [...props.modelValue] : []
    const index = values.indexOf(option[props.valueKey])

    if (index > -1) {
      values.splice(index, 1)
      emit('update:modelValue', values)
      emit('change', values, option)
      emit('remove-tag', option[props.valueKey])
    }
  }
}

/**
 * 判断选项是否被选中
 */
const isSelected = (option) => {
  if (props.multiple) {
    const values = Array.isArray(props.modelValue) ? props.modelValue : []
    return values.includes(option[props.valueKey])
  }
  return option[props.valueKey] === props.modelValue
}

/**
 * 清空选中值
 */
const handleClear = () => {
  const newValue = props.multiple ? [] : null
  emit('update:modelValue', newValue)
  emit('change', newValue)
  emit('clear')
}

// ===== 搜索功能 =====
/**
 * 处理搜索输入
 */
const handleSearch = () => {
  if (props.remote && props.remoteMethod) {
    props.remoteMethod(searchKeyword.value)
  }
}

/**
 * 选择第一个选项
 */
const selectFirstOption = () => {
  if (filteredOptions.value.length > 0) {
    selectOption(filteredOptions.value[0])
  }
}

// ===== 生命周期钩子 =====
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

// ===== 对外暴露方法 =====
defineExpose({
  /**
   * 聚焦方法
   */
  focus: () => {
    if (props.filterable && searchInput.value) {
      searchInput.value.focus()
    }
  },
  /**
   * 失焦方法
   */
  blur: () => {
    closeDropdown()
  }
})
</script>

<style scoped lang="scss">
/* ===== 基础样式 ===== */
.custom-select {
  position: relative;
  width: 100%;

  /* ===== 输入框样式 ===== */
  .select-input-wrapper {
    display: flex;
    align-items: center;
    min-height: 32px;
    background: #FFFFFF;
    border: 1px solid var(--el-border-color);
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      border-color: var(--el-color-primary-light-7);
    }

    &:focus-within {
      border-color: var(--el-color-primary);
      box-shadow: 0 0 0 2px var(--el-color-primary-light-9);
    }

    .select-input {
      flex: 1;
      padding: 6px 0 6px 12px;
      min-height: 20px;
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      gap: 4px;
      overflow: hidden;

      .single-value,
      .placeholder {
        color: var(--el-text-color-primary);
        font-size: 14px;
        line-height: 20px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        flex: 1;
        min-width: 0;
        max-width: calc(100% - 20px);
      }

      .placeholder {
        color: var(--el-text-color-placeholder);
      }

      .multiple-values {
        display: flex;
        flex-wrap: wrap;
        gap: 4px;
        width: 100%;
        overflow: hidden;

        .selected-tag {
          display: flex;
          align-items: center;
          gap: 4px;
          padding: 2px 6px;
          background: var(--el-color-primary-light-9);
          border: 1px solid var(--el-color-primary-light-8);
          border-radius: 3px;
          font-size: 12px;
          color: var(--el-color-primary);
          max-width: calc(100% - 8px);
          flex-shrink: 1;

          .tag-text {
            max-width: calc(100% - 14px);
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            display: inline-block;
          }

          .tag-close {
            cursor: pointer;
            font-size: 10px;
            flex-shrink: 0;

            &:hover {
              color: var(--el-color-primary-dark-2);
            }
          }
        }
      }

      .search-input {
        border: none;
        outline: none;
        background: transparent;
        font-size: 14px;
        color: var(--el-text-color-primary);
        flex: 1;
        min-width: 100px;

        &::placeholder {
          color: var(--el-text-color-placeholder);
        }
      }
    }

    .select-suffix {
      display: flex;
      align-items: center;
      gap: 4px;
      padding: 0 12px 0 4px;
      flex-shrink: 0;

      .clear-icon,
      .dropdown-icon {
        color: var(--el-text-color-placeholder);
        font-size: 12px;
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
          color: var(--el-text-color-regular);
        }
      }

      .dropdown-icon {
        transition: transform 0.3s ease;

        &.is-reverse {
          transform: rotate(180deg);
        }
      }
    }
  }

  /* ===== 下拉框样式 ===== */
  .select-dropdown {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background: #FFFFFF;
    border: 1px solid var(--el-border-color-light);
    border-radius: 4px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    z-index: 1000;
    max-height: 200px;
    overflow: hidden;

    .dropdown-header,
    .dropdown-footer {
      padding: 8px 12px;
      border-bottom: 1px solid var(--el-border-color-lighter);
      background: var(--el-fill-color-light);
    }

    .dropdown-footer {
      border-bottom: none;
      border-top: 1px solid var(--el-border-color-lighter);
    }

    .options-list {
      max-height: 200px;
      overflow-y: auto;

      .select-option {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 8px 12px;
        cursor: pointer;
        font-size: 14px;
        color: var(--el-text-color-regular);
        transition: background-color 0.3s ease;

        &:hover {
          background: var(--el-fill-color-light);
        }

        &.is-selected {
          color: var(--el-color-primary);
          background: var(--el-color-primary-light-9);
        }

        &.is-disabled {
          color: var(--el-text-color-disabled);
          cursor: not-allowed;

          &:hover {
            background: transparent;
          }
        }

        .option-check {
          color: var(--el-color-primary);
          font-size: 12px;
        }
      }

      .loading-option,
      .empty-option {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
        padding: 16px 12px;
        color: var(--el-text-color-placeholder);
        font-size: 14px;

        i {
          font-size: 16px;
        }
      }
    }
  }

  /* ===== 状态样式 ===== */
  // 禁用状态
  &.is-disabled {
    .select-input-wrapper {
      background: var(--el-fill-color-light);
      border-color: var(--el-border-color-lighter);
      cursor: not-allowed;

      .select-input {
        color: var(--el-text-color-disabled);
      }

      .select-suffix {

        .clear-icon,
        .dropdown-icon {
          color: var(--el-text-color-disabled);
          cursor: not-allowed;
        }
      }
    }
  }

  /* ===== 尺寸变体 ===== */
  // 大尺寸
  &.custom-select--large {
    font-size: 16px;

    .select-input-wrapper {
      min-height: 40px;

      .select-input {
        padding: 10px 16px;
      }
    }
  }

  // 小尺寸
  &.custom-select--small {
    font-size: 12px;

    .select-input-wrapper {
      min-height: 24px;

      .select-input {
        padding: 4px 8px;
      }
    }
  }
}

/* ===== 动画效果 ===== */
// 下拉动画
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.3s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
