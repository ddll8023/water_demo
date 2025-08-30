<template>
    <div class="inspection-management">
        <PageHeader title="工程巡检" icon="fa-search" description="巡检任务管理与巡检记录采集">
            <template #actions>
                <CustomButton type="success" :icon="'fa-upload'"
                    @click="activeTab === 'tasks' ? tasksRef?.handleExport() : recordsRef?.handleExport()">
                    导出
                </CustomButton>
            </template>
        </PageHeader>
        <TabSection v-model="activeTab" :tabs="tabs" />

        <div class="tab-content-wrapper">
            <!-- 巡检任务 Tab -->
            <div v-if="activeTab === 'tasks'" class="task-section">
                <InspectionTasks ref="tasksRef" :dict-maps="dictMaps" :facility-type-options="facilityTypeOptions"
                    :facility-options="facilityOptions" :personnel-options="personnelOptions"
                    :load-facility-options="loadFacilityOptions" :get-dict-label-sync="getDictLabelSync"
                    :get-facility-display="getFacilityDisplay" @task-updated="recordsRef?.loadRecordData()" />
            </div>

            <!-- 巡检记录 Tab -->
            <div v-else-if="activeTab === 'records'" class="record-section">
                <InspectionRecords ref="recordsRef" :dict-maps="dictMaps" :facility-type-options="facilityTypeOptions"
                    :facility-options="facilityOptions" :personnel-options="personnelOptions"
                    :load-facility-options="loadFacilityOptions" :get-dict-label-sync="getDictLabelSync"
                    :get-facility-display="getFacilityDisplay" @record-created="tasksRef?.loadTaskData()" />
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import PageHeader from '@/components/Common/PageHeader.vue'
import TabSection from '@/components/Common/TabSection.vue'
import CustomButton from '@/components/Common/CustomButton.vue'
import InspectionTasks from '@/components/Inspection/InspectionTasks.vue'
import InspectionRecords from '@/components/Inspection/InspectionRecords.vue'
import { useDictionary } from '@/composables/useDictionary'
import { useFacilityTypes } from '@/composables/useFacilityTypes'
import { listTasks, listRecords } from '@/api/inspection'
import {
    getAvailablePumpingStations, getAvailableWaterPlants, getAvailableReservoirs
} from '@/api/engineering-service'
import { getAvailableMonitoringStations } from '@/api/monitoring'
import { getPersonnelList } from '@/api/management-info'

// ================= Refs for child components =================
const tasksRef = ref(null)
const recordsRef = ref(null)

// ================= 组合式函数 =================
const { getDictData } = useDictionary()
const { getFacilityTypeOptions, getFacilityTypeLabelSync, loadFacilityTypeMap } = useFacilityTypes()

// ================= 响应式数据 =================
const activeTab = ref('tasks')
const tabs = [
    { name: 'tasks', label: '巡检任务', icon: 'fa-tasks' },
    { name: 'records', label: '巡检记录', icon: 'fa-file-image-o' }
]

// 字典数据
const dictMaps = reactive({
    inspection_status: [],
    inspection_frequency: [],
    device_status: []
})

// 设施类型数据
const facilityTypeOptions = ref([])

// 设施选项数据
const facilityOptions = reactive({
    pumping_station: [],
    water_plant: [],
    reservoir: [],
    monitoring_station: []
})

// 人员选项数据
const personnelOptions = ref([])

// ================= 生命周期 =================
onMounted(async () => {
    await loadDictionaries()
    await loadPersonnelOptions()
    await loadAllFacilityOptions()
})

// ================= 计算属性/工具函数 =================
const getDictLabelSync = (dictType, value) => {
    if (dictType === 'facility_type') {
        return getFacilityTypeLabelSync(value)
    }
    const dict = dictMaps[dictType] || []
    const item = dict.find(d => String(d.value) === String(value))
    return item ? item.label : value || '-'
}

const getFacilityDisplay = (facilityType, facilityId) => {
    if (!facilityType || !facilityId) return '-'

    const facilityTypeLabel = getDictLabelSync('facility_type', facilityType)
    const facilityList = facilityOptions[facilityType] || []
    const facility = facilityList.find(f => f.value === facilityId)
    const facilityName = facility ? facility.label : `ID:${facilityId}`

    return `${facilityTypeLabel} - ${facilityName}`
}

// ================= 字典与选项加载 =================
const loadDictionaries = async () => {
    try {
        const [inspectionStatus, inspectionFrequency, deviceStatus] = await Promise.all([
            getDictData('inspection_status'),
            getDictData('inspection_frequency'),
            getDictData('device_status')
        ])

        dictMaps.inspection_status = inspectionStatus
        dictMaps.inspection_frequency = inspectionFrequency
        dictMaps.device_status = deviceStatus

        // 加载设施类型数据
        facilityTypeOptions.value = await getFacilityTypeOptions()
        // 预加载设施类型映射，用于同步获取标签
        await loadFacilityTypeMap()
    } catch (error) {
        console.error('加载字典数据失败:', error)
    }
}

const loadPersonnelOptions = async () => {
    try {
        const response = await getPersonnelList({ page: 1, size: 100 })
        personnelOptions.value = response.items.map(p => ({
            label: p.fullName,
            value: p.id
        }))
    } catch (error) {
        console.error('加载人员选项失败:', error)
    }
}

const loadAllFacilityOptions = async () => {
    try {
        await Promise.all([
            loadFacilityOptions('pumping_station'),
            loadFacilityOptions('water_plant'),
            loadFacilityOptions('reservoir'),
            loadFacilityOptions('monitoring_station')
        ])
    } catch (error) {
        console.error('加载设施选项失败:', error)
    }
}

const loadFacilityOptions = async (facilityType) => {
    if (!facilityType || facilityOptions[facilityType].length > 0) return

    try {
        let response = null
        switch (facilityType) {
            case 'pumping_station':
                response = await getAvailablePumpingStations()
                break
            case 'water_plant':
                response = await getAvailableWaterPlants()
                break
            case 'reservoir':
                response = await getAvailableReservoirs()
                break
            case 'monitoring_station':
                response = await getAvailableMonitoringStations()
                break
        }

        if (response) {
            facilityOptions[facilityType] = response.map(item => ({
                label: item.name,
                value: item.id
            }))
        }
    } catch (error) {
        console.error(`加载${facilityType}选项失败:`, error)
    }
}
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

.inspection-management {
    padding: var(--spacing-lg);
    background-color: var(--bg-secondary);
    min-height: calc(100vh - var(--header-height));

    .tab-content-wrapper {
        background: var(--bg-primary);
        border-radius: var(--border-radius-md);
        box-shadow: var(--shadow-light);
        border: 1px solid var(--border-color-light);
        overflow: hidden;
    }

    .task-section,
    .record-section {
        padding: var(--spacing-md);
    }

    @include respond-to(md) {
        padding: var(--spacing-md);
    }

    @include respond-to(sm) {
        padding: var(--spacing-sm);

        .task-section,
        .record-section {
            padding: var(--spacing-sm);
        }
    }
}
</style>