<template>
  <div class="water-level-monitoring">
    <!-- 使用页面头部组件 -->
    <PageHeader title="水位监测" icon="fa-bar-chart" description="实时监测各站点水位数据，支持历史数据查询和分析" />

    <!-- 筛选区域 -->
    <div class="filter-section">
      <CommonSearch v-model="searchForm" :items="searchFields" :single-row="true" @search="handleSearch"
        @reset="handleReset">
        <template #actions>
          <!-- 时间范围快捷选择按钮组 -->
          <div class="time-range-buttons">
            <CustomButton :type="activeTimeRange === '7days' ? 'primary' : 'secondary'" size="small"
              @click="handleTimeRangeChange('7days')">
              7天
            </CustomButton>
            <CustomButton :type="activeTimeRange === '30days' ? 'primary' : 'secondary'" size="small"
              @click="handleTimeRangeChange('30days')">
              30天
            </CustomButton>
            <CustomButton :type="activeTimeRange === 'all' ? 'primary' : 'secondary'" size="small"
              @click="handleTimeRangeChange('all')">
              全部
            </CustomButton>
          </div>
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
      <MonitoringChart :chart-data="chartData" :chart-items="waterLevelMonitoringItems"
        :active-index="currentChartIndex" :loading="chartLoading" :has-searched="hasSearched"
        :has-station="!!searchForm.stationId && chartSearchTriggered" title="水位监测数据"
        @update:active-index="currentChartIndex = $event" @chart-initialized="handleChartInitialized"
        @chart-zoom="handleChartZoom" @chart-pan="handleChartPan" @chart-reset="handleChartReset" />
    </div>

    <!-- 数据表格区域 -->
    <div class="table-section">
      <div class="table-content">
        <CommonTable :data="tableData" :columns="tableColumns" :loading="tableLoading" :show-index="true"
          :show-actions="false" :show-pagination="false" />
        <!-- 添加自定义分页组件 -->
        <CustomPagination :current-page="pagination.currentPage" :page-size="pagination.pageSize"
          :total="pagination.total" :page-sizes="[10, 20, 50, 100]" @update:current-page="handleCurrentChange"
          @update:page-size="handleSizeChange" @current-change="handleCurrentChange" @size-change="handleSizeChange" />
      </div>
    </div>

    <!-- Excel导入组件 -->
    <ExcelImporter v-model:visible="showImportDialog" import-type="waterLevel"
      :template-columns="waterLevelImportColumns" @import-success="handleImportSuccess" />
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
  getWaterLevelMonitoringData,
  getAvailableMonitoringStations,
  getWaterLevelChartData,
  exportWaterLevelData
} from '@/api/monitoring'
import { formatLocalTimeForAPI, formatDateTime } from '@/utils/shared/common'

// ===================================
// 基础配置与常量定义
// ===================================

// 水位监测项目配置
const waterLevelMonitoringItems = [
  {
    code: 'WATER_LEVEL',
    name: '水位高度',
    unit: 'm',
    color: '#409EFF',
    icon: 'fa-bar-chart',
    description: '水位高度监测'
  }
]

// 搜索字段配置
const searchFields = ref([
  {
    prop: 'stationId',
    label: '监测站点',
    type: 'select',
    options: [],
    placeholder: '请选择监测站点',
    width: '240px',
    labelWidth: 'var(--form-label-width-standard)'
  },
  {
    prop: 'timeRange',
    label: '监测时间',
    type: 'datetimerange',
    startPlaceholder: '请选择开始时间',
    endPlaceholder: '请选择结束时间',
    showDuration: true,
    width: '405px',
    labelWidth: 'var(--form-label-width-standard)'
  }
])

// 水位导入列配置
const waterLevelImportColumns = [
  { prop: 'rowNumber', label: '序号', width: 60 },
  { prop: 'monitoringTime', label: '监测时间', width: 160 },
  { prop: 'monitoringPoint', label: '监测点', width: 120 },
  { prop: 'stationCode', label: '站码', width: 100 },
  { prop: 'waterLevel', label: '监测值(m)', width: 120 }
]

// 表格列配置
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
    width: 120,
    showOverflowTooltip: true
  },
  {
    prop: 'waterLevel',
    label: '水位高度(m)',
    width: 130,
    formatter: (row) => {
      const value = row.waterLevel
      return value !== null && value !== undefined ? `${Number(value).toFixed(3)}` : '-'
    }
  }
]

// ===================================
// 响应式数据与状态管理
// ===================================

// 搜索表单状态
const searchForm = ref({
  stationId: null,
  timeRange: []
})

// 搜索状态管理
const hasSearched = ref(false) // 是否已执行过搜索
const chartSearchTriggered = ref(false) // 是否已触发图表搜索（通过搜索按钮）

// 时间范围状态管理
const activeTimeRange = ref('7days') // 当前激活的时间范围
const quickSearchTimeRange = ref(null) // 快捷按钮搜索时使用的时间范围

// 数据状态管理
const tableData = ref([])
const stationOptions = ref([])
const chartData = ref({ labels: [], values: [] })

// 加载状态管理
const tableLoading = ref(false)
const chartLoading = ref(false)
const exportLoading = ref(false)
const showImportDialog = ref(false)

// 图表状态管理
const currentChartIndex = ref(0)

// 分页配置
const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 0
})

// 监听时间选择器变化，重置快捷按钮状态
watch(() => searchForm.value.timeRange, (newTimeRange) => {
  // 如果时间选择器有值且不是通过快捷按钮设置的，则重置快捷按钮状态
  if (newTimeRange && newTimeRange.length === 2 && !quickSearchTimeRange.value) {
    activeTimeRange.value = null
  }
  // 清除快捷搜索标记
  quickSearchTimeRange.value = null
}, { deep: true })

// ===================================
// 辅助工具函数
// ===================================

// 使用统一的时间格式化函数（从 common.js 导入）

// 通用的时间范围参数处理函数
const processTimeRangeParams = (timeRange) => {
  const params = {};

  if (timeRange && timeRange.length === 2) {
    params.startTime = formatLocalTimeForAPI(timeRange[0]);
    params.endTime = formatLocalTimeForAPI(timeRange[1]);
  }

  return params;
};

// 防抖版本的图表加载函数
const debouncedLoadChart = (() => {
  let timer;
  return () => {
    clearTimeout(timer);
    timer = setTimeout(() => loadChartData(), 300);
  };
})();

// 计算快捷时间范围
const calculateQuickTimeRange = (timeRangeType) => {
  const now = new Date()
  let startTime, endTime

  switch (timeRangeType) {
    case '7days':
      // 最近7天
      startTime = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000)
      endTime = now
      break
    case '30days':
      // 最近30天
      startTime = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000)
      endTime = now
      break
    case 'all':
      // 全部数据，返回空数组
      return []
    default:
      return []
  }

  return [startTime, endTime]
}

// 获取当前有效的时间范围
const getCurrentTimeRange = () => {
  // 如果是快捷搜索，使用快捷搜索的时间范围
  if (quickSearchTimeRange.value) {
    return quickSearchTimeRange.value
  }
  // 否则使用时间选择器的值
  return searchForm.value.timeRange
}

// ===================================
// 数据加载函数
// ===================================

// 加载监测站点
const loadStations = async () => {
  try {
    // 只获取水位监测站点（H类型）
    const response = await getAvailableMonitoringStations({ monitoringItemCode: 'H' })

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

// 加载表格数据
const loadTableData = async () => {
  tableLoading.value = true
  try {
    // 获取当前有效的时间范围
    const currentTimeRange = getCurrentTimeRange()
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      stationId: searchForm.value.stationId,
      // 使用当前有效的时间范围参数
      ...processTimeRangeParams(currentTimeRange)
    }

    const response = await getWaterLevelMonitoringData(params)
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

// 加载图表数据
const loadChartData = async () => {
  if (!searchForm.value.stationId || !hasSearched.value || !chartSearchTriggered.value) return;

  chartLoading.value = true;

  try {
    // 获取当前有效的时间范围
    const currentTimeRange = getCurrentTimeRange()
    // 构建API参数
    const params = {
      stationId: searchForm.value.stationId,
      // 使用当前有效的时间范围参数
      ...processTimeRangeParams(currentTimeRange)
    };

    // 调用API获取数据
    const response = await getWaterLevelChartData(params);
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

// ===================================
// 事件处理函数
// ===================================

// 搜索相关事件处理
const handleSearch = () => {
  pagination.currentPage = 1
  loadTableData()

  // 标记已执行搜索
  hasSearched.value = true

  // 只有选择了站点才加载图表数据并标记触发图表搜索
  if (searchForm.value.stationId) {
    chartSearchTriggered.value = true
    loadChartData()
  } else {
    chartSearchTriggered.value = false
  }
}

// 处理快捷按钮搜索
const handleQuickSearch = (timeRange) => {
  // 设置快捷搜索标记，防止监听器重置按钮状态
  quickSearchTimeRange.value = timeRange

  pagination.currentPage = 1
  loadTableData()

  // 标记已执行搜索
  hasSearched.value = true

  // 只有在选择了站点时才触发图表搜索
  if (searchForm.value.stationId) {
    chartSearchTriggered.value = true
    loadChartData()
  } else {
    chartSearchTriggered.value = false
  }
}

// 时间范围切换处理
const handleTimeRangeChange = (timeRangeType) => {
  activeTimeRange.value = timeRangeType

  // 计算时间范围但不设置到时间选择器
  const timeRange = calculateQuickTimeRange(timeRangeType)

  // 自动触发快捷搜索
  handleQuickSearch(timeRange)
}

const handleReset = () => {
  searchForm.value = {
    stationId: null,
    timeRange: []
  }
  // 重置搜索状态
  hasSearched.value = false
  chartSearchTriggered.value = false
  // 重置时间范围状态为7天激活，但不设置时间选择器值
  activeTimeRange.value = '7days'
  quickSearchTimeRange.value = null
  pagination.currentPage = 1
  loadTableData()
  // 重置图表数据
  chartData.value = { labels: [], values: [] }
}

// 分页相关事件处理
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  loadTableData()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadTableData()
}

// 图表相关事件处理
const handleChartInitialized = () => {
  // 如果已选择站点且已搜索并触发了图表搜索，加载图表数据
  if (searchForm.value.stationId && hasSearched.value && chartSearchTriggered.value) {
    loadChartData()
  }
}

const handleChartZoom = (event) => {
  // 这里可以添加额外的缩放处理逻辑
}

const handleChartPan = (event) => {
  // 这里可以添加额外的平移处理逻辑
}

const handleChartReset = (event) => {
  // 这里可以添加额外的重置处理逻辑
}

// 导出相关事件处理
const handleExport = async () => {
  exportLoading.value = true
  try {
    const params = {
      ...searchForm.value,
      format: 'excel'
    }

    const response = await exportWaterLevelData(params)

    // 创建下载链接
    const blob = new Blob([response], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `水位监测数据_${new Date().toISOString().slice(0, 10)}.xlsx`
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

// 导入相关事件处理
const handleImportSuccess = () => {
  ElMessage.success('数据导入成功！')
  // 刷新表格数据
  loadTableData()
  // 刷新图表数据
  if (searchForm.value.stationId && hasSearched.value && chartSearchTriggered.value) {
    debouncedLoadChart()
  }
}

// ===================================
// 生命周期钩子
// ===================================

// 页面初始化
onMounted(async () => {
  try {
    // 加载站点数据
    await loadStations()

    // 设置默认7天时间范围并加载对应数据
    handleTimeRangeChange('7days')
  } catch (error) {
    console.error('页面初始化失败:', error)
    ElMessage.error('页面初始化失败，请刷新重试')
  }
})
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

// ============================================
// 页面布局样式
// ============================================
.water-level-monitoring {

  // ============================================
  // 页面内容区域样式
  // ============================================
  .filter-section,
  .chart-carousel-section,
  .table-section {
    margin-bottom: var(--spacing-large);
  }

  // 时间范围按钮组样式
  .time-range-buttons {
    display: flex;
    gap: var(--spacing-small);
    margin-right: var(--spacing-medium);
  }

  // 表格区域样式
  .table-section {
    background: var(--bg-primary);
    border-radius: var(--border-radius-md);
    box-shadow: var(--shadow-light);
    border: 1px solid var(--border-color-light);
    overflow: hidden;

    .table-content {
      padding: var(--spacing-md);
      padding-bottom: 20px;
    }

    // 响应式适配
    @include respond-to(md) {
      .table-content {
        padding: var(--spacing-md);
        padding-bottom: 20px;
      }
    }

    @include respond-to(sm) {
      .table-content {
        padding: var(--spacing-sm);
        padding-bottom: 20px;
      }
    }
  }
}
</style>
