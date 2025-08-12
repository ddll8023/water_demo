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
@use "@/assets/styles/index.scss" as *;

/* ===== 基础样式 ===== */
.custom-select {
  position: relative;
  width: 100%;

  /* ===== 输入框样式 ===== */
  .select-input-wrapper {
    @include flex-start;
    min-height: var(--form-item-height);
    background: var(--bg-primary);
    border: 1px solid var(--border-color);
    border-radius: var(--border-radius-base);
    cursor: pointer;
    transition: var(--transition-base);

    &:hover {
      border-color: var(--primary-light);
    }

    &:focus-within {
      border-color: var(--primary-color);
      box-shadow: 0 0 0 2px var(--primary-transparent-light);
    }

    .select-input {
      flex: 1;
      padding: var(--spacing-xs) 0 var(--spacing-xs) var(--spacing-medium);
      min-height: calc(var(--form-item-height) - 2px);
      @include flex-start;
      flex-wrap: wrap;
      gap: var(--spacing-mini);
      overflow: hidden;

      .single-value,
      .placeholder {
        color: var(--text-primary);
        font-size: var(--font-size-base);
        line-height: var(--line-height-base);
        @include text-ellipsis;
        flex: 1;
        min-width: 0;
        max-width: calc(100% - var(--spacing-large));
      }

      .placeholder {
        color: var(--text-placeholder);
      }

      .multiple-values {
        @include flex-start;
        flex-wrap: wrap;
        gap: var(--spacing-mini);
        width: 100%;
        overflow: hidden;

        .selected-tag {
          @include flex-start;
          gap: var(--spacing-mini);
          padding: var(--spacing-micro) var(--spacing-xs);
          background: var(--primary-bg-light);
          border: 1px solid var(--primary-lighter);
          border-radius: var(--border-radius-small);
          font-size: var(--font-size-extra-small);
          color: var(--primary-color);
          max-width: calc(100% - var(--spacing-small));
          flex-shrink: 1;

          .tag-text {
            max-width: calc(100% - var(--spacing-base));
            @include text-ellipsis;
          }

          .tag-close {
            cursor: pointer;
            font-size: var(--icon-size-xs);
            flex-shrink: 0;
            transition: var(--transition-fast);

            &:hover {
              color: var(--primary-dark);
            }
          }
        }
      }

      .search-input {
        border: none;
        outline: none;
        background: transparent;
        font-size: var(--font-size-base);
        color: var(--text-primary);
        flex: 1;
        min-width: 100px;

        &::placeholder {
          color: var(--text-placeholder);
        }
      }
    }

    .select-suffix {
      @include flex-start;
      gap: var(--spacing-mini);
      padding: 0 var(--spacing-medium) 0 var(--spacing-mini);
      flex-shrink: 0;

      .clear-icon,
      .dropdown-icon {
        color: var(--text-placeholder);
        font-size: var(--font-size-extra-small);
        cursor: pointer;
        transition: var(--transition-base);

        &:hover {
          color: var(--text-secondary);
        }
      }

      .dropdown-icon {
        transition: var(--transition-transform);

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
    background: var(--bg-primary);
    border: 1px solid var(--border-light);
    border-radius: var(--border-radius-base);
    box-shadow: var(--shadow-popup);
    z-index: var(--z-index-dropdown);
    max-height: 200px;
    overflow: hidden;

    .dropdown-header,
    .dropdown-footer {
      padding: var(--spacing-small) var(--spacing-medium);
      border-bottom: 1px solid var(--border-light);
      background: var(--bg-secondary);
    }

    .dropdown-footer {
      border-bottom: none;
      border-top: 1px solid var(--border-light);
    }

    .options-list {
      max-height: 200px;
      overflow-y: auto;
      @include custom-scrollbar;

      .select-option {
        @include flex-between;
        padding: var(--spacing-small) var(--spacing-medium);
        cursor: pointer;
        font-size: var(--font-size-base);
        color: var(--text-secondary);
        transition: var(--transition-fast);

        &:hover {
          background: var(--bg-secondary);
        }

        &.is-selected {
          color: var(--primary-color);
          background: var(--primary-bg-light);
        }

        &.is-disabled {
          color: var(--text-disabled);
          cursor: not-allowed;

          &:hover {
            background: transparent;
          }
        }

        .option-check {
          color: var(--primary-color);
          font-size: var(--font-size-extra-small);
        }
      }

      .loading-option,
      .empty-option {
        @include flex-center;
        gap: var(--spacing-small);
        padding: var(--spacing-base) var(--spacing-medium);
        color: var(--text-placeholder);
        font-size: var(--font-size-base);

        i {
          font-size: var(--icon-size-md);
        }
      }
    }
  }

  /* ===== 状态样式 ===== */
  // 禁用状态
  &.is-disabled {
    .select-input-wrapper {
      background: var(--bg-disabled);
      border-color: var(--border-light);
      cursor: not-allowed;

      .select-input {
        color: var(--text-disabled);
      }

      .select-suffix {

        .clear-icon,
        .dropdown-icon {
          color: var(--text-disabled);
          cursor: not-allowed;
        }
      }
    }
  }

  /* ===== 尺寸变体 ===== */
  // 大尺寸
  &.custom-select--large {
    font-size: var(--font-size-medium);

    .select-input-wrapper {
      min-height: calc(var(--form-item-height) + var(--spacing-small));

      .select-input {
        padding: var(--spacing-sm) var(--spacing-base);
      }
    }
  }

  // 小尺寸
  &.custom-select--small {
    font-size: var(--font-size-extra-small);

    .select-input-wrapper {
      min-height: calc(var(--form-item-height) - var(--spacing-base));

      .select-input {
        padding: var(--spacing-mini) var(--spacing-small);
      }
    }
  }
}

/* ===== 动画效果 ===== */
// 下拉动画
.dropdown-enter-active,
.dropdown-leave-active {
  transition: var(--transition-base);
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-var(--spacing-sm));
}
</style>
