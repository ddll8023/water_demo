<template>
  <div class="flow-monitoring">
    <!-- 使用页面头部组件 -->
    <PageHeader title="流量检测" icon="fa-tint" description="实时监测各站点流量数据，支持历史数据查询和分析" />


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

    <!-- 图表轮播区域 -->
    <div class="chart-carousel-section">
      <MonitoringChart :chart-data="chartData" :chart-items="flowMonitoringItems" :active-index="currentChartIndex"
        :loading="chartLoading" :has-searched="hasSearched"
        :has-station="!!searchForm.stationId && chartSearchTriggered" title="流量监测数据"
        @update:active-index="currentChartIndex = $event" @chart-initialized="handleChartInitialized" />
    </div>

    <!-- 数据表格区域 -->
    <div class="table-section">
      <div class="table-content">
        <CommonTable :data="tableData" :columns="tableColumns" :loading="tableLoading" :show-selection="false"
          :show-index="true" :show-actions="false" />

        <!-- 使用自定义分页组件 -->
        <CustomPagination :current-page="pagination.currentPage" :page-size="pagination.pageSize"
          :total="pagination.total" :page-sizes="[10, 20, 50, 100]" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </div>

    <!-- Excel导入组件 -->
    <ExcelImporter v-model:visible="showImportDialog" import-type="flow" :template-columns="flowImportColumns"
      @import-success="handleImportSuccess" />
  </div>
</template>

<script setup>
// ============================================
// 依赖导入
// ============================================
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import CommonSearch from '@/components/Common/CommonSearch.vue'
import CommonTable from '@/components/Common/CommonTable.vue'
import CustomButton from '@/components/Common/CustomButton.vue'
import CustomPagination from '@/components/Common/CustomPagination.vue'
import ExcelImporter from '@/components/Common/ExcelImporter.vue'
import MonitoringChart from '@/components/Chart/MonitoringChart.vue'
import PageHeader from '@/components/Common/PageHeader.vue'
import {
  getFlowMonitoringData,
  getAvailableMonitoringStations,
  getFlowChartData,
  exportFlowData
} from '@/api/monitoring'
import { formatLocalTimeForAPI, formatDateTime } from '@/utils/shared/common'

// ============================================
// 常量定义
// ============================================
// 流量监测项目配置
const flowMonitoringItems = [
  {
    code: 'FLOW_RATE',
    name: '瞬时流量',
    unit: 'm³/s',
    color: '#409EFF',
    icon: 'fa-tint',
    description: '瞬时流量监测'
  },
  {
    code: 'CUMULATIVE_FLOW',
    name: '累计流量',
    unit: 'm³',
    color: '#67C23A',
    icon: 'fa-database',
    description: '累计流量监测'
  }
]

// 流量导入列配置
const flowImportColumns = [
  { prop: 'rowNumber', label: '序号', width: 60 },
  { prop: 'monitoringTime', label: '监测时间', width: 160 },
  { prop: 'monitoringPoint', label: '监测点', width: 120 },
  { prop: 'stationCode', label: '站码', width: 100 },
  { prop: 'flowRate', label: '瞬时流量(m³/s)', width: 130 },
  { prop: 'cumulativeFlow', label: '累计流量(m³)', width: 130 }
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
    prop: 'flowRate',
    label: '瞬时流量',
    width: 130,
    formatter: (row) => {
      if (row.flowRate === null || row.flowRate === undefined) return '-'
      return `${Number(row.flowRate).toFixed(3)} m³/s`
    }
  },
  {
    prop: 'cumulativeFlow',
    label: '累计流量',
    width: 130,
    formatter: (row) => {
      if (row.cumulativeFlow === null || row.cumulativeFlow === undefined) return '-'
      return `${Number(row.cumulativeFlow).toFixed(3)} m³`
    }
  },
  {
    prop: 'dataQualityText',
    label: '数据质量',
    width: 100,
    formatter: (row) => {
      return row.dataQualityText || '-'
    }
  },
  {
    prop: 'collectionMethodText',
    label: '采集方式',
    width: 100,
    formatter: (row) => {
      return row.collectionMethodText || '-'
    }
  }
]

// ============================================
// 响应式数据定义
// ============================================
// 搜索表单
const searchForm = ref({
  stationId: null,
  timeRange: []
})



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

// 搜索状态管理
const hasSearched = ref(false) // 是否已执行过搜索
const chartSearchTriggered = ref(false) // 是否已触发图表搜索（通过搜索按钮）

// 时间范围状态管理
const activeTimeRange = ref('7days') // 当前激活的时间范围
const quickSearchTimeRange = ref(null) // 快捷按钮搜索时使用的时间范围

// 表格数据
const tableData = ref([])
const tableLoading = ref(false)

// 图表数据
const chartData = ref({ labels: [], values: [] })
const chartLoading = ref(false)
const currentChartIndex = ref(0)

// 监听图表类型变化
watch(() => currentChartIndex.value, (newIndex) => {
  // 如果已经搜索并选择了站点，切换图表类型时重新加载数据
  if (searchForm.value.stationId && hasSearched.value && chartSearchTriggered.value) {
    loadChartData()
  }
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

// 站点数据
const stationOptions = ref([])

// 导入导出
const exportLoading = ref(false)
const showImportDialog = ref(false)

// 分页配置
const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 0
})

// ============================================
// 生命周期钩子
// ============================================
onMounted(async () => {
  try {
    // 加载站点数据
    await loadStations()

    // 设置默认7天时间范围并加载对应数据
    handleTimeRangeChange('7days')
  } catch (error) {
    ElMessage.error('页面初始化失败，请刷新重试')
  }
})

// ============================================
// 数据加载方法
// ============================================
// 加载监测站点
const loadStations = async () => {
  try {
    // 只获取流量监测站点（Q类型）
    const response = await getAvailableMonitoringStations({ monitoringItemCode: 'Q' })

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

    const response = await getFlowMonitoringData(params)
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
  if (!searchForm.value.stationId || !hasSearched.value || !chartSearchTriggered.value) {
    return;
  }

  chartLoading.value = true;

  try {
    // 获取当前有效的时间范围
    const currentTimeRange = getCurrentTimeRange()

    // 构建API参数
    const item = flowMonitoringItems[currentChartIndex.value];
    const params = {
      stationId: searchForm.value.stationId,
      dataType: item.code === 'FLOW_RATE' ? 'flowRate' : 'cumulativeFlow',
      // 使用当前有效的时间范围参数
      ...processTimeRangeParams(currentTimeRange)
    };

    // 调用API获取数据
    const response = await getFlowChartData(params);
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

// ============================================
// 搜索和分页事件处理
// ============================================
// 处理搜索
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

// 处理重置
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

// 处理页面大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  loadTableData()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadTableData()
}

// ============================================
// 图表事件处理
// ============================================
// 图表初始化完成事件处理
const handleChartInitialized = () => {
  // 仅在已明确触发图表搜索时加载图表数据
  if (searchForm.value.stationId && hasSearched.value && chartSearchTriggered.value) {
    loadChartData()
  }
}

// ============================================
// 数据导入导出处理
// ============================================
// 导出数据
const handleExport = async () => {
  exportLoading.value = true
  try {
    // 处理搜索参数 - 像loadTableData一样处理timeRange参数
    const { timeRange, ...otherParams } = searchForm.value
    const params = {
      ...otherParams,
      ...processTimeRangeParams(timeRange),
      format: 'excel'
    }

    const response = await exportFlowData(params)

    // 根据响应类型处理
    if (response instanceof Blob) {
      // 获取文件名（优先使用响应头，否则使用默认名称）
      const fileName = `流量监测数据_${new Date().toISOString().slice(0, 10)}.xlsx`;

      // 创建下载链接
      const blob = new Blob([response], {
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = fileName
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)

      ElMessage.success('数据导出成功')
    } else {
      // 如果响应不是Blob对象，可能是服务器返回了错误信息
      ElMessage.error('导出失败：服务器返回格式错误')
    }
  } catch (error) {
    // 捕获并显示具体错误
    let errorMsg = '导出数据失败';
    if (error.response && error.response.data) {
      // 尝试读取服务器返回的错误信息
      const reader = new FileReader();
      reader.onload = () => {
        try {
          const errorData = JSON.parse(reader.result);
          errorMsg = errorData.message || errorMsg;
          ElMessage.error(errorMsg);
        } catch (e) {
          ElMessage.error(errorMsg);
        }
      };
      reader.onerror = () => ElMessage.error(errorMsg);
      reader.readAsText(error.response.data);
    } else {
      ElMessage.error(`${errorMsg}: ${error.message || '未知错误'}`);
    }
  } finally {
    exportLoading.value = false
  }
}

// 导入成功处理
const handleImportSuccess = () => {
  ElMessage.success('数据导入成功！')
  // 刷新表格数据
  loadTableData()
  // 刷新图表数据
  if (searchForm.value.stationId && hasSearched.value && chartSearchTriggered.value) {
    loadChartData()
  }
}

// ============================================
// 工具函数
// ============================================
// 处理时间范围参数
const processTimeRangeParams = (timeRange) => {
  if (!timeRange || !Array.isArray(timeRange) || timeRange.length !== 2) {
    return {}
  }

  return {
    startTime: formatLocalTimeForAPI(timeRange[0]),
    endTime: formatLocalTimeForAPI(timeRange[1])
  }
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

// 时间范围切换处理
const handleTimeRangeChange = (timeRangeType) => {
  activeTimeRange.value = timeRangeType

  // 计算时间范围但不设置到时间选择器
  const timeRange = calculateQuickTimeRange(timeRangeType)

  // 自动触发快捷搜索
  handleQuickSearch(timeRange)
}
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

// ============================================
// 页面布局样式
// ============================================
.flow-monitoring {

  // ============================================
  // 页面内容区域样式
  // ============================================
  .filter-section,
  .chart-carousel-section,
  .table-section {
    margin-bottom: var(--spacing-large);
  }

  // ============================================
  // 时间范围按钮组样式
  // ============================================
  .time-range-buttons {
    display: flex;
    gap: var(--spacing-small);
    margin-right: var(--spacing-medium);
  }

  // ============================================
  // 表格区域样式
  // ============================================
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
