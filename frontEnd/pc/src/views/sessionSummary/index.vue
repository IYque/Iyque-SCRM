<script setup lang="ts">
import { getList, findGroupAll, summaryKfmsgByAi } from './api'
import SelectCustomer from '@/views/customer/SelectCustomer.vue'

const userList = ref([])

function findGroupAllFn(isFresh) {
  findGroupAll().then(({ data }) => {
    userList.value = data
  })
}
</script>

<template>
  <div class="warning">
    <strong>基于客户会话内容，AI智能归纳聊天意图</strong>
  </div>

  <div :_="$store.setBusininessDesc(`<div></div>`)">
    <RequestChartTable ref="rctRef" :request="getList" searchBtnType="icon">
      <template #query="{ query }">
        <el-form-item label="客户名称" prop="kfName">
          <el-input v-model="query.kfName" placeholder="请输入" />
        </el-form-item>
      </template>

      <template #operation="{ selectedIds }">
        <el-button type="primary" @click="$refs.dialogRef.action()">圈选客户记录</el-button>

        <BaseDialog
          ref="dialogRef"
          title="圈选客户记录"
          width="1000"
          :isFooter="!isDetail"
          :formProps="{
            'label-width': 'auto',
            disabled: isDetail,
            class: isDetail && 'form-detail',
          }"
          :rules="{
            scopeType: [$sdk.ruleRequiredChange],
            sendType: [$sdk.ruleRequiredChange],
            complainTime: [$sdk.ruleRequiredChange],
            content: [$sdk.ruleRequiredBlur],
            groupMsgName: [$sdk.ruleRequiredBlur],
          }"
          @confirm="submit">
          <template #form="{ form }">
            <el-form-item label="选择客户" prop="scopeType">
              <el-button
                class="mr10"
                v-if="!isDetail"
                type="primary"
                @click="$refs.SelectCustomerRef.dialogRef.visible = true">
                选择客户
              </el-button>

              <TagEllipsis :list="userList" defaultProps="acceptName" :emptyText="!!isDetail"></TagEllipsis>

              <div class="g-tip">选择服务记录中的咨询客户</div>

              <SelectCustomer
                ref="SelectCustomerRef"
                @confirm="
                  ({ visible, loading, selected }) => (
                    (userList = selected.map((e) => ({
                      acceptId: e.externalUserid,
                      acceptName: e.customerName,
                      acceptType: '1',
                      // senderId: e.owner,
                    }))),
                    (visible.value = false),
                    (loading.value = false)
                  )
                "></SelectCustomer>
            </el-form-item>
            <el-form-item label="选择记录时间" prop="complainTime">
              <el-date-picker
                v-model="form.complainTime"
                type="datetime"
                placeholder="选择日期时间"
                :picker-options="{
                  disabledDate(time) {
                    return time.getTime() < Date.now()
                  },
                }"
                :default-time="['00:00:00', '23:59:59']"
                format="yyyy-MM-dd HH:mm:ss"
                value-format="yyyy-MM-dd HH:mm:ss"
                clearable></el-date-picker>
              <div class="g-tip">分析时间范围内的所选客户记录</div>
            </el-form-item>
          </template>
        </BaseDialog>
      </template>

      <template #table="{ data }">
        <el-table-column label="客户名称" fixed="left" prop="" min-width="140">
          <template #default="{ row }">
            <div class="flex items-center">
              <el-image class="flex-none" :src="row.avatar" style="width: 50px"></el-image>
              <div class="ml10">{{ row.nickname }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="会话时间段" min-width="100" prop="switchUserName">
          <template #default="{ row }">
            {{ row.startTime }}
            <br />
            {{ row.endTime }}
          </template>
        </el-table-column>
        <el-table-column label="分析时间" prop="createTime" width="180"></el-table-column>
        <el-table-column prop="summaryContent" label="AI总结"></el-table-column>
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
