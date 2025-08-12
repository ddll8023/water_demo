<template>
    <div class="rainfall-monitoring">
        <!-- 使用页面头部组件 -->
        <PageHeader title="雨情监测" icon="fa-umbrella" description="实时监测各站点降雨数据，支持历史数据查询和分析" />

        <!-- 筛选区域 -->
        <div class="filter-section">
            <CommonSearch v-model="searchForm" :items="searchFields" :single-row="true" @search="handleSearch"
                @reset="handleReset">
                <template #actions>
                    <!-- 时间模式切换按钮组 -->
                    <div class="time-mode-buttons">
                        <CustomButton :type="activeTimeRange === 'hourly' ? 'primary' : 'secondary'" size="small"
                            @click="handleTimeRangeChange('hourly')">
                            时段雨量
                        </CustomButton>
                        <CustomButton :type="activeTimeRange === 'daily' ? 'primary' : 'secondary'" size="small"
                            @click="handleTimeRangeChange('daily')">
                            日雨量
                        </CustomButton>
                        <CustomButton :type="activeTimeRange === 'realtime' ? 'primary' : 'secondary'" size="small"
                            @click="handleTimeRangeChange('realtime')">
                            实时雨量
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
            <MonitoringChart :chart-data="chartData" :chart-items="rainfallMonitoringItems"
                :active-index="currentChartIndex" :loading="chartLoading" :has-searched="hasSearched"
                :has-station="!!searchForm.stationId && chartSearchTriggered" title="雨情监测数据"
                @update:active-index="currentChartIndex = $event" @chart-initialized="handleChartInitialized" />
        </div>

        <!-- 数据表格区域 -->
        <div class="table-section">
            <div class="table-section__header">
                <div class="table-section__unit">
                    单位: 时段雨量(mm) 累计雨量(mm)
                </div>
            </div>
            <CommonTable :data="tableData" :columns="tableColumns" :loading="tableLoading" :show-index="true"
                :show-actions="false" />

            <!-- 使用自定义分页组件 -->
            <CustomPagination :current-page="pagination.currentPage" :page-size="pagination.pageSize"
                :total="pagination.total" :page-sizes="[10, 20, 50, 100]" @size-change="handleSizeChange"
                @current-change="handleCurrentChange" />
        </div>

        <!-- Excel导入组件 -->
        <ExcelImporter v-model:visible="showImportDialog" import-type="rainfall"
            :template-columns="rainfallImportColumns" @import-success="handleImportSuccess" />
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import CommonSearch from '@/components/Common/CommonSearch.vue'
import CommonTable from '@/components/Common/CommonTable.vue'
import CustomButton from '@/components/Common/CustomButton.vue'
import ExcelImporter from '@/components/Common/ExcelImporter.vue'
import PageHeader from '@/components/Common/PageHeader.vue'
import MonitoringChart from '@/components/Chart/MonitoringChart.vue'
import CustomPagination from '@/components/Common/CustomPagination.vue'
import {
    getRainfallMonitoringData,
    getAvailableMonitoringStations,
    getRainfallChartData,
    exportRainfallData
} from '@/api/monitoring'
import { formatLocalTimeForAPI, formatDateTime } from '@/utils/shared/common'

// ===================================
// 基础配置与常量定义
// ===================================

// 雨情监测项目配置
const rainfallMonitoringItems = [
    {
        code: 'RAINFALL',
        name: '时段雨量',
        unit: 'mm',
        color: '#409EFF',
        icon: 'fa-tint',
        description: '时段雨量监测'
    },
    {
        code: 'CUMULATIVE_RAINFALL',
        name: '累计雨量',
        unit: 'mm',
        color: '#67C23A',
        icon: 'fa-database',
        description: '累计雨量监测'
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

// 雨情导入列配置
const rainfallImportColumns = [
    { prop: 'rowNumber', label: '序号', width: 80 },
    { prop: 'monitoringTime', label: '时间', width: 180 },
    { prop: 'rainfall', label: '时段雨量(mm)', width: 130 },
    { prop: 'cumulativeRainfall', label: '累计雨量(mm)', width: 130 }
]

// 表格列配置
const tableColumns = [
    {
        prop: 'monitoringTime',
        label: '时间',
        width: 180,
        sortable: true,
        formatter: (row) => formatDateTime(row.monitoringTime)
    },
    {
        prop: 'rainfall',
        label: '时段雨量',
        width: 130,
        formatter: (row) => {
            const value = row.rainfall
            return value !== null && value !== undefined ? `${Number(value).toFixed(3)}` : '-'
        }
    },
    {
        prop: 'cumulativeRainfall',
        label: '累计雨量',
        width: 130,
        formatter: (row) => {
            const value = row.cumulativeRainfall
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
const activeTimeRange = ref(null) // 当前活动的时间范围类型，默认不选中任何按钮
const chartSearchTriggered = ref(false) // 是否已触发图表搜索（通过搜索按钮）

// 数据状态管理
const tableData = ref([])
const stationOptions = ref([])

// 图表数据
const chartData = ref({ labels: [], values: [] })
const chartLoading = ref(false)
const currentChartIndex = ref(0)

// 导出状态管理
const exportLoading = ref(false)

// 监听图表类型变化
watch(() => currentChartIndex.value, (newIndex) => {
    // 如果已经搜索并选择了站点，切换图表类型时重新加载数据
    if (searchForm.value.stationId && hasSearched.value && chartSearchTriggered.value) {
        loadChartData()
    }
})

// 加载状态管理
const tableLoading = ref(false)
const showImportDialog = ref(false)

// 分页配置
const pagination = reactive({
    currentPage: 1,
    pageSize: 20,
    total: 0
})

// ===================================
// 辅助工具函数
// ===================================

// 通用的时间范围参数处理函数
const processTimeRangeParams = (timeRange) => {
    const params = {};

    if (timeRange && timeRange.length === 2) {
        params.startTime = formatLocalTimeForAPI(timeRange[0]);
        params.endTime = formatLocalTimeForAPI(timeRange[1]);
    }

    return params;
};

// ===================================
// 数据加载函数
// ===================================

// 加载监测站点
const loadStations = async () => {
    try {
        // 只获取雨情监测站点（R类型）
        const response = await getAvailableMonitoringStations({ monitoringItemCode: 'R' })

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

            // 默认选择第一个监测站点
            if (stationOptions.value.length > 0) {
                searchForm.value.stationId = stationOptions.value[0].value
            } else {
                ElMessage.warning('没有找到可用的雨情监测站点')
            }
        } else {
            ElMessage.warning('监测站点数据格式不正确')
        }
    } catch (error) {
        console.error('降雨监测页面：加载监测站点失败:', error)
        ElMessage.error(`加载监测站点失败: ${error.message || '未知错误'}`)
    }
}

// 加载表格数据
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

        const response = await getRainfallMonitoringData(params)
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
        // 构建API参数
        const item = rainfallMonitoringItems[currentChartIndex.value];
        const params = {
            stationId: searchForm.value.stationId,
            dataType: item.code === 'RAINFALL' ? 'rainfall' : 'cumulativeRainfall',
            // 使用通用函数处理时间范围参数
            ...processTimeRangeParams(searchForm.value.timeRange)
        };

        // 调用API获取数据
        const response = await getRainfallChartData(params);
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

// 处理重置
const handleReset = () => {
    searchForm.value = {
        stationId: null,
        timeRange: []
    }
    // 重置搜索状态
    hasSearched.value = false
    chartSearchTriggered.value = false
    activeTimeRange.value = null
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

// 导入相关事件处理
const handleImportSuccess = () => {
    ElMessage.success('数据导入成功！')
    // 刷新表格数据
    loadTableData()
}

// 图表初始化事件处理
const handleChartInitialized = () => {
    // 图表初始化完成后，如果已经搜索并选择了站点，则加载图表数据
    if (searchForm.value.stationId && hasSearched.value && chartSearchTriggered.value) {
        loadChartData()
    }
}

// 导出数据
const handleExport = async () => {
    exportLoading.value = true
    try {
        const params = {
            ...searchForm.value,
            ...processTimeRangeParams(searchForm.value.timeRange)
        }
        const response = await exportRainfallData(params)
        if (response) {
            const filename = `雨情监测数据_${formatDateTime(new Date(), 'YYYYMMDDHHmmss')}.xlsx`
            const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
            const url = URL.createObjectURL(blob)
            const link = document.createElement('a')
            link.href = url
            link.download = filename
            document.body.appendChild(link)
            link.click()
            document.body.removeChild(link)
            URL.revokeObjectURL(url)
            ElMessage.success('数据导出成功！')
        } else {
            ElMessage.error('导出数据失败')
        }
    } catch (error) {
        console.error('导出数据失败:', error)
        ElMessage.error(`导出数据失败: ${error.message || '未知错误'}`)
    } finally {
        exportLoading.value = false
    }
}

// 时间范围切换处理
const handleTimeRangeChange = (timeRangeType) => {
    activeTimeRange.value = timeRangeType

    // 根据不同的时间范围类型设置不同的时间范围
    const now = new Date()
    let startTime, endTime

    switch (timeRangeType) {
        case 'hourly':
            // 时段雨量：最近24小时
            startTime = new Date(now.getTime() - 24 * 60 * 60 * 1000)
            endTime = now
            break
        case 'daily':
            // 日雨量：最近7天
            startTime = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000)
            endTime = now
            break
        case 'realtime':
            // 实时雨量：最近1小时
            startTime = new Date(now.getTime() - 60 * 60 * 1000)
            endTime = now
            break
        default:
            startTime = null
            endTime = null
    }

    // 更新搜索表单的时间范围
    if (startTime && endTime) {
        searchForm.value.timeRange = [startTime, endTime]
    } else {
        searchForm.value.timeRange = []
    }

    // 自动触发搜索
    handleSearch()
}

// ===================================
// 生命周期钩子
// ===================================

// 页面初始化
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
// ============================================
// 页面布局样式
// ============================================
.rainfall-monitoring {

    // ============================================
    // 页面内容区域样式
    // ============================================
    .filter-section,
    .chart-carousel-section,
    .table-section {
        margin-bottom: 20px;
    }

    .table-section {
        &__header {
            height: var(--table-row-height);
            border-bottom: 1px solid var(--border-color);
            display: flex;
            align-items: center;
            justify-content: flex-end;
            padding: 0 var(--spacing-base);
        }

        &__unit {
            font-size: var(--font-size-small);
            color: var(--text-secondary);
        }
    }

    // 时间模式切换按钮组
    .time-mode-buttons {
        display: flex;
        gap: 8px;
        margin-right: 12px;

        .custom-button {
            min-width: 80px;
            transition: all 0.3s ease;

            &:hover {
                transform: translateY(-1px);
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            }
        }
    }
}
</style>