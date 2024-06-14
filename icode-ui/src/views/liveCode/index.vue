<script>
import { getList, del } from './api'
export default {
  data() {
    return {
      loading: false,
      query: { page: 0, size: 10 },
      list: '',
      total: 0,
      multipleSelection: [], // 多选数据
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
      page && (this.query.pageNum = page)
      this.loading = true
      getList(this.query)
        .then(({ data, count }) => {
          this.list = data
          this.total = +count
          this.loading = false
          this.multipleSelection = []
        })
        .catch(() => {
          this.loading = false
        })
    },
    del(id) {
      
      let ids = id || this.multipleSelection?.join?.(',')
      if (!ids) {
        return this.msgError('请选择需要删除的数据')
      }

      this.$confirm()
        .then(() => {
          this.loading = true
          return del(ids).then((res) => {
            this.msgSuccess('删除成功')
            this.getList()
          })
        })
        .catch((e) => {
          console.error(e)
        })
        .finally(() => {
          this.loading = false
        })


    },
  },
}
</script>
<template>
  <div>
    <div class="warning">
      <a href="https://www.iyque.cn?utm_source=iyquecode" target="_blank">
        <strong>源雀SCRM — 基于SpringCloud+Vue架构,100%开放源码的企微私域营销系统:https://www.iyque.cn/</strong>
      </a>
    </div>

    <div class="g-card">
      <div class="fxbw">
        <el-button type="primary" @click="$router.push('add')">新建</el-button>
        <el-button :disabled="!multipleSelection.length" @click="del()" type="danger">批量删除</el-button>
      </div>
      <el-table
        :data="list"
        tooltip-effect="dark"
        highlight-current-row
        @selection-change="(selection) => (multipleSelection = selection.map((item) => item.id))">
        <el-table-column type="selection" width="50" align="center"></el-table-column>
        <el-table-column label="活码名称" prop="codeName" show-overflow-tooltip />
        <el-table-column label="活码地址" prop="codeUrl" show-overflow-tooltip>
          <template #default="{ row }">
            <el-image :src="row.codeUrl" style="width: 100px"></el-image>
          </template>
        </el-table-column>
        <el-table-column label="使用员工">
          <template #default="{ row }">
            <TagEllipsis :list="row.userName"></TagEllipsis>
          </template>
        </el-table-column>
        <el-table-column label="标签">
          <template #default="{ row }">
            <TagEllipsis :list="row.tagName"></TagEllipsis>
          </template>
        </el-table-column>

        <el-table-column label="更新时间" prop="updateTime" />

        <el-table-column label="操作" fixed="right">
          <template #default="{ row }">
            <el-button text @click="del(row.id)">删除</el-button>
            <el-button text @click="downloadBlob(row.codeUrl, row.codeName)">活码下载</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="query.pageNum"
        v-model:limit="query.pageSize"
        @pagination="getList()" />
    </div>
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
