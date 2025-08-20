<!-- 工程信息服务页面 - 水利工程设施和设备档案管理平台 -->
<template>
  <div class="page-container">
    <!-- 使用页面头部组件 -->
    <PageHeader title="工程信息服务" icon="fa-wrench" description="水利工程设施和设备档案管理平台" />

    <!-- 功能选项卡 -->
    <TabSection v-model="activeTab" :tabs="tabOptions" @tab-change="handleTabChange" />

    <!-- Tab内容容器 -->
    <div class="tab-content-wrapper">
      <!-- 监测站点 -->
      <div v-if="activeTab === 'monitoring-stations'" class="content-section">
        <FacilityManagement ref="monitoringStationRef" :facility-type="'monitoring-stations'" :facility-name="'监测站点'"
          :search-fields="monitoringStationSearchFields" :table-columns="monitoringStationColumns"
          :form-fields="monitoringStationFormFields" :api-functions="monitoringStationApi"
          :validation-rules="getValidationRules()" @refresh="refreshData" />
      </div>

      <!-- 泵站信息 -->
      <div v-else-if="activeTab === 'pumping-stations'" class="content-section">
        <FacilityManagement ref="pumpingStationRef" :facility-type="'pumping-stations'" :facility-name="'泵站'"
          :search-fields="pumpingStationSearchFields" :table-columns="pumpingStationColumns"
          :form-fields="pumpingStationFormFields" :api-functions="pumpingStationApi"
          :validation-rules="getValidationRules()" @refresh="refreshData" />
      </div>

      <!-- 水库信息 -->
      <div v-else-if="activeTab === 'reservoirs'" class="content-section">
        <FacilityManagement ref="reservoirRef" :facility-type="'reservoirs'" :facility-name="'水库'"
          :search-fields="reservoirSearchFields" :table-columns="reservoirColumns" :form-fields="reservoirFormFields"
          :api-functions="reservoirApi" :validation-rules="getValidationRules()" @refresh="refreshData" />
      </div>

      <!-- 水厂信息 -->
      <div v-else-if="activeTab === 'water-plants'" class="content-section">
        <FacilityManagement ref="waterPlantRef" :facility-type="'water-plants'" :facility-name="'水厂'"
          :search-fields="waterPlantSearchFields" :table-columns="waterPlantColumns" :form-fields="waterPlantFormFields"
          :api-functions="waterPlantApi" :validation-rules="getValidationRules()" @refresh="refreshData" />
      </div>

      <!-- 村庄信息 -->
      <div v-else-if="activeTab === 'villages'" class="content-section">
        <FacilityManagement ref="villageRef" :facility-type="'villages'" :facility-name="'村庄'"
          :search-fields="villageSearchFields" :table-columns="villageColumns" :form-fields="villageFormFields"
          :api-functions="villageApi" :validation-rules="getValidationRules()" @refresh="refreshData" />
      </div>

      <!-- 浮船信息 -->
      <div v-else-if="activeTab === 'floating-boats'" class="content-section">
        <FacilityManagement ref="floatingBoatRef" :facility-type="'floating-boats'" :facility-name="'浮船'"
          :search-fields="floatingBoatSearchFields" :table-columns="floatingBoatColumns"
          :form-fields="floatingBoatFormFields" :api-functions="floatingBoatApi"
          :validation-rules="getValidationRules()" @refresh="refreshData" />
      </div>

      <!-- 消毒药材 -->
      <div v-else-if="activeTab === 'disinfection-materials'" class="content-section">
        <FacilityManagement ref="disinfectionMaterialRef" :facility-type="'disinfection-materials'"
          :facility-name="'消毒药材'" :search-fields="disinfectionMaterialSearchFields"
          :table-columns="disinfectionMaterialColumns" :form-fields="disinfectionMaterialFormFields"
          :api-functions="disinfectionMaterialApi" :validation-rules="getValidationRules()" @refresh="refreshData" />
      </div>

      <!-- 管道信息 -->
      <div v-else-if="activeTab === 'pipelines'" class="content-section">
        <FacilityManagement ref="pipelineRef" :facility-type="'pipelines'" :facility-name="'管道'"
          :search-fields="pipelineSearchFields" :table-columns="pipelineColumns" :form-fields="pipelineFormFields"
          :api-functions="pipelineApi" :validation-rules="getValidationRules()" @refresh="refreshData" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import FacilityManagement from '@/components/Engineering/FacilityManagement.vue'
import * as engineeringApi from '@/api/engineering-service'
import { useDictionary } from '@/composables/useDictionary'
import { useFacilityTypes } from '@/composables/useFacilityTypes'
import PageHeader from '@/components/Common/PageHeader.vue'
import TabSection from '@/components/Common/TabSection.vue'

/**
 * ----------------------------------------
 * 组件状态和引用
 * ----------------------------------------
 */
// 组合式函数
const { getDictData } = useDictionary()
const { getFacilityTypeOptions } = useFacilityTypes()

// 当前激活的标签页
const activeTab = ref('monitoring-stations')

// 标签选项配置
const tabOptions = [
  { name: 'monitoring-stations', label: '监测站点', icon: 'fa-map-marker' },
  { name: 'pumping-stations', label: '泵站信息', icon: 'fa-tint' },
  { name: 'reservoirs', label: '水库信息', icon: 'fa-database' },
  { name: 'water-plants', label: '水厂信息', icon: 'fa-building' },
  { name: 'villages', label: '村庄信息', icon: 'fa-home' },
  { name: 'floating-boats', label: '浮船信息', icon: 'fa-ship' },
  { name: 'disinfection-materials', label: '消毒药材', icon: 'fa-flask' },
  { name: 'pipelines', label: '管道信息', icon: 'fa-exchange' }
]

// 各个设施类型的引用
const pumpingStationRef = ref(null)
const monitoringStationRef = ref(null)
const pipelineRef = ref(null)
const villageRef = ref(null)
const reservoirRef = ref(null)
const waterPlantRef = ref(null)
const floatingBoatRef = ref(null)
const disinfectionMaterialRef = ref(null)

/**
 * ----------------------------------------
 * 泵站信息配置
 * ----------------------------------------
 */
// 泵站信息搜索字段
const pumpingStationSearchFields = reactive([
  {
    prop: 'name',
    label: '泵站名称',
    type: 'input',
    placeholder: '请输入泵站名称',
    width: '240px',
    labelWidth: 'var(--form-label-width-standard)',
    clearable: true
  }
])

// 泵站信息表格列
const pumpingStationColumns = [
  { prop: 'name', label: '泵站名称', width: 120 },
  { prop: 'stationCode', label: '泵站编码', width: 100 },
  {
    prop: 'stationType',
    label: '泵站类型',
    width: 80,
    type: 'facilityType'
  },
  { prop: 'waterProject', label: '所属供水工程', width: 140 },
  { prop: 'waterCompany', label: '所属供水公司', width: 120 },
  { prop: 'longitude', label: '经度', width: 90 },
  { prop: 'latitude', label: '纬度', width: 90 },
  { prop: 'address', label: '地址', width: 140 },
  {
    prop: 'operationMode',
    label: '运行方式(几备几用)',
    width: 120,
    type: 'dict',
    dictType: 'operation_mode'
  },
  { prop: 'unitCount', label: '机组数量(台)', width: 60 },
  { prop: 'designScale', label: '设计规模(m³/天)', width: 60 },
  { prop: 'installedCapacity', label: '装机容量(kw)', width: 60 },
  { prop: 'liftHead', label: '扬程', width: 60 },
  { prop: 'establishmentDate', label: '建站年月', width: 90 }
]

// 泵站信息表单字段
const pumpingStationFormFields = [
  {
    prop: 'name',
    label: '泵站名称',
    type: 'input',
    required: true,
    placeholder: '请输入泵站名称',
    span: 12,
    rules: 'name'
  },
  {
    prop: 'stationCode',
    label: '泵站编码',
    type: 'input',
    required: true,
    placeholder: '请输入泵站编码',
    span: 12,
    rules: 'stationCode'
  },
  {
    prop: 'stationType',
    label: '泵站类型',
    type: 'select',
    required: true,
    placeholder: '请选择泵站类型',
    options: [],
    span: 12,
    rules: 'stationType'
  },
  {
    prop: 'waterProject',
    label: '所属供水工程',
    type: 'input',
    placeholder: '请输入所属供水工程',
    span: 12
  },
  {
    prop: 'waterCompany',
    label: '所属供水公司',
    type: 'input',
    placeholder: '请输入所属供水公司',
    span: 12
  },
  {
    prop: 'longitude',
    label: '经度',
    type: 'number',
    placeholder: '请输入经度(-180~180)',
    span: 12,
    rules: [
      {
        validator: (rule, value, callback) => {
          // 允许空值
          if (value === '' || value === null || value === undefined) {
            callback()
            return
          }
          // 转换为数字
          const num = typeof value === 'number' ? value : Number(value)
          // 验证数字有效性和范围
          if (isNaN(num)) {
            callback(new Error('请输入有效的数字'))
          } else if (num < -180 || num > 180) {
            callback(new Error('经度范围为 -180 到 180'))
          } else {
            callback()
          }
        },
        trigger: 'change'
      }
    ]
  },
  {
    prop: 'latitude',
    label: '纬度',
    type: 'number',
    placeholder: '请输入纬度(-90~90)',
    span: 12,
    rules: [
      {
        validator: (rule, value, callback) => {
          // 允许空值
          if (value === '' || value === null || value === undefined) {
            callback()
            return
          }
          // 转换为数字
          const num = typeof value === 'number' ? value : Number(value)
          // 验证数字有效性和范围
          if (isNaN(num)) {
            callback(new Error('请输入有效的数字'))
          } else if (num < -90 || num > 90) {
            callback(new Error('纬度范围为 -90 到 90'))
          } else {
            callback()
          }
        },
        trigger: 'change'
      }
    ]
  },
  {
    prop: 'address',
    label: '地址',
    type: 'textarea',
    placeholder: '请输入详细地址',
    span: 24
  },
  {
    prop: 'operationMode',
    label: '运行方式',
    type: 'select',
    required: true,
    placeholder: '请选择运行方式',
    options: [],
    span: 12,
    rules: 'operationMode'
  },
  {
    prop: 'unitCount',
    label: '机组数量',
    type: 'number',
    placeholder: '请输入机组数量',
    span: 12,
    rules: 'unitCount'
  },
  {
    prop: 'designScale',
    label: '设计规模(m³/天)',
    type: 'number',
    placeholder: '请输入设计规模',
    span: 12,
    rules: 'designScale'
  },
  {
    prop: 'installedCapacity',
    label: '装机容量(kW)',
    type: 'number',
    placeholder: '请输入装机容量',
    span: 12,
    rules: 'installedCapacity'
  },
  {
    prop: 'liftHead',
    label: '扬程(m)',
    type: 'number',
    placeholder: '请输入扬程',
    span: 12,
    rules: 'liftHead'
  },
  {
    prop: 'establishmentDate',
    label: '建站年月',
    type: 'date',
    placeholder: '请选择建站年月',
    span: 12
  }
]

// 泵站信息API接口
const pumpingStationApi = {
  getList: engineeringApi.getPumpingStationList,
  getDetail: engineeringApi.getPumpingStationDetail,
  create: engineeringApi.createPumpingStation,
  update: engineeringApi.updatePumpingStation,
  delete: engineeringApi.deletePumpingStation
}

/**
 * ----------------------------------------
 * 监测站点信息配置
 * ----------------------------------------
 */
// 监测站点搜索字段
const monitoringStationSearchFields = reactive([
  {
    prop: 'keyword',
    label: '监测站',
    type: 'select',
    placeholder: '请选择监测站名称',
    options: [],
    width: '240px',
    labelWidth: 'var(--form-label-width-standard)',
    clearable: true
  }
])

// 监测站点表格列
const monitoringStationColumns = [
  { prop: 'stationCode', label: '站码', width: 120 },
  { prop: 'name', label: '站名', minWidth: 150 },
  { prop: 'waterSystemName', label: '水系名称', width: 150 },
  { prop: 'riverName', label: '河流名称', width: 150 },
  { prop: 'monitoringItemCode', label: '施测项目码', width: 120 },
  { prop: 'adminRegionCode', label: '行政区划码', width: 120 },
  { prop: 'establishmentDate', label: '设站年月', width: 120 },
  { prop: 'longitude', label: '经度', width: 100 },
  { prop: 'latitude', label: '纬度', width: 100 },
  { prop: 'remark', label: '备注', minWidth: 150 }
]

// 监测站点表单字段
const monitoringStationFormFields = [
  {
    prop: 'name',
    label: '站名',
    type: 'input',
    required: true,
    placeholder: '请输入站名',
    span: 12
  },
  {
    prop: 'stationCode',
    label: '站码',
    type: 'input',
    required: true,
    placeholder: '请输入站码',
    span: 12
  },
  {
    prop: 'waterSystemName',
    label: '水系名称',
    type: 'input',
    placeholder: '请输入水系名称',
    span: 12
  },
  {
    prop: 'riverName',
    label: '河流名称',
    type: 'input',
    placeholder: '请输入河流名称',
    span: 12
  },
  {
    prop: 'monitoringItemCode',
    label: '施测项目',
    type: 'select',
    required: true,
    placeholder: '请选择施测项目',
    options: [
      { label: '流量站', value: 'Q' },
      { label: '视频站', value: 'V' },
      { label: '水位站', value: 'Z' },
      { label: '雨量站', value: 'P' }
    ],
    span: 12
  },
  {
    prop: 'longitude',
    label: '经度',
    type: 'number',
    placeholder: '请输入经度(-180~180)',
    span: 12,
    rules: [
      {
        validator: (rule, value, callback) => {
          // 允许空值
          if (value === '' || value === null || value === undefined) {
            callback()
            return
          }
          // 转换为数字
          const num = typeof value === 'number' ? value : Number(value)
          // 验证数字有效性和范围
          if (isNaN(num)) {
            callback(new Error('请输入有效的数字'))
          } else if (num < -180 || num > 180) {
            callback(new Error('经度范围为 -180 到 180'))
          } else {
            callback()
          }
        },
        trigger: 'change'
      }
    ]
  },
  {
    prop: 'latitude',
    label: '纬度',
    type: 'number',
    placeholder: '请输入纬度(-90~90)',
    span: 12,
    rules: [
      {
        validator: (rule, value, callback) => {
          // 允许空值
          if (value === '' || value === null || value === undefined) {
            callback()
            return
          }
          // 转换为数字
          const num = typeof value === 'number' ? value : Number(value)
          // 验证数字有效性和范围
          if (isNaN(num)) {
            callback(new Error('请输入有效的数字'))
          } else if (num < -90 || num > 90) {
            callback(new Error('纬度范围为 -90 到 90'))
          } else {
            callback()
          }
        },
        trigger: 'change'
      }
    ]
  },
  {
    prop: 'establishmentDate',
    label: '设站年月',
    type: 'date',
    placeholder: '请选择设站年月',
    span: 12
  },
  {
    prop: 'remark',
    label: '备注',
    type: 'textarea',
    placeholder: '请输入备注信息',
    span: 24
  }
]

// 监测站点API接口
const monitoringStationApi = {
  getList: engineeringApi.getMonitoringStationList,
  getDetail: engineeringApi.getMonitoringStationDetail,
  create: engineeringApi.createMonitoringStation,
  update: engineeringApi.updateMonitoringStation,
  delete: engineeringApi.deleteMonitoringStation,
  batchDelete: engineeringApi.batchDeleteMonitoringStations
}

/**
 * ----------------------------------------
 * 管道信息配置
 * ----------------------------------------
 */
// 管道信息搜索字段
const pipelineSearchFields = reactive([
  {
    prop: 'keyword',
    label: '管道名称',
    type: 'input',
    placeholder: '请输入管道名称',
    width: '240px',
    labelWidth: 'var(--form-label-width-standard)'
  },
  {
    prop: 'pipelineType',
    label: '管道类型',
    type: 'select',
    placeholder: '请选择管道类型',
    options: [],
    width: '240px',
    labelWidth: 'var(--form-label-width-standard)'
  }
])

// 管道信息表格列
const pipelineColumns = [
  { prop: 'name', label: '管道名称', minWidth: 150 },
  {
    prop: 'pipelineType',
    label: '管道类型',
    width: 120,
    type: 'dict',
    dictType: 'pipeline_type'
  },
  { prop: 'remark', label: '备注', minWidth: 200 }
]

// 管道信息表单字段
const pipelineFormFields = [
  {
    prop: 'name',
    label: '管道名称',
    type: 'input',
    required: true,
    placeholder: '请输入管道名称',
    span: 12
  },
  {
    prop: 'pipelineType',
    label: '管道类型',
    type: 'select',
    required: true,
    placeholder: '请选择管道类型',
    options: [],
    span: 12
  },
  {
    prop: 'remark',
    label: '备注',
    type: 'textarea',
    placeholder: '请输入备注信息',
    span: 24
  }
]

// 管道信息API接口
const pipelineApi = {
  getList: engineeringApi.getPipelineList,
  getDetail: engineeringApi.getPipelineDetail,
  create: engineeringApi.createPipeline,
  update: engineeringApi.updatePipeline,
  delete: engineeringApi.deletePipeline,
  batchDelete: engineeringApi.batchDeletePipelines
}

/**
 * ----------------------------------------
 * 村庄信息配置
 * ----------------------------------------
 */
// 村庄信息搜索字段
const villageSearchFields = [
  {
    prop: 'keyword',
    label: '村庄名称',
    type: 'input',
    placeholder: '请输入村庄名称',
    width: '240px',
    labelWidth: 'var(--form-label-width-standard)',
    clearable: true
  }
]

// 村庄信息表格列
const villageColumns = [
  { prop: 'name', label: '村庄名称', minWidth: 150 },
  { prop: 'longitude', label: '经度', width: 120 },
  { prop: 'latitude', label: '纬度', width: 120 },
  { prop: 'currentPopulation', label: '现状人口(人)', width: 120 },
  { prop: 'remark', label: '备注', minWidth: 200 }
]

// 村庄信息表单字段
const villageFormFields = [
  {
    prop: 'name',
    label: '村庄名称',
    type: 'input',
    required: true,
    placeholder: '请输入村庄名称',
    span: 12,
    rules: 'name'
  },
  {
    prop: 'administrativeCode',
    label: '行政区划代码',
    type: 'input',
    placeholder: '请输入行政区划代码',
    span: 12,
    rules: 'administrativeCode'
  },
  {
    prop: 'longitude',
    label: '经度',
    type: 'number',
    placeholder: '请输入经度(-180~180)',
    span: 12,
    rules: 'longitude'
  },
  {
    prop: 'latitude',
    label: '纬度',
    type: 'number',
    placeholder: '请输入纬度(-90~90)',
    span: 12,
    rules: 'latitude'
  },
  {
    prop: 'currentPopulation',
    label: '现状人口(人)',
    type: 'number',
    placeholder: '请输入现状人口',
    span: 12,
    rules: 'currentPopulation'
  },
  {
    prop: 'remark',
    label: '备注',
    type: 'textarea',
    placeholder: '请输入备注信息',
    span: 24
  }
]

// 村庄信息API接口
const villageApi = {
  getList: engineeringApi.getVillageList,
  getDetail: engineeringApi.getVillageDetail,
  create: engineeringApi.createVillage,
  update: engineeringApi.updateVillage,
  delete: engineeringApi.deleteVillage,
  batchDelete: engineeringApi.batchDeleteVillages
}

/**
 * ----------------------------------------
 * 水库信息配置
 * ----------------------------------------
 */
// 水库信息搜索字段
const reservoirSearchFields = reactive([
  {
    prop: 'keyword',
    label: '水库名称',
    type: 'input',
    placeholder: '请输入水库名称',
    width: '240px',
    labelWidth: 'var(--form-label-width-standard)',
    clearable: true
  }
])

// 水库信息表格列
const reservoirColumns = [
  { prop: 'waterProject', label: '所属水利工程', minWidth: 150 },
  { prop: 'reservoirCode', label: '水库编码', minWidth: 120 },
  { prop: 'name', label: '水库名称', minWidth: 150 },
  { prop: 'longitude', label: '经度', width: 100 },
  { prop: 'latitude', label: '纬度', width: 100 },
  { prop: 'location', label: '水库所在位置', minWidth: 150 },
  { prop: 'registrationNo', label: '水库注册登记号', minWidth: 140 },
  { prop: 'adminRegionCode', label: '水库注册登记行政区划', minWidth: 160 },
  {
    prop: 'engineeringGrade',
    label: '水库工程等级',
    width: 120,
    type: 'dict',
    dictType: 'engineering_grade'
  },
  {
    prop: 'engineeringScale',
    label: '水库工程规模',
    width: 120,
    type: 'dict',
    dictType: 'engineering_scale'
  },
  { prop: 'totalCapacity', label: '总容量(万m³)', width: 120 },
  { prop: 'regulatingCapacity', label: '调节容量(万m³)', width: 130 },
  { prop: 'deadCapacity', label: '死容量(万m³)', width: 120 },
  { prop: 'establishmentDate', label: '建库年月', width: 120 }
]

// 水库信息表单字段
const reservoirFormFields = [
  {
    prop: 'name',
    label: '水库名称',
    type: 'input',
    required: true,
    placeholder: '请输入水库名称',
    span: 12
  },
  {
    prop: 'reservoirCode',
    label: '水库编码',
    type: 'input',
    required: true,
    placeholder: '请输入水库编码',
    span: 12
  },
  {
    prop: 'waterProject',
    label: '所属水利工程',
    type: 'input',
    placeholder: '请输入所属水利工程',
    span: 12
  },
  {
    prop: 'longitude',
    label: '经度',
    type: 'number',
    placeholder: '请输入经度',
    span: 12
  },
  {
    prop: 'latitude',
    label: '纬度',
    type: 'number',
    placeholder: '请输入纬度',
    span: 12
  },
  {
    prop: 'location',
    label: '水库所在位置',
    type: 'input',
    placeholder: '请输入水库所在位置',
    span: 12
  },
  {
    prop: 'registrationNo',
    label: '水库注册登记号',
    type: 'input',
    placeholder: '请输入水库注册登记号',
    span: 12
  },
  {
    prop: 'adminRegionCode',
    label: '水库注册登记行政区划',
    type: 'input',
    placeholder: '请输入行政区划代码',
    span: 12
  },
  {
    prop: 'engineeringGrade',
    label: '水库工程等级',
    type: 'select',
    placeholder: '请选择工程等级',
    options: [],
    span: 12
  },
  {
    prop: 'engineeringScale',
    label: '水库工程规模',
    type: 'select',
    placeholder: '请选择工程规模',
    options: [],
    span: 12
  },
  {
    prop: 'totalCapacity',
    label: '总容量(万m³)',
    type: 'number',
    placeholder: '请输入总容量',
    span: 12
  },
  {
    prop: 'regulatingCapacity',
    label: '调节容量(万m³)',
    type: 'number',
    placeholder: '请输入调节容量',
    span: 12
  },
  {
    prop: 'deadCapacity',
    label: '死容量(万m³)',
    type: 'number',
    placeholder: '请输入死容量',
    span: 12
  },
  {
    prop: 'establishmentDate',
    label: '建库年月',
    type: 'date',
    placeholder: '请选择建库年月',
    span: 12
  }
]

// 水库信息API接口
const reservoirApi = {
  getList: engineeringApi.getReservoirList,
  getDetail: engineeringApi.getReservoirDetail,
  create: engineeringApi.createReservoir,
  update: engineeringApi.updateReservoir,
  delete: engineeringApi.deleteReservoir,
  batchDelete: engineeringApi.batchDeleteReservoirs
}

/**
 * ----------------------------------------
 * 水厂信息配置
 * ----------------------------------------
 */
// 水厂信息搜索字段
const waterPlantSearchFields = reactive([
  {
    prop: 'keyword',
    label: '水厂名称',
    type: 'input',
    placeholder: '请输入水厂名称',
    width: '240px',
    labelWidth: 'var(--form-label-width-standard)'
  }
])

// 水厂信息表格列
const waterPlantColumns = [
  { prop: 'waterProject', label: '所属供水工程', minWidth: 150 },
  { prop: 'plantCode', label: '水厂编码', minWidth: 120 },
  { prop: 'name', label: '水厂名称', minWidth: 150 },
  { prop: 'address', label: '地址', minWidth: 200 },
  { prop: 'managementUnit', label: '管理单位', width: 120 },
  { prop: 'longitude', label: '经度', width: 100 },
  { prop: 'latitude', label: '纬度', width: 100 },
  { prop: 'designScale', label: '设计规模(m³/天)', width: 140 },
  { prop: 'supplyArea', label: '供水范围(村镇)', minWidth: 150 },
  { prop: 'supplyLoadRatio', label: '供水负荷率(%)', width: 120 },
  { prop: 'supplyPopulation', label: '供水人口(万人)', width: 130 },
  { prop: 'managerName', label: '负责人', width: 100 },
  { prop: 'managerPhone', label: '负责人电话', width: 120 },
  { prop: 'establishmentDate', label: '建站年份', width: 120 }
]

// 水厂信息表单字段
const waterPlantFormFields = [
  {
    prop: 'name',
    label: '水厂名称',
    type: 'input',
    required: true,
    placeholder: '请输入水厂名称',
    span: 12
  },
  {
    prop: 'plantCode',
    label: '水厂编码',
    type: 'input',
    required: true,
    placeholder: '请输入水厂编码',
    span: 12
  },
  {
    prop: 'waterProject',
    label: '所属供水工程',
    type: 'input',
    placeholder: '请输入所属供水工程',
    span: 12
  },
  {
    prop: 'address',
    label: '地址',
    type: 'input',
    placeholder: '请输入地址',
    span: 12
  },
  {
    prop: 'managementUnit',
    label: '管理单位',
    type: 'input',
    placeholder: '请输入管理单位',
    span: 12
  },
  {
    prop: 'longitude',
    label: '经度',
    type: 'number',
    placeholder: '请输入经度',
    span: 12
  },
  {
    prop: 'latitude',
    label: '纬度',
    type: 'number',
    placeholder: '请输入纬度',
    span: 12
  },
  {
    prop: 'designScale',
    label: '设计规模(m³/天)',
    type: 'number',
    placeholder: '请输入设计规模',
    span: 12
  },
  {
    prop: 'supplyArea',
    label: '供水范围(村镇)',
    type: 'textarea',
    placeholder: '请输入供水范围',
    span: 24
  },
  {
    prop: 'supplyLoadRatio',
    label: '供水负荷率(%)',
    type: 'number',
    placeholder: '请输入供水负荷率',
    span: 12
  },
  {
    prop: 'supplyPopulation',
    label: '供水人口(万人)',
    type: 'number',
    placeholder: '请输入供水人口',
    span: 12
  },
  {
    prop: 'contactPhone',
    label: '联系电话',
    type: 'input',
    placeholder: '请输入联系电话',
    span: 12
  },
  {
    prop: 'establishmentDate',
    label: '建站年份',
    type: 'date',
    placeholder: '请选择建站年份',
    span: 12
  }
]

// 水厂信息API接口
const waterPlantApi = {
  getList: engineeringApi.getWaterPlantList,
  getDetail: engineeringApi.getWaterPlantDetail,
  create: engineeringApi.createWaterPlant,
  update: engineeringApi.updateWaterPlant,
  delete: engineeringApi.deleteWaterPlant,
  batchDelete: engineeringApi.batchDeleteWaterPlants
}

/**
 * ----------------------------------------
 * 浮船信息配置
 * ----------------------------------------
 */
// 浮船信息搜索字段
const floatingBoatSearchFields = reactive([
  {
    prop: 'keyword',
    label: '浮船名称',
    type: 'input',
    placeholder: '请输入浮船名称',
    width: '240px',
    labelWidth: 'var(--form-label-width-standard)',
    clearable: true
  }
])

// 浮船信息表格列
const floatingBoatColumns = [
  { prop: 'name', label: '名称', minWidth: 150 },
  { prop: 'longitude', label: '经度', width: 120 },
  { prop: 'latitude', label: '纬度', width: 120 },
  { prop: 'capacity', label: '容量(m³/h)', width: 130 },
  { prop: 'powerConsumption', label: '能耗(kw/h)', width: 130 },
  {
    prop: 'pumpingStatus',
    label: '抽水状态',
    width: 120,
    type: 'dict',
    dictType: 'device_status'
  }
]

// 浮船信息表单字段
const floatingBoatFormFields = [
  {
    prop: 'name',
    label: '名称',
    type: 'input',
    required: true,
    placeholder: '请输入浮船名称',
    span: 12
  },
  {
    prop: 'longitude',
    label: '经度',
    type: 'number',
    placeholder: '请输入经度',
    span: 12,
    rules: 'longitude'
  },
  {
    prop: 'latitude',
    label: '纬度',
    type: 'number',
    placeholder: '请输入纬度',
    span: 12,
    rules: 'latitude'
  },
  {
    prop: 'capacity',
    label: '容量(m³/h)',
    type: 'number',
    placeholder: '请输入容量',
    span: 12,
    rules: 'capacity'
  },
  {
    prop: 'powerConsumption',
    label: '能耗(kw/h)',
    type: 'number',
    placeholder: '请输入能耗',
    span: 12,
    rules: 'powerConsumption'
  },
  {
    prop: 'pumpingStatus',
    label: '抽水状态',
    type: 'select',
    required: true,
    placeholder: '请选择抽水状态',
    options: [],
    span: 12
  }
]

// 浮船信息API接口
const floatingBoatApi = {
  getList: engineeringApi.getFloatingBoatList,
  getDetail: engineeringApi.getFloatingBoatDetail,
  create: engineeringApi.createFloatingBoat,
  update: engineeringApi.updateFloatingBoat,
  delete: engineeringApi.deleteFloatingBoat,
  batchDelete: engineeringApi.batchDeleteFloatingBoats
}

/**
 * ----------------------------------------
 * 消毒药材配置
 * ----------------------------------------
 */
// 消毒药材搜索字段
const disinfectionMaterialSearchFields = reactive([
  {
    prop: 'keyword',
    label: '药材名称',
    type: 'input',
    placeholder: '请输入名称',
    width: '240px',
    labelWidth: 'var(--form-label-width-standard)'
  }
])

// 消毒药材表格列
const disinfectionMaterialColumns = [
  { prop: 'id', label: '序号', width: 80 },
  { prop: 'name', label: '名称', minWidth: 120 },
  { prop: 'storageCondition', label: '存储条件', width: 120 },
  { prop: 'productionDate', label: '生产日期', width: 120 },
  { prop: 'validityPeriod', label: '有效期', width: 100 },
  { prop: 'remark', label: '备注', minWidth: 150 }
]

// 消毒药材表单字段
const disinfectionMaterialFormFields = [
  {
    prop: 'name',
    label: '名称',
    type: 'input',
    required: true,
    placeholder: '请输入名称',
    span: 12
  },
  {
    prop: 'storageCondition',
    label: '存储条件',
    type: 'input',
    placeholder: '请输入存储条件',
    span: 12
  },
  {
    prop: 'productionDate',
    label: '生产日期',
    type: 'date',
    placeholder: '请选择生产日期',
    span: 12
  },
  {
    prop: 'validityPeriod',
    label: '有效期',
    type: 'input',
    placeholder: '请输入有效期',
    span: 12
  },
  {
    prop: 'remark',
    label: '备注',
    type: 'textarea',
    placeholder: '请输入备注',
    span: 24
  }
]

// 消毒药材API接口
const disinfectionMaterialApi = {
  getList: engineeringApi.getDisinfectionMaterialList,
  getDetail: engineeringApi.getDisinfectionMaterialDetail,
  create: engineeringApi.createDisinfectionMaterial,
  update: engineeringApi.updateDisinfectionMaterial,
  delete: engineeringApi.deleteDisinfectionMaterial,
  batchDelete: engineeringApi.batchDeleteDisinfectionMaterials
}

/**
 * ----------------------------------------
 * 工具方法
 * ----------------------------------------
 */
// 标签页切换处理
const handleTabChange = (tabName) => {
  // 标签页切换处理
}

// 刷新当前标签页数据
const refreshData = () => {
  // 刷新当前标签页的数据
  const currentRef = getCurrentRef()
  if (currentRef && currentRef.value) {
    currentRef.value.refreshData()
  }
}

// 获取当前激活标签页的引用
const getCurrentRef = () => {
  const refMap = {
    'pumping-stations': pumpingStationRef,
    'monitoring-stations': monitoringStationRef,
    'pipelines': pipelineRef,
    'villages': villageRef,
    'reservoirs': reservoirRef,
    'water-plants': waterPlantRef,
    'floating-boats': floatingBoatRef,
    'disinfection-materials': disinfectionMaterialRef
  }
  return refMap[activeTab.value]
}

/**
 * ----------------------------------------
 * 生命周期钩子和初始化
 * ----------------------------------------
 */
// 组件挂载后执行
onMounted(() => {
  // 初始化数据
  loadSelectOptions()
})

// 加载下拉选项数据
const loadSelectOptions = async () => {
  try {
    // 使用工程服务API加载设施类型选项
    const facilityTypes = await getFacilityTypeOptions()
    const operationModes = await getDictData('operation_mode')
    const monitoringItems = await getDictData('monitoring_item')
    const pipelineTypes = await getDictData('pipeline_type')
    const engineeringGrades = await getDictData('engineering_grade')
    const engineeringScales = await getDictData('engineering_scale')
    const deviceStatuses = await getDictData('device_status')

    // 为水库和水厂类型创建更完整的选项
    const reservoirTypes = [
      { label: '大型水库', value: 'large_reservoir' },
      { label: '中型水库', value: 'medium_reservoir' },
      { label: '小型水库', value: 'small_reservoir' },
      { label: '调节水库', value: 'regulating_reservoir' }
    ]

    const waterPlantTypes = [
      { label: '地表水厂', value: 'surface_water_plant' },
      { label: '地下水厂', value: 'groundwater_plant' },
      { label: '净水厂', value: 'purification_plant' },
      { label: '污水处理厂', value: 'wastewater_plant' }
    ]

    // 创建药材类型的临时数据（因为数据库中暂无专门的药材类型字典）
    const materialTypes = [
      { label: '混凝剂', value: 'coagulant' },
      { label: '消毒剂', value: 'disinfectant' },
      { label: '吸附剂', value: 'adsorbent' },
      { label: 'pH调节剂', value: 'ph_adjuster' }
    ]

    // 更新泵站配置中的options（只更新真正存在的字段）
    // 泵站搜索字段只有泵站名称，无需更新选项
    updateFieldOptions(pumpingStationFormFields, 'stationType', facilityTypes)
    updateFieldOptions(pumpingStationFormFields, 'operationMode', operationModes)

    // 加载监测站点名称选项
    try {
      const stationResponse = await engineeringApi.getMonitoringStationList({ page: 1, size: 1000 })
      const stations = stationResponse.items || []

      // 去重处理：使用Set去除重复的站点名称
      const uniqueStationNames = [...new Set(stations.map(station => station.name))]
      const stationNameOptions = uniqueStationNames.map(name => ({
        label: name,
        value: name
      }))

      // 更新监测站点配置中的options
      updateFieldOptions(monitoringStationSearchFields, 'keyword', stationNameOptions)
    } catch (error) {
      console.error('加载监测站点名称选项失败:', error)
    }

    updateFieldOptions(monitoringStationFormFields, 'monitoringItemCode', monitoringItems)

    // 更新管道配置中的options（只更新存在的字段）
    updateFieldOptions(pipelineSearchFields, 'pipelineType', pipelineTypes)
    updateFieldOptions(pipelineFormFields, 'pipelineType', pipelineTypes)

    // 更新水库配置中的options（只更新存在的字段）
    updateFieldOptions(reservoirFormFields, 'engineeringGrade', engineeringGrades)
    updateFieldOptions(reservoirFormFields, 'engineeringScale', engineeringScales)

    // 移除不存在字段的更新
    // updateFieldOptions(waterPlantSearchFields, 'plantType', waterPlantTypes)

    // 更新浮船配置中的options（只更新存在的字段）
    updateFieldOptions(floatingBoatFormFields, 'pumpingStatus', deviceStatuses)

    // 移除不存在字段的更新
    // updateFieldOptions(disinfectionMaterialSearchFields, 'materialType', materialTypes)

    // 所有下拉选项数据加载完成

  } catch (error) {
    console.error('加载选项数据失败:', error)
    ElMessage.error('加载下拉选项数据失败')
  }
}

// 辅助函数：更新字段配置中的options
const updateFieldOptions = (fields, fieldProp, options) => {
  const field = fields.find(f => f.prop === fieldProp)
  if (field) {
    // 使用 nextTick 确保在下一个更新周期中更新，避免递归更新
    nextTick(() => {
      if (field.options !== options) {
        field.options = [...options]
      }
    })
  } else {
    console.warn(`❌ 未找到字段 ${fieldProp}`)
  }
}

/**
 * ----------------------------------------
 * 表单验证规则
 * ----------------------------------------
 */
// 获取所有验证规则
const getValidationRules = () => {
  return {
    // 通用验证规则
    name: [
      { required: true, message: '名称不能为空', trigger: 'blur' },
      { min: 2, max: 255, message: '名称长度在 2 到 255 个字符', trigger: 'blur' }
    ],
    stationCode: [
      { required: true, message: '编码不能为空', trigger: 'blur' },
      { min: 2, max: 100, message: '编码长度在 2 到 100 个字符', trigger: 'blur' },
      { pattern: /^[A-Za-z0-9_-]+$/, message: '编码只能包含字母、数字、下划线和短横线', trigger: 'blur' }
    ],
    plantCode: [
      { required: true, message: '编码不能为空', trigger: 'blur' },
      { min: 2, max: 100, message: '编码长度在 2 到 100 个字符', trigger: 'blur' },
      { pattern: /^[A-Za-z0-9_-]+$/, message: '编码只能包含字母、数字、下划线和短横线', trigger: 'blur' }
    ],
    reservoirCode: [
      { required: true, message: '编码不能为空', trigger: 'blur' },
      { min: 2, max: 100, message: '编码长度在 2 到 100 个字符', trigger: 'blur' },
      { pattern: /^[A-Za-z0-9_-]+$/, message: '编码只能包含字母、数字、下划线和短横线', trigger: 'blur' }
    ],
    stationType: [
      { required: true, message: '请选择类型', trigger: 'change' }
    ],
    operationMode: [
      { required: true, message: '请选择运行方式', trigger: 'change' }
    ],
    waterProject: [
      { max: 255, message: '所属供水工程长度不能超过255个字符', trigger: 'blur' }
    ],
    waterCompany: [
      { max: 255, message: '所属供水公司长度不能超过255个字符', trigger: 'blur' }
    ],
    longitude: [
      {
        validator: (rule, value, callback) => {
          // 允许空值
          if (value === '' || value === null || value === undefined) {
            callback()
            return
          }
          // 转换为数字
          const num = typeof value === 'number' ? value : Number(value)
          // 验证数字有效性和范围
          if (isNaN(num)) {
            callback(new Error('请输入有效的数字'))
          } else if (num < -180 || num > 180) {
            callback(new Error('经度范围为 -180 到 180'))
          } else {
            callback()
          }
        },
        trigger: 'change'
      }
    ],
    latitude: [
      {
        validator: (rule, value, callback) => {
          // 允许空值
          if (value === '' || value === null || value === undefined) {
            callback()
            return
          }
          // 转换为数字
          const num = typeof value === 'number' ? value : Number(value)
          // 验证数字有效性和范围
          if (isNaN(num)) {
            callback(new Error('请输入有效的数字'))
          } else if (num < -90 || num > 90) {
            callback(new Error('纬度范围为 -90 到 90'))
          } else {
            callback()
          }
        },
        trigger: 'change'
      }
    ],
    email: [
      { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
    ],
    phone: [
      { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
    ],
    unitCount: [
      {
        validator: (rule, value, callback) => {
          // 允许空值
          if (value === '' || value === null || value === undefined) {
            callback()
            return
          }
          // 转换为数字
          const num = typeof value === 'number' ? value : Number(value)
          // 验证数字有效性和范围
          if (isNaN(num)) {
            callback(new Error('请输入有效的数字'))
          } else if (num < 1) {
            callback(new Error('机组数量必须大于0'))
          } else {
            callback()
          }
        },
        trigger: 'change'
      }
    ],
    designScale: [
      {
        validator: (rule, value, callback) => {
          // 允许空值
          if (value === '' || value === null || value === undefined) {
            callback()
            return
          }
          // 转换为数字
          const num = typeof value === 'number' ? value : Number(value)
          // 验证数字有效性和范围
          if (isNaN(num)) {
            callback(new Error('请输入有效的数字'))
          } else if (num < 0) {
            callback(new Error('设计规模不能为负数'))
          } else {
            callback()
          }
        },
        trigger: 'change'
      }
    ],
    installedCapacity: [
      {
        validator: (rule, value, callback) => {
          // 允许空值
          if (value === '' || value === null || value === undefined) {
            callback()
            return
          }
          // 转换为数字
          const num = typeof value === 'number' ? value : Number(value)
          // 验证数字有效性和范围
          if (isNaN(num)) {
            callback(new Error('请输入有效的数字'))
          } else if (num < 0) {
            callback(new Error('装机容量不能为负数'))
          } else {
            callback()
          }
        },
        trigger: 'change'
      }
    ],
    liftHead: [
      {
        validator: (rule, value, callback) => {
          // 允许空值
          if (value === '' || value === null || value === undefined) {
            callback()
            return
          }
          // 转换为数字
          const num = typeof value === 'number' ? value : Number(value)
          // 验证数字有效性和范围
          if (isNaN(num)) {
            callback(new Error('请输入有效的数字'))
          } else if (num < 0) {
            callback(new Error('扬程不能为负数'))
          } else {
            callback()
          }
        },
        trigger: 'change'
      }
    ],
    length: [
      { type: 'number', min: 0, message: '长度不能为负数', trigger: 'blur' }
    ],
    totalCapacity: [
      { type: 'number', min: 0, message: '总库容不能为负数', trigger: 'blur' }
    ],
    regulatingCapacity: [
      { type: 'number', min: 0, message: '调节库容不能为负数', trigger: 'blur' }
    ],
    deadCapacity: [
      { type: 'number', min: 0, message: '死库容不能为负数', trigger: 'blur' }
    ],
    supplyPopulation: [
      { type: 'number', min: 0, message: '供水人口不能为负数', trigger: 'blur' }
    ],
    supplyLoadRatio: [
      { type: 'number', min: 0, max: 100, message: '供水负荷率范围为 0 到 100', trigger: 'blur' }
    ],
    capacity: [
      { type: 'number', min: 0, message: '容量不能为负数', trigger: 'blur' }
    ],
    powerConsumption: [
      { type: 'number', min: 0, message: '能耗不能为负数', trigger: 'blur' }
    ],
    quantity: [
      { type: 'number', min: 0, message: '数量不能为负数', trigger: 'blur' }
    ],
    administrativeCode: [
      { min: 2, max: 20, message: '行政区划代码长度在 2 到 20 个字符', trigger: 'blur' },
      { pattern: /^[0-9]+$/, message: '行政区划代码只能包含数字', trigger: 'blur' }
    ],
    currentPopulation: [
      {
        validator: (rule, value, callback) => {
          // 允许空值
          if (value === '' || value === null || value === undefined) {
            callback()
            return
          }
          // 转换为数字
          const num = typeof value === 'number' ? value : Number(value)
          // 验证数字有效性和范围
          if (isNaN(num)) {
            callback(new Error('请输入有效的数字'))
          } else if (num < 0) {
            callback(new Error('现状人口不能为负数'))
          } else {
            callback()
          }
        },
        trigger: 'change'
      }
    ]
  }
}
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

.page-container {
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

  .content-section {
    padding: var(--spacing-md);
  }

  @include respond-to(md) {
    padding: var(--spacing-md);
  }

  @include respond-to(sm) {
    padding: var(--spacing-sm);

    .content-section {
      padding: var(--spacing-sm);
    }
  }
}
</style>
