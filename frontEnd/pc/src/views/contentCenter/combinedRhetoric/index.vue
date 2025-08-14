<script setup>
import { getList, del, save } from './api'
import aev from './aev.vue'

defineProps({
  isSelect: { type: Boolean, default: false },
})

const query = ref({})

const dialogRef = ref()
const aevRef = ref()
function action(data) {
  dialogRef.value.action(data)
  nextTick(() => {
    aevRef.value.form = data || { categoryId: query.value.categoryId }
    aevRef.value.talkList = data?.scriptSubs || []
  })
}
</script>

<template>
  <LeftRightGroupListPage
    :_="$store.setBusininessDesc(`<div>创建企业标准运营话术，通过聊天工具栏一键发送，提升沟通效率</div>`)"
    ref="lrPageRef"
    :isDisabledAction="isSelect"
    type="13"
    @currentChange="(data) => ((query.categoryId = data.id), $refs.rctRef.getList(1))">
    <RequestChartTable
      ref="rctRef"
      :isCreateRequest="false"
      :params="query"
      :request="getList"
      @selectionChange="(val) => $emit('selectionChange', val)">
      <template #query="{ query }">
        <el-form-item label="话术标题" prop="title">
          <el-input v-model="query.title" placeholder="请输入" clearable />
        </el-form-item>
      </template>

      <template #operation="{ selectedIds }" v-if="!isSelect">
        <div class="mid-action mb0">
          <el-button type="primary" @click="action()">新建组合话术</el-button>

          <BaDialog
            ref="dialogRef"
            width="1000px"
            dynamicTitle="组合话术"
            @confirm="
              ({ visible, loading }) => {
                loading.value = true
                $refs.aevRef
                  .submit()
                  .then(() => {
                    visible.value = false
                    $refs.rctRef.getList(1)
                  })
                  .finally(() => {
                    loading.value = false
                  })
              }
            ">
            <aev ref="aevRef" />
          </BaDialog>

          <el-button type="primary" plain :disabled="!selectedIds?.length" @click="$refs.rctRef?.apiConfirm(del)">
            删除
          </el-button>
        </div>
      </template>

      <template #table="{ data }">
        <el-table-column label="话术标题" prop="title" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column label="话术素材数量" prop="scriptNumber">
          <template #default="{ row }">
            <div class="scriptNum" @click="goDetail(row)">
              {{ row.scriptNumber }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="最近更新" prop="updateTime" width="180">
          <template #default="{ row }">
            {{ row.updateBy }}
            <br />
            {{ row.updateTime }}
          </template>
        </el-table-column>
        <el-table-column label="操作" v-if="!isSelect" fixed="right" width="220">
          <template #default="{ row }">
            <!-- <TableOperateBtn type="detail" @click="$refs.rctRef.goRoute('aev', row.id)">详情</TableOperateBtn> -->
            <TableOperateBtn type="edit" @click="action(row)">编辑</TableOperateBtn>
            <TableOperateBtn type="delete" @click="$refs.rctRef.apiConfirm(del, row.id)">删除</TableOperateBtn>
          </template>
        </el-table-column>
      </template>
    </RequestChartTable>
  </LeftRightGroupListPage>
</template>

<style lang="scss" scoped>
:deep() .el-dialog__body .g-card {
  padding: 0;
  border-radius: 0;
}
</style>
