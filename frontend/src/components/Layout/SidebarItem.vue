<template>
  <div v-if="!item.meta?.hidden">
    <!-- 有子菜单的情况 -->
    <el-sub-menu v-if="item.children && item.children.length > 0 && item.children.some(child => !child.meta?.hidden)"
      :index="item.path" :popper-append-to-body="false">
      <template #title>
        <i v-if="item.meta?.icon" class="fa" :class="item.meta.icon"></i>
        <span>{{ item.meta?.title }}</span>
      </template>

      <sidebar-item v-for="child in item.children" :key="child.path" :item="child" :base-path="item.path" />
    </el-sub-menu>

    <!-- 没有子菜单的情况 -->
    <el-menu-item v-else :index="item.path">
      <i v-if="item.meta?.icon" class="fa" :class="item.meta.icon"></i>
      <template #title>
        <span>{{ item.meta?.title }}</span>
      </template>
    </el-menu-item>
  </div>
</template>

<script setup>
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
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

/**
 * ==================== 图标样式 ====================
 */
// Font Awesome 图标样式
.fa {
  font-size: var(--font-size-medium);
  color: var(--text-primary);
  width: var(--spacing-large);
  text-align: center;
  margin-right: var(--spacing-small);
}
</style>
