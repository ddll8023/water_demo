<!-- 自定义级联选择器组件 - 替换Element UI的el-cascader -->
<template>
  <div class="custom-cascader" :class="cascaderClasses">
    <!-- 选择器输入框 -->
    <div class="cascader-input-wrapper" @click="toggleDropdown">
      <div class="cascader-input">
        <!-- 显示选中值 -->
        <div v-if="displayText" class="selected-value">
          {{ displayText }}
        </div>
        <div v-else class="placeholder">
          {{ placeholder }}
        </div>

        <!-- 清除按钮 -->
        <div v-if="clearable && selectedValue.length > 0 && !disabled" class="clear-button" @click.stop="handleClear">
          <i class="fa fa-times"></i>
        </div>

        <!-- 下拉箭头 -->
        <div class="dropdown-arrow" :class="{ 'is-reverse': dropdownVisible }">
          <i class="fa fa-angle-down"></i>
        </div>
      </div>
    </div>

    <!-- 下拉面板 -->
    <transition name="cascader-dropdown">
      <div v-if="dropdownVisible" class="cascader-dropdown">
        <div class="cascader-menus">
          <!-- 各级菜单 -->
          <div v-for="(menu, level) in menus" :key="level" class="cascader-menu">
            <div class="menu-title" v-if="level > 0">
              {{ getMenuTitle(level) }}
            </div>
            <div class="menu-options">
              <div v-for="option in menu" :key="option[valueKey]" class="menu-option" :class="{
                'is-selected': isSelected(option, level),
                'is-disabled': option.disabled,
                'has-children': hasChildren(option)
              }" @click="selectOption(option, level)">
                <slot name="option" :option="option" :level="level">
                  <span class="option-label">{{ option[labelKey] }}</span>
                </slot>

                <!-- 子级指示器 -->
                <i v-if="hasChildren(option)" class="fa fa-angle-right option-arrow"></i>
              </div>

              <!-- 空数据 -->
              <div v-if="menu.length === 0" class="empty-option">
                <i class="fa fa-inbox"></i>
                <span>暂无数据</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'

/**
 * ----------------------------------------
 * 组件属性和事件定义
 * ----------------------------------------
 */
// Props定义
const props = defineProps({
  // v-model绑定值
  modelValue: {
    type: Array,
    default: () => []
  },
  // 选项数据
  options: {
    type: Array,
    default: () => []
  },
  // 配置选项
  props: {
    type: Object,
    default: () => ({
      value: 'value',
      label: 'label',
      children: 'children',
      disabled: 'disabled'
    })
  },
  // 占位文字
  placeholder: {
    type: String,
    default: '请选择'
  },
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  },
  // 是否可清空
  clearable: {
    type: Boolean,
    default: false
  },
  // 是否显示完整路径
  showAllLevels: {
    type: Boolean,
    default: true
  },
  // 分隔符
  separator: {
    type: String,
    default: ' / '
  },
  // 是否可搜索
  filterable: {
    type: Boolean,
    default: false
  },
  // 尺寸
  size: {
    type: String,
    default: 'default',
    validator: (value) => ['large', 'default', 'small'].includes(value)
  }
})

// Emits定义
const emit = defineEmits([
  'update:modelValue',
  'change',
  'expand-change',
  'visible-change'
])

/**
 * ----------------------------------------
 * 组件状态和计算属性
 * ----------------------------------------
 */
// 响应式数据
const dropdownVisible = ref(false)
const selectedValue = ref([...props.modelValue])
const menus = ref([])

// 计算属性
const valueKey = computed(() => props.props.value || 'value')
const labelKey = computed(() => props.props.label || 'label')
const childrenKey = computed(() => props.props.children || 'children')
const disabledKey = computed(() => props.props.disabled || 'disabled')

// 级联选择器CSS类
const cascaderClasses = computed(() => {
  return [
    `custom-cascader--${props.size}`,
    {
      'is-disabled': props.disabled,
      'is-focused': dropdownVisible.value
    }
  ]
})

// 显示文本
const displayText = computed(() => {
  if (selectedValue.value.length === 0) return ''

  const labels = getSelectedLabels()
  if (props.showAllLevels) {
    return labels.join(props.separator)
  } else {
    return labels[labels.length - 1] || ''
  }
})

/**
 * ----------------------------------------
 * 选项和菜单处理
 * ----------------------------------------
 */
// 获取选中项的标签集合
const getSelectedLabels = () => {
  const labels = []
  let currentOptions = props.options

  for (let i = 0; i < selectedValue.value.length; i++) {
    const value = selectedValue.value[i]
    const option = currentOptions.find(opt => opt[valueKey.value] === value)

    if (option) {
      labels.push(option[labelKey.value])
      currentOptions = option[childrenKey.value] || []
    } else {
      break
    }
  }

  return labels
}

// 判断选项是否有子级
const hasChildren = (option) => {
  return option[childrenKey.value] && option[childrenKey.value].length > 0
}

// 判断选项是否被选中
const isSelected = (option, level) => {
  return selectedValue.value[level] === option[valueKey.value]
}

// 获取菜单标题
const getMenuTitle = (level) => {
  if (level === 0) return ''
  const labels = getSelectedLabels()
  return labels[level - 1] || ''
}

// 构建级联菜单数据
const buildMenus = () => {
  const newMenus = []
  let currentOptions = props.options

  // 第一级菜单
  newMenus.push([...currentOptions])

  // 根据选中值构建后续菜单
  for (let i = 0; i < selectedValue.value.length; i++) {
    const value = selectedValue.value[i]
    const option = currentOptions.find(opt => opt[valueKey.value] === value)

    if (option && hasChildren(option)) {
      currentOptions = option[childrenKey.value]
      newMenus.push([...currentOptions])
    } else {
      break
    }
  }

  menus.value = newMenus
}

/**
 * ----------------------------------------
 * 用户交互事件处理
 * ----------------------------------------
 */
// 选择选项
const selectOption = (option, level) => {
  if (option[disabledKey.value]) return

  // 更新选中值
  const newValue = [...selectedValue.value.slice(0, level), option[valueKey.value]]

  // 如果有子级，继续选择
  if (hasChildren(option)) {
    selectedValue.value = newValue
    buildMenus()
    emit('expand-change', newValue)
  } else {
    // 没有子级，完成选择
    selectedValue.value = newValue
    emit('update:modelValue', newValue)
    emit('change', newValue)
    toggleDropdown()
  }
}

// 清除选择
const handleClear = () => {
  selectedValue.value = []
  emit('update:modelValue', [])
  emit('change', [])
  buildMenus()
}

// 切换下拉面板显示状态
const toggleDropdown = () => {
  if (props.disabled) return

  dropdownVisible.value = !dropdownVisible.value
  emit('visible-change', dropdownVisible.value)

  if (dropdownVisible.value) {
    buildMenus()
  }
}

// 处理点击外部区域
const handleClickOutside = (event) => {
  const cascaderEl = event.target.closest('.custom-cascader')
  if (!cascaderEl) {
    dropdownVisible.value = false
    emit('visible-change', false)
  }
}

/**
 * ----------------------------------------
 * 监听器和生命周期钩子
 * ----------------------------------------
 */
// 监听外部值变化
watch(() => props.modelValue, (newValue) => {
  selectedValue.value = [...newValue]
  if (dropdownVisible.value) {
    buildMenus()
  }
}, { immediate: true })

// 监听选项数据变化
watch(() => props.options, () => {
  if (dropdownVisible.value) {
    buildMenus()
  }
}, { deep: true })

// 生命周期钩子
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped lang="scss">
/**
 * ----------------------------------------
 * 基础组件样式
 * ----------------------------------------
 */
.custom-cascader {
  position: relative;
  width: 100%;
  font-size: 14px;

  /**
   * 输入框样式
   */
  .cascader-input-wrapper {
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

    .cascader-input {
      flex: 1;
      display: flex;
      align-items: center;
      padding: 6px 12px;
      min-height: 20px;

      .selected-value {
        color: var(--el-text-color-primary);
        font-size: 14px;
        line-height: 20px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        flex: 1;
        min-width: 0;
      }

      .placeholder {
        color: var(--el-text-color-placeholder);
        font-size: 14px;
        line-height: 20px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        flex: 1;
        min-width: 0;
      }
    }

    /**
     * 功能按钮样式
     */
    .clear-button {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 20px;
      height: 20px;
      margin-right: 4px;
      color: var(--el-text-color-regular);
      cursor: pointer;
      border-radius: 2px;
      transition: all 0.3s ease;

      &:hover {
        color: var(--el-color-primary);
        background: var(--el-color-primary-light-9);
      }

      .fa {
        font-size: 12px;
      }
    }

    .dropdown-arrow {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 20px;
      height: 20px;
      margin-right: 12px;
      color: var(--el-text-color-regular);
      transition: transform 0.3s ease;

      &.is-reverse {
        transform: rotate(180deg);
      }

      .fa {
        font-size: 12px;
      }
    }
  }

  /**
   * 下拉菜单样式
   */
  .cascader-dropdown {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    z-index: 1000;
    margin-top: 4px;
    background: #FFFFFF;
    border: 1px solid var(--el-border-color-light);
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

    .cascader-menus {
      display: flex;
      max-height: 300px;
      overflow: hidden;

      .cascader-menu {
        min-width: 180px;
        border-right: 1px solid var(--el-border-color-lighter);

        &:last-child {
          border-right: none;
        }

        .menu-title {
          padding: 8px 12px;
          background: var(--el-fill-color-light);
          border-bottom: 1px solid var(--el-border-color-lighter);
          font-size: 12px;
          color: var(--el-text-color-regular);
          font-weight: 500;
        }

        .menu-options {
          max-height: 260px;
          overflow-y: auto;

          /**
           * 选项项样式
           */
          .menu-option {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 8px 12px;
            cursor: pointer;
            transition: all 0.3s ease;

            &:hover:not(.is-disabled) {
              background: var(--el-fill-color-light);
            }

            &.is-selected {
              background: var(--el-color-primary-light-9);
              color: var(--el-color-primary);
              font-weight: 500;
            }

            &.is-disabled {
              color: var(--el-text-color-disabled);
              cursor: not-allowed;
            }

            .option-label {
              flex: 1;
              font-size: 14px;
              line-height: 20px;
            }

            .option-arrow {
              margin-left: 8px;
              font-size: 12px;
              color: var(--el-text-color-placeholder);
            }
          }

          /**
           * 空数据状态样式
           */
          .empty-option {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            padding: 20px;
            color: var(--el-text-color-placeholder);

            .fa {
              font-size: 24px;
              margin-bottom: 8px;
            }

            span {
              font-size: 14px;
            }
          }
        }
      }
    }
  }

  /**
   * ----------------------------------------
   * 尺寸变体样式
   * ----------------------------------------
   */
  &.custom-cascader--large {
    font-size: 16px;

    .cascader-input-wrapper {
      min-height: 40px;

      .cascader-input {
        padding: 10px 16px;
      }
    }
  }

  &.custom-cascader--small {
    font-size: 12px;

    .cascader-input-wrapper {
      min-height: 24px;

      .cascader-input {
        padding: 4px 8px;
      }
    }
  }

  /**
   * ----------------------------------------
   * 组件状态样式
   * ----------------------------------------
   */
  &.is-focused .cascader-input-wrapper {
    border-color: var(--el-color-primary);
    box-shadow: 0 0 0 2px var(--el-color-primary-light-9);
  }

  &.is-disabled .cascader-input-wrapper {
    background: var(--el-fill-color-light);
    border-color: var(--el-border-color-lighter);
    cursor: not-allowed;

    .cascader-input {
      color: var(--el-text-color-disabled);
    }
  }
}

/**
 * ----------------------------------------
 * 过渡动画样式
 * ----------------------------------------
 */
.cascader-dropdown-enter-active,
.cascader-dropdown-leave-active {
  transition: all 0.3s ease;
}

.cascader-dropdown-enter-from,
.cascader-dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
