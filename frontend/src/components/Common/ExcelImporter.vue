<template>
  <CustomDialog :visible="dialogVisible" @update:visible="dialogVisible = $event" title="Excel数据导入" width="80%"
    :close-on-click-modal="false" :close-on-press-escape="false" :show-cancel-button="false"
    :show-confirm-button="false" @close="handleClose">
    <div class="excel-importer">
      <!-- 文件上传区域 -->
      <div v-if="currentStep === 1" class="upload-section">
        <el-upload ref="uploadRef" class="upload-dragger" drag :auto-upload="false" :show-file-list="false"
          :accept="'.xlsx'" :on-change="handleFileChange" :before-upload="beforeUpload">
          <i class="fa fa-upload fa-3x" style="color: var(--primary-color); margin-bottom: var(--spacing-base);"></i>
          <div class="el-upload__text">
            将Excel文件拖拽到此处，或<em>点击选择文件</em>
          </div>
          <div class="el-upload__tip">
            仅支持.xlsx格式文件，文件大小不超过50MB，支持大批量数据导入
          </div>
        </el-upload>

        <div v-if="selectedFile" class="file-info">
          <CustomCard :bordered="true" shadow="hover" padding="normal">
            <div class="file-details">
              <i class="fa fa-file-excel-o"
                style="color: var(--success-color); margin-right: var(--spacing-small);"></i>
              <span>{{ selectedFile.name }}</span>
              <span class="file-size">({{ formatFileSize(selectedFile.size) }})</span>
              <CustomButton type="danger" size="small" @click="removeFile" style="margin-left: var(--spacing-base);">
                <i class="fa fa-times"></i>
                移除
              </CustomButton>
            </div>
          </CustomCard>
        </div>

        <!-- Excel格式说明 -->
        <CustomCard class="format-info" style="margin-top: var(--spacing-large);" :bordered="true" shadow="never"
          padding="normal">
          <template #header>
            <div class="card-header">
              <i class="fa fa-info-circle" style="margin-right: var(--spacing-small);"></i>
              Excel文件格式要求
            </div>
          </template>
          <div class="format-details">
            <!-- 流量数据格式 -->
            <div v-if="props.importType === 'flow'">
              <p><strong>固定列结构（共5列）：</strong></p>
              <ol>
                <li>序号</li>
                <li>监测时间（格式：YYYY-MM-DD HH:mm:ss）</li>
                <li>站码（8位字符串）</li>
                <li>瞬时流量(m³/s)（数值或空值）</li>
                <li>累计流量(m³)（数值或空值）</li>
              </ol>
              <p><strong>示例数据：</strong></p>
              <div class="example-data">
                <code>1 | 2025-02-27 18:55:00 | 00000002 | 0 | [空值]</code>
              </div>
            </div>
            <!-- 水质数据格式 -->
            <div v-else-if="props.importType === 'waterQuality'">
              <p><strong>固定列结构（共12列）：</strong></p>
              <ol>
                <li>序号</li>
                <li>监测时间（格式：YYYY-MM-DD HH:mm:ss）</li>
                <li>监测点（站点名称，仅用于显示）</li>
                <li>站码（8位字符串）</li>
                <li>水温(℃)（数值或空值）</li>
                <li>浊度(NTU)（数值或空值）</li>
                <li>PH（数值或空值）</li>
                <li>电导率(uS/cm)（数值或空值）</li>
                <li>溶解氧(mg/L)（数值或空值）</li>
                <li>氨氮(mg/L)（数值或空值）</li>
                <li>化学需氧量(mg/L)（数值或空值）</li>
                <li>余氯(mg/L)（数值或空值）</li>
              </ol>
              <p><strong>示例数据：</strong></p>
              <div class="example-data">
                <code>1 | 2025-02-27 18:55:00 | 水厂监测点 | 24083436 | 25.5 | 1.2 | 7.5 | 450 | 8.5 | 0.15 | 2.8 | 0.3</code>
              </div>
            </div>
            <!-- 水位数据格式 -->
            <div v-else-if="props.importType === 'waterLevel'">
              <p><strong>固定列结构（共5列）：</strong></p>
              <ol>
                <li>序号</li>
                <li>监测时间（格式：YYYY-MM-DD HH:mm:ss）</li>
                <li>监测点（站点名称，仅用于显示）</li>
                <li>站码（8位字符串）</li>
                <li>监测值(m)（数值，不能为空）</li>
              </ol>
              <p><strong>示例数据：</strong></p>
              <div class="example-data">
                <code>1 | 2025-02-27 18:55:00 | 两河口水库 | 24043426 | 125.8</code>
              </div>
            </div>
          </div>
        </CustomCard>
      </div>

      <!-- 数据预览区域 -->
      <div v-if="currentStep === 2" class="preview-section">
        <div class="preview-header">
          <div class="stats-info">
            <el-tag type="info">总行数: {{ totalRows }}</el-tag>
            <el-tag type="success" style="margin-left: var(--spacing-small);">有效数据: {{ validRows }}</el-tag>
            <el-tag v-if="errorRows > 0" type="danger" style="margin-left: var(--spacing-small);">
              错误数据: {{ errorRows }}
            </el-tag>
          </div>
        </div>

        <!-- 错误信息展示 -->
        <div v-if="errors.length > 0" class="error-section">
          <el-alert title="数据验证错误" type="error" :closable="false" style="margin-bottom: var(--spacing-base);">
            <template #default>
              <div>发现 {{ errors.length }} 个错误，请检查以下问题：</div>
              <ul style="margin: var(--spacing-small) 0 0 var(--spacing-large);">
                <li v-for="error in errors.slice(0, 10)" :key="error.row">
                  第{{ error.row }}行：{{ error.error }}
                </li>
                <li v-if="errors.length > 10">
                  还有 {{ errors.length - 10 }} 个错误...
                </li>
              </ul>
            </template>
          </el-alert>
        </div>

        <!-- 数据预览表格 -->
        <div class="preview-table">
          <CommonTable :data="previewData" :columns="previewColumns" :loading="false" :show-pagination="false"
            :show-selection="false" :show-index="true" :show-actions="false" :max-height="400" />
        </div>
      </div>

      <!-- 导入进度区域 -->
      <div v-if="currentStep === 3" class="progress-section">
        <div class="progress-content">
          <el-progress :percentage="importProgress"
            :status="importStatus === 'success' ? 'success' : (importStatus === 'error' || importStatus === 'failed') ? 'exception' : ''"
            :stroke-width="8" />
          <div class="progress-text">
            {{ progressText }}
          </div>

          <div v-if="importResult" class="import-result">
            <CustomCard :bordered="true" shadow="always" padding="normal">
              <template #header>
                <div class="card-header">
                  <i v-if="importResult.successRows > 0 && importResult.errorRows === 0" class="fa fa-check-circle"
                    style="color: var(--success-color); margin-right: var(--spacing-small);"></i>
                  <i v-else-if="importResult.successRows > 0 && importResult.errorRows > 0"
                    class="fa fa-exclamation-triangle"
                    style="color: var(--warning-color); margin-right: var(--spacing-small);"></i>
                  <i v-else class="fa fa-times-circle"
                    style="color: var(--danger-color); margin-right: var(--spacing-small);"></i>
                  {{ importResult.successRows > 0 && importResult.errorRows === 0 ? '导入成功' :
                    importResult.successRows > 0 && importResult.errorRows > 0 ? '导入完成' : '导入失败' }}
                </div>
              </template>
              <div class="result-stats">
                <div class="stat-item">
                  <span class="label">总处理行数：</span>
                  <span class="value">{{ importResult.totalRows }}</span>
                </div>
                <div class="stat-item">
                  <span class="label">成功导入：</span>
                  <span class="value success">{{ importResult.successRows }}</span>
                </div>
                <div v-if="importResult.errorRows > 0" class="stat-item">
                  <span class="label">导入失败：</span>
                  <span class="value error">{{ importResult.errorRows }}</span>
                </div>
                <div v-if="importResult.duplicateRows > 0" class="stat-item">
                  <span class="label">重复数据：</span>
                  <span class="value warning">{{ importResult.duplicateRows }}</span>
                </div>
              </div>
            </CustomCard>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <CustomButton @click="handleClose">取消</CustomButton>
        <CustomButton v-if="currentStep === 1" type="primary" :disabled="!selectedFile" @click="parseExcelFile">
          <i class="fa fa-search"></i>
          解析预览
        </CustomButton>
        <CustomButton v-if="currentStep === 2" type="secondary" @click="currentStep = 1">
          <i class="fa fa-arrow-left"></i>
          上一步
        </CustomButton>
        <CustomButton v-if="currentStep === 2" type="primary" :disabled="validRows === 0" @click="startImport">
          <i class="fa fa-upload"></i>
          开始导入
        </CustomButton>
        <CustomButton v-if="currentStep === 3 && importStatus === 'success'" type="primary" @click="handleClose">
          <i class="fa fa-check"></i>
          完成
        </CustomButton>
      </div>
    </template>
  </CustomDialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import * as XLSX from 'xlsx'
import CustomButton from './CustomButton.vue'
import CustomDialog from './CustomDialog.vue'
import CustomCard from './CustomCard.vue'
import CommonTable from './CommonTable.vue'
import { importFlowData, importWaterQualityData, importWaterLevelData } from '@/api/monitoring'

/**
 * ----------------------------------------
 * 组件属性和事件定义
 * ----------------------------------------
 */
// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  importType: {
    type: String,
    default: 'flow', // 'flow'、'waterQuality' 或 'waterLevel'
    validator: (value) => ['flow', 'waterQuality', 'waterLevel'].includes(value)
  },
  templateColumns: {
    type: Array,
    default: () => [
      { prop: 'rowNumber', label: '序号', width: 80 },
      { prop: 'monitoringTime', label: '监测时间', width: 160 },
      { prop: 'stationCode', label: '站码', width: 120 },
      { prop: 'instantFlow', label: '瞬时流量(m³/s)', width: 140 },
      { prop: 'cumulativeFlow', label: '累计流量(m³)', width: 140 }
    ]
  }
})

// Emits
const emit = defineEmits(['update:visible', 'import-success'])

/**
 * ----------------------------------------
 * 组件状态和引用
 * ----------------------------------------
 */
const dialogVisible = ref(false)
const currentStep = ref(1) // 1: 文件上传, 2: 数据预览, 3: 导入进度
const selectedFile = ref(null)
const uploadRef = ref(null)

// 解析后的数据
const parsedData = ref([])
const errors = ref([])
const previewData = ref([])

// 导入相关
const importProgress = ref(0)
const importStatus = ref('') // '', 'success', 'error'
const importResult = ref(null)

/**
 * ----------------------------------------
 * 计算属性和监听器
 * ----------------------------------------
 */
// 计算属性
const totalRows = computed(() => parsedData.value.length)
const validRows = computed(() => parsedData.value.filter(item => !item.hasError).length)
const errorRows = computed(() => errors.value.length)

// 根据导入类型获取期望的列数
const expectedColumns = computed(() => {
  if (props.importType === 'waterQuality') return 12
  if (props.importType === 'waterLevel') return 5
  return 5 // flow 默认5列
})

const progressText = computed(() => {
  if (importStatus.value === 'success') {
    return '导入完成！'
  } else if (importStatus.value === 'failed') {
    return '导入失败，请查看详情'
  } else if (importStatus.value === 'error') {
    return '导入失败，请重试'
  } else {
    return `正在导入数据... ${importProgress.value}%`
  }
})

const previewColumns = computed(() => [
  ...props.templateColumns,
  {
    prop: 'status',
    label: '状态',
    width: 100,
    formatter: (row) => {
      return row.hasError ? '错误' : '正常'
    }
  }
])

// Watch
watch(() => props.visible, (newVal) => {
  dialogVisible.value = newVal
  if (newVal) {
    resetComponent()
  }
})

watch(dialogVisible, (newVal) => {
  emit('update:visible', newVal)
})

/**
 * ----------------------------------------
 * 组件状态管理方法
 * ----------------------------------------
 */
// 重置组件状态
const resetComponent = () => {
  currentStep.value = 1
  selectedFile.value = null
  parsedData.value = []
  errors.value = []
  previewData.value = []
  importProgress.value = 0
  importStatus.value = ''
  importResult.value = null
}

// 关闭对话框
const handleClose = () => {
  dialogVisible.value = false
  // 只有真正成功导入数据时才触发import-success事件
  if (importStatus.value === 'success' && importResult.value && importResult.value.successRows > 0) {
    emit('import-success')
  }
}

/**
 * ----------------------------------------
 * 文件处理方法
 * ----------------------------------------
 */
// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 上传前检查文件
const beforeUpload = (file) => {
  const isXlsx = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
  const isLt50M = file.size / 1024 / 1024 < 50

  if (!isXlsx) {
    ElMessage.error('只能上传.xlsx格式的文件!')
    return false
  }
  if (!isLt50M) {
    ElMessage.error('文件大小不能超过50MB!')
    return false
  }
  return false // 阻止自动上传
}

// 文件变更处理
const handleFileChange = (file) => {
  selectedFile.value = file.raw
}

// 移除文件
const removeFile = () => {
  selectedFile.value = null
  uploadRef.value?.clearFiles()
}

/**
 * ----------------------------------------
 * Excel解析方法
 * ----------------------------------------
 */
// 读取文件为ArrayBuffer
const readFileAsArrayBuffer = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = (e) => resolve(e.target.result)
    reader.onerror = reject
    reader.readAsArrayBuffer(file)
  })
}

// Excel解析主方法
const parseExcelFile = async () => {
  if (!selectedFile.value) {
    ElMessage.error('请先选择文件')
    return
  }

  try {
    const fileBuffer = await readFileAsArrayBuffer(selectedFile.value)
    const workbook = XLSX.read(fileBuffer, { type: 'array' })
    const sheetName = workbook.SheetNames[0]
    const worksheet = workbook.Sheets[sheetName]

    // 转换为JSON数据
    const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 })

    if (jsonData.length < 2) {
      ElMessage.error('Excel文件数据不足，至少需要标题行和一行数据')
      return
    }

    // 验证列结构
    const headers = jsonData[0]
    if (headers.length !== expectedColumns.value) {
      ElMessage.error(`Excel文件列数不正确，应该包含${expectedColumns.value}列`)
      return
    }

    // 解析数据行
    const dataRows = jsonData.slice(1)
    const parsed = []
    const parseErrors = []

    dataRows.forEach((row, index) => {
      const rowNumber = index + 2 // Excel行号（从第2行开始）
      let item = {}

      if (props.importType === 'waterQuality') {
        // 水质数据12列格式
        item = {
          rowNumber: rowNumber,
          originalRowNumber: row[0], // 序号列
          monitoringTime: row[1],
          monitoringPoint: row[2], // 监测点名称
          stationCode: row[3],
          waterTemperature: row[4], // 水温
          turbidity: row[5], // 浊度
          phValue: row[6], // PH值
          conductivity: row[7], // 电导率
          dissolvedOxygen: row[8], // 溶解氧
          ammoniaNitrogen: row[9], // 氨氮
          codValue: row[10], // 化学需氧量
          residualChlorine: row[11], // 余氯
          hasError: false,
          errors: []
        }
      } else if (props.importType === 'waterLevel') {
        // 水位数据5列格式
        item = {
          rowNumber: rowNumber,
          originalRowNumber: row[0], // 序号列
          monitoringTime: row[1],
          monitoringPoint: row[2], // 监测点名称
          stationCode: row[3],
          waterLevel: row[4], // 监测值(m)
          hasError: false,
          errors: []
        }
      } else {
        // 流量数据5列格式（序号、监测时间、站码、瞬时流量、累计流量）
        item = {
          rowNumber: rowNumber,
          originalRowNumber: row[0], // 序号列
          monitoringTime: row[1],
          stationCode: row[2],
          instantFlow: row[3],
          cumulativeFlow: row[4],
          hasError: false,
          errors: []
        }
      }

      // 数据验证
      validateRowData(item, parseErrors)
      parsed.push(item)
    })

    parsedData.value = parsed
    errors.value = parseErrors
    previewData.value = parsed.slice(0, 100) // 显示前100行用于预览
    currentStep.value = 2

    ElMessage.success(`解析完成！共${totalRows.value}行数据，${validRows.value}行有效，${errorRows.value}行错误`)
  } catch (error) {
    console.error('Excel解析失败:', error)
    ElMessage.error('Excel文件解析失败，请检查文件格式')
  }
}

/**
 * ----------------------------------------
 * 数据验证方法
 * ----------------------------------------
 */
// 验证行数据
const validateRowData = (item, parseErrors) => {
  const errors = []

  // 验证监测时间
  if (!item.monitoringTime) {
    errors.push('监测时间不能为空')
  } else {
    const timeStr = String(item.monitoringTime).trim()
    const timeRegex = /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/
    if (!timeRegex.test(timeStr)) {
      errors.push('监测时间格式错误，应为YYYY-MM-DD HH:mm:ss')
    } else {
      const date = new Date(timeStr)
      if (isNaN(date.getTime())) {
        errors.push('监测时间无效')
      } else if (date > new Date()) {
        errors.push('监测时间不能是未来时间')
      } else if (date < new Date('2020-01-01')) {
        errors.push('监测时间不能早于2020-01-01')
      }
    }
  }

  // 验证站码
  if (!item.stationCode) {
    errors.push('站码不能为空')
  } else {
    const stationCode = String(item.stationCode).trim()
    if (stationCode.length !== 8) {
      errors.push('站码必须是8位字符串')
    }
    item.stationCode = stationCode
  }

  if (props.importType === 'waterQuality') {
    // 水质数据验证
    validateWaterQualityData(item, errors)
  } else if (props.importType === 'waterLevel') {
    // 水位数据验证
    validateWaterLevelData(item, errors)
  } else {
    // 流量数据验证
    validateFlowData(item, errors)
  }

  // 处理错误
  if (errors.length > 0) {
    item.hasError = true
    item.errors = errors
    parseErrors.push({
      row: item.rowNumber,
      stationCode: item.stationCode,
      error: errors.join('; ')
    })
  }
}

// 验证流量数据
const validateFlowData = (item, errors) => {
  // 验证瞬时流量
  if (item.instantFlow !== null && item.instantFlow !== undefined && item.instantFlow !== '') {
    const instantFlow = Number(item.instantFlow)
    if (isNaN(instantFlow)) {
      errors.push('瞬时流量必须是数值')
    } else if (instantFlow < -1000 || instantFlow > 10000) {
      errors.push('瞬时流量超出有效范围(-1000~10000)')
    } else {
      item.instantFlow = instantFlow
    }
  } else {
    item.instantFlow = null
  }

  // 验证累计流量
  if (item.cumulativeFlow !== null && item.cumulativeFlow !== undefined && item.cumulativeFlow !== '') {
    const cumulativeFlow = Number(item.cumulativeFlow)
    if (isNaN(cumulativeFlow)) {
      errors.push('累计流量必须是数值')
    } else if (cumulativeFlow < 0 || cumulativeFlow > 999999999) {
      errors.push('累计流量超出有效范围(0~999999999)')
    } else {
      item.cumulativeFlow = cumulativeFlow
    }
  } else {
    item.cumulativeFlow = null
  }
}

// 验证水质数据
const validateWaterQualityData = (item, errors) => {
  // 验证至少有一个监测项目数据
  const hasData = item.waterTemperature !== null && item.waterTemperature !== undefined && item.waterTemperature !== '' ||
    item.turbidity !== null && item.turbidity !== undefined && item.turbidity !== '' ||
    item.phValue !== null && item.phValue !== undefined && item.phValue !== '' ||
    item.conductivity !== null && item.conductivity !== undefined && item.conductivity !== '' ||
    item.dissolvedOxygen !== null && item.dissolvedOxygen !== undefined && item.dissolvedOxygen !== '' ||
    item.ammoniaNitrogen !== null && item.ammoniaNitrogen !== undefined && item.ammoniaNitrogen !== '' ||
    item.codValue !== null && item.codValue !== undefined && item.codValue !== '' ||
    item.residualChlorine !== null && item.residualChlorine !== undefined && item.residualChlorine !== ''

  if (!hasData) {
    errors.push('至少需要提供一个监测项目的数据')
  }

  // 验证各个监测项目的数值范围
  validateWaterQualityField(item, 'waterTemperature', '水温', -10, 100, errors)
  validateWaterQualityField(item, 'turbidity', '浊度', 0, 1000, errors)
  validateWaterQualityField(item, 'phValue', 'PH值', 0, 14, errors)
  validateWaterQualityField(item, 'conductivity', '电导率', 0, 10000, errors)
  validateWaterQualityField(item, 'dissolvedOxygen', '溶解氧', 0, 50, errors)
  validateWaterQualityField(item, 'ammoniaNitrogen', '氨氮', 0, 100, errors)
  validateWaterQualityField(item, 'codValue', '化学需氧量', 0, 1000, errors)
  validateWaterQualityField(item, 'residualChlorine', '余氯', 0, 10, errors)
}

// 验证水质监测项目字段
const validateWaterQualityField = (item, fieldName, fieldLabel, min, max, errors) => {
  const value = item[fieldName]
  if (value !== null && value !== undefined && value !== '') {
    const numValue = Number(value)
    if (isNaN(numValue)) {
      errors.push(`${fieldLabel}必须是数值`)
    } else if (numValue < min || numValue > max) {
      errors.push(`${fieldLabel}超出有效范围(${min}~${max})`)
    } else {
      item[fieldName] = numValue
    }
  } else {
    item[fieldName] = null
  }
}

// 验证水位数据
const validateWaterLevelData = (item, errors) => {
  // 验证水位值
  if (item.waterLevel === null || item.waterLevel === undefined || item.waterLevel === '') {
    errors.push('监测值(m)不能为空')
  } else {
    const waterLevel = Number(item.waterLevel)
    if (isNaN(waterLevel)) {
      errors.push('监测值(m)必须是数值')
    } else if (waterLevel < -1000 || waterLevel > 10000) {
      errors.push('监测值(m)超出有效范围(-1000~10000)')
    } else {
      item.waterLevel = waterLevel
    }
  }
}

/**
 * ----------------------------------------
 * 数据导入方法
 * ----------------------------------------
 */
// 开始导入
const startImport = async () => {
  if (validRows.value === 0) {
    ElMessage.error('没有有效数据可以导入')
    return
  }

  currentStep.value = 3
  importProgress.value = 0
  importStatus.value = ''

  try {
    // 准备导入数据（只导入有效数据）
    let validData = []

    if (props.importType === 'waterQuality') {
      // 水质数据格式
      validData = parsedData.value
        .filter(item => !item.hasError)
        .map(item => ({
          stationCode: item.stationCode,
          stationName: (item.monitoringPoint ?? '').toString().trim(),
          monitoringTime: item.monitoringTime,
          waterTemperature: item.waterTemperature,
          turbidity: item.turbidity,
          phValue: item.phValue,
          conductivity: item.conductivity,
          dissolvedOxygen: item.dissolvedOxygen,
          ammoniaNitrogen: item.ammoniaNitrogen,
          codValue: item.codValue,
          residualChlorine: item.residualChlorine,
          dataQuality: 1,
          collectionMethod: 'MANUAL',
          dataSource: 'EXCEL_IMPORT',
          remark: 'Excel批量导入'
        }))
    } else if (props.importType === 'waterLevel') {
      // 水位数据格式
      validData = parsedData.value
        .filter(item => !item.hasError)
        .map(item => ({
          rowNumber: item.rowNumber,
          monitoringTime: item.monitoringTime,
          stationCode: item.stationCode,
          stationName: item.monitoringPoint || item.stationCode, // 使用解析的监测点名称，否则用站码
          waterLevel: item.waterLevel,
          dataQuality: 1,
          collectionMethod: 'MANUAL',
          dataSource: 'EXCEL_IMPORT',
          remark: 'Excel批量导入'
        }))
    } else {
      // 流量数据格式（流量数据格式中没有监测点名称列）
      validData = parsedData.value
        .filter(item => !item.hasError)
        .map(item => ({
          rowNumber: item.rowNumber,
          monitoringTime: item.monitoringTime,
          stationCode: item.stationCode,
          stationName: item.stationCode, // 流量数据使用站码作为站点名称
          instantFlow: item.instantFlow,
          cumulativeFlow: item.cumulativeFlow
        }))
    }

    // 模拟进度更新
    const progressInterval = setInterval(() => {
      if (importProgress.value < 90) {
        importProgress.value += 10
      }
    }, 200)

    // 根据导入类型调用不同的API
    let response
    if (props.importType === 'waterQuality') {
      response = await importWaterQualityData(validData)
    } else if (props.importType === 'waterLevel') {
      response = await importWaterLevelData(validData)
    } else {
      response = await importFlowData(validData)
    }

    clearInterval(progressInterval)
    importProgress.value = 100
    importStatus.value = 'success'
    importResult.value = response

    // 根据导入结果显示不同的消息
    if (response.successRows === 0 && response.errorRows > 0) {
      // 全部失败 - 显示具体错误信息
      const firstError = response.errors && response.errors.length > 0
        ? response.errors[0].error
        : '数据导入失败，请检查数据格式'
      ElMessage.error(`数据导入失败：${firstError}`)
    } else if (response.successRows > 0 && response.errorRows > 0) {
      ElMessage.warning(`数据导入完成，成功${response.successRows}条，失败${response.errorRows}条`)
    } else {
      ElMessage.success('数据导入成功！')
    }
  } catch (error) {
    importProgress.value = 100
    console.error('导入失败:', error)

    // 处理业务错误（如导入失败）
    if (error.businessError && error.data) {
      importStatus.value = 'failed' // 设置为failed，表示导入失败但有结果显示
      importResult.value = error.data.data

      // 显示具体的错误信息
      const firstError = error.data.data.errors && error.data.data.errors.length > 0
        ? error.data.data.errors[0].error
        : '数据导入失败，请检查数据格式'
      ElMessage.error(`数据导入失败：${firstError}`)
    } else {
      importStatus.value = 'error'
      ElMessage.error(error.response?.data?.message || '导入失败，请重试')
    }
  }
}
</script>

<style lang="scss" scoped>
@use "@/assets/styles/index.scss" as *;

/**
 * ----------------------------------------
 * 主容器样式
 * ----------------------------------------
 */
.excel-importer {
  min-height: var(--panel-content-min-height);
}

/**
 * ----------------------------------------
 * 上传区域样式
 * ----------------------------------------
 */
.upload-section {
  text-align: center;
}

.upload-dragger {
  width: 100%;
}

.upload-dragger .el-upload {
  width: 100%;
}

.upload-dragger .el-upload-dragger {
  width: 100%;
  height: 200px;
  @include flex-center;
  flex-direction: column;
}

.file-info {
  margin-top: var(--spacing-large);
}

.file-details {
  @include flex-center;
}

.file-size {
  color: var(--text-tertiary);
  margin-left: var(--spacing-small);
}

/**
 * ----------------------------------------
 * 格式说明区域样式
 * ----------------------------------------
 */
.format-info .card-header {
  @include flex-start;
  font-weight: var(--font-weight-medium);
}

.format-details {
  line-height: 1.6;
}

.format-details ol {
  margin: var(--spacing-small) 0;
  padding-left: var(--spacing-large);
}

.example-data {
  background: var(--bg-disabled);
  padding: var(--spacing-small) var(--spacing-medium);
  border-radius: var(--border-radius-base);
  margin-top: var(--spacing-small);
}

.example-data code {
  font-family: var(--font-family-number);
  color: var(--warning-color);
}

/**
 * ----------------------------------------
 * 对话框底部样式
 * ----------------------------------------
 */
.dialog-footer {
  @include flex-end;
  gap: var(--spacing-medium);
}

/**
 * ----------------------------------------
 * 预览区域样式
 * ----------------------------------------
 */
.preview-section {
  max-height: var(--panel-height-default);
  overflow-y: auto;
}

.preview-header {
  margin-bottom: var(--spacing-base);
  @include flex-between;
}

.stats-info {
  @include flex-start;
}

.error-section {
  margin-bottom: var(--spacing-base);
}

.preview-table {
  border: 1px solid var(--border-light);
  border-radius: var(--border-radius-base);
}

/**
 * ----------------------------------------
 * 进度区域样式
 * ----------------------------------------
 */
.progress-section {
  text-align: center;
  padding: calc(var(--spacing-extra-large) * 2) var(--spacing-large);
}

.progress-content {
  max-width: 500px;
  margin: 0 auto;
}

.progress-text {
  margin-top: var(--spacing-base);
  font-size: var(--font-size-medium);
  color: var(--text-secondary);
}

.import-result {
  margin-top: var(--spacing-extra-large);
}

.result-stats {
  text-align: left;
}

.stat-item {
  @include flex-between;
  padding: var(--spacing-small) 0;
  border-bottom: 1px solid var(--border-light);
}

.stat-item:last-child {
  border-bottom: none;
}

.stat-item .label {
  font-weight: var(--font-weight-medium);
  color: var(--text-secondary);
}

.stat-item .value {
  font-weight: var(--font-weight-bold);
}

.stat-item .value.success {
  color: var(--success-color);
}

.stat-item .value.error {
  color: var(--danger-color);
}

.stat-item .value.warning {
  color: var(--warning-color);
}



/**
 * ----------------------------------------
 * 响应式设计
 * ----------------------------------------
 */
@include respond-to(sm) {
  .excel-importer {
    min-height: var(--panel-content-min-height);
  }

  .upload-dragger .el-upload-dragger {
    height: 150px;
  }

  .preview-section {
    max-height: 400px;
  }

  .progress-section {
    padding: var(--spacing-large) var(--spacing-sm);
  }
}
</style>
