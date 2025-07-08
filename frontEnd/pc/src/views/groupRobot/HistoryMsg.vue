<script>
import { appMsg } from './api'

export default {
  name: '',
  components: {},
  props: {
    id: {
      type: [String, Number],
      required: true,
    },
  },
  data() {
    return {
      query: {
        pageNum: 1,
        pageSize: 10,
        robotId: this.id,
      },
      total: 0,
      list: [],
      loading: false,

      typeDict: {
        text: '文本',
        image: '图片',
        news: '图文',
        video: '视频',
        file: '文件',
      },
    }
  },
  computed: {},
  watch: {},
  created() {
    this.getList()
  },
  mounted() {},
  methods: {
    getList(page) {
      // if (this.dateRange) {
      //   this.query.beginTime = this.dateRange[0]
      //   this.query.endTime = this.dateRange[1]
      // } else {
      //   this.query.beginTime = ''
      //   this.query.endTime = ''
      // }
      page && (this.query.pageNum = page)
      this.loading = true
      appMsg
        .getList(this.query)
        .then(({ rows, total }) => {
          this.list = rows
          this.total = +total
        })
        .finally(() => {
          this.loading = false
        })
    },
    edit(data) {
      this.form = Object.assign({}, data || {})
      data.weMessageTemplate || (data.weMessageTemplate = data)
      this.$emit('edit', data)
    },
  },
}
</script>

<template>
  <div>
    <el-table v-loading="loading" :data="list">
      <el-table-column label="消息标题" align="center" prop="msgTitle" />
      <el-table-column prop="msgType" label="类型" align="center">
        <template #default="{ row }">{{ typeDict[row.msgType] }}</template>
      </el-table-column>
      <el-table-column label="发送时间" align="center" prop="sendTime" />
      <el-table-column label="操作" align="center">
        <template #default="{ row }">
          <el-button text @click="$emit('edit', row)">详情</el-button>
          <!-- v-hasPermi="['customerManage:tag:remove']" -->
        </template>
      </el-table-column>
    </el-table>
    <pagination
      style="text-align: center"
      class="mt20"
      v-show="total > 0"
      :total="total"
      :background="false"
      layout="prev, pager, next"
      v-model:page="query.pageNum"
      v-model:limit="query.pageSize"
      @pagination="getList()" />
  </div>
</template>

<style lang="less" scoped></style>
