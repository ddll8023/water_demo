<template>
  <div class="water-quality-monitoring">
    <!-- 使用页面头部组件 -->
    <PageHeader title="水质检测" icon="fa-flask" description="实时监测各站点水质数据，支持历史数据查询和分析" />

    <!-- 筛选区域 -->
    <div class="filter-section">
      <CommonSearch v-model="searchForm" :items="searchFields" :single-row="true" @search="handleSearch"
        @reset="handleReset">
        <template #actions>
          <CustomButton type="secondary" @click="showImportDialog = true" v-permission="'business:operate'">
            <i class="fa fa-upload"></i>
            导入数据
          </CustomButton>
          <CustomButton type="primary" @click="handleExport" :loading="exportLoading">
            <i class="fa fa-download"></i>
            导出数据
          </CustomButton>
        </template>
      </CommonSearch>
    </div>

    <!-- 图表区域 -->
    <div class="chart-carousel-section">
      <MonitoringChart :chart-data="chartData" :chart-items="waterQualityItems" :active-index="currentChartIndex"
        :loading="chartLoading" :has-searched="hasSearched"
        :has-station="!!searchForm.stationId && chartSearchTriggered" title="水质监测数据"
        @update:active-index="currentChartIndex = $event" @chart-initialized="handleChartInitialized"
        @chart-zoom="handleChartZoom" @chart-pan="handleChartPan" @chart-reset="handleChartReset" />
    </div>

    <!-- 数据表格区域 -->
    <div class="table-section">
      <CommonTable :data="tableData" :columns="tableColumns" :loading="tableLoading" :show-selection="false"
        :show-index="true" :show-actions="false" />

      <!-- 使用自定义分页组件 -->
      <CustomPagination v-model:current-page="pagination.currentPage" v-model:page-size="pagination.pageSize"
        :total="pagination.total" :page-sizes="[10, 20, 50, 100]" @size-change="handleSizeChange"
        @current-change="handleCurrentChange" layout="total, sizes, prev, pager, next, jumper" />
    </div>

    <!-- Excel导入组件 -->
    <ExcelImporter v-model:visible="showImportDialog" import-type="waterQuality"
      :template-columns="waterQualityImportColumns" @import-success="handleImportSuccess" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import CommonSearch from '@/components/Common/CommonSearch.vue'
import CommonTable from '@/components/Common/CommonTable.vue'
import CustomButton from '@/components/Common/CustomButton.vue'
import ExcelImporter from '@/components/Common/ExcelImporter.vue'
import MonitoringChart from '@/components/Chart/MonitoringChart.vue'
import PageHeader from '@/components/Common/PageHeader.vue'
import CustomPagination from '@/components/Common/CustomPagination.vue'
import {
  getWaterQualityMonitoringData,
  getAvailableMonitoringStations,
  getWaterQualityChartData,
  exportWaterQualityData
} from '@/api/monitoring'
import { formatLocalTimeForAPI, formatDateTime } from '@/utils/shared/common'

// ======================================================
// 基础数据与配置
// ======================================================

/**
 * 8种水质监测项目配置
 */
const waterQualityItems = [
  {
    code: 'WT',
    name: '水温',
    unit: '℃',
    color: '#FF6B6B',
    icon: 'fa-thermometer-half',
    description: '水温监测'
  },
  {
    code: 'TU',
    name: '浊度',
    unit: 'NTU',
    color: '#4ECDC4',
    icon: 'fa-eye',
    description: '浊度监测'
  },
  {
    code: 'PH',
    name: 'PH值',
    unit: '',
    color: '#45B7D1',
    icon: 'fa-flask',
    description: 'PH值监测'
  },
  {
    code: 'EC',
    name: '电导率',
    unit: 'uS/cm',
    color: '#96CEB4',
    icon: 'fa-bolt',
    description: '电导率监测'
  },
  {
    code: 'DO',
    name: '溶解氧',
    unit: 'mg/L',
    color: '#FFEAA7',
    icon: 'fa-tint',
    description: '溶解氧监测'
  },
  {
    code: 'AN',
    name: '氨氮',
    unit: 'mg/L',
    color: '#DDA0DD',
    icon: 'fa-leaf',
    description: '氨氮监测'
  },
  {
    code: 'COD',
    name: '化学需氧量',
    unit: 'mg/L',
    color: '#98D8C8',
    icon: 'fa-cog',
    description: '化学需氧量监测'
  },
  {
    code: 'RC',
    name: '余氯',
    unit: 'mg/L',
    color: '#F7DC6F',
    icon: 'fa-tint',
    description: '余氯监测'
  }
]

/**
 * 搜索字段配置
 */
const searchFields = ref([
  {
    prop: 'stationId',
    label: '监测站点',
    type: 'select',
    options: [],
    placeholder: '请选择监测站点',
    span: 4,
    labelWidth: '60px'
  },
  {
    prop: 'timeRange',
    label: '监测时间',
    type: 'datetimerange',
    startPlaceholder: '请选择开始时间',
    endPlaceholder: '请选择结束时间',
    showDuration: true,
    span: 8,
    labelWidth: '60px'
  }
])

/**
 * 水质导入列配置
 */
const waterQualityImportColumns = [
  { prop: 'rowNumber', label: '序号', width: 60 },
  { prop: 'monitoringTime', label: '监测时间', width: 160 },
  { prop: 'monitoringPoint', label: '监测点', width: 120 },
  { prop: 'stationCode', label: '站码', width: 100 },
  { prop: 'waterTemperature', label: '水温(℃)', width: 100 },
  { prop: 'turbidity', label: '浊度(NTU)', width: 100 },
  { prop: 'phValue', label: 'PH', width: 80 },
  { prop: 'conductivity', label: '电导率(uS/cm)', width: 120 },
  { prop: 'dissolvedOxygen', label: '溶解氧(mg/L)', width: 120 },
  { prop: 'ammoniaNitrogen', label: '氨氮(mg/L)', width: 110 },
  { prop: 'codValue', label: '化学需氧量(mg/L)', width: 140 },
  { prop: 'residualChlorine', label: '余氯(mg/L)', width: 110 }
]

/**
 * 表格列配置
 */
const tableColumns = [
  {
    prop: 'monitoringTime',
    label: '监测时间',
    width: 180,
    sortable: true,
    formatter: (row) => formatDateTime(row.monitoringTime)
  },
  {
    prop: 'stationName',
    label: '监测站点',
    width: 140,
    showOverflowTooltip: true,
    formatter: (row) => {
      return row.stationName || '-'
    }
  },
  {
    prop: 'stationCode',
    label: '站码',
    width: 110,
    showOverflowTooltip: true
  },
  {
    prop: 'waterTemperature',
    label: '水温(℃)',
    width: 100,
    formatter: (row) => {
      const value = row.waterTemperature
      return value !== null && value !== undefined ? `${Number(value).toFixed(3)}` : '-'
    }
  },
  {
    prop: 'turbidity',
    label: '浊度(NTU)',
    width: 110,
    formatter: (row) => {
      const value = row.turbidity
      return value !== null && value !== undefined ? `${Number(value).toFixed(3)}` : '-'
    }
  },
  {
    prop: 'phValue',
    label: 'PH值',
    width: 100,
    formatter: (row) => {
      const value = row.phValue
      return value !== null && value !== undefined ? `${Number(value).toFixed(3)}` : '-'
    }
  },
  {
    prop: 'conductivity',
    label: '电导率(uS/cm)',
    width: 130,
    formatter: (row) => {
      const value = row.conductivity
      return value !== null && value !== undefined ? `${Number(value).toFixed(3)}` : '-'
    }
  },
  {
    prop: 'dissolvedOxygen',
    label: '溶解氧(mg/L)',
    width: 130,
    formatter: (row) => {
      const value = row.dissolvedOxygen
      return value !== null && value !== undefined ? `${Number(value).toFixed(3)}` : '-'
    }
  },
  {
    prop: 'ammoniaNitrogen',
    label: '氨氮(mg/L)',
    width: 120,
    formatter: (row) => {
      const value = row.ammoniaNitrogen
      return value !== null && value !== undefined ? `${Number(value).toFixed(3)}` : '-'
    }
  },
  {
    prop: 'codValue',
    label: '化学需氧量(mg/L)',
    width: 150,
    formatter: (row) => {
      const value = row.codValue
      return value !== null && value !== undefined ? `${Number(value).toFixed(3)}` : '-'
    }
  },
  {
    prop: 'residualChlorine',
    label: '余氯(mg/L)',
    width: 120,
    formatter: (row) => {
      const value = row.residualChlorine
      return value !== null && value !== undefined ? `${Number(value).toFixed(3)}` : '-'
    }
  }
]

// ======================================================
// 响应式数据与状态
// ======================================================

/**
 * 搜索表单数据
 */
const searchForm = ref({
  stationId: null,
  timeRange: []
})

/**
 * 搜索状态标记
 */
const hasSearched = ref(false) // 是否已执行过搜索
const chartSearchTriggered = ref(false) // 是否已触发图表搜索（通过搜索按钮）

/**
 * 表格数据与状态
 */
const tableData = ref([])
const tableLoading = ref(false)

/**
 * 图表相关状态
 */
const chartLoading = ref(false)
const currentChartIndex = ref(0)
const chartData = ref({ labels: [], values: [] })

/**
 * 其他UI状态
 */
const exportLoading = ref(false)
const showImportDialog = ref(false)
const stationOptions = ref([])

/**
 * 分页配置
 */
const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 0
})

// ======================================================
// 工具函数
// ======================================================

// 使用统一的时间格式化函数（从 common.js 导入）

/**
 * 通用的时间范围参数处理函数
 * @param {Array} timeRange - 时间范围数组
 * @param {Object} options - 配置选项
 * @returns {Object} 处理后的参数对象
 */
const processTimeRangeParams = (timeRange, options = {}) => {
  const params = {};

  if (timeRange && timeRange.length === 2) {
    params.startTime = formatLocalTimeForAPI(timeRange[0]);
    params.endTime = formatLocalTimeForAPI(timeRange[1]);
  }

  return params;
};

// ======================================================
// 数据加载函数
// ======================================================

/**
 * 加载监测站点
 */
const loadStations = async () => {
  try {
    // 只获取水质监测站点（WQ类型）
    const response = await getAvailableMonitoringStations({ monitoringItemCode: 'WQ' })

    if (response && Array.isArray(response)) {
      stationOptions.value = response.map(station => ({
        label: station.name,
        value: station.id
      }))

      // 更新搜索字段的选项
      const stationField = searchFields.value.find(field => field.prop === 'stationId')
      if (stationField) {
        stationField.options = stationOptions.value
      }
    } else {
      ElMessage.warning('监测站点数据格式不正确')
    }
  } catch (error) {
    console.error('加载监测站点失败:', error)
    ElMessage.error(`加载监测站点失败: ${error.message || '未知错误'}`)
  }
}

/**
 * 加载表格数据
 */
const loadTableData = async () => {
  tableLoading.value = true
  try {
    // 处理搜索参数
    const { timeRange, ...otherParams } = searchForm.value
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      ...otherParams,
      // 使用通用函数处理时间范围参数
      ...processTimeRangeParams(timeRange)
    }

    const response = await getWaterQualityMonitoringData(params)
    if (response) {
      // 直接使用API返回的数据
      tableData.value = response.items || []
      pagination.total = response.total || 0
    }
  } catch (error) {
    console.error('加载表格数据失败:', error)
    ElMessage.error('加载表格数据失败')
  } finally {
    tableLoading.value = false
  }
}

/**
 * 图表数据加载函数
 */
const loadChartData = async () => {
  if (!searchForm.value.stationId || !hasSearched.value || !chartSearchTriggered.value) return;

  chartLoading.value = true;

  try {
    // 构建API参数
    const item = waterQualityItems[currentChartIndex.value];
    const params = {
      stationId: searchForm.value.stationId,
      monitoringItemCode: item.code,
      // 使用通用函数处理时间范围参数
      ...processTimeRangeParams(searchForm.value.timeRange)
    };

    // 调用API获取数据
    const response = await getWaterQualityChartData(params);
    if (!response) {
      chartLoading.value = false;
      return;
    }

    // 更新图表数据
    chartData.value = {
      labels: response.labels || [],
      values: response.values || []
    };
  } catch (error) {
    console.error('加载图表数据失败:', error);
    ElMessage.error('加载图表数据失败');
  } finally {
    // 重置加载状态
    chartLoading.value = false;
  }
}

// 添加监听图表类型变化
watch(() => currentChartIndex.value, (newIndex) => {
  // 如果已经搜索并选择了站点，切换图表类型时重新加载数据
  if (searchForm.value.stationId && hasSearched.value && chartSearchTriggered.value) {
    loadChartData()
  }
})

// ======================================================
// 图表相关处理函数
// ======================================================

/**
 * 图表初始化完成事件处理
 */
const handleChartInitialized = () => {
  // 仅在已明确触发图表搜索时加载图表数据
  if (searchForm.value.stationId && hasSearched.value && chartSearchTriggered.value) {
    loadChartData()
  }
}

/**
 * 图表事件处理函数（简化版）
 */
const handleChartZoom = () => { }
const handleChartPan = () => { }
const handleChartReset = () => { }

// ======================================================
// 用户交互事件处理函数
// ======================================================

/**
 * 搜索按钮点击事件处理
 */
const handleSearch = () => {
  pagination.currentPage = 1
  loadTableData()

  // 标记已执行搜索
  hasSearched.value = true

  // 只有在点击搜索按钮并选择了站点时才触发图表搜索
  if (searchForm.value.stationId) {
    chartSearchTriggered.value = true
    loadChartData()
  } else {
    chartSearchTriggered.value = false
  }
}

/**
 * 重置按钮点击事件处理
 */
const handleReset = () => {
  searchForm.value = {
    stationId: null,
    timeRange: []
  }
  // 重置搜索状态
  hasSearched.value = false
  chartSearchTriggered.value = false
  pagination.currentPage = 1
  loadTableData()
  // 重置图表数据
  chartData.value = { labels: [], values: [] }
}

/**
 * 表格分页大小变化事件处理
 * @param {number} size - 新的分页大小
 */
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  loadTableData()
}

/**
 * 表格分页页码变化事件处理
 * @param {number} page - 新的页码
 */
const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadTableData()
}

/**
 * 导出数据按钮点击事件处理
 */
const handleExport = async () => {
  exportLoading.value = true
  try {
    const params = {
      ...searchForm.value,
      format: 'excel'
    }

    const response = await exportWaterQualityData(params)

    // 创建下载链接
    const blob = new Blob([response], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `水质监测数据_${new Date().toISOString().slice(0, 10)}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

    ElMessage.success('数据导出成功')
  } catch (error) {
    console.error('导出数据失败:', error)
    ElMessage.error('导出数据失败')
  } finally {
    exportLoading.value = false
  }
}

/**
 * 导入成功事件处理
 */
const handleImportSuccess = () => {
  ElMessage.success('数据导入成功！')
  // 刷新表格数据
  loadTableData()
  // 刷新图表数据
  if (searchForm.value.stationId && hasSearched.value && chartSearchTriggered.value) {
    loadChartData()
  }
  // 整页刷新
  window.location.reload()
}

// ======================================================
// 生命周期钩子
// ======================================================

/**
 * 组件挂载完成后执行
 */
onMounted(async () => {
  try {
    // 并行加载站点和表格数据
    await Promise.all([
      loadStations(),
      loadTableData()
    ])
  } catch (error) {
    console.error('页面初始化失败:', error)
    ElMessage.error('页面初始化失败，请刷新重试')
  }
})
</script>

<style scoped lang="scss">
/**
 * 水质监测页面样式
 */
.water-quality-monitoring {

  /**
   * 页面内容区域样式
   */
  .filter-section,
  .chart-carousel-section,
  .table-section {
    margin-bottom: 20px;
  }
}
</style>
