<!--
  ===========================
  自定义日期选择器组件
  ===========================
  基于EnhancedDateTimePicker的逻辑重写，支持所有Element Plus日期选择器类型
  提供更稳定的日期处理和自定义交互体验
-->
<template>
  <div class="custom-date-picker" :class="{ 'range-picker': isRangeType, 'single-picker': !isRangeType }">
    <!-- 单值选择器 -->
    <div v-if="!isRangeType" class="single-picker-container">
      <div class="input-wrapper" @click="openPicker" :class="{ 'disabled': disabled }">
        <CustomInput v-model="singleInputValue" type="text" :placeholder="placeholder" readonly :disabled="disabled"
          :suffix-icon="'fa-calendar'">
          <template #suffix v-if="clearable && hasValue && !disabled">
            <i @click.stop="clearSelection" class="fa fa-times clear-icon"></i>
          </template>
        </CustomInput>
      </div>
    </div>

    <!-- 范围选择器 -->
    <div v-else class="range-picker-container">
      <div class="picker-inputs">
        <div class="input-group">
          <div class="input-wrapper" @click="openPicker('start')" :class="{ 'disabled': disabled }">
            <CustomInput v-model="startInputValue" type="text" :placeholder="startPlaceholder" readonly
              :disabled="disabled" :suffix-icon="'fa-calendar'" />
          </div>
        </div>

        <div class="range-separator">{{ rangeSeparator }}</div>

        <div class="input-group">
          <div class="input-wrapper" @click="openPicker('end')" :class="{ 'disabled': disabled }">
            <CustomInput v-model="endInputValue" type="text" :placeholder="endPlaceholder" readonly :disabled="disabled"
              :suffix-icon="'fa-calendar'" />
          </div>
        </div>

        <div class="picker-actions" v-if="clearable && hasValue && !disabled">
          <CustomButton type="text" @click="clearSelection">
            <i class="fa fa-times"></i>
          </CustomButton>
        </div>
      </div>

      <!-- 时间间隔显示 -->
      <div class="duration-display" v-if="showDuration && hasValue && isDurationSupported">
        <span class="duration-label">时间间隔：</span>
        <span class="duration-value">{{ durationText }}</span>
      </div>
    </div>

    <!-- 弹窗选择器 -->
    <el-dialog v-model="pickerVisible" :title="pickerTitle" width="var(--panel-height-default)"
      :before-close="closePicker" append-to-body class="custom-date-picker-dialog">
      <div class="picker-content">
        <!-- 年份选择 -->
        <div v-if="type === 'year'" class="year-section">
          <h4 class="section-title">选择年份</h4>
          <el-date-picker v-model="tempSingleValue" type="year" placeholder="选择年份" style="width: 100%"
            @change="handleTempChange" />
        </div>

        <!-- 月份选择 -->
        <div v-else-if="type === 'month'" class="month-section">
          <h4 class="section-title">选择月份</h4>
          <el-date-picker v-model="tempSingleValue" type="month" placeholder="选择月份" style="width: 100%"
            @change="handleTempChange" />
        </div>

        <!-- 周选择 -->
        <div v-else-if="type === 'week'" class="week-section">
          <h4 class="section-title">选择周</h4>
          <el-date-picker v-model="tempSingleValue" type="week" placeholder="选择周" style="width: 100%"
            format="YYYY 第 ww 周" @change="handleTempChange" />
        </div>

        <!-- 时间选择 -->
        <div v-else-if="type === 'time'" class="time-section">
          <h4 class="section-title">选择时间</h4>
          <el-time-picker v-model="tempSingleValue" placeholder="选择时间" style="width: 100%"
            :format="format || 'HH:mm:ss'" @change="handleTempChange" />
        </div>

        <!-- 多日期选择 -->
        <div v-else-if="type === 'dates'" class="dates-section">
          <h4 class="section-title">选择多个日期</h4>
          <el-date-picker v-model="tempMultipleValue" type="dates" placeholder="选择多个日期" style="width: 100%"
            @change="handleTempChange" />
        </div>

        <!-- 日期选择 -->
        <div v-else class="date-section">
          <h4 class="section-title">选择日期</h4>
          <el-date-picker v-model="tempDate" type="date" placeholder="选择日期" style="width: 100%"
            @change="handleDateChange" />
        </div>

        <!-- 时间选择 (仅datetime和datetimerange类型) -->
        <div v-if="needTimeSelection" class="time-section">
          <h4 class="section-title">选择时间</h4>
          <div class="time-inputs">
            <div class="time-group">
              <label>小时</label>
              <el-select v-model="tempHour" placeholder="时" style="width: 80px">
                <el-option v-for="hour in hours" :key="hour" :label="hour" :value="hour" />
              </el-select>
            </div>
            <span class="time-separator">:</span>
            <div class="time-group">
              <label>分钟</label>
              <el-select v-model="tempMinute" placeholder="分" style="width: 80px">
                <el-option v-for="minute in minutes" :key="minute" :label="minute" :value="minute" />
              </el-select>
            </div>
            <span class="time-separator">:</span>
            <div class="time-group">
              <label>秒钟</label>
              <el-select v-model="tempSecond" placeholder="秒" style="width: 80px">
                <el-option v-for="second in seconds" :key="second" :label="second" :value="second" />
              </el-select>
            </div>
          </div>
        </div>

        <!-- 快捷选项 (仅范围类型) -->
        <div v-if="isRangeType && shortcuts && shortcuts.length > 0" class="shortcuts-section">
          <h4 class="section-title">快捷选择</h4>
          <div class="shortcuts-buttons">
            <CustomButton v-for="shortcut in shortcuts" :key="shortcut.text" size="small"
              @click="selectShortcut(shortcut)">
              {{ shortcut.text }}
            </CustomButton>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <CustomButton type="secondary" @click="closePicker">取消</CustomButton>
          <CustomButton type="primary" @click="confirmSelection">确认</CustomButton>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * ===========================
 * 导入依赖
 * ===========================
 */
import { ref, computed, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { formatDateTime } from '@/utils/shared/common'
import CustomInput from './CustomInput.vue'
import CustomButton from './CustomButton.vue'

/**
 * ===========================
 * Props 定义
 * ===========================
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
    validator: (value) => ['date', 'datetime', 'daterange', 'datetimerange', 'month', 'year', 'dates', 'week', 'time'].includes(value)
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
  },
  // 是否显示时间间隔（自定义属性）
  showDuration: {
    type: Boolean,
    default: true
  }
})

/**
 * ===========================
 * Emits 定义
 * ===========================
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
 * 响应式数据状态
 * ===========================
 */
// 弹窗控制
const pickerVisible = ref(false)
const currentPickerType = ref('single') // 'single' | 'start' | 'end'

// 临时选择状态
const tempDate = ref(null)
const tempHour = ref('00')
const tempMinute = ref('00')
const tempSecond = ref('00')
const tempSingleValue = ref(null)
const tempMultipleValue = ref([])

// 内部值状态
const internalValue = ref(null)

/**
 * ===========================
 * 计算属性
 * ===========================
 */
// 是否为范围类型
const isRangeType = computed(() => {
  return ['daterange', 'datetimerange'].includes(props.type)
})

// 是否需要时间选择
const needTimeSelection = computed(() => {
  return ['datetime', 'datetimerange'].includes(props.type)
})

// 是否支持时间间隔显示
const isDurationSupported = computed(() => {
  return props.type === 'datetimerange'
})

// 弹窗标题
const pickerTitle = computed(() => {
  const typeNames = {
    'date': '选择日期',
    'datetime': '选择日期时间',
    'daterange': '选择日期范围',
    'datetimerange': '选择日期时间范围',
    'month': '选择月份',
    'year': '选择年份',
    'dates': '选择多个日期',
    'week': '选择周',
    'time': '选择时间'
  }

  if (isRangeType.value) {
    return currentPickerType.value === 'start' ? '选择开始时间' : '选择结束时间'
  }

  return typeNames[props.type] || '选择日期'
})

// 是否有值
const hasValue = computed(() => {
  if (isRangeType.value) {
    return Array.isArray(internalValue.value) && internalValue.value.length > 0 &&
      (internalValue.value[0] || internalValue.value[1])
  } else {
    return internalValue.value !== null && internalValue.value !== undefined && internalValue.value !== ''
  }
})

// 单值显示
const singleValueDisplay = computed(() => {
  if (!hasValue.value || isRangeType.value) return ''

  if (props.type === 'dates' && Array.isArray(internalValue.value)) {
    return internalValue.value.map(date => formatValueByType(date)).join(', ')
  }

  return formatValueByType(internalValue.value)
})

// 开始值显示
const startValueDisplay = computed(() => {
  if (!isRangeType.value || !Array.isArray(internalValue.value) || !internalValue.value[0]) return ''
  return formatValueByType(internalValue.value[0])
})

// 结束值显示
const endValueDisplay = computed(() => {
  if (!isRangeType.value || !Array.isArray(internalValue.value) || !internalValue.value[1]) return ''
  return formatValueByType(internalValue.value[1])
})

// 时间选项
const hours = computed(() => Array.from({ length: 24 }, (_, i) => i.toString().padStart(2, '0')))
const minutes = computed(() => Array.from({ length: 60 }, (_, i) => i.toString().padStart(2, '0')))
const seconds = computed(() => Array.from({ length: 60 }, (_, i) => i.toString().padStart(2, '0')))

// 时间间隔计算
const durationText = computed(() => {
  if (!isRangeType.value || !Array.isArray(internalValue.value) ||
    !internalValue.value[0] || !internalValue.value[1]) {
    return '请选择时间范围'
  }

  const start = new Date(internalValue.value[0])
  const end = new Date(internalValue.value[1])
  const duration = end.getTime() - start.getTime()

  if (duration < 0) return '时间范围无效'

  const seconds = Math.floor(duration / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)

  let result = ''
  if (days > 0) result += `${days}天`
  if (hours % 24 > 0) result += `${hours % 24}小时`
  if (minutes % 60 > 0) result += `${minutes % 60}分钟`
  if (seconds % 60 > 0) result += `${seconds % 60}秒`

  return result || '0秒'
})

// CustomInput专用的computed属性（只读）
const singleInputValue = computed({
  get: () => singleValueDisplay.value,
  set: () => { } // 只读，不处理set
})

const startInputValue = computed({
  get: () => startValueDisplay.value,
  set: () => { } // 只读，不处理set
})

const endInputValue = computed({
  get: () => endValueDisplay.value,
  set: () => { } // 只读，不处理set
})

/**
 * ===========================
 * 工具方法
 * ===========================
 */
// 根据类型格式化值
const formatValueByType = (value) => {
  if (!value) return ''

  // time 类型直接返回时间字符串，不需要转换为Date对象
  if (props.type === 'time') {
    return typeof value === 'string' ? value : value.toString()
  }

  const date = new Date(value)
  if (isNaN(date.getTime())) return ''

  const formatMap = {
    'date': props.format || 'YYYY-MM-DD',
    'datetime': props.format || 'YYYY-MM-DD HH:mm:ss',
    'daterange': props.format || 'YYYY-MM-DD',
    'datetimerange': props.format || 'YYYY-MM-DD HH:mm:ss',
    'month': 'YYYY-MM',
    'year': 'YYYY',
    'week': 'YYYY 第 ww 周',
    'dates': props.format || 'YYYY-MM-DD',
    'time': props.format || 'HH:mm:ss'
  }

  return formatDateTime(date, formatMap[props.type] || props.format)
}

// 根据类型格式化输出值
const formatOutputValue = (value) => {
  if (!value) return value

  // time 类型直接处理时间字符串
  if (props.type === 'time') {
    return typeof value === 'string' ? value : value.toString()
  }

  const date = new Date(value)
  if (isNaN(date.getTime())) return value

  const formatMap = {
    'date': props.valueFormat || 'YYYY-MM-DD',
    'datetime': props.valueFormat || 'YYYY-MM-DDTHH:mm:ss',
    'daterange': props.valueFormat || 'YYYY-MM-DD',
    'datetimerange': props.valueFormat || 'YYYY-MM-DDTHH:mm:ss',
    'month': 'YYYY-MM',
    'year': 'YYYY',
    'week': 'YYYY-MM-DD',
    'dates': props.valueFormat || 'YYYY-MM-DD',
    'time': props.valueFormat || 'HH:mm:ss'
  }

  return formatDateTime(date, formatMap[props.type] || props.valueFormat)
}

/**
 * ===========================
 * 监听器
 * ===========================
 */
// 监听modelValue变化
watch(() => props.modelValue, (newValue) => {
  internalValue.value = newValue
}, { immediate: true })

/**
 * ===========================
 * 选择器控制方法
 * ===========================
 */
// 打开选择器
const openPicker = (type = 'single') => {
  if (props.disabled || props.readonly) return

  currentPickerType.value = type

  if (isRangeType.value) {
    const targetValue = Array.isArray(internalValue.value) ?
      (type === 'start' ? internalValue.value[0] : internalValue.value[1]) : null

    if (targetValue) {
      tempDate.value = new Date(targetValue)
      if (needTimeSelection.value) {
        const date = new Date(targetValue)
        tempHour.value = date.getHours().toString().padStart(2, '0')
        tempMinute.value = date.getMinutes().toString().padStart(2, '0')
        tempSecond.value = date.getSeconds().toString().padStart(2, '0')
      }
    } else {
      tempDate.value = new Date()
      if (needTimeSelection.value) {
        tempHour.value = type === 'start' ? '00' : '23'
        tempMinute.value = type === 'start' ? '00' : '59'
        tempSecond.value = type === 'start' ? '00' : '59'
      }
    }
  } else {
    // 单值类型
    if (hasValue.value) {
      if (props.type === 'dates') {
        tempMultipleValue.value = Array.isArray(internalValue.value) ? [...internalValue.value] : []
      } else {
        tempSingleValue.value = new Date(internalValue.value)
        tempDate.value = new Date(internalValue.value)

        if (needTimeSelection.value) {
          const date = new Date(internalValue.value)
          tempHour.value = date.getHours().toString().padStart(2, '0')
          tempMinute.value = date.getMinutes().toString().padStart(2, '0')
          tempSecond.value = date.getSeconds().toString().padStart(2, '0')
        }
      }
    } else {
      tempSingleValue.value = new Date()
      tempMultipleValue.value = []
      tempDate.value = new Date()

      if (needTimeSelection.value) {
        tempHour.value = '00'
        tempMinute.value = '00'
        tempSecond.value = '00'
      }
    }
  }

  pickerVisible.value = true
  emit('visible-change', true)
}

// 关闭选择器
const closePicker = () => {
  pickerVisible.value = false
  emit('visible-change', false)
}

// 处理临时值变化
const handleTempChange = (value) => {
  if (props.type === 'dates') {
    tempMultipleValue.value = value || []
  } else {
    tempSingleValue.value = value
  }
}

// 日期变化处理
const handleDateChange = (value) => {
  tempDate.value = value
  emit('calendar-change', value)
}

// 确认选择
const confirmSelection = () => {
  let newValue = null

  if (props.type === 'dates') {
    // 多日期选择
    newValue = tempMultipleValue.value.map(date => formatOutputValue(date))
  } else if (['year', 'month', 'week', 'time'].includes(props.type)) {
    // 特殊类型直接使用临时值
    newValue = tempSingleValue.value ? formatOutputValue(tempSingleValue.value) : null
  } else if (isRangeType.value) {
    // 范围类型
    if (!tempDate.value) {
      ElMessage.warning('请选择日期')
      return
    }

    const selectedDateTime = new Date(tempDate.value)
    if (needTimeSelection.value) {
      selectedDateTime.setHours(parseInt(tempHour.value))
      selectedDateTime.setMinutes(parseInt(tempMinute.value))
      selectedDateTime.setSeconds(parseInt(tempSecond.value))
    }

    const currentValue = Array.isArray(internalValue.value) ? [...internalValue.value] : [null, null]

    if (currentPickerType.value === 'start') {
      currentValue[0] = formatOutputValue(selectedDateTime)
      if (currentValue[1] && selectedDateTime > new Date(currentValue[1])) {
        ElMessage.warning('开始时间不能晚于结束时间')
        return
      }
    } else {
      currentValue[1] = formatOutputValue(selectedDateTime)
      if (currentValue[0] && selectedDateTime < new Date(currentValue[0])) {
        ElMessage.warning('结束时间不能早于开始时间')
        return
      }
    }

    newValue = currentValue
  } else {
    // 单值类型
    if (!tempDate.value) {
      ElMessage.warning('请选择日期')
      return
    }

    const selectedDateTime = new Date(tempDate.value)
    if (needTimeSelection.value) {
      selectedDateTime.setHours(parseInt(tempHour.value))
      selectedDateTime.setMinutes(parseInt(tempMinute.value))
      selectedDateTime.setSeconds(parseInt(tempSecond.value))
    }

    newValue = formatOutputValue(selectedDateTime)
  }

  updateModelValue(newValue)
  closePicker()
}

// 清除选择
const clearSelection = () => {
  if (props.disabled) return

  let newValue = null
  if (isRangeType.value || props.type === 'dates') {
    newValue = []
  }

  updateModelValue(newValue)
}

// 快捷选择
const selectShortcut = (shortcut) => {
  if (typeof shortcut.value === 'function') {
    const result = shortcut.value()
    if (Array.isArray(result) && result.length === 2) {
      const newValue = [
        formatOutputValue(result[0]),
        formatOutputValue(result[1])
      ]
      updateModelValue(newValue)
      closePicker()
    }
  }
}

// 更新模型值
const updateModelValue = (value) => {
  internalValue.value = value
  emit('update:modelValue', value)
  emit('change', value)
}

/**
 * ===========================
 * 组件导出方法
 * ===========================
 */
defineExpose({
  focus: () => {
    // 聚焦第一个输入框
    emit('focus')
  },
  blur: () => {
    emit('blur')
  }
})
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

/**
 * ===========================
 * 主容器样式
 * ===========================
 */
.custom-date-picker {
  width: 100%;

  &.single-picker {
    .single-picker-container {
      width: 100%;
    }
  }

  &.range-picker {
    .range-picker-container {
      width: 100%;
    }
  }
}

/**
 * ===========================
 * 输入框样式
 * ===========================
 */
.input-wrapper {
  position: relative;
  cursor: pointer;
  width: 100%;

  &.disabled {
    cursor: not-allowed;
  }

  /* CustomInput组件样式由组件本身处理，这里只需要基本布局 */
}





.clear-icon {
  position: absolute;
  right: 32px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-tertiary);
  cursor: pointer;
  padding: var(--spacing-micro);
  border-radius: 50%;
  transition: var(--transition-all-fast);

  &:hover {
    color: var(--danger-color);
    background: var(--bg-secondary);
  }
}

/**
 * ===========================
 * 范围选择器样式
 * ===========================
 */
.picker-inputs {
  @include flex-start;
  gap: var(--spacing-medium);
  margin-bottom: var(--spacing-medium);

  .input-group {
    flex: 1;
  }

  .range-separator {
    color: var(--text-secondary);
    font-size: var(--font-size-base);
    white-space: nowrap;
  }

  .picker-actions {
    // CustomButton样式由组件本身处理
  }
}

/**
 * ===========================
 * 时间间隔显示样式
 * ===========================
 */
.duration-display {
  padding: var(--spacing-small) var(--spacing-medium);
  background: var(--bg-secondary);
  border-radius: var(--border-radius-base);
  font-size: var(--font-size-extra-small);

  .duration-label {
    color: var(--text-secondary);
  }

  .duration-value {
    color: var(--text-primary);
    font-weight: var(--font-weight-medium);
  }
}

/**
 * ===========================
 * 弹窗选择器样式
 * ===========================
 */
:deep(.custom-date-picker-dialog) {
  .el-dialog__header {
    padding: var(--spacing-base) var(--spacing-large);
    border-bottom: 1px solid var(--border-color);
  }

  .el-dialog__body {
    padding: var(--spacing-large);
  }
}

.picker-content {
  .section-title {
    font-size: var(--font-size-base);
    color: var(--text-primary);
    margin-bottom: var(--spacing-medium);
    font-weight: var(--font-weight-medium);
  }

  .date-section,
  .year-section,
  .month-section,
  .week-section,
  .dates-section {
    margin-bottom: var(--spacing-extra-large);
  }

  .time-section {
    margin-bottom: var(--spacing-extra-large);

    .time-inputs {
      display: flex;
      align-items: flex-end;
      gap: var(--spacing-small);

      .time-group {
        display: flex;
        flex-direction: column;
        gap: var(--spacing-mini);

        label {
          font-size: var(--font-size-extra-small);
          color: var(--text-secondary);
        }
      }

      .time-separator {
        font-size: var(--font-size-large);
        color: var(--text-tertiary);
        margin-bottom: var(--spacing-small);
      }
    }
  }

  .shortcuts-section {
    .shortcuts-buttons {
      display: flex;
      flex-wrap: wrap;
      gap: var(--spacing-small);
    }
  }
}

.dialog-footer {
  @include flex-end;
  gap: var(--spacing-medium);
}

/**
 * ===========================
 * 响应式设计
 * ===========================
 */
.picker-inputs {
  @include respond-to(md) {
    flex-direction: column;
    gap: var(--spacing-small);

    .range-separator {
      transform: rotate(90deg);
    }
  }
}
</style>
