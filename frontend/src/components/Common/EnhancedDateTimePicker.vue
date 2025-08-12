<!-- 增强版日期时间范围选择器组件 -->
<template>
  <div class="enhanced-datetime-picker">
    <!-- 输入框区域 -->
    <div class="picker-inputs">
      <div class="input-group">
        <div class="input-wrapper" @click="openPicker('start')">
          <input type="text" :value="startTimeDisplay" :placeholder="startPlaceholder" readonly class="time-input"
            :disabled="disabled">
          <i class="fa fa-calendar input-icon"></i>
        </div>
      </div>

      <div class="input-group">
        <div class="input-wrapper" @click="openPicker('end')">
          <input type="text" :value="endTimeDisplay" :placeholder="endPlaceholder" readonly class="time-input"
            :disabled="disabled">
          <i class="fa fa-calendar input-icon"></i>
        </div>
      </div>

      <div class="picker-actions" v-if="clearable && hasValue">
        <button @click="clearSelection" class="clear-btn" :disabled="disabled">
          <i class="fa fa-times"></i>
          清除
        </button>
      </div>
    </div>

    <!-- 时间间隔显示 -->
    <div class="duration-display" v-if="showDuration && hasValue">
      <span class="duration-label">时间间隔：</span>
      <span class="duration-value">{{ durationText }}</span>
    </div>

    <!-- 弹窗选择器 -->
    <el-dialog v-model="pickerVisible" :title="pickerTitle" width="600px" :before-close="closePicker" append-to-body
      class="datetime-picker-dialog">
      <div class="picker-content">
        <!-- 日期选择 -->
        <div class="date-section">
          <h4 class="section-title">选择日期</h4>
          <el-date-picker v-model="tempDate" type="date" placeholder="选择日期" style="width: 100%"
            @change="handleDateChange" />
        </div>

        <!-- 时间选择 -->
        <div class="time-section">
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
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closePicker">取消</el-button>
          <el-button type="primary" @click="confirmSelection">确认</el-button>
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
import { ref, computed, watch, nextTick, triggerRef } from 'vue'
import { ElMessage } from 'element-plus'
import { formatDateTime } from '@/utils/shared/common'

/**
 * ===========================
 * Props 定义
 * ===========================
 */
const props = defineProps({
  // v-model绑定值 [startTime, endTime]
  modelValue: {
    type: Array,
    default: () => []
  },
  // 开始时间占位符
  startPlaceholder: {
    type: String,
    default: '请选择开始时间'
  },
  // 结束时间占位符
  endPlaceholder: {
    type: String,
    default: '请选择结束时间'
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
  // 是否显示快捷选择
  showShortcuts: {
    type: Boolean,
    default: false
  },
  // 是否显示时间间隔
  showDuration: {
    type: Boolean,
    default: true
  },
  // 时间格式
  format: {
    type: String,
    default: 'YYYY-MM-DD HH:mm:ss'
  }
})

/**
 * ===========================
 * Emits 定义
 * ===========================
 */
const emit = defineEmits(['update:modelValue', 'change'])

/**
 * ===========================
 * 响应式数据状态
 * ===========================
 */
// 弹窗控制
const pickerVisible = ref(false)
const currentPickerType = ref('start') // 'start' | 'end'

// 临时选择状态
const tempDate = ref(null)
const tempHour = ref('00')
const tempMinute = ref('00')
const tempSecond = ref('00')

// 内部时间状态
const startTime = ref(null)
const endTime = ref(null)

// 快捷选择选项 (已废弃，快捷选择UI已移除)
const shortcuts = ref([
  { label: '今天', value: 'today' },
  { label: '昨天', value: 'yesterday' },
  { label: '过去7天', value: 'last7days' },
  { label: '本月', value: 'thismonth' },
  { label: '上月', value: 'lastmonth' }
])

/**
 * ===========================
 * 计算属性
 * ===========================
 */
// 弹窗标题
const pickerTitle = computed(() => {
  return currentPickerType.value === 'start' ? '选择开始时间' : '选择结束时间'
})

// 是否有已选值
const hasValue = computed(() => {
  return startTime.value || endTime.value
})

// 开始时间显示
const startTimeDisplay = computed(() => {
  return startTime.value ? formatDateTime(startTime.value) : ''
})

// 结束时间显示
const endTimeDisplay = computed(() => {
  return endTime.value ? formatDateTime(endTime.value) : ''
})

// 时间选项
const hours = computed(() => {
  return Array.from({ length: 24 }, (_, i) => i.toString().padStart(2, '0'))
})

const minutes = computed(() => {
  return Array.from({ length: 60 }, (_, i) => i.toString().padStart(2, '0'))
})

const seconds = computed(() => {
  return Array.from({ length: 60 }, (_, i) => i.toString().padStart(2, '0'))
})

// 时间间隔计算
const durationText = computed(() => {
  if (!startTime.value && !endTime.value) return '未选择'
  if (!startTime.value) return '请选择开始时间'
  if (!endTime.value) return '请选择结束时间'

  const duration = endTime.value.getTime() - startTime.value.getTime()

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

/**
 * ===========================
 * 监听器
 * ===========================
 */
// 监听modelValue变化
watch(() => props.modelValue, (newValue) => {
  if (Array.isArray(newValue) && newValue.length >= 1) {
    // 处理开始时间
    if (newValue[0] && newValue[0] !== '' && newValue[0] !== null) {
      const newStartTime = new Date(newValue[0])
      if (!startTime.value || newStartTime.getTime() !== startTime.value.getTime()) {
        startTime.value = newStartTime
      }
    } else {
      startTime.value = null
    }

    // 处理结束时间（如果数组长度大于1）
    if (newValue.length >= 2) {
      if (newValue[1] && newValue[1] !== '' && newValue[1] !== null) {
        const newEndTime = new Date(newValue[1])
        if (!endTime.value || newEndTime.getTime() !== endTime.value.getTime()) {
          endTime.value = newEndTime
        }
      } else {
        endTime.value = null
      }
    }
  } else if (!newValue || newValue.length === 0) {
    startTime.value = null
    endTime.value = null
  }
}, { immediate: true })

/**
 * ===========================
 * 选择器控制方法
 * ===========================
 */
// 打开日期时间选择器
const openPicker = (type) => {
  if (props.disabled) return

  currentPickerType.value = type
  const targetTime = type === 'start' ? startTime.value : endTime.value

  if (targetTime) {
    tempDate.value = new Date(targetTime)
    tempHour.value = targetTime.getHours().toString().padStart(2, '0')
    tempMinute.value = targetTime.getMinutes().toString().padStart(2, '0')
    tempSecond.value = targetTime.getSeconds().toString().padStart(2, '0')
  } else {
    tempDate.value = new Date()
    tempHour.value = type === 'start' ? '00' : '23'
    tempMinute.value = type === 'start' ? '00' : '59'
    tempSecond.value = type === 'start' ? '00' : '59'
  }

  pickerVisible.value = true
}

// 关闭选择器
const closePicker = () => {
  pickerVisible.value = false
}

// 日期变化处理
const handleDateChange = () => {
  // 日期变化时的处理逻辑
}

// 确认选择
const confirmSelection = () => {
  if (!tempDate.value) {
    ElMessage.warning('请选择日期')
    return
  }

  const selectedDateTime = new Date(tempDate.value)
  selectedDateTime.setHours(parseInt(tempHour.value))
  selectedDateTime.setMinutes(parseInt(tempMinute.value))
  selectedDateTime.setSeconds(parseInt(tempSecond.value))

  if (currentPickerType.value === 'start') {
    startTime.value = new Date(selectedDateTime) // 创建新的Date对象
    triggerRef(startTime) // 强制触发响应式更新

    // 只有当结束时间存在且开始时间晚于结束时间时，才提示用户
    if (endTime.value && selectedDateTime > endTime.value) {
      ElMessage.warning('开始时间不能晚于结束时间，请重新选择结束时间')
    }
  } else {
    endTime.value = new Date(selectedDateTime) // 创建新的Date对象
    triggerRef(endTime) // 强制触发响应式更新

    // 只有当开始时间存在且结束时间早于开始时间时，才提示用户
    if (startTime.value && selectedDateTime < startTime.value) {
      ElMessage.warning('结束时间不能早于开始时间，请重新选择开始时间')
    }
  }

  updateModelValue()
  closePicker()
}

/**
 * ===========================
 * 时间选择快捷功能
 * ===========================
 */
// 清除选择
const clearSelection = () => {
  startTime.value = null
  endTime.value = null
  updateModelValue()
}

// 快捷选择 (已废弃，快捷选择UI已移除，但保留方法供可能的API调用)
const selectShortcut = (shortcutValue) => {
  if (props.disabled) return

  const now = new Date()
  let start, end

  switch (shortcutValue) {
    case 'today':
      start = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 0, 0, 0, 0)
      end = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59, 59, 0)
      break
    case 'yesterday':
      const yesterday = new Date(now)
      yesterday.setDate(yesterday.getDate() - 1)
      start = new Date(yesterday.getFullYear(), yesterday.getMonth(), yesterday.getDate(), 0, 0, 0, 0)
      end = new Date(yesterday.getFullYear(), yesterday.getMonth(), yesterday.getDate(), 23, 59, 59, 0)
      break
    case 'last7days':
      const sevenDaysAgo = new Date(now)
      sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7)
      start = new Date(sevenDaysAgo.getFullYear(), sevenDaysAgo.getMonth(), sevenDaysAgo.getDate(), 0, 0, 0, 0)
      end = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59, 59, 0)
      break
    case 'thismonth':
      start = new Date(now.getFullYear(), now.getMonth(), 1, 0, 0, 0, 0)
      end = new Date(now.getFullYear(), now.getMonth() + 1, 0, 23, 59, 59, 0)
      break
    case 'lastmonth':
      start = new Date(now.getFullYear(), now.getMonth() - 1, 1, 0, 0, 0, 0)
      end = new Date(now.getFullYear(), now.getMonth(), 0, 23, 59, 59, 0)
      break
  }

  startTime.value = start
  endTime.value = end

  // 强制触发响应式更新
  triggerRef(startTime)
  triggerRef(endTime)

  updateModelValue()
}

/**
 * ===========================
 * 数据更新和事件触发
 * ===========================
 */
// 更新v-model值并触发事件
const updateModelValue = () => {
  let value = []

  // 如果两个时间都存在，返回完整的时间范围
  if (startTime.value && endTime.value) {
    value = [formatDateTime(startTime.value), formatDateTime(endTime.value)]
  }
  // 如果只有开始时间，返回包含开始时间的数组
  else if (startTime.value && !endTime.value) {
    value = [formatDateTime(startTime.value), null]
  }
  // 如果只有结束时间，返回包含结束时间的数组
  else if (!startTime.value && endTime.value) {
    value = [null, formatDateTime(endTime.value)]
  }
  // 如果都没有，返回空数组
  else {
    value = []
  }

  emit('update:modelValue', value)
  emit('change', value)
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

/**
 * ===========================
 * 主容器样式
 * ===========================
 */
.enhanced-datetime-picker {
  width: 100%;
}

/**
 * ===========================
 * 输入框区域样式
 * ===========================
 */
.picker-inputs {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  margin-bottom: 12px;

  .input-group {
    flex: 1;

    .input-wrapper {
      position: relative;
      cursor: pointer;

      .time-input {
        width: 100%;
        padding: 8px 32px 8px 12px;
        border: 1px solid var(--border-color);
        border-radius: var(--border-radius-base);
        font-size: var(--font-size-base);
        color: var(--text-primary);
        background: var(--bg-primary);
        cursor: pointer;
        transition: all 0.2s ease;

        &:hover {
          border-color: var(--primary-color);
        }

        &:focus {
          outline: none;
          border-color: var(--primary-color);
          box-shadow: 0 0 0 2px rgba(22, 93, 255, 0.1);
        }

        &:disabled {
          background: var(--bg-secondary);
          color: var(--text-tertiary);
          cursor: not-allowed;
        }

        &::placeholder {
          color: var(--text-tertiary);
        }
      }

      .input-icon {
        position: absolute;
        right: 12px;
        top: 50%;
        transform: translateY(-50%);
        color: var(--text-tertiary);
        pointer-events: none;
      }
    }
  }

  .picker-actions {
    .clear-btn {
      padding: 8px 12px;
      border: 1px solid var(--border-color);
      border-radius: var(--border-radius-base);
      background: var(--bg-primary);
      color: var(--text-secondary);
      font-size: var(--font-size-extra-small);
      cursor: pointer;
      transition: all 0.2s ease;

      &:hover {
        color: var(--danger-color);
        border-color: var(--danger-color);
      }

      &:disabled {
        opacity: 0.6;
        cursor: not-allowed;
      }

      i {
        margin-right: 4px;
      }
    }
  }
}

/**
 * ===========================
 * 时间间隔显示样式
 * ===========================
 */
.duration-display {
  padding: 8px 12px;
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
:deep(.datetime-picker-dialog) {
  .el-dialog__header {
    padding: 16px 20px;
    border-bottom: 1px solid var(--border-color);
  }

  .el-dialog__body {
    padding: 20px;
  }
}

.picker-content {
  .section-title {
    font-size: var(--font-size-base);
    color: var(--text-primary);
    margin-bottom: 12px;
    font-weight: var(--font-weight-medium);
  }

  .date-section {
    margin-bottom: 24px;
  }

  .time-section {
    .time-inputs {
      display: flex;
      align-items: flex-end;
      gap: 8px;

      .time-group {
        display: flex;
        flex-direction: column;
        gap: 4px;

        label {
          font-size: var(--font-size-extra-small);
          color: var(--text-secondary);
        }
      }

      .time-separator {
        font-size: var(--font-size-large);
        color: var(--text-tertiary);
        margin-bottom: 8px;
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/**
 * ===========================
 * 响应式设计
 * ===========================
 */
@media (max-width: 768px) {
  .picker-inputs {
    flex-direction: column;
    gap: 8px;
  }

  .shortcuts-buttons {
    .shortcut-btn {
      flex: 1;
      text-align: center;
    }
  }
}
</style>
