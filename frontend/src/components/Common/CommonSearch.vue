<!-- 通用搜索组件 - 支持多种搜索条件和快速搜索 -->
<template>
  <div class="common-search">
    <div class="search-form" :class="{ 'single-row': singleRow }">
      <!-- 搜索区域：包含搜索字段和搜索按钮 -->
      <div class="search-area">
        <!-- 搜索字段容器 -->
        <div class="search-fields-container" :style="getFieldsContainerStyle()">
          <div v-for="(item, index) in items" :key="item.prop || index" class="search-field-item"
            :style="getFieldItemStyle(item)">
            <div class="search-field-label" v-if="item.label" :style="getFieldLabelStyle(item)">{{ item.label }}</div>
            <div class="search-field-content">
              <!-- 输入框 -->
              <CustomInput v-if="item.type === 'input' || !item.type" v-model="searchData[item.prop]"
                :placeholder="item.placeholder || `请输入${item.label}`" :clearable="item.clearable !== false"
                :disabled="item.disabled" :maxlength="item.maxlength" @keyup.enter="handleSearch"
                @clear="handleClear" />

              <!-- 选择器 -->
              <CustomSelect v-else-if="item.type === 'select'" v-model="searchData[item.prop]" :options="item.options"
                value-key="value" label-key="label" :placeholder="item.placeholder || `请选择${item.label}`"
                :clearable="item.clearable !== false" :disabled="item.disabled" :multiple="item.multiple"
                :filterable="item.filterable" :remote="item.remote" :remote-method="item.remoteMethod"
                :loading="item.loading" style="width: 100%" />

              <!-- 日期选择器 -->
              <CustomDatePicker v-else-if="item.type === 'date'" v-model="searchData[item.prop]" type="date"
                :placeholder="item.placeholder || `请选择${item.label}`" :clearable="item.clearable !== false"
                :disabled="item.disabled" :format="item.format || 'YYYY-MM-DD'"
                :value-format="item.valueFormat || 'YYYY-MM-DD'" style="width: 100%" />

              <!-- 日期时间选择器 -->
              <CustomDatePicker v-else-if="item.type === 'datetime'" v-model="searchData[item.prop]" type="datetime"
                :placeholder="item.placeholder || `请选择${item.label}`" :clearable="item.clearable !== false"
                :disabled="item.disabled" :format="item.format || 'YYYY-MM-DD HH:mm:ss'"
                :value-format="item.valueFormat || 'YYYY-MM-DDTHH:mm:ss'" style="width: 100%" />

              <!-- 日期范围选择器 -->
              <CustomDatePicker v-else-if="item.type === 'daterange'" v-model="searchData[item.prop]" type="daterange"
                range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期"
                :clearable="item.clearable !== false" :disabled="item.disabled" :format="item.format || 'YYYY-MM-DD'"
                :value-format="item.valueFormat || 'YYYY-MM-DD'" style="width: 100%" />

              <!-- 日期时间范围选择器 -->
              <CustomDatePicker v-else-if="item.type === 'datetimerange'" v-model="searchData[item.prop]"
                type="datetimerange" range-separator="至" :start-placeholder="item.startPlaceholder || '开始时间'"
                :end-placeholder="item.endPlaceholder || '结束时间'" :clearable="item.clearable !== false"
                :disabled="item.disabled" :format="item.format || 'YYYY-MM-DD HH:mm:ss'"
                :value-format="item.valueFormat || 'YYYY-MM-DDTHH:mm:ss'" :shortcuts="item.shortcuts || []"
                :show-duration="item.showDuration" style="width: 100%" />

              <!-- 数字输入框 -->
              <CustomInputNumber v-else-if="item.type === 'number'" v-model="searchData[item.prop]"
                :placeholder="item.placeholder || `请输入${item.label}`" :disabled="item.disabled" :min="item.min"
                :max="item.max" :step="item.step" :precision="item.precision" style="width: 100%" />

              <!-- 开关 -->
              <CustomSwitch v-else-if="item.type === 'switch'" v-model="searchData[item.prop]" :disabled="item.disabled"
                :active-text="item.activeText" :inactive-text="item.inactiveText"
                :active-value="item.activeValue !== undefined ? item.activeValue : true"
                :inactive-value="item.inactiveValue !== undefined ? item.inactiveValue : false" />

              <!-- 单选框组 -->
              <CustomRadioGroup v-else-if="item.type === 'radio'" v-model="searchData[item.prop]"
                :options="item.options" value-key="value" label-key="label" disabled-key="disabled"
                :disabled="item.disabled" />

              <!-- 复选框组 -->
              <el-checkbox-group v-else-if="item.type === 'checkbox'" v-model="searchData[item.prop]"
                :disabled="item.disabled">
                <el-checkbox v-for="option in item.options" :key="option.value" :label="option.value"
                  :disabled="option.disabled">
                  {{ option.label }}
                </el-checkbox>
              </el-checkbox-group>
            </div>
          </div>

          <!-- 搜索按钮 -->
          <div class="search-field-item" :style="getButtonStyle()">
            <div class="search-field-content">
              <CustomButton type="primary" :loading="searchLoading" @click="handleSearch" :size="size">
                <i class="fa fa-search"></i>
                {{ searchText }}
              </CustomButton>
              <CustomButton type="secondary" @click="handleReset" :size="size">
                <i class="fa fa-refresh"></i>
                {{ resetText }}
              </CustomButton>
            </div>
          </div>
        </div>
      </div>

      <!-- 自定义操作按钮容器 -->
      <div class="custom-actions-container">
        <slot name="actions"></slot>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 导入依赖模块
 * -----------------------------
 * 从Vue和自定义组件中导入所需的功能
 */
import { ref, reactive, computed, watch } from 'vue'
import CustomInput from './CustomInput.vue'
import CustomSelect from './CustomSelect.vue'
import CustomDatePicker from './CustomDatePicker.vue'
import CustomInputNumber from './CustomInputNumber.vue'
import CustomSwitch from './CustomSwitch.vue'
import CustomRadioGroup from './CustomRadioGroup.vue'
import CustomButton from './CustomButton.vue'

/**
 * 属性和事件定义
 * -----------------------------
 * 组件接收的属性和向外发送的事件
 */
// Props定义
const props = defineProps({
  // 搜索项配置
  items: {
    type: Array,
    default: () => []
  },
  // 搜索数据
  modelValue: {
    type: Object,
    default: () => ({})
  },
  // 是否单行显示（符合设计规范）
  singleRow: {
    type: Boolean,
    default: false
  },
  // 表单尺寸
  size: {
    type: String,
    default: 'default'
  },
  // 标签宽度
  labelWidth: {
    type: [String, Number],
    default: 'auto'
  },
  // 搜索按钮文本
  searchText: {
    type: String,
    default: '搜索'
  },
  // 重置按钮文本
  resetText: {
    type: String,
    default: '重置'
  },
  // 搜索加载状态
  searchLoading: {
    type: Boolean,
    default: false
  }
})

// Emits定义
const emit = defineEmits([
  'update:modelValue',
  'search',
  'reset',
  'change'
])

/**
 * 响应式状态
 * -----------------------------
 * 组件内部的响应式数据
 */
const searchData = reactive({})

/**
 * 工具方法
 * -----------------------------
 * 处理表单数据和布局计算的辅助函数
 */
// 根据字段类型获取默认值
const getDefaultValue = (type) => {
  switch (type) {
    case 'number':
      return undefined
    case 'switch':
      return false
    case 'checkbox':
      return []
    case 'daterange':
    case 'datetimerange':
      return []
    default:
      return ''
  }
}

// 根据字段类型获取默认宽度
const getDefaultWidth = (item) => {
  // 获取类型默认宽度
  const getTypeDefaultWidth = (type) => {
    switch (type) {
      case 'input':
      case 'select':
        return '200px'
      case 'date':
      case 'datetime':
        return '200px'
      case 'daterange':
      case 'datetimerange':
        return '320px'
      case 'number':
        return '160px'
      case 'switch':
        return '120px'
      default:
        return '200px'
    }
  }

  // 如果有自定义宽度，直接使用
  if (item.width) {
    return item.width
  }
  // 如果有span配置，转换为宽度（向后兼容）
  else if (item.span) {
    const spanToWidth = {
      3: '180px',
      4: '200px',
      5: '240px',
      6: '280px',
      8: '320px',
      12: '400px',
      16: '480px',
      18: '520px',
      24: '100%'
    }
    return spanToWidth[item.span] || '200px'
  }
  // 使用类型默认宽度
  else {
    return getTypeDefaultWidth(item.type)
  }
}

// 获取字段容器样式
const getFieldsContainerStyle = () => {
  if (!props.singleRow) {
    return {
      display: 'grid',
      gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
      gap: 'var(--spacing-base)',
      width: '100%'
    }
  }

  // 单行模式：使用flexbox布局支持不同宽度，允许自动换行
  return {
    display: 'flex',
    alignItems: 'center',
    gap: 'var(--spacing-base)',
    flexWrap: 'wrap',
    // 设置容器宽度为100%，确保有足够空间换行
    width: '100%',
    // 最小高度确保单行显示时的对齐
    minHeight: 'var(--form-item-height)',
    // 确保内容左对齐
    justifyContent: 'flex-start'
  }
}

// 获取字段项样式
const getFieldItemStyle = (item) => {
  if (!props.singleRow) {
    return {
      display: 'flex',
      flexDirection: 'column',
      gap: 'var(--spacing-mini)'
    }
  }

  // 单行模式：设置字段项宽度，允许换行和适当收缩
  return {
    display: 'flex',
    alignItems: 'center',
    gap: 'var(--spacing-small)',
    // 允许在空间不足时适当收缩
    flexShrink: '1',
    // 不自动增长，保持设定的宽度
    flexGrow: '0',
    // 设置基础宽度，但允许在换行时调整
    flexBasis: getDefaultWidth(item),
    // 设置最大宽度，避免单个字段项过大
    maxWidth: getDefaultWidth(item),
    // 确保最小宽度，保证可用性
    minWidth: '200px'
  }
}

// 获取字段标签样式
const getFieldLabelStyle = (item) => {
  const baseStyle = {
    fontSize: 'var(--font-size-base)',
    color: 'var(--text-secondary)',
    fontWeight: 'var(--font-weight-medium)',
    textAlign: 'center'
  }

  if (!props.singleRow) {
    return baseStyle
  }

  // 单行模式：设置标签宽度
  const labelWidth = item.labelWidth || props.labelWidth || 'var(--form-label-width-compact)'

  return {
    ...baseStyle,
    flexShrink: '0',
    width: labelWidth,
    minWidth: labelWidth
  }
}

// 获取按钮样式
const getButtonStyle = () => {
  if (!props.singleRow) {
    return {
      display: 'flex',
      flexDirection: 'row',
      gap: 'var(--spacing-small)',
      justifyContent: 'flex-start',
      alignItems: 'center',
      marginTop: 'var(--spacing-base)'
    }
  }

  // 单行模式：设置按钮样式，使其能够正确换行
  return {
    display: 'flex',
    alignItems: 'center',
    gap: 'var(--spacing-small)',
    flexShrink: '0',
    flexGrow: '0',
    flexBasis: 'auto',
    minWidth: 'auto',
    maxWidth: 'auto'
  }
}

/**
 * 数据初始化和监听
 * -----------------------------
 * 处理表单数据的初始化和响应外部数据变化
 */
// 初始化搜索数据
const initSearchData = (items) => {
  items.forEach(item => {
    if (item.prop && !(item.prop in searchData)) {
      searchData[item.prop] = item.defaultValue || getDefaultValue(item.type)
    }
  })
}

// 监听搜索项配置变化
watch(() => props.items, (newItems) => {
  initSearchData(newItems)
}, { immediate: true, deep: true })

// 监听外部数据变化
watch(() => props.modelValue, (newValue) => {
  Object.assign(searchData, newValue)
}, { immediate: true, deep: true })

// 监听内部数据变化
watch(searchData, (newValue) => {
  emit('update:modelValue', { ...newValue })
  emit('change', { ...newValue })
}, { deep: true })

/**
 * 事件处理方法
 * -----------------------------
 * 处理用户交互事件的函数
 */
// 处理搜索事件
const handleSearch = () => {
  emit('search', { ...searchData })
}

// 处理重置事件
const handleReset = () => {
  // 重置表单数据
  props.items.forEach(item => {
    if (item.prop) {
      searchData[item.prop] = item.defaultValue || getDefaultValue(item.type)
    }
  })

  // 先发出重置事件，然后才是搜索事件
  emit('reset')
  emit('search', { ...searchData })
}

// 处理清除事件
const handleClear = () => {
  // 当某个输入框清空时触发搜索
  handleSearch()
}

/**
 * 暴露接口
 * -----------------------------
 * 向父组件暴露的方法
 */
defineExpose({
  handleSearch,
  handleReset,
  searchData
})
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

.common-search {
  flex-shrink: 0;
  width: 100%;
  margin-bottom: var(--spacing-base);

  .search-form {
    background: var(--bg-primary);
    padding: var(--spacing-base);
    border-radius: var(--border-radius-base);
    border: 1px solid var(--border-light);
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: var(--spacing-base);
    min-height: var(--form-item-height);

    // 单行模式布局
    &.single-row {
      align-items: center;
      padding: var(--spacing-medium) var(--spacing-base);

      .search-area {
        display: flex;
        flex-direction: column;
        gap: var(--spacing-base);
        flex: 1;
        min-width: 0;
        width: 100%;

        .search-fields-container {
          width: 100%;
          align-items: center;
          align-content: center;
          display: flex;
          flex-wrap: wrap;
          gap: var(--spacing-base);
          justify-content: flex-start;
        }
      }

      .custom-actions-container {
        @include flex-center-y;
        gap: var(--spacing-small);
        flex-shrink: 0;
        flex-wrap: wrap;
        justify-content: flex-end;
        align-self: center;
      }

      .search-field-content {
        // 让内容区占据标签外的剩余空间
        flex: 1;
        min-width: 0; // 防止flex子项溢出
        @include flex-center-y; // 添加垂直居中对齐
        display: flex;
        gap: var(--spacing-small);

        // 确保自定义组件能够填满可用空间
        >* {
          width: 100% !important;
        }
      }
    }

    // 多行模式布局
    &:not(.single-row) {
      flex-direction: column;
      gap: var(--spacing-base);

      .search-area {
        width: 100%;
        @include flex-between;
        gap: var(--spacing-base);
      }

      .custom-actions-container {
        @include flex-center-y;
        gap: var(--spacing-small);
        flex-wrap: wrap;
        justify-content: flex-end;
      }

      .search-field-content {
        width: 100%;
      }
    }

    // 统一表单控件样式 - 只对Element Plus原生组件设置100%宽度
    :deep(.el-input),
    :deep(.el-select),
    :deep(.el-date-picker),
    :deep(.el-cascader) {
      width: 100%;
    }

    // 自定义组件继承父容器宽度，不强制设置100%
    :deep(.custom-input),
    :deep(.custom-select),
    :deep(.custom-cascader),
    :deep(.custom-date-picker) {
      width: inherit;
    }

    :deep(.el-input__wrapper) {
      padding: var(--spacing-micro) var(--spacing-small);
    }

    :deep(.el-select .el-input__wrapper) {
      padding: var(--spacing-micro) var(--spacing-medium) var(--spacing-micro) var(--spacing-small);
    }

    // 修复选择器文本溢出问题
    :deep(.el-select .el-input__inner),
    :deep(.el-select .el-select__selected-item) {
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      max-width: 100%;
    }

    // 调整Element Plus选择器图标位置
    :deep(.el-select .el-input__suffix) {
      padding-right: var(--spacing-mini);
    }
  }

  // 响应式设计
  @include respond-to(md) {
    .search-form {
      &.single-row {
        flex-direction: column;
        align-items: stretch;

        .search-area {
          flex-direction: column;
          gap: var(--spacing-medium);
          align-items: stretch;

          .search-fields-container {
            flex-direction: column;
            gap: var(--spacing-medium);

            .search-field-item {
              width: 100% !important;
            }
          }
        }

        .custom-actions-container {
          justify-content: center;
          margin-top: var(--spacing-small);
        }
      }
    }
  }

  @include respond-to(sm) {
    .search-form {
      padding: var(--spacing-medium);

      .search-fields-container {
        .search-field-item {
          width: 100% !important;
          flex-direction: column;
          align-items: flex-start !important;
          gap: var(--spacing-mini);

          .search-field-label {
            width: auto !important;
            min-width: auto !important;
          }
        }
      }

      .custom-actions-container {
        justify-content: center;
        flex-direction: column;
        width: 100%;

        .custom-button {
          width: 100%;
        }
      }
    }
  }
}
</style>
