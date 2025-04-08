<!-- 查询数据表格或图表展示 -->
<script>
export default defineComponent({
  components: {
    ChartLine: defineAsyncComponent(() => import('@/components/ChartLine')),
    ChartBar: defineAsyncComponent(() => import('@/components/ChartBar')),
    TimeButtonGroup: defineAsyncComponent(() => import('@/components/TimeButtonGroup')),
  },
  props: {
    // 标题
    title: {},
    // 数据展示图表类型
    type: {
      type: String, // lineChart, barChart, table
      default: 'table',
    },
    // 查询参数
    params: {
      type: Object,
      default: () => ({}),
    },
    // 接口请求
    request: {
      type: Function,
    },
    // 是否在创建的时候自动调用request接口请求
    isCreateRequest: {
      type: Boolean,
      default: true,
    },
    // 是否显示快捷时间查询
    isTimeQuery: {
      type: Boolean,
    },

    // 自定义的查询参数处理方法，可用于添加和修改查询参数
    dealQueryFun: {
      type: Function,
      default: null,
    },
    // 自定义的返回数据处理方法
    dealDataFun: {
      type: Function,
      default: null,
    },
    // 数据主键
    dataKey: {
      type: String,
      default: 'id',
    },

    // 导出接口请求
    requestExport: {
      type: Function,
      default: null,
    },
    // 导出文件名(需包含后缀名)
    exportFileName: { type: String },

    // 图表图例; 单个图例可传字符串，多个图例传数组
    legend: {
      type: [Array, String],
      default: null,
    },
    // 自定义图表配置项，使用loadsh.merge(origin, option)和原有的配置进行覆盖合并
    // loadsh.merge: https://www.html.cn/doc/lodash/#_mergeobject-sources
    option: {
      type: Object,
      default: null,
    },
    searchBtnType: {
      type: String,
      // default: button,
      // validator(value) {
      // 	// 这个值必须匹配下列字符串中的一个
      // 	return Object.values(dict).includes(value)
      // },
    },
  },
  data() {
    return {
      loading: false,
      query: { pageNum: 1, pageSize: 10 },
      // 列表接口返回数据
      response: {},
      // 表格列表数据，或图表的series数据
      data: [],
      xData: [],

      total: 0,

      selectedIds: [], // 多选数据
    }
  },
  computed: {},
  watch: {},
  created() {
    this.isCreateRequest && !this.isTimeQuery && this.getList()
  },
  mounted() {},
  methods: {
    getList(page) {
      this.loading = true
      if (this.type == 'table') {
        page && (this.query.pageNum = page)
      }

      Object.assign(this.query, this.params)

      this.dealQueryFun && this.dealQueryFun(this.query)

      let query = JSON.parse(JSON.stringify(this.query))
      if (query.dateRange) {
        query.beginTime = query.dateRange[0]
        query.endTime = query.dateRange[1]
      } else if (!this.isTimeQuery) {
        delete query.beginTime
        delete query.endTime
      }
      delete query.dateRange

      return this.request(query)
        .then((resp) => {
          let { rows, total, data } = resp
          let res = data || rows
          // if (!data) return
          if (this.type == 'table') {
            // 表格
            this.dealDataFun && this.dealDataFun(res, resp)
            this.response = resp
            this.data = res
            this.total = +total
          } else {
            // 自定义echarts图表数据处理
            // this.data = data
            this.dealDataFun && this.dealDataFun(res, this.data, this.xData, resp)
            this.response = resp
          }
        })
        .catch((e) => {
          console.error(e)
        })
        .finally(() => {
          this.loading = false
        })
    },
    reset(form) {
      this.$refs[form].resetFields()
      this.$emit('reset')
      setTimeout(() => {
        this.getList(1)
      }, 0)
    },

    /**
     * (批量)删除 供父组件调用
     * @param {*} removeApi 删除接口
     * @param {*} id 字符串id
     */
    remove(removeApi, id) {
      return this.apiConfirm(removeApi, id, '是否确认删除当前数据？')
    },

    /**
     * (批量)下载 供父组件调用
     * @param {*} download 下载接口函数 必需
     * @param {*} filename 文件名 需含文件后缀名 必需
     * @param {*} tips 弹窗提示语
     * @param {*} type 文件类型 enum: excel,zip,image
     */
    download(download, filename, tips = '是否确认下载', type = 'zip') {
      if (!(download && filename)) {
        return
      }
      this.$confirm(tips)
        .then(() => {
          return download(this.selectedIds?.join?.(','))
        })
        .then((res) => {
          this.downloadBlob(res, filename, type)
        })
        .catch(function () {})
    },

    /**
     * 路由跳转 供父组件调用
     * @param {*} path 路径
     * @param {*} idOrQuery 字符串id或路由query对象
     */
    goRoute(path = 'aev', idOrQuery) {
      this.$router.push({
        path,
        query: typeof idOrQuery === 'string' ? { id: idOrQuery } : idOrQuery,
      })
    },

    /**
     * 通用确认接口操作 供父组件调用
     * @param {*} removeApi 功能接口
     * @param {*} params 接口参数
     * @param {*} tips 弹窗提示语
     */
    apiConfirm(apiRequest, params, tips = '是否确认删除当前数据？') {
      return new Promise((resolve, reject) => {
        if (!apiRequest) {
          return reject(this.msgError('无可用接口服务'))
        }
        params ??= this.selectedIds?.join?.(',')
        if (!params) {
          return reject(this.msgError('请先选择需要操作的数据'))
        }
        tips && !tips.includes('该操作不可撤销，请谨慎操作') && (tips += '该操作不可撤销，请谨慎操作。')
        this.$confirm(tips)
          .then(() => {
            this.loading = true
            return apiRequest(params).then((res) => {
              this.msgSuccess()
              this.getList()
            })
          })
          .catch((e) => {
            console.error(e)
          })
          .finally(() => {
            this.loading = false
            resolve()
          })
      })
    },
  },
})
</script>

<template>
  <div class="RequestChartTable" :class="$slots.query || 'g-card'" v-loading="loading">
    <!-- 顶部查询框 -->
    <el-form v-if="$slots.query" :model="query" ref="queryForm" inline class="searchForm">
      <slot name="query" v-bind="{ query }"></slot>
      <SearchResetButton @search="getList(1)" @reset="reset('queryForm')" :type="searchBtnType"></SearchResetButton>
    </el-form>

    <div :class="$slots.query && 'g-card'">
      <!-- 卡片标题 -->
      <div class="g-card-title justify-between" v-if="title">
        {{ title }}
        <!-- 快捷时间查询 -->
        <TimeButtonGroup
          style="display: inline-flex"
          v-if="isTimeQuery"
          isDiy
          @search="(data) => (Object.assign(query, data), getList(1))"></TimeButtonGroup>
      </div>

      <!-- 查询框和操作栏 -->
      <div
        class="RequestChartTable-operation"
        v-if="requestExport || isTimeQuery || $slots.queryMiddle || $slots.operation">
        <el-form v-if="$slots.queryMiddle" :model="query" ref="queryFormMiddle" inline class="query-wrap">
          <slot name="queryMiddle" v-bind="{ query }"></slot>
          <SearchResetButton
            @search="getList(1)"
            @reset="reset('queryFormMiddle')"
            :type="searchBtnType"></SearchResetButton>
        </el-form>

        <!-- 操作slot -->
        <slot name="operation" v-bind="{ selectedIds, response }"></slot>

        <el-button
          v-if="requestExport"
          class="export fr"
          type="primary"
          @click="$exportData(requestExport.bind(null, query), exportFileName)">
          导出 Excel
        </el-button>
      </div>

      <!-- 折线图 -->
      <ChartLine
        v-if="type == 'lineChart'"
        :xData="xData"
        :legend="legend"
        :series="data"
        :option="option"
        :bgLinearGradient="$attrs.bgLinearGradient"></ChartLine>

      <!-- 柱状图 -->
      <ChartBar v-else-if="'barChart'.includes(type)" :xData="xData" :series="data" :option="option"></ChartBar>

      <!-- 表格 -->
      <template v-else-if="type == 'table'">
        <slot :data="data"></slot>

        <el-table
          v-if="$slots.table"
          :data="data"
          @selection-change="(val) => ((selectedIds = val.map((e) => e[dataKey])), $emit('selectionChange', val))">
          <el-table-column type="selection" width="50" align="center"></el-table-column>
          <slot name="table"></slot>
        </el-table>

        <pagination
          style="margin-bottom: -20px"
          :total="total"
          v-model:page="query.pageNum"
          v-model:limit="query.pageSize"
          @pagination="getList()" />
      </template>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.g-card {
  overflow: initial;
  > .pagination-container {
    bottom: 0 !important;
  }
}
.RequestChartTable-operation {
  position: relative;
  overflow: auto;
  // display: flex;
  // align-items: center;
  margin-bottom: var(--card-margin, 16px);
}
::v-deep.query-wrap {
  display: inline-flex;
  flex-wrap: wrap;
  gap: 18px;
  .tag-input,
  .el-input,
  .el-select,
  .el-date-editor--daterange {
    width: 210px;
  }
  .el-form-item {
    margin: 0 !important;
    transition: all 0.2s;
  }
}

// 该组件下的表格默认居中
::v-deep .el-table {
  .el-table__cell:not(.is-left, .is-right) {
    text-align: center;
    // .cell {
    // }
  }
}
</style>
