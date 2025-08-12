<!-- 通用表单组件 - 支持动态表单配置和验证 -->
<template>
  <div class="common-form">
    <el-form ref="formRef" :model="formData" :rules="formRules" :label-width="labelWidth"
      :label-position="labelPosition" :size="size" :disabled="disabled" :validate-on-rule-change="validateOnRuleChange"
      :hide-required-asterisk="hideRequiredAsterisk" :show-message="showMessage" :inline-message="inlineMessage"
      :status-icon="statusIcon" @validate="handleValidate" @submit.prevent>
      <template v-for="(item, index) in formItems" :key="item.prop || index">
        <!-- 分组标题 -->
        <div v-if="item.type === 'group'" class="form-group">
          <div class="group-title">{{ item.label }}</div>
          <div class="group-content">
            <el-row :gutter="item.gutter || gutter">
              <el-col v-for="subItem in item.children" :key="subItem.prop"
                :span="subItem.span || getDefaultSpan(subItem)" :xs="subItem.xs" :sm="subItem.sm" :md="subItem.md"
                :lg="subItem.lg" :xl="subItem.xl">
                <FormItem :item="subItem" :form-data="formData" :disabled="disabled || subItem.disabled"
                  @change="handleItemChange" />
              </el-col>
            </el-row>
          </div>
        </div>

        <!-- 普通表单项 -->
        <el-row v-else :gutter="gutter">
          <el-col :span="item.span || getDefaultSpan(item)" :xs="item.xs" :sm="item.sm" :md="item.md" :lg="item.lg"
            :xl="item.xl">
            <!-- 自定义插槽表单项 -->
            <el-form-item v-if="item.type === 'slot'" :label="item.label" :prop="item.prop" :required="item.required"
              :label-width="item.labelWidth" :error="item.error" :show-message="item.showMessage !== false"
              :inline-message="item.inlineMessage" :size="item.size">
              <slot :name="item.prop" :item="item" :form-data="formData" :disabled="disabled || item.disabled" />
            </el-form-item>
            <!-- 普通表单项 -->
            <FormItem v-else :item="item" :form-data="formData" :disabled="disabled || item.disabled"
              @change="handleItemChange" />
          </el-col>
        </el-row>
      </template>

      <!-- 表单操作按钮 -->
      <div class="form-actions" v-if="showActions">
        <slot name="actions" :form-data="formData" :validate="validate" :reset="resetForm">
          <el-form-item>
            <CustomButton type="primary" :loading="submitLoading" @click="handleSubmit">
              {{ submitText }}
            </CustomButton>
            <CustomButton type="secondary" @click="handleReset">
              {{ resetText }}
            </CustomButton>
            <CustomButton v-if="showCancel" type="secondary" @click="handleCancel">
              {{ cancelText }}
            </CustomButton>
          </el-form-item>
        </slot>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import CustomButton from './CustomButton.vue'
import FormItem from './FormItem.vue'

/**
 * =========================================================
 * 组件属性定义
 * =========================================================
 */
// Props定义
const props = defineProps({
  // 表单配置项
  items: {
    type: Array,
    default: () => []
  },
  // 表单数据
  modelValue: {
    type: Object,
    default: () => ({})
  },
  // 表单验证规则
  rules: {
    type: Object,
    default: () => ({})
  },
  // 标签宽度
  labelWidth: {
    type: [String, Number],
    default: '120px'
  },
  // 标签位置
  labelPosition: {
    type: String,
    default: 'right'
  },
  // 表单尺寸
  size: {
    type: String,
    default: 'default'
  },
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  },
  // 栅格间隔
  gutter: {
    type: Number,
    default: 20
  },
  // 默认列数
  columns: {
    type: Number,
    default: 2
  },
  // 是否显示操作按钮
  showActions: {
    type: Boolean,
    default: true
  },
  // 提交按钮文本
  submitText: {
    type: String,
    default: '提交'
  },
  // 重置按钮文本
  resetText: {
    type: String,
    default: '重置'
  },
  // 取消按钮文本
  cancelText: {
    type: String,
    default: '取消'
  },
  // 是否显示取消按钮
  showCancel: {
    type: Boolean,
    default: false
  },
  // 提交加载状态
  submitLoading: {
    type: Boolean,
    default: false
  },
  // 是否在规则改变时验证
  validateOnRuleChange: {
    type: Boolean,
    default: true
  },
  // 是否隐藏必填星号
  hideRequiredAsterisk: {
    type: Boolean,
    default: false
  },
  // 是否显示校验错误信息
  showMessage: {
    type: Boolean,
    default: true
  },
  // 是否以行内形式展示校验信息
  inlineMessage: {
    type: Boolean,
    default: false
  },
  // 是否显示校验状态图标
  statusIcon: {
    type: Boolean,
    default: false
  }
})

/**
 * =========================================================
 * 事件定义与发射
 * =========================================================
 */
// Emits定义
const emit = defineEmits([
  'update:modelValue',
  'submit',
  'reset',
  'cancel',
  'change',
  'validate'
])

/**
 * =========================================================
 * 响应式状态管理
 * =========================================================
 */
// 表单引用和数据
const formRef = ref()
const formData = reactive({})
const formItems = ref([])

/**
 * =========================================================
 * 计算属性
 * =========================================================
 */
// 表单验证规则计算属性
const formRules = computed(() => {
  const rules = { ...props.rules }

  // 从表单项配置中提取验证规则
  const extractRules = (items) => {
    items.forEach(item => {
      if (item.type === 'group' && item.children) {
        extractRules(item.children)
      } else if (item.prop && item.rules) {
        // 如果 item.rules 是字符串，从 props.rules 中获取对应的规则
        if (typeof item.rules === 'string') {
          if (props.rules[item.rules]) {
            rules[item.prop] = props.rules[item.rules]
          }
        } else {
          // 如果 item.rules 是数组，直接使用
          rules[item.prop] = item.rules
        }
      }
    })
  }

  extractRules(formItems.value)
  return rules
})

/**
 * =========================================================
 * 工具函数
 * =========================================================
 */
// 根据表单项类型获取默认值
const getDefaultValue = (type) => {
  switch (type) {
    case 'number':
    case 'input-number':
      return 0
    case 'switch':
      return false
    case 'checkbox':
    case 'checkbox-group':
      return []
    case 'date':
    case 'datetime':
    case 'time':
      return null
    case 'select':
      return undefined
    default:
      return ''
  }
}

// 初始化表单数据
const initFormData = () => {
  const initData = (items) => {
    items.forEach(item => {
      if (item.type === 'group' && item.children) {
        initData(item.children)
      } else if (item.prop && !(item.prop in formData)) {
        formData[item.prop] = item.defaultValue || getDefaultValue(item.type)
      }
    })
  }

  initData(formItems.value)
}

// 获取表单项默认宽度
const getDefaultSpan = (item) => {
  if (item.fullWidth) return 24
  return Math.floor(24 / props.columns)
}

/**
 * =========================================================
 * 工具函数与变量
 * =========================================================
 */
// 深度比较两个对象是否相等
const deepEqual = (obj1, obj2) => {
  if (obj1 === obj2) return true
  if (!obj1 || !obj2) return false
  if (typeof obj1 !== 'object' || typeof obj2 !== 'object') return obj1 === obj2

  const keys1 = Object.keys(obj1)
  const keys2 = Object.keys(obj2)
  if (keys1.length !== keys2.length) return false

  for (let key of keys1) {
    if (!keys2.includes(key)) return false
    if (!deepEqual(obj1[key], obj2[key])) return false
  }
  return true
}

// 标记是否正在内部更新，避免循环
let isInternalUpdate = false

/**
 * =========================================================
 * 生命周期与监听器
 * =========================================================
 */
// 监听表单项配置变化
watch(() => props.items, (newItems) => {
  formItems.value = newItems
  initFormData()
}, { immediate: true, deep: true })

// 监听父组件modelValue变化
watch(() => props.modelValue, (newValue) => {
  // 深度比较：只有当新值与当前内部数据确实不同时才更新
  if (!deepEqual(newValue, formData)) {
    // 设置内部更新标记，避免触发向上的数据同步
    isInternalUpdate = true

    try {
      // 使用增量更新替代全量替换，避免正在编辑的字段被清空
      if (newValue && typeof newValue === 'object') {
        Object.keys(newValue).forEach(key => {
          // 只更新确实发生变化的字段
          if (formData[key] !== newValue[key]) {
            formData[key] = newValue[key]
          }
        })

        // 清理不再存在的字段
        Object.keys(formData).forEach(key => {
          if (!(key in newValue)) {
            delete formData[key]
          }
        })
      } else {
        // 如果newValue为空或非对象，清空所有数据
        Object.keys(formData).forEach(key => {
          delete formData[key]
        })
      }
    } finally {
      // 在下一个tick重置标记，确保用户操作能正常触发数据同步
      nextTick(() => {
        isInternalUpdate = false
      })
    }
  }
}, { immediate: true, deep: true })

// 监听内部表单数据变化
watch(formData, (newValue) => {
  // 如果正在内部更新，跳过
  if (isInternalUpdate) return

  // 深度比较：只有当新值与父组件值确实不同时才发射更新事件
  if (!deepEqual(newValue, props.modelValue)) {
    emit('update:modelValue', { ...newValue })
  }
}, { deep: true })

/**
 * =========================================================
 * 表单验证与操作方法
 * =========================================================
 */
// 表单验证
const validate = async () => {
  try {
    await formRef.value?.validate()
    return true
  } catch (error) {
    return false
  }
}

// 验证指定字段
const validateField = (prop) => {
  return formRef.value?.validateField(prop)
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  emit('reset')
}

// 清除验证信息
const clearValidate = (props) => {
  formRef.value?.clearValidate(props)
}

/**
 * =========================================================
 * 事件处理方法
 * =========================================================
 */
// 表单项变化处理
const handleItemChange = (prop, value, item) => {
  formData[prop] = value
  emit('change', prop, value, item)
}

// 表单验证处理
const handleValidate = (prop, isValid, message) => {
  emit('validate', prop, isValid, message)
}

// 提交表单处理
const handleSubmit = async () => {
  const isValid = await validate()
  if (isValid) {
    emit('submit', { ...formData })
  }
}

// 重置表单处理
const handleReset = () => {
  resetForm()
}

// 取消操作处理
const handleCancel = () => {
  emit('cancel')
}

/**
 * =========================================================
 * 组件方法暴露
 * =========================================================
 */
// 暴露方法给父组件
defineExpose({
  validate,
  validateField,
  resetForm,
  clearValidate,
  formData
})
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

/**
 * =========================================================
 * 表单组件样式
 * =========================================================
 */
.common-form {

  /* 表单分组样式 */
  .form-group {
    margin-bottom: var(--spacing-extra-large);

    .group-title {
      font-size: var(--font-size-medium);
      font-weight: var(--font-weight-medium);
      color: var(--text-primary);
      margin-bottom: var(--spacing-base);
      padding-bottom: var(--spacing-small);
      border-bottom: 1px solid var(--border-light);
    }

    .group-content {
      padding-left: var(--spacing-base);
    }
  }

  /* 表单操作区样式 */
  .form-actions {
    margin-top: var(--spacing-extra-large);
    padding-top: var(--spacing-base);
    border-top: 1px solid var(--border-light);

    :deep(.el-form-item) {
      margin-bottom: 0;
    }
  }
}
</style>
