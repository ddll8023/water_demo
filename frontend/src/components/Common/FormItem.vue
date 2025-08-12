<!-- 表单项组件 - 根据配置动态渲染不同类型的表单控件 -->
<template>
  <el-form-item :label="item.label" :prop="item.prop" :required="item.required" :label-width="item.labelWidth"
    :error="item.error" :show-message="item.showMessage !== false" :inline-message="item.inlineMessage"
    :size="item.size">
    <!-- 输入框 -->
    <CustomInput v-if="item.type === 'input' || !item.type" :model-value="formData[item.prop]"
      @update:model-value="handleChange" :placeholder="item.placeholder" :disabled="disabled" :readonly="item.readonly"
      :clearable="item.clearable !== false" :maxlength="item.maxlength" :minlength="item.minlength"
      :show-word-limit="item.showWordLimit" :prefix-icon="item.prefixIcon" :suffix-icon="item.suffixIcon" />

    <!-- 文本域 -->
    <CustomTextarea v-else-if="item.type === 'textarea'" :model-value="formData[item.prop]"
      @update:model-value="handleChange" :placeholder="item.placeholder" :disabled="disabled" :readonly="item.readonly"
      :rows="item.rows || 3" :maxlength="item.maxlength" :show-word-limit="item.showWordLimit" :resize="item.resize" />

    <!-- 数字输入框 -->
    <CustomInputNumber v-else-if="item.type === 'number' || item.type === 'input-number'"
      :model-value="formData[item.prop]" @update:model-value="handleChange" :placeholder="item.placeholder"
      :disabled="disabled" :min="item.min" :max="item.max" :step="item.step" :precision="item.precision"
      :controls="item.controls !== false" :controls-position="item.controlsPosition" style="width: 100%" />

    <!-- 选择器 -->
    <CustomSelect v-else-if="item.type === 'select'" :model-value="formData[item.prop]"
      @update:model-value="handleChange" :options="item.options || []" :placeholder="item.placeholder"
      :disabled="disabled" :clearable="item.clearable !== false" :multiple="item.multiple" :filterable="item.filterable"
      :remote="item.remote" :remote-method="item.remoteMethod" :loading="item.loading" value-key="value"
      label-key="label" style="width: 100%" />

    <!-- 级联选择器 -->
    <CustomCascader v-else-if="item.type === 'cascader'" :model-value="formData[item.prop]"
      @update:model-value="handleChange" :options="item.options || []" :props="item.cascaderProps"
      :placeholder="item.placeholder" :disabled="disabled" :clearable="item.clearable !== false"
      :filterable="item.filterable" :show-all-levels="item.showAllLevels !== false" style="width: 100%" />

    <!-- 日期选择器 -->
    <el-date-picker v-else-if="item.type === 'date'" :model-value="getSafeValue(item.prop)"
      @update:model-value="(value) => handleDateChange(item.prop, value)" type="date" :placeholder="item.placeholder"
      :disabled="disabled" :clearable="item.clearable !== false" :format="item.format || 'YYYY-MM-DD'"
      :value-format="item.valueFormat || 'YYYY-MM-DD'" style="width: 100%" />

    <!-- 日期时间选择器 -->
    <CustomDatePicker v-else-if="item.type === 'datetime'" :model-value="formData[item.prop]"
      @update:model-value="handleChange" type="datetime" :placeholder="item.placeholder" :disabled="disabled"
      :clearable="item.clearable !== false" :format="item.format || 'YYYY-MM-DD HH:mm:ss'"
      :value-format="item.valueFormat || 'YYYY-MM-DDTHH:mm:ss'" style="width: 100%" />

    <!-- 时间选择器 -->
    <el-time-picker v-else-if="item.type === 'time'" :model-value="formData[item.prop]"
      @update:model-value="handleChange" :placeholder="item.placeholder" :disabled="disabled"
      :clearable="item.clearable !== false" :format="item.format || 'HH:mm:ss'"
      :value-format="item.valueFormat || 'HH:mm:ss'" style="width: 100%" />

    <!-- 开关 -->
    <CustomSwitch v-else-if="item.type === 'switch'" :model-value="formData[item.prop]"
      @update:model-value="handleChange" :disabled="disabled" :active-text="item.activeText"
      :inactive-text="item.inactiveText" :active-value="item.activeValue !== undefined ? item.activeValue : true"
      :inactive-value="item.inactiveValue !== undefined ? item.inactiveValue : false" />

    <!-- 单选框组 -->
    <CustomRadioGroup v-else-if="item.type === 'radio'" :model-value="formData[item.prop]"
      @update:model-value="handleChange" :options="item.options || []" :disabled="disabled" value-key="value"
      label-key="label" disabled-key="disabled" />

    <!-- 复选框组 -->
    <el-checkbox-group v-else-if="item.type === 'checkbox' || item.type === 'checkbox-group'"
      :model-value="formData[item.prop]" @update:model-value="handleChange" :disabled="disabled">
      <el-checkbox v-for="option in item.options" :key="option.value" :label="option.value" :disabled="option.disabled">
        {{ option.label }}
      </el-checkbox>
    </el-checkbox-group>

    <!-- 滑块 -->
    <el-slider v-else-if="item.type === 'slider'" :model-value="formData[item.prop]" @update:model-value="handleChange"
      :disabled="disabled" :min="item.min || 0" :max="item.max || 100" :step="item.step || 1"
      :show-input="item.showInput" :show-stops="item.showStops" :range="item.range" />

    <!-- 评分 -->
    <el-rate v-else-if="item.type === 'rate'" :model-value="formData[item.prop]" @update:model-value="handleChange"
      :disabled="disabled" :max="item.max || 5" :allow-half="item.allowHalf" :show-text="item.showText"
      :texts="item.texts" />

    <!-- 颜色选择器 -->
    <el-color-picker v-else-if="item.type === 'color'" :model-value="formData[item.prop]"
      @update:model-value="handleChange" :disabled="disabled" :show-alpha="item.showAlpha"
      :color-format="item.colorFormat" :predefine="item.predefine" />

    <!-- 上传组件 -->
    <el-upload v-else-if="item.type === 'upload'" :action="item.action" :headers="item.headers" :data="item.data"
      :name="item.name || 'file'" :with-credentials="item.withCredentials" :multiple="item.multiple"
      :accept="item.accept" :auto-upload="item.autoUpload !== false" :list-type="item.listType || 'text'"
      :disabled="disabled" :limit="item.limit" :on-success="item.onSuccess" :on-error="item.onError"
      :on-progress="item.onProgress" :on-change="item.onChange" :on-preview="item.onPreview" :on-remove="item.onRemove"
      :before-upload="item.beforeUpload" :before-remove="item.beforeRemove" :file-list="formData[item.prop] || []">
      <slot :name="`upload-${item.prop}`">
        <el-button type="primary" :disabled="disabled">
          {{ item.uploadText || '点击上传' }}
        </el-button>
      </slot>
    </el-upload>

    <!-- 未知类型，显示警告 -->
    <div v-else class="unknown-type-warning">
      <el-alert title="未知的表单项类型" :description="`类型 '${item.type}' 不被支持`" type="warning" show-icon :closable="false" />
    </div>

    <!-- 帮助文本 -->
    <div v-if="item.help" class="form-item-help">
      <el-text type="info" size="small">{{ item.help }}</el-text>
    </div>
  </el-form-item>
</template>

<script setup>
/**
 * 导入依赖
 * ---------------------------------------------------------------------------
 */
import { computed } from 'vue'
import CustomInput from './CustomInput.vue'
import CustomTextarea from './CustomTextarea.vue'
import CustomInputNumber from './CustomInputNumber.vue'
import CustomSelect from './CustomSelect.vue'
import CustomCascader from './CustomCascader.vue'
import CustomDatePicker from './CustomDatePicker.vue'
import CustomSwitch from './CustomSwitch.vue'
import CustomRadioGroup from './CustomRadioGroup.vue'

/**
 * 组件属性定义
 * ---------------------------------------------------------------------------
 */
const props = defineProps({
  // 表单项配置
  item: {
    type: Object,
    required: true
  },
  // 表单数据对象
  formData: {
    type: Object,
    required: true
  },
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  }
})

/**
 * 事件定义
 * ---------------------------------------------------------------------------
 */
const emit = defineEmits(['change'])

/**
 * 事件处理方法
 * ---------------------------------------------------------------------------
 */
/**
 * 处理表单项值变更
 * @param {*} value - 新的表单项值
 */
const handleChange = (value) => {
  emit('change', props.item.prop, value, props.item)
}

/**
 * 获取安全的值，用于日期选择器
 * @param {string} prop - 属性名
 * @returns {*} - 安全的值
 */
const getSafeValue = (prop) => {
  const value = props.formData[prop]

  // 如果是 null 或 undefined，返回 undefined
  if (value === null || value === undefined) {
    return undefined
  }

  // 如果是字符串且包含 Invalid 或 NaN，返回 undefined
  if (typeof value === 'string' && (value.includes('Invalid') || value.includes('NaN'))) {
    return undefined
  }

  // 如果是 Date 对象且无效，返回 undefined
  if (value instanceof Date && isNaN(value.getTime())) {
    return undefined
  }

  return value
}

/**
 * 处理日期变化
 * @param {string} prop - 属性名
 * @param {*} value - 新的日期值
 */
const handleDateChange = (prop, value) => {
  // 防止传递无效值
  if (value === 'Invalid Date' ||
    (value instanceof Date && isNaN(value.getTime())) ||
    (typeof value === 'string' && (value.includes('Invalid') || value.includes('NaN')))) {
    return
  }

  emit('change', prop, value, props.item)
}
</script>

<style scoped lang="scss">
/**
 * 组件样式
 * ---------------------------------------------------------------------------
 */
.form-item-help {
  margin-top: 4px;
  line-height: 1.4;
}

.unknown-type-warning {
  margin: 8px 0;
}
</style>
