<template>
  <div v-if="!item.meta?.hidden">
    <!-- 有子菜单的情况 -->
    <el-sub-menu v-if="hasChildren" :index="item.path" :popper-append-to-body="false">
      <template #title>
        <i v-if="item.meta?.icon" class="fa" :class="item.meta.icon"></i>
        <span>{{ item.meta?.title }}</span>
      </template>

      <sidebar-item v-for="child in item.children" :key="child.path" :item="child"
        :base-path="resolvePath(child.path)" />
    </el-sub-menu>

    <!-- 没有子菜单的情况 -->
    <el-menu-item v-else :index="resolvePath(item.path)">
      <i v-if="item.meta?.icon" class="fa" :class="item.meta.icon"></i>
      <template #title>
        <span>{{ item.meta?.title }}</span>
      </template>
    </el-menu-item>
  </div>
</template>

<script setup>
import { computed } from 'vue'

/**
 * ==================== Props定义 ====================
 */
const props = defineProps({
  // 菜单项配置
  item: {
    type: Object,
    required: true
  },
  // 基础路径
  basePath: {
    type: String,
    default: ''
  }
})

/**
 * ==================== 计算属性 ====================
 */
// 判断是否有子菜单
const hasChildren = computed(() => {
  return props.item.children &&
    props.item.children.length > 0 &&
    props.item.children.some(child => !child.meta?.hidden)
})

/**
 * ==================== 工具函数 ====================
 */
// 判断是否为外部链接
const isExternal = (path) => {
  return /^(https?:|mailto:|tel:)/.test(path)
}

// 解析路径
const resolvePath = (routePath) => {
  if (isExternal(routePath)) {
    return routePath
  }

  if (isExternal(props.basePath)) {
    return props.basePath
  }

  return pathResolve(props.basePath, routePath)
}

// 简单的路径解析函数
const pathResolve = (basePath, relativePath) => {
  if (relativePath.startsWith('/')) {
    return relativePath
  }

  const baseSegments = basePath.split('/').filter(Boolean)
  const relativeSegments = relativePath.split('/').filter(Boolean)

  return '/' + [...baseSegments, ...relativeSegments].join('/')
}
</script>

<style scoped lang="scss">
/**
 * ==================== 图标样式 ====================
 */
// Font Awesome 图标样式
.fa {
  width: 20px;
  text-align: center;
  margin-right: 8px;
  font-size: 16px;
}

/**
 * ==================== 菜单样式 ====================
 */
// 子菜单图标样式
:deep(.el-sub-menu__title) .fa {
  margin-right: 8px;
}

// 菜单项图标样式
:deep(.el-menu-item) .fa {
  margin-right: 8px;
}
</style>
