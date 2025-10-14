<template>
  <div>
    <!-- <ElButton class="--MarginB" type="primary" plain @click="api.sync(query)">同步</ElButton> -->

    <CardGroupIndex :data="cardData" />

    <RequestChartTable
      title="员工执行明细"
      :isSelection="false"
      searchBtnType="icon"
      :request="api.getDataDetailUser"
      :params="query"
      :requestExport="api.getDataDetailExportUser"
      :exportFileName="`客${typeText}SOP员工执行明细导出.xls`">
      <template #queryMiddle="{ query }">
        <BaInput label="员工名称" prop="userName" v-model="query.userName"></BaInput>
      </template>
      <template #table>
        <el-table-column label="员工" prop="userName" />
        <el-table-column label="任务总数" prop="totalTaskNumber" />
        <el-table-column label="已完成任务数" prop="yzxTaskNumber" />
        <el-table-column label="未完成任务数" prop="wzxTaskNumber" />
        <el-table-column label="任务完成率" prop="executeRate" />
      </template>
    </RequestChartTable>

    <RequestChartTable
      title="任务完成明细"
      :isSelection="false"
      searchBtnType="icon"
      :request="api.getDataDetailTask"
      :params="query"
      :requestExport="api.getDataDetailExportTask"
      :exportFileName="`客${typeText}SOP任务完成明细导出.xls`">
      <template #table>
        <el-table-column label="任务" prop="taskName" />
        <el-table-column :label="`执行客${typeText}总数`" prop="totalTargetNumber" />
        <el-table-column :label="`已执行客${typeText}数`" prop="yzxTargetNumber" />
        <el-table-column :label="`未执行客${typeText}数`" prop="wzxTargetNumber" />
        <el-table-column :label="`客${typeText}执行率`" prop="executeRate" />
      </template>
    </RequestChartTable>
  </div>
</template>

<script setup>
import * as api from './api'
const $route = useRoute()

const taskType = useRoute().path.includes('customerSop') ? 1 : 2
const typeText = taskType == 1 ? '户' : '群'

let query = {
  sopId: $route.query.id,
}

let cardData = ref([])
;(function () {
  api.getStatistic(query).then(({ data }) => {
    cardData.value = [
      {
        title: '执行总员工数',
        tips: '当前SOP执行涉及的全部员工量',
        value: data.totalExecuteNumber,
      },
      {
        title: `执行总客${typeText}数`,
        tips: `当前SOP执行涉及的全部客${typeText}量`,
        value: data.totalTargetNumber,
      },
      {
        title: '执行总任务数',
        tips: '当前SOP执行涉及的全部任务量',
        value: data.totalTaskNumber,
      },
      {
        title: '执行任务完成数',
        tips: '当前SOP执行涉及任务的完成量',
        value: data.yzxTaskNumber,
      },
      {
        title: '执行任务完成率',
        tips: '当前SOP执行涉及任务的完成率',
        value: data.executeRate,
      },
    ]
  })
})()
</script>

<style lang="scss" scoped></style>
