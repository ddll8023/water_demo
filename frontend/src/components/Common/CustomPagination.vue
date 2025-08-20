<template>
    <div class="custom-pagination" v-if="totalPages > 0">
        <!-- 总条目数 -->
        <span class="total-items" v-if="layout.includes('total')">共 {{ total }} 条</span>

        <!-- 每页数量选择器 -->
        <select v-if="layout.includes('sizes')" class="size-selector" :value="pageSize" @change="handleSizeChange">
            <option v-for="size in pageSizes" :key="size" :value="size">{{ size }} 条/页</option>
        </select>

        <!-- 导航按钮和页码 -->
        <div class="pagination-nav">
            <CustomButton type="secondary" size="small" :disabled="currentPage === 1" @click="prevPage">
                < </CustomButton>
                    <ul class="page-list">
                        <template v-for="(page, index) in pageNumbers" :key="index">
                            <li v-if="page === '...'" class="page-item ellipsis">
                                {{ page }}
                            </li>
                            <li v-else class="page-button-wrapper">
                                <CustomButton :type="page === currentPage ? 'primary' : 'secondary'" size="small"
                                    @click="goToPage(page)">
                                    {{ page }}
                                </CustomButton>
                            </li>
                        </template>
                    </ul>
                    <CustomButton type="secondary" size="small" :disabled="currentPage === totalPages"
                        @click="nextPage">
                        >
                    </CustomButton>
        </div>

        <!-- 跳转输入框 -->
        <div class="jumper" v-if="layout.includes('jumper')">
            前往
            <input type="number" class="jumper-input" :value="currentPage" @keyup.enter="handleJumper"
                @blur="handleJumper" />
            页
        </div>
    </div>
</template>

<script setup>
import { defineProps, defineEmits, computed } from 'vue'
import CustomButton from './CustomButton.vue'

// 定义组件的 props
const props = defineProps({
    currentPage: {
        type: Number,
        required: true
    },
    pageSize: {
        type: Number,
        required: true
    },
    total: {
        type: Number,
        required: true
    },
    pageSizes: {
        type: Array,
        default: () => [10, 20, 50, 100]
    },
    layout: {
        type: String,
        default: 'total, sizes, prev, pager, next, jumper'
    },
    // 显示的页码按钮数量
    pagerCount: {
        type: Number,
        default: 7
    }
})

// 定义组件的 emits
const emit = defineEmits(['update:currentPage', 'update:pageSize', 'size-change', 'current-change'])

// 计算总页数
const totalPages = computed(() => {
    return Math.ceil(props.total / props.pageSize)
})

// 计算要显示的页码数组
const pageNumbers = computed(() => {
    const pagerCount = props.pagerCount
    const halfPagerCount = Math.floor(pagerCount / 2)
    const currentPage = props.currentPage
    const total = totalPages.value

    if (total <= pagerCount) {
        return Array.from({ length: total }, (_, i) => i + 1)
    }

    const pages = []
    let showPrevEllipsis = false
    let showNextEllipsis = false

    if (currentPage > pagerCount - halfPagerCount) {
        showPrevEllipsis = true
    }

    if (currentPage < total - halfPagerCount) {
        showNextEllipsis = true
    }

    if (showPrevEllipsis && !showNextEllipsis) {
        const startPage = total - pagerCount + 2
        pages.push(1)
        pages.push('...')
        for (let i = startPage; i <= total; i++) {
            pages.push(i)
        }
    } else if (!showPrevEllipsis && showNextEllipsis) {
        for (let i = 1; i < pagerCount - 1; i++) {
            pages.push(i)
        }
        pages.push('...')
        pages.push(total)
    } else if (showPrevEllipsis && showNextEllipsis) {
        const offset = Math.floor((pagerCount - 2) / 2)
        pages.push(1)
        pages.push('...')
        for (let i = currentPage - offset; i <= currentPage + offset; i++) {
            pages.push(i)
        }
        pages.push('...')
        pages.push(total)
    } else {
        for (let i = 1; i <= total; i++) {
            pages.push(i)
        }
    }

    return pages
})


// 跳转到指定页
const goToPage = (page) => {
    if (page === '...' || page === props.currentPage) return
    if (page < 1) page = 1
    if (page > totalPages.value) page = totalPages.value
    emit('update:currentPage', page)
    emit('current-change', page)
}

// 上一页
const prevPage = () => {
    if (props.currentPage > 1) {
        goToPage(props.currentPage - 1)
    }
}

// 下一页
const nextPage = () => {
    if (props.currentPage < totalPages.value) {
        goToPage(props.currentPage + 1)
    }
}

// 处理每页数量变化
const handleSizeChange = (event) => {
    const newSize = Number(event.target.value)
    emit('update:pageSize', newSize)
    emit('size-change', newSize)
    // 当页面大小改变时，通常需要回到第一页
    goToPage(1)
}

// 处理跳转输入框
const handleJumper = (event) => {
    const page = Number(event.target.value)
    if (!isNaN(page)) {
        goToPage(page)
    }
}
</script>

<style scoped lang="scss">
@use "@/assets/styles/index.scss" as *;

.custom-pagination {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: var(--spacing-medium);
    font-size: var(--font-size-base);
    color: var(--text-secondary);
    margin-top: var(--spacing-large);

    .total-items {
        margin-right: var(--spacing-small);
    }

    .size-selector {
        padding: 6px 12px;
        border: 1px solid var(--border-color);
        border-radius: var(--border-radius-base);
        cursor: pointer;

        &:focus {
            outline: none;
            border-color: var(--primary-color);
        }
    }

    .pagination-nav {
        display: flex;
        align-items: center;
        gap: var(--spacing-small);
    }



    .page-list {
        display: flex;
        list-style: none;
        padding: 0;
        margin: 0;
        gap: var(--spacing-small);

        .page-item {
            &.ellipsis {
                min-width: 32px;
                height: 32px;
                padding: 0 var(--spacing-xs);
                display: flex;
                align-items: center;
                justify-content: center;
                border: none;
                cursor: default;
            }
        }

        .page-button-wrapper {
            display: flex;
        }
    }

    .jumper {
        display: flex;
        align-items: center;
        gap: var(--spacing-small);

        .jumper-input {
            width: 50px;
            padding: 6px 12px;
            border: 1px solid var(--border-color);
            border-radius: var(--border-radius-base);
            text-align: center;

            &:focus {
                outline: none;
                border-color: var(--primary-color);
            }
        }
    }

    /* 响应式适配 */
    @include respond-to(md) {
        flex-wrap: wrap;
        justify-content: center;

        .total-items {
            order: -1;
            width: 100%;
            text-align: center;
            margin-bottom: var(--spacing-small);
        }

        .size-selector {
            order: 1;
        }

        .jumper {
            order: 2;
        }
    }

    @include respond-to(sm) {
        gap: var(--spacing-mini);

        .page-list {
            gap: var(--spacing-mini);

            .page-button-wrapper:not(:first-child):not(:last-child) {
                display: none;
            }

            .page-item.ellipsis {
                min-width: 28px;
                height: 28px;
            }
        }

        .jumper {
            .jumper-input {
                width: 40px;
                padding: var(--spacing-micro) var(--spacing-mini);
            }
        }

        .size-selector {
            padding: var(--spacing-micro) var(--spacing-mini);
        }
    }
}
</style>
