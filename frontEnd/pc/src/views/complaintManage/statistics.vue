<template>
  <div>
    <CardGroup :data="cardData" />


    <RequestChartTable
      type="lineChart"
      :params="{ svipGroupId: $route.query.id }"
      :request="api.getDataTrend"
      :legend="['客户投诉数', '待处理投诉', '已处理投诉']"
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
          title: '客户投诉总数',
          value: data.complaintTotalNumber,
        },
        {
          title: '待处理投诉总数',
          value: data.complaintTotalNumber,
        },
        {
          title: '已处理投诉总数',
          value: data.yesComplaintTotalNumber,
        },
        {
          title: '今日客户投诉总数',
          value: data.tdComplaintTotalNumber,
        },
        {
          title: '今日待处理投诉数',
          value: data.tdNoComplaintTotalNumber,
        },
        {
          title: '今日已处理投诉数',
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
  xData = data.xdata
  data.series = data.series
  // data.series?.forEach((element) => {
  //   xData.push(element.xdata)
  //   _data[0].push(element.addGroupTotalNumber)
  // })
  // series.push(..._data)
}
</script>

<style lang="scss" scoped></style>
