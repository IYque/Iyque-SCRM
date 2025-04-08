<script setup>
import { useRoute, useRouter } from 'vue-router'
import * as api from './api'
import * as apiCategory from './apiCategory'

import SelectHotWord from './SelectHotWord.vue'

let loading = ref(false)
let id = useRoute().query.id

let cardData = ref([])
// 获取指标数据
;(function getStatistic() {
  loading.value = true
  api
    .getStatistic()
    .then(({ data = {} }) => {
      cardData.value = [
        {
          title: '热词讨论总数',
          tips: '预设的热词讨论累计总数',
          value: data.discussTotalNumber || 0,
        },
        {
          title: '昨日热词讨论数',
          tips: '昨日热词讨论总数',
          value: data.ydDiscussTotalNumber || 0,
        },
        {
          title: '昨日客户焦点热词',
          tips: '昨日客户讨论热度最高的热词',
          value: data.ydHotWord || 0,
        },
        {
          title: '昨日客户焦点类别',
          tips: '昨日客户讨论热度最高的热词分组',
          value: data.ydHotWordCategory || 0,
        },
      ]
    })
    .finally(() => {
      loading.value = false
    })
})()
let hotWordTop5 = ref([])
function dealDataConversionTop(data, series, xData) {
  series.length = 0
  hotWordTop5.value = []
  if (data?.length) {
    let _data = []
    data.forEach((element) => {
      hotWordTop5.value.push(element.hotWord)
      _data.push(element.hotWordDiscussNumber)
    })
    series.push(_data)
  }
}

const categoryList = ref([])
function getCategoryList() {
  apiCategory.getList().then(({ data }) => {
    categoryList.value = data
  })
}
getCategoryList()

let hotWordCategoryTop5 = ref([])
function dealDataWordCategoryTop5(data, series, xData) {
  series.length = 0
  hotWordCategoryTop5.value = []
  if (data?.length) {
    let _data = []
    data.forEach((element) => {
      hotWordCategoryTop5.value.push(element.userName)
      _data.push(element.rate)
    })
    series.push(_data)
  }
}

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

<template>
  <div>
    <CardGroup :data="cardData" />

    <div class="grid grid-cols-2 --Gap --MarginT">
      <RequestChartTable
        title="热词Top5"
        type="lineChart"
        isTimeQuery
        :request="api.hotWordTop5"
        :option="{
          xAxis: [{ type: 'value' }],
          yAxis: [{ type: 'category', data: hotWordTop5 }],
        }"
        :dealDataFun="dealDataConversionTop"></RequestChartTable>

      <RequestChartTable
        class="mt0"
        ref="rctRef"
        title="分类热词 Top5"
        type="lineChart"
        isTimeQuery
        :request="api.hotWordCategoryTop5"
        :option="{
          xAxis: [{ type: 'value' }],
          yAxis: [{ type: 'category', data: hotWordCategoryTop5 }],
        }"
        :dealDataFun="dealDataWordCategoryTop5">
        <template #queryMiddle="{ query }">
          <el-form-item label="热词分类" prop="categoryId">
            <el-select v-model="query.categoryId" :popper-append-to-body="false" @change="$refs.rctRef.getList()">
              <el-option v-for="(item, key) in categoryList" :key="key" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
        </template>
      </RequestChartTable>
    </div>

    <RequestChartTable ref="rctRef2" type="table" title="热词讨论明细" isTimeQuery :request="api.findAll">
      <template #queryMiddle="{ query }">
        <el-form-item label="选择热词" prop="categoryId">
          <el-input
            v-model="query._name"
            placeholder="请选择热词"
            @click="$refs.selectHotWordRef.dialogRef.visible = true" />

          <SelectHotWord
            ref="selectHotWordRef"
            @confirm="
              ({ visible, loading, selected }) => (
                (query._name = selected.value?.map((e) => e.name)?.join(',')),
                (query.hotWordId = selected.value?.map((e) => e.id)?.join(',')),
                (visible.value = false),
                (loading.value = false),
                $nextTick(() => {
                  $refs.rctRef2.getList(1)
                })
              )
            "></SelectHotWord>
        </el-form-item>
      </template>

      <template #table="{ data }">
        <el-table-column label="热词讨论客户" prop="fromName"></el-table-column>
        <el-table-column label="会话员工" prop="acceptName" show-overflow-tooltip />
        <el-table-column label="会话原文" prop="content" min-width="160px" show-overflow-tooltip>
          <!-- <template #default="{ row }">
            <div v-html="row.content.repleace()"></div>
          </template> -->
        </el-table-column>
        <el-table-column label="讨论时间" prop="msgTime" show-overflow-tooltip />
      </template>
    </RequestChartTable>
  </div>
</template>

<style lang="scss" scoped></style>
