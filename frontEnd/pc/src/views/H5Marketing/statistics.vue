<template>
  <div>
    <CardGroup :data="cardData" />

    <RequestChartTable
      isTimeQuery
      title="数据趋势"
      :params="query"
      :request="api.getDataTrend"
      type="lineChart"
      :legend="['H5访问数', 'H5访问人数']"
      :dealDataFun="dealDataTrend"></RequestChartTable>

    <RequestChartTable
      isTimeQuery
      title="数据报表"
      :isSelection="false"
      searchBtnType="icon"
      :params="query"
      :request="api.getDataDetail"
      exportFileName="H5营销数据报表.xls">
      <template #table>
        <el-table-column label="日期" prop="date" />
        <el-table-column label="H5访问总数" prop="viewTotalNumber" />
        <el-table-column label="H5访问总人数" prop="viewTotalPeopleNumber" />
        <el-table-column label="今日H5访问数" prop="tdViewTotalNumber" />
        <el-table-column label="今日H5访问人数" prop="tdViewTotalPeopleNumber" />
      </template>
    </RequestChartTable>
  </div>
</template>

<script setup>
import * as api from './api'
const $route = useRoute()

let query = {
  h5MarketId: $route.query.id,
}

let cardData = ref([])
;(function () {
  api.getStatistic(query).then(({ data }) => {
    cardData.value = [
      {
        title: 'H5访问总数',
        tips: '访问当前H5页面的总PV数(不去重)',
        value: data.viewTotalNumber,
      },
      {
        title: 'H5访问总人数',
        tips: '访问当前H5页面的总UV数(去重)',
        value: data.viewTotalPeopleNumber,
      },
      {
        title: '今日H5访问数',
        tips: '今日访问当前H5页面的PV数(不去重)',
        value: data.tdViewTotalNumber,
      },
      {
        title: '今日H5访问人数',
        tips: '今日访问当前H5页面的UV数(去重)',
        value: data.tdViewTotalPeopleNumber,
      },
    ]
  })
})()

function dealDataTrend(data, series, xData) {
  xData.length = 0
  series.length = 0
  let _data = [[], []]
  data.forEach((element) => {
    xData.push(element.date)
    _data[0].push(element.tdViewTotalNumber)
    _data[1].push(element.tdViewTotalPeopleNumber)
  })
  series.push(..._data)
}
</script>

<style lang="scss" scoped></style>
