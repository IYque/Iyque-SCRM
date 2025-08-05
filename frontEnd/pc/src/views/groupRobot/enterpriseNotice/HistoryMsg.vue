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
        title: '',
        status: '',
        agentId: this.id,
      },
      total: 0,
      list: [],
      loading: false,

      status: { '': '全部状态', 0: '草稿', 1: '待发送', 2: '已发送', 3: '发送失败', 4: '已撤回' },
      typeDict: {
        text: '文本',
        image: '图片',
        news: '图文',
        video: '视频',
        file: '文件',
      },
      form: {},
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
        .then(({ data, count }) => {
          this.list = data
          this.total = +count
        })
        .finally(() => {
          this.loading = false
        })
    },
    edit(data, type) {
      this.$emit('edit', data, type)
    },
    // 删除和撤销
    removke(id, action, tip) {
      // const operIds = id || this.ids + ''
      this.$confirm('是否确认' + tip + '？该操作不可撤销，请谨慎操作。', {
        title: '警告',
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(function () {
          return appMsg[action](id)
        })
        .then(() => {
          this.getList()
          this.msgSuccess('删除成功')
        })
    },
    resetQuery() {
      this.query.status = ''
      this.getList(1)
    },
  },
}
</script>

<template>
  <div>
    <!-- <el-select class="mb10 mr10" v-model="query.status" placeholder="">
      <el-option v-for="(value, key) in status" :key="key" :label="value" :value="key"></el-option>
    </el-select>
    <el-button type="primary" @click="getList(1)">查询</el-button>
    <el-button @click="resetQuery">重置</el-button> -->

    <el-table v-loading="loading" :data="list">
      <el-table-column label="消息标题" align="center" prop="msgTitle" />
      <el-table-column prop="msgType" label="类型" align="center">
        <template #default="{ row }">{{ typeDict[row.msgType] }}</template>
      </el-table-column>
      <el-table-column prop="status" label="消息状态" align="center">
        <template #default="{ row }">{{ status[row.status] }}</template>
      </el-table-column>
      <el-table-column label="发送时间" align="center" prop="sendTime" />
      <el-table-column label="操作" align="center">
        <template #default="{ row }">
          <el-button text @click="edit(row, 'detail')">详情</el-button>
          <el-button v-if="['0', '1'].includes(row.status + '')" text @click="edit(row, 'edit')">编辑</el-button>
          <!-- <el-button v-if="row.status == 2" text @click="removke(row.id, 'revoke', '撤回')">撤回</el-button>
          <el-button v-else @click="removke(row.id, 'remove', '删除')" text>删除</el-button> -->
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
