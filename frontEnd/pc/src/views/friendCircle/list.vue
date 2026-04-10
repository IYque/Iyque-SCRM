<script setup lang="ts">
import { ref, onMounted, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import stores from '@/stores'
import { getFriendCircleList, deleteFriendCircle } from './api'
import Pagination from '@/components/Pagination'
import TableOperateBtn from '@/components/TableOperateBtn'
import SearchResetButton from '@/components/SearchResetButton'

const $router = useRouter()
const $store = stores()

// 表格数据
const tableData = ref([])
// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})
// 搜索条件
const searchForm = reactive({
  name: ''
})

// 加载列表数据
async function loadData() {
  $store.loading = true
  try {
    const response = await getFriendCircleList({
      name: searchForm.name,
      page: pagination.currentPage,
      size: pagination.pageSize
    })
    tableData.value = response.data
    pagination.total = response.count
  } catch (error) {
    console.error('加载朋友圈列表失败:', error)
  } finally {
    $store.loading = false
  }
}

// 搜索
function handleSearch() {
  pagination.currentPage = 1
  loadData()
}

// 重置
function handleReset() {
  searchForm.name = ''
  handleSearch()
}

// 新增
function handleAdd() {
  $router.push('/marketing/marketingTools/friendCircle/aev')
}

// 编辑
function handleEdit(row: any) {
  $router.push(`/marketing/marketingTools/friendCircle/aev?id=${row.id}`)
}

// 删除
function handleDelete(row: any) {
  $store.$sdk.confirm(`确定要删除朋友圈 "${row.name}" 吗？`).then(() => {
    $store.loading = true
    deleteFriendCircle(row.id).then(() => {
      $store.$sdk.msgSuccess('删除成功')
      loadData()
    }).catch((error: any) => {
      $store.$sdk.msgError(error.message || '删除失败')
    }).finally(() => {
      $store.loading = false
    })
  })
}

// 分页变化
function handleSizeChange(size: number) {
  pagination.pageSize = size
  loadData()
}

function handleCurrentChange(current: number) {
  pagination.currentPage = current
  loadData()
}

// 初始化加载数据
onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">朋友圈管理</div>
    </div>
    
    <div class="page-body">
      <div class="g-card">
        <div class="g-card-body">
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="朋友圈名称">
              <el-input v-model="searchForm.name" placeholder="请输入朋友圈名称" clearable></el-input>
            </el-form-item>
            <el-form-item>
              <SearchResetButton @search="handleSearch" @reset="handleReset" />
            </el-form-item>
          </el-form>
        </div>
      </div>
      
      <div class="g-card mt10">
        <div class="g-card-header flex between items-center">
          <el-button type="primary" @click="handleAdd">新增</el-button>
        </div>
        <div class="g-card-body">
          <el-table :data="tableData" style="width: 100%" v-loading="$store.loading">
            <el-table-column prop="name" label="朋友圈名称" min-width="180"></el-table-column>
            <el-table-column prop="content" label="文本内容" min-width="300">
              <template #default="{ row }">
                <span class="ellipsis">{{ row.content }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <TableOperateBtn>
                  <el-button type="primary" text size="small" @click="handleEdit(row)">
                    编辑
                  </el-button>
                  <el-button type="danger" text size="small" @click="handleDelete(row)">
                    删除
                  </el-button>
                </TableOperateBtn>
              </template>
            </el-table-column>
          </el-table>
          
          <div class="flex justify-end mt10">
            <Pagination
              v-model:currentPage="pagination.currentPage"
              v-model:pageSize="pagination.pageSize"
              :total="pagination.total"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.page-header {
  padding: 16px 0;
  border-bottom: 1px solid #e8e8e8;
}

.page-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.page-body {
  flex: 1;
  padding: 16px 0;
  overflow: auto;
}

.search-form {
  margin-bottom: 0;
}

.ellipsis {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>