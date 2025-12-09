<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getList, synchCustomer, getCustomerTags, tagCustomers } from './api'
import TagEllipsis from '@/components/TagEllipsis'

const props = defineProps({
  isSelect: { type: Boolean, default: false },
})

const emit = defineEmits(['selectionChange'])

const rctRef = ref()
const dialogRefDetail = ref()
const tagDialogVisible = ref(false)
const customerTags = ref([])
const selectedTags = ref([])
const selectedCustomerIds = ref([])
const selectedCustomerUserId = ref('')
const selectedRows = ref([])

// 路由跳转
const goRoute = (row) => {
  $router.push({ path: `/customer/detail/${row.customerId}` })
}

const customerType = Object.freeze({ 1: '微信客户', 2: '企业客户' })

// 获取客户标签列表
const loadCustomerTags = async () => {
  try {
    const res = await getCustomerTags({})
    if (res.code === 200) {
      customerTags.value = res.data || []
    }
  } catch (error) {
    console.error('获取客户标签失败:', error)
  }
}

// 单个客户打标签按钮点击事件
const handleTagCustomer = async (row) => {
  // 设置当前要打标签的客户externalUserid
  selectedCustomerIds.value = [row.externalUserid]
  // 设置当前要打标签的客户userId
  selectedCustomerUserId.value = row.userId || ''
  
  // 确保标签列表已经加载
  if (customerTags.value.length === 0) {
    await loadCustomerTags()
  }
  
  // 加载客户当前已有的标签，将逗号分隔的字符串转换为数组，并确保与标签列表中的ID类型一致
  if (row.tagIds) {
    const tagIdArray = row.tagIds.split(',').map(id => id.trim())
    // 过滤出存在于标签列表中的标签ID，确保能正确显示标签名
    selectedTags.value = tagIdArray.filter(tagId => 
      customerTags.value.some(tag => tag.id === tagId)
    )
  } else {
    selectedTags.value = []
  }
  
  // 打开标签选择对话框
  tagDialogVisible.value = true
}

// 提交标签
const submitTags = async () => {
  if (selectedTags.value.length === 0) {
    $sdk.msgWarning('请选择标签')
    return
  }
  
  try {
    const res = await tagCustomers({
      externalUserid: selectedCustomerIds.value[0], // 使用数组第一个元素作为externalUserid
      tagIds: selectedTags.value,
      userId: selectedCustomerUserId.value // 传递userId
    })
    
    if (res.code === 200) {
      $sdk.msgSuccess('打标签成功')
      tagDialogVisible.value = false
      // 刷新客户列表
      rctRef.value?.getList()
    } else {
      $sdk.msgError(res.msg || '打标签失败')
    }
  } catch (error) {
    console.error('打标签失败:', error)
    $sdk.msgError('打标签失败')
  }
}

// 监听选中项变化
const handleSelectionChange = (val) => {
  selectedRows.value = val
  emit('selectionChange', val)
}



// 组件挂载时加载标签列表
onMounted(() => {
  loadCustomerTags()
})
</script>

<template>
  <!-- <div class="warning"></div> -->

  <div :_="$store.setBusininessDesc(`<div>查看当前企业所有的客户及详细信息</div>`)">
    <RequestChartTable
      ref="rctRef"
      :request="getList"
      searchBtnType="icon"
      @selectionChange="(val) => $emit('selectionChange', val)">
      <template #query="{ query }">
        <el-form-item label="客户名称" prop="customerName">
          <el-input v-model="query.customerName" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="客户类型" prop="type">
          <el-select v-model="query.type" :popper-append-to-body="false">
            <el-option v-for="(value, key) in customerType" :key="key" :label="value" :value="key" />
          </el-select>
        </el-form-item>
      </template>

      <template #operation="{ selectedIds }" v-if="!isSelect">
        <el-button type="primary" @click="synchCustomer().then(() => $refs.rctRef.getList(), $sdk.msgSuccess())">
          同步
        </el-button>
      </template>

      <template #table="{ data, apiConfirm, goRoute }">
        <el-table-column label="客户" prop="customerName" header-align="center" align="" width="180">
          <template #default="{ row }">
            <CustomerInfoCell :data="row" @click="goRoute(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="tagNames" label="客户标签" align="center" width="220">
          <template #default="{ row }">
            <TagEllipsis :list="row.tagNames || []" emptyText="无标签"></TagEllipsis>
          </template>
        </el-table-column>
        <el-table-column label="跟进员工" min-width="100" prop="userName">
          <!-- <template #default="{ row }">
            {{ row.switchUserName ? row.switchUserName : '-' }}
          </template> -->
        </el-table-column>
        <el-table-column prop="addWay" label="客户来源"></el-table-column>
        <el-table-column label="添加时间" prop="addTime" width=""></el-table-column>
        <el-table-column label="操作" fixed="right" width="80">
          <template #default="{ row }">
            <el-button text @click="handleTagCustomer(row)">打标签</el-button>
          </template>
        </el-table-column>
      </template>
    </RequestChartTable>

    <!-- 标签选择对话框 -->
    <el-dialog
      v-model="tagDialogVisible"
      title="选择标签"
      width="500px"
      @close="() => selectedTags = []"
    >
      <div class="tag-selector">
        <el-select
          v-model="selectedTags"
          multiple
          placeholder="请选择标签"
          style="width: 100%"
          :popper-append-to-body="false"
          :fit-input-width="false"
        >
          <el-option
            v-for="tag in customerTags"
            :key="tag.id"
            :label="tag.name"
            :value="tag.id"
          ></el-option>
        </el-select>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="tagDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitTags">确定</el-button>
        </span>
      </template>
    </el-dialog>


  </div>
</template>

<style scoped lang="scss">
.warning {
  padding: 8px 16px;
  background-color: #fff6f7;
  border-radius: 4px;
  border-left: 5px solid #fe6c6f;
  margin: 20px 0;
  line-height: 40px;
}
</style>
