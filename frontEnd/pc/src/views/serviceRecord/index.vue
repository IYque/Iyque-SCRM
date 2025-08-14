<script setup lang="ts">
import { getList } from './api'
</script>

<template>
  <div :_="$store.setBusininessDesc(`<div>查看所有客服场景中客户咨询的详细记录</div>`)">
    <div class="warning">
      <strong>客户为客服发送的聊天信息(目前:仅支持文字内容)(如果接待员工位【-】则为AI回复的)</strong>
    </div>
    <RequestChartTable
      ref="rctRef"
      :request="getList"
      searchBtnType="icon"
      @selectionChange="(val) => $emit('selectionChange', val)">
      <template #query="{ query }">
        <el-form-item label="所属客服" prop="kfName">
          <el-input v-model="query.kfName" placeholder="请输入" />
          <!-- <SelectStaffQrCode
            isSigleSelect
            ref="SelectStaffQrCodeRef"
            title="选择客服"
            @confirm="
              ({ visible, loading, selected }) => (
                (form.swichQrUrl = selected[0].codeUrl), (visible.value = false), (loading.value = false)
              )
            "></SelectStaffQrCode> -->
        </el-form-item>
      </template>

      <template #table="{ data }">
        <el-table-column label="咨询客户" fixed="left" prop="" min-width="140">
          <template #default="{ row }">
            <div class="flex items-center">
              <el-image class="flex-none" :src="row.avatar" style="width: 50px"></el-image>
              <div class="ml10">{{ row.nickname }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="所属客服" min-width="200" prop="list">
          <template #default="{ row }">
            <div class="flex items-center">
              <el-image class="flex-none" :src="row.kfPicUrl" style="width: 50px"></el-image>
              <div class="ml10">{{ row.kfName }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="接待员工" min-width="100" prop="switchUserName">
          <!-- <template #default="{ row }">
            {{ row.switchUserName ? row.switchUserName : '-' }}
          </template> -->
        </el-table-column>
        <el-table-column label="咨询开始时间" prop="sendTime" width="180"></el-table-column>
        <!-- <el-table-column label="咨询结束时间" prop="sessionEndTime" width="180"></el-table-column>
        <el-table-column label="咨询时长" prop="duration" width="140"></el-table-column> -->
        <el-table-column prop="content" label="会话内容"></el-table-column>
        <!-- <el-table-column label="操作" fixed="right" width="130">
          <template #default="{ row }">
            <el-button text @click="showResultList(row)">咨询记录</el-button>
          </template>
        </el-table-column> -->
      </template>
    </RequestChartTable>

    <!-- <BaDialog ref="dialogRefDetail" title="咨询记录" width="500">
      <PhoneChatList />
    </BaDialog> -->
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
