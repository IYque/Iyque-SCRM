<template>
  <div>
    <el-form :model="query" ref="queryForm" inline class="searchForm">
      <el-form-item label="客服名称" prop="complainType">
        <el-select v-model="query.handleState" :popper-append-to-body="false">
          <el-option v-for="(value, key) in handleState" :key="key" :label="value" :value="key" />
        </el-select>
      </el-form-item>
      <el-form-item label="客服名称" prop="complainType">
        <el-date-picker
          clearable
          v-model="value"
          type="daterange"
          range-separator="至"
          v-bind="pickerOptions"
          start-placeholder="开始日期"
          end-placeholder="结束日期"></el-date-picker>
      </el-form-item>
      <SearchResetButton @search="getList(1)" @reset="reset('queryForm')" :type="searchBtnType"></SearchResetButton>
    </el-form>

    <CardGroup :data="cardData" isCardWrap style="grid: auto/repeat(4, 1fr)" />

    <RequestChartTable
      type="lineChart"
      :params="{ svipGroupId: $route.query.id }"
      :request="api.getDataTrend"
      :legend="['访问客户数', '咨询客户数', '接待客户数', '回复客户数']"
      :dealDataFun="dealDataTrend" />
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import * as api from './api'

let loading = ref(false)
let id = useRoute().query.id
let cardData = ref([])
// 获取指标数据
;(function getStatistic() {
  loading.value = true
  api
    .getStatistic(id)
    .then(({ data }) => {
      cardData.value = [
        {
          title: '访问客户总数',
          value: data.complaintTotalNumber,
        },
        {
          title: '咨询客户总数',
          value: data.complaintTotalNumber,
        },
        {
          title: '接待客户总数',
          value: data.yesComplaintTotalNumber,
        },
        {
          title: '回复客户总数',
          value: data.tdComplaintTotalNumber,
        },
        {
          title: '今日访问客户数',
          value: data.tdNoComplaintTotalNumber,
        },
        {
          title: '今日咨询客户数',
          value: data.tdYesComplaintTotalNumber,
        },
        {
          title: '今日接待客户数',
          value: data.tdYesComplaintTotalNumber,
        },
        {
          title: '今日回复客户数',
          value: data.tdYesComplaintTotalNumber,
        },
      ]
    })
    .finally(() => {
      loading.value = false
    })
})()

// 数据趋势 数据处理
function dealDataTrend(data, series, xData) {
  xData.length = 0
  series.length = 0
  let _data = [[]]
  xData.push(...data.xdata)
  series.push(...data.series)
  // data.series?.forEach((element) => {
  //   xData.push(element.xdata)
  //   _data[0].push(element.addGroupTotalNumber)
  // })
  // series.push(..._data)
}
</script>

<style lang="scss" scoped></style>
