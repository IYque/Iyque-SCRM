<script setup lang="ts">
import { getList, synchCustomer } from './api'

defineProps({
  isSelect: { type: Boolean, default: false },
})

const customerType = Object.freeze({ 1: '微信客户', 2: '企业客户' })
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

      <template #table="{ data }">
        <el-table-column label="客户" prop="customerName" header-align="center" align="" width="180">
          <template #default="{ row }">
            <CustomerInfoCell :data="row" @click="goRoute(row)" />
          </template>
        </el-table-column>
        <!-- <el-table-column prop="tagNames" label="客户标签" align="center" width="220">
          <template #default="{ row }">
            <TagEllipsis :list="row.tagNames" emptyText="无标签"></TagEllipsis>
          </template>
        </el-table-column> -->
        <el-table-column label="跟进员工" min-width="100" prop="userName">
          <!-- <template #default="{ row }">
            {{ row.switchUserName ? row.switchUserName : '-' }}
          </template> -->
        </el-table-column>
        <el-table-column prop="addWay" label="客户来源"></el-table-column>
        <el-table-column label="添加时间" prop="addTime" width=""></el-table-column>
        <!-- <el-table-column label="操作" fixed="right" width="130">
          <template #default="{ row }">
            <el-button text @click="showResultList(row)">咨询记录</el-button>
          </template>
        </el-table-column> -->
      </template>
    </RequestChartTable>

    <!-- <BaseDialog ref="dialogRefDetail" title="咨询记录" width="500">
      <PhoneChatList />
    </BaseDialog> -->
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
