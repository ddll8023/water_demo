<!-- 通用搜索组件 - 支持多种搜索条件和快速搜索 -->
<template>
  <div class="common-search">
    <div class="search-form" :class="{ 'single-row': singleRow }">
      <el-row :gutter="12" class="search-row">
        <!-- 搜索字段区域 -->
        <el-col v-for="(item, index) in items" :key="item.prop || index" :span="singleRow ? getColumnSpan(item) : 4"
          :xs="singleRow ? 24 : 12" :sm="singleRow ? getColumnSpan(item) : 8" :md="singleRow ? getColumnSpan(item) : 6"
          :lg="singleRow ? getColumnSpan(item) : 4" :xl="singleRow ? getColumnSpan(item) : 4">
          <el-form-item :label="item.label" :prop="item.prop"
            :label-width="singleRow ? (item.labelWidth || 'var(--form-label-width-standard)') : (item.labelWidth || 'var(--form-label-width-compact)')"
            class="search-item">
            <!-- 输入框 -->
            <CustomInput v-if="item.type === 'input' || !item.type" v-model="searchData[item.prop]"
              :placeholder="item.placeholder || `请输入${item.label}`" :clearable="item.clearable !== false"
              :disabled="item.disabled" :maxlength="item.maxlength" @keyup.enter="handleSearch" @clear="handleClear" />

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
              range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" :clearable="item.clearable !== false"
              :disabled="item.disabled" :format="item.format || 'YYYY-MM-DD'"
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
            <CustomRadioGroup v-else-if="item.type === 'radio'" v-model="searchData[item.prop]" :options="item.options"
              value-key="value" label-key="label" disabled-key="disabled" :disabled="item.disabled" />

            <!-- 复选框组 -->
            <el-checkbox-group v-else-if="item.type === 'checkbox'" v-model="searchData[item.prop]"
              :disabled="item.disabled">
              <el-checkbox v-for="option in item.options" :key="option.value" :label="option.value"
                :disabled="option.disabled">
                {{ option.label }}
              </el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-col>

        <!-- 搜索按钮区域 -->
        <el-col :xs="24" :sm="singleRow ? getRemainingSpan() : 24" :md="singleRow ? getRemainingSpan() : 24"
          :lg="singleRow ? getRemainingSpan() : 24" :xl="singleRow ? getRemainingSpan() : 24" class="search-actions-col"
          :class="{ 'single-row-actions': singleRow }">
          <div class="search-actions" :class="{ 'single-row-layout': singleRow }">
            <div class="search-buttons">
              <CustomButton type="primary" :loading="searchLoading" @click="handleSearch"
                :size="singleRow ? 'default' : 'default'">
                <i class="fa fa-search"></i>
                {{ searchText }}
              </CustomButton>
              <CustomButton type="secondary" @click="handleReset" :size="singleRow ? 'default' : 'default'">
                <i class="fa fa-refresh"></i>
                {{ resetText }}
              </CustomButton>
            </div>

            <!-- 自定义操作按钮插槽 -->
            <div class="custom-actions">
              <slot name="actions"></slot>
            </div>
          </div>
        </el-col>
      </el-row>
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
  // 是否行内显示
  inline: {
    type: Boolean,
    default: true
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
const searchFormRef = ref()
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

// 获取单行模式下的列宽
const getColumnSpan = (item) => {
  if (item.span) return item.span

  // 根据字段类型自动分配列宽，优化布局更紧凑
  switch (item.type) {
    case 'input':
      return 3  // 减少输入框列宽
    case 'select':
      return 3
    case 'date':
    case 'datetime':
      return 3  // 减少日期选择器列宽
    case 'daterange':
      return 4  // 减少日期范围选择器列宽
    default:
      return 3
  }
}

// 计算按钮区域剩余的列宽
const getRemainingSpan = () => {
  if (!props.singleRow) return 24

  // 计算搜索字段占用的总列宽
  const usedSpan = props.items.reduce((total, item) => {
    return total + getColumnSpan(item)
  }, 0)

  // 返回剩余的列宽，确保至少有6列给按钮区域
  const remaining = 24 - usedSpan
  return Math.max(remaining, 6)
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

  // 重置表单验证
  searchFormRef.value?.resetFields()

  // 先发出重置事件，然后才是搜索事件
  emit('reset')
  // 修改：不要在重置时自动触发搜索事件
  // emit('search', { ...searchData })
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
  flex-shrink: 0; // 防止搜索区域被压缩
  width: 100%; // 确保搜索组件占满全宽
  margin-bottom: var(--spacing-base); // 与表格区域保持间距

  .search-form {
    background: var(--bg-primary);
    padding: var(--spacing-15) var(--spacing-base);
    border-radius: var(--border-radius-base);
    border: 1px solid var(--border-light);
    min-height: 56px; // 表单项基础高度 + 内边距
    box-sizing: border-box;
    width: 100%; // 确保搜索表单占满全宽

    // 通用搜索项样式
    .search-item {
      margin-bottom: 0;

      :deep(.el-form-item__label) {
        font-size: var(--font-size-base);
        color: var(--text-secondary);
        font-weight: var(--font-weight-medium);
        padding-right: var(--spacing-mini);
      }

      :deep(.el-form-item__content) {
        width: 100%;
        flex: 1;
      }

      // 统一表单控件样式
      :deep(.el-input),
      :deep(.el-select),
      :deep(.el-date-picker),
      :deep(.el-cascader),
      :deep(.custom-input),
      :deep(.custom-select),
      :deep(.custom-cascader) {
        width: 100%;
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

    .search-actions-col {
      margin-top: var(--spacing-base);

      &.single-row-actions {
        margin-top: 0;
        display: flex;
        align-items: center;
        height: var(--form-item-height); // 确保与搜索字段高度一致
      }
    }

    .search-actions {
      display: flex;
      justify-content: space-between; // 两端对齐：左边搜索按钮，右边自定义按钮
      align-items: center;
      flex-wrap: wrap;
      gap: var(--spacing-small);
      width: 100%; // 确保占满整个列宽

      &.single-row-layout {
        justify-content: space-between; // 两端对齐：左边搜索按钮，右边自定义按钮
        height: var(--form-item-height);
        flex-wrap: nowrap; // 防止按钮换行
        width: 100%; // 确保占满整个列宽
      }

      .search-buttons {
        display: flex;
        gap: var(--spacing-small);
        flex-wrap: wrap;
        flex-shrink: 0; // 防止搜索按钮被压缩
      }

      .custom-actions {
        display: flex;
        gap: var(--spacing-small);
        flex-wrap: wrap;
        justify-content: flex-end; // 确保自定义按钮右对齐
        margin-left: auto; // 推到右边
        flex-shrink: 0; // 防止自定义按钮被压缩
      }
    }
  }

  // 单行模式样式
  &.single-row {
    .search-form {
      padding: var(--spacing-15) var(--spacing-base);
      min-height: 56px; // 表单项基础高度 + 内边距
      display: flex;
      align-items: center;
      width: 100%; // 确保单行模式下表单占满全宽

      .search-row {
        align-items: center;
        width: 100%;
        margin: 0; // 移除默认边距
        flex: 1; // 让搜索行占满剩余空间
      }

      // 单行模式下的搜索项样式调整
      .search-item {

        :deep(.el-form-item__label),
        :deep(.el-form-item__content) {
          line-height: var(--form-item-height) !important;
          margin-bottom: 0;
        }
      }

      .search-actions-col {
        margin-top: 0;
        flex: 1; // 让按钮列占满剩余空间

        &.single-row-actions {
          display: flex;
          align-items: center;
          justify-content: flex-end; // 确保按钮右对齐
          height: var(--form-item-height);
        }
      }
    }
  }

  // 响应式设计
  @include respond-to(md) {
    .search-form {
      .search-actions {
        flex-direction: column;
        align-items: stretch;
        gap: var(--spacing-medium);

        .search-buttons {
          justify-content: center;
          order: 1; // 搜索按钮在上方
        }

        .custom-actions {
          justify-content: center;
          order: 2; // 自定义按钮在下方
          margin-left: 0; // 移动端取消左边距
        }
      }
    }
  }

  @include respond-to(sm) {
    .search-form {
      padding: var(--spacing-medium);

      .search-actions {
        gap: var(--spacing-small);

        .search-buttons,
        .custom-actions {
          flex-direction: column;
          gap: var(--spacing-small);

          .custom-button {
            width: 100%;
          }
        }

        .custom-actions {
          margin-left: 0; // 小屏幕取消左边距
        }
      }
    }
  }
}
</style>
