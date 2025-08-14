<!-- 自定义响应式表格组件 - 完全移除Element Plus表格依赖 -->
<template>
  <div class="custom-table">


    <!-- 表格主体 -->
    <div class="table-content">
      <div class="table-container">
        <!-- 加载状态 -->
        <div v-if="loading" class="table-loading">
          <i class="fa fa-spinner fa-spin"></i>
          <span>加载中...</span>
        </div>

        <!-- 自定义表格 -->
        <table v-else class="responsive-table" :class="{
          'table-striped': stripe,
          'table-bordered': border,
          [`table-${size}`]: size !== 'default'
        }" :style="tableStyles">
          <!-- 表头 -->
          <thead>
            <tr>
              <!-- 选择列 -->
              <th v-if="showSelection" class="selection-col">
                <el-checkbox v-if="selectMode === 'multiple'" :model-value="isAllSelected"
                  :indeterminate="isIndeterminate" @change="handleSelectAll" />
                <span v-else></span>
              </th>

              <!-- 序号列 -->
              <th v-if="showIndex" class="index-col">
                {{ indexLabel }}
              </th>

              <!-- 数据列（过滤掉ID列） -->
              <th v-for="column in filteredColumns" :key="column.prop" :class="getColumnClass(column)"
                :style="getColumnStyle(column)" @click="column.sortable ? handleHeaderClick(column) : null">
                <slot :name="`header-${column.prop}`" :column="column">
                  <span>{{ column.label }}</span>
                  <span v-if="column.sortable" class="sort-icons">
                    <i class="fa fa-caret-up"
                      :class="{ active: sortState.prop === column.prop && sortState.order === 'ascending' }"></i>
                    <i class="fa fa-caret-down"
                      :class="{ active: sortState.prop === column.prop && sortState.order === 'descending' }"></i>
                  </span>
                </slot>
              </th>

              <!-- 操作列 -->
              <th v-if="showActions" class="actions-col">
                {{ actionsLabel }}
              </th>
            </tr>
          </thead>

          <!-- 表体 -->
          <tbody>
            <tr v-for="(row, index) in tableData" :key="getRowKey(row, index)"
              :class="{ 'row-selected': isRowSelected(row) }" @click="handleRowClick(row, null, $event)"
              @dblclick="handleRowDblClick(row, null, $event)"
              @contextmenu="enableContextMenu ? handleRowContextMenu(row, $event) : null">
              <!-- 选择列 -->
              <td v-if="showSelection" class="selection-col">
                <el-checkbox v-if="selectMode === 'multiple'" :model-value="isRowSelected(row)"
                  @change="(checked) => handleSelectRow(row, checked)" :disabled="!selectableFunction(row, index)" />
                <el-radio v-else v-model="selectedRowKey" :label="getRowKey(row)"
                  :disabled="!selectableFunction(row, index)" @change="() => handleRadioSelect(row)" />
              </td>

              <!-- 序号列 -->
              <td v-if="showIndex" class="index-col">
                {{ getIndex(index) }}
              </td>

              <!-- 数据列 -->
              <td v-for="column in filteredColumns" :key="column.prop" :class="getColumnClass(column)"
                :style="getColumnStyle(column)"
                :title="showOverflowTooltip || column.showOverflowTooltip ? row[column.prop] : ''">
                <slot :name="column.prop" :row="row" :column="column" :index="index">
                  <span v-if="column.formatter">
                    {{ column.formatter(row, column, row[column.prop], index) }}
                  </span>
                  <span v-else class="cell-content"
                    :class="{ 'ellipsis': showOverflowTooltip || column.showOverflowTooltip }">
                    {{ row[column.prop] }}
                  </span>
                </slot>
              </td>

              <!-- 操作列 -->
              <td v-if="showActions" class="actions-col">
                <div class="action-buttons">
                  <slot name="actions" :row="row" :index="index">
                    <CustomButton v-if="showEdit" type="text" text-type="primary" size="small"
                      @click.stop="handleEdit(row, index)">
                      编辑
                    </CustomButton>
                    <CustomButton v-if="showDelete" type="text" text-type="danger" size="small"
                      @click.stop="handleDelete(row, index)">
                      删除
                    </CustomButton>
                  </slot>
                </div>
              </td>
            </tr>

            <!-- 空数据提示 -->
            <tr v-if="tableData.length === 0" class="empty-row">
              <td :colspan="totalColumns" class="empty-cell">
                <div class="empty-content">
                  <i class="fa fa-file-o"></i>
                  <span>{{ emptyText }}</span>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>


  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { ElMessageBox } from 'element-plus'
import CustomButton from './CustomButton.vue'
// 使用Font Awesome图标，无需导入

/**
 * ----------------------------------------
 * 组件属性定义
 * ----------------------------------------
 */
const props = defineProps({
  // 表格数据配置
  data: {
    type: Array,
    default: () => []
  },
  columns: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  emptyText: {
    type: String,
    default: '暂无数据'
  },

  // 表格样式配置
  height: {
    type: [String, Number],
    default: undefined
  },
  maxHeight: {
    type: [String, Number],
    default: undefined
  },
  stripe: {
    type: Boolean,
    default: true
  },
  border: {
    type: Boolean,
    default: true
  },
  size: {
    type: String,
    default: 'default'
  },
  highlightCurrentRow: {
    type: Boolean,
    default: false
  },
  showOverflowTooltip: {
    type: Boolean,
    default: false
  },

  // 行数据配置
  rowKey: {
    type: [String, Function],
    default: 'id'
  },
  defaultExpandAll: {
    type: Boolean,
    default: false
  },
  treeProps: {
    type: Object,
    default: () => ({ children: 'children', hasChildren: 'hasChildren' })
  },
  selectableFunction: {
    type: Function,
    default: () => true
  },

  // 选择模式
  selectMode: {
    type: String,
    default: 'multiple',
    validator: (value) => ['multiple', 'radio'].includes(value)
  },

  // 列显示配置
  showSelection: {
    type: Boolean,
    default: false
  },
  showIndex: {
    type: Boolean,
    default: false
  },
  indexLabel: {
    type: String,
    default: '序号'
  },
  showExpand: {
    type: Boolean,
    default: false
  },

  // 操作列配置
  showActions: {
    type: Boolean,
    default: true
  },
  actionsLabel: {
    type: String,
    default: '操作'
  },
  actionsWidth: {
    type: [String, Number],
    default: undefined
  },
  actionsMinWidth: {
    type: [String, Number],
    default: 120
  },
  actionsFixed: {
    type: [String, Boolean],
    default: 'right'
  },
  showEdit: {
    type: Boolean,
    default: true
  },
  showDelete: {
    type: Boolean,
    default: true
  },

  // 分页配置（保留部分属性用于序号计算）
  showPagination: {
    type: Boolean,
    default: false
  },
  currentPage: {
    type: Number,
    default: 1
  },
  pageSize: {
    type: Number,
    default: 10
  },

  // 交互配置
  enableContextMenu: {
    type: Boolean,
    default: false
  }
})

/**
 * ----------------------------------------
 * 事件定义
 * ----------------------------------------
 */
const emit = defineEmits([
  'edit',
  'delete',
  'selection-change',
  'row-click',
  'row-dblclick',
  'sort-change',
  'row-contextmenu'
])

/**
 * ----------------------------------------
 * 响应式数据状态
 * ----------------------------------------
 */
// 选择状态
const selectedRows = ref([])
// 单选按钮选中的行Key
const selectedRowKey = ref(null)

// 排序状态
const sortState = ref({
  prop: null,
  order: null
})

// 设备检测状态
const isMobile = ref(false)

/**
 * ----------------------------------------
 * 计算属性
 * ----------------------------------------
 */
// 表格数据
const tableData = computed(() => props.data)

// 过滤掉ID列的列配置
const filteredColumns = computed(() => {
  return props.columns.filter(col =>
    col.visible !== false &&
    col.prop !== 'id' // 彻底删除ID列
  )
})

// 选择状态计算
const isAllSelected = computed(() => {
  return tableData.value.length > 0 &&
    selectedRows.value.length === tableData.value.length
})

const isIndeterminate = computed(() => {
  return selectedRows.value.length > 0 &&
    selectedRows.value.length < tableData.value.length
})

// 表格布局计算
const totalColumns = computed(() => {
  let count = filteredColumns.value.length
  if (props.showSelection) count++
  if (props.showIndex) count++
  if (props.showActions) count++
  return count
})



const tableStyles = computed(() => {
  const styles = {}
  if (props.actionsWidth) {
    styles['--actions-width'] = typeof props.actionsWidth === 'number'
      ? `${props.actionsWidth}px`
      : props.actionsWidth
  }
  return styles
})

/**
 * ----------------------------------------
 * 设备检测和响应式处理
 * ----------------------------------------
 */
// 检查是否为移动端
const checkMobile = () => {
  isMobile.value = window.innerWidth <= 768
}

/**
 * ----------------------------------------
 * 工具方法
 * ----------------------------------------
 */
// 获取序号
const getIndex = (index) => {
  // 确保即使在没有分页的情况下也能正确计算序号
  const currentPage = props.currentPage || 1
  const pageSize = props.pageSize || 10
  return (currentPage - 1) * pageSize + index + 1
}

// 获取行的唯一标识
const getRowKey = (row, index) => {
  if (typeof props.rowKey === 'function') {
    return props.rowKey(row)
  }
  return row[props.rowKey] || index
}

// 获取列的CSS类名
const getColumnClass = (column) => {
  const classes = [`col-${column.prop}`]
  if (column.className) {
    classes.push(column.className)
  }
  return classes.join(' ')
}

// 获取列的样式
const getColumnStyle = (column) => {
  const style = {}

  // 应用列的对齐方式（默认居中）
  style.textAlign = column.align || 'center'

  // 如果是可排序列，添加指针样式
  if (column.sortable) {
    style.cursor = 'pointer'
  }

  // 应用列宽度
  if (column.width) {
    style.width = typeof column.width === 'number' ? `${column.width}px` : column.width
    style.minWidth = typeof column.width === 'number' ? `${column.width}px` : column.width
  } else if (column.minWidth) {
    style.minWidth = typeof column.minWidth === 'number' ? `${column.minWidth}px` : column.minWidth
  }

  return style
}

// 检查行是否被选中
const isRowSelected = (row) => {
  return selectedRows.value.some(selectedRow =>
    getRowKey(selectedRow) === getRowKey(row)
  )
}

/**
 * ----------------------------------------
 * 选择处理方法
 * ----------------------------------------
 */
// 全选/取消全选
const handleSelectAll = (checked) => {
  if (checked) {
    selectedRows.value = [...tableData.value]
  } else {
    selectedRows.value = []
  }
  emit('selection-change', selectedRows.value)
}

// 单行选择/取消选择
const handleSelectRow = (row, checked) => {
  const rowKey = getRowKey(row)

  if (props.selectMode === 'radio' && checked) {
    // 在单选模式下，清除之前的选择，只保留当前行
    selectedRows.value = [row]
    emit('selection-change', selectedRows.value)
    return
  }

  if (checked) {
    if (!isRowSelected(row)) {
      selectedRows.value.push(row)
    }
  } else {
    const index = selectedRows.value.findIndex(selectedRow =>
      getRowKey(selectedRow) === rowKey
    )
    if (index > -1) {
      selectedRows.value.splice(index, 1)
    }
  }
  emit('selection-change', selectedRows.value)
}

/**
 * ----------------------------------------
 * 单选按钮处理方法
 * ----------------------------------------
 */
// 单选按钮选中处理
const handleRadioSelect = (row) => {
  // 清除之前的选择，只保留当前行
  selectedRows.value = [row]
  emit('selection-change', selectedRows.value)
}

// 监听selectedRows变化，更新selectedRowKey
watch(selectedRows, (newRows) => {
  if (props.selectMode === 'radio') {
    if (newRows.length > 0) {
      selectedRowKey.value = getRowKey(newRows[0])
    } else {
      selectedRowKey.value = null
    }
  }
}, { deep: true })

/**
 * ----------------------------------------
 * 操作处理方法
 * ----------------------------------------
 */
// 编辑操作
const handleEdit = (row, index) => {
  emit('edit', row, index)
}

// 删除操作
const handleDelete = (row, index) => {
  ElMessageBox.confirm(
    '确定要删除这条记录吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    emit('delete', row, index)
  }).catch(() => {
    // 用户取消删除
  })
}

/**
 * ----------------------------------------
 * 行事件处理方法
 * ----------------------------------------
 */
// 行点击事件
const handleRowClick = (row, column, event) => {
  emit('row-click', row, column, event)
}

// 行双击事件
const handleRowDblClick = (row, column, event) => {
  emit('row-dblclick', row, column, event)
}



/**
 * ----------------------------------------
 * 排序处理方法
 * ----------------------------------------
 */
// 表头点击排序
const handleHeaderClick = (column) => {
  if (!column.sortable) return

  let order = 'ascending'
  if (sortState.value.prop === column.prop) {
    if (sortState.value.order === 'ascending') {
      order = 'descending'
    } else if (sortState.value.order === 'descending') {
      order = null
    }
  }

  sortState.value = {
    prop: order ? column.prop : null,
    order: order
  }

  emit('sort-change', { column, prop: column.prop, order })
}

/**
 * ----------------------------------------
 * 右键菜单处理方法
 * ----------------------------------------
 */
// 右键菜单处理
const handleRowContextMenu = (row, event) => {
  event.preventDefault()
  emit('row-contextmenu', row, event)
}

/**
 * ----------------------------------------
 * 公共方法（暴露给父组件）
 * ----------------------------------------
 */
// 清空选择
const clearSelection = () => {
  selectedRows.value = []
  emit('selection-change', selectedRows.value)
}

// 切换行选择状态
const toggleRowSelection = (row, selected) => {
  handleSelectRow(row, selected)
}

// 设置全选
const toggleAllSelection = () => {
  handleSelectAll(!isAllSelected.value)
}

/**
 * ----------------------------------------
 * 生命周期钩子
 * ----------------------------------------
 */
onMounted(() => {
  checkMobile()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})

// 窗口大小变化监听
const handleResize = () => {
  checkMobile()
}

/**
 * ----------------------------------------
 * 组件暴露方法
 * ----------------------------------------
 */
defineExpose({
  clearSelection,
  toggleRowSelection,
  toggleAllSelection,
  selectedRows
})
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

/**
 * ----------------------------------------
 * 表格容器基础样式
 * ----------------------------------------
 */
.custom-table {
  display: flex;
  flex-direction: column;
  height: 100%;

  /**
   * 表格内容区域
   */
  .table-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    position: relative;

    /**
     * 加载状态样式
     */
    .table-loading {
      @include absolute-center;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: var(--spacing-small);
      color: var(--el-text-color-secondary);
      z-index: var(--z-index-base);
    }

    /**
     * 表格容器样式
     */
    .table-container {
      flex: 1;
      width: 100%;
      height: 100%; // 确保容器填充父元素的固定高度
      overflow-x: visible;
      overflow-y: auto; // 允许表格内容滚动

      /**
       * ----------------------------------------
       * 响应式表格基础样式
       * ----------------------------------------
       */
      .responsive-table {
        width: 100%;
        table-layout: fixed; // 使用固定布局以便更好地控制列宽
        border-collapse: collapse;
        background-color: var(--el-bg-color);

        /**
         * 表头样式
         */
        thead {
          background-color: var(--el-fill-color-light);

          th {
            padding: var(--spacing-medium) var(--spacing-small);
            border: 1px solid var(--el-border-color-light);
            font-weight: var(--font-weight-medium);
            color: var(--el-text-color-primary);
            text-align: center;
            white-space: normal;
            word-wrap: break-word;
            word-break: break-all;
            height: var(--table-header-height); // 固定表头高度
          }
        }

        /**
         * 表体样式
         */
        tbody {
          tr {
            transition: background-color var(--transition-duration-fast);
            cursor: pointer;

            &:hover {
              background-color: var(--el-fill-color-light);
            }

            &.row-selected {
              background-color: var(--el-color-primary-light-9);
            }

            td {
              padding: var(--spacing-medium) var(--spacing-small);
              border: 1px solid var(--el-border-color-light);
              color: var(--el-text-color-primary);
              white-space: normal;
              word-wrap: break-word;
              word-break: break-all;
              vertical-align: middle;
              min-height: var(--form-item-height);
            }
          }

          /**
           * 空数据行样式
           */
          .empty-row {
            &:hover {
              background-color: transparent;
            }

            .empty-cell {
              text-align: center;
              padding: calc(var(--spacing-extra-large) * 1.7) var(--spacing-large);
              border: 1px solid var(--el-border-color-light);

              .empty-content {
                display: flex;
                flex-direction: column;
                align-items: center;
                gap: var(--spacing-small);
                color: var(--el-text-color-secondary);

                .fa {
                  font-size: var(--icon-size-xxl);
                }
              }
            }
          }
        }

        /**
         * ----------------------------------------
         * 表格主题样式
         * ----------------------------------------
         */
        // 斑马纹样式
        &.table-striped {
          tbody tr:nth-child(even) {
            background-color: var(--el-fill-color-lighter);
          }
        }

        // 边框样式
        &.table-bordered {
          border: 1px solid var(--el-border-color);
        }

        /**
         * ----------------------------------------
         * 表格尺寸样式
         * ----------------------------------------
         */
        &.table-small {

          thead th,
          tbody td {
            padding: var(--spacing-small) var(--spacing-xs);
          }
        }

        &.table-large {

          thead th,
          tbody td {
            padding: var(--spacing-base) var(--spacing-medium);
          }
        }

        /**
         * ----------------------------------------
         * 固定列样式定义 - 使用更高优先级确保生效
         * ----------------------------------------
         */
        .selection-col {
          width: calc(var(--form-item-height) + var(--spacing-sm)) !important;
          min-width: calc(var(--form-item-height) + var(--spacing-sm)) !important;
          text-align: center;
        }

        .index-col {
          width: calc(var(--form-item-height) + var(--spacing-sm)) !important;
          min-width: calc(var(--form-item-height) + var(--spacing-sm)) !important;
          text-align: center;
        }

        .actions-col {
          width: var(--actions-width, 120px) !important;
          min-width: var(--actions-width, 120px) !important;
          text-align: center;
          white-space: nowrap;

          .action-buttons {
            @include flex-center;
            gap: var(--spacing-mini);
            flex-wrap: wrap;
            min-height: calc(var(--spacing-base) * 2);

            .custom-button {
              margin: 0;

              &.custom-button--small {
                padding: var(--spacing-5) var(--spacing-small);
                font-size: var(--font-size-extra-small);
              }
            }
          }

          @include respond-to(lg) {
            .actions-col .action-buttons {
              gap: var(--spacing-micro);
            }

            .actions-col .action-buttons .custom-button {
              padding: var(--spacing-mini) var(--spacing-xs);
              font-size: var(--font-size-extra-small);
            }
          }
        }

        /**
         * ----------------------------------------
         * 内容列宽度定义
         * 注意：这些预定义的宽度可能与动态设置的列宽冲突
         * 如果遇到冲突，请检查表格列配置是否正确设置了宽度
         * ----------------------------------------
         */

        /* 确保内联样式的宽度优先级高于预定义的列宽类 */
        th[style*="width"],
        td[style*="width"] {
          width: attr(style width) !important;
          min-width: attr(style width) !important;
        }

        // 用户管理页面列宽
        .col-username {
          width: calc(15% - 2px);
          min-width: 100px;
        }

        .col-fullName {
          width: calc(12% - 2px);
          min-width: var(--button-min-width);
        }

        .col-phone {
          width: calc(15% - 2px);
          min-width: 120px;
        }

        .col-email {
          width: calc(18% - 2px);
          min-width: 150px;
        }

        .col-departmentName {
          width: calc(12% - 2px);
          min-width: 100px;
        }

        .col-positionName {
          width: calc(10% - 2px);
          min-width: var(--button-min-width);
        }

        .col-roleName {
          width: calc(8% - 2px);
          min-width: 70px;
        }

        .col-isActive {
          width: calc(8% - 2px);
          min-width: 60px;
        }

        .col-createdAt {
          width: calc(12% - 2px);
          min-width: 120px;
        }

        // 角色管理页面列宽
        .col-name {
          width: calc(20% - 2px);
          min-width: 120px;
        }

        .col-code {
          width: calc(20% - 2px);
          min-width: 120px;
        }

        .col-description {
          width: calc(30% - 2px);
          min-width: var(--panel-min-width-collapsed);
        }

        .col-sortOrder {
          width: calc(10% - 2px);
          min-width: 60px;
        }

        // 通用列样式 - 彻底隐藏ID列
        .col-id {
          display: none !important;
        }

        /**
         * ----------------------------------------
         * 表格交互元素样式
         * ----------------------------------------
         */
        // 排序图标样式
        .sort-icons {
          margin-left: var(--spacing-mini);
          display: inline-flex;
          flex-direction: column;

          i {
            font-size: var(--icon-size-xs);
            color: var(--el-text-color-placeholder);
            line-height: 1;

            &.active {
              color: var(--el-color-primary);
            }

            &:first-child {
              margin-bottom: calc(var(--spacing-micro) * -1);
            }
          }
        }

        // 溢出省略样式
        .cell-content.ellipsis {
          @include text-ellipsis(1);
          max-width: 100%;
          display: block;
        }
      }

      /**
       * ----------------------------------------
       * 响应式布局样式
       * ----------------------------------------
       */
      // 大屏幕自适应（Chrome兼容性优化）
      // 大于lg断点的样式使用标准写法
      .responsive-table {
        width: 100%;
        table-layout: fixed; // 保持固定布局以便控制列宽
      }

      // 响应式断点控制
      @include respond-to(lg) {
        overflow-x: scroll;
        -webkit-overflow-scrolling: touch;

        .responsive-table {
          min-width: #{$breakpoint-lg}; // 保持最小宽度，触发滚动
          table-layout: fixed; // 小屏幕使用固定布局
        }
      }
    }
  }


}
</style>
