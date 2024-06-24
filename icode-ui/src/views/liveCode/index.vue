<script>
import { getList, del,distributeUserCode} from './api'

import aev from './aev.vue'
export default {
  data() {
    return {
      query: { page: 0, size: 10 },
      list: '',
      total: 0,
      multipleSelection: [], // 多选数据
      loading: false,
      dialogVisible: false, // 弹窗显示控制
      form: {},
    }
  },
  components: { aev },
  watch: {},
  created() {
    this.getList()
  },
  mounted() {},
  methods: {
    getList(page) {
      page && (this.query.page = page)
      this.$store.loading = true
      getList(this.query)
        .then(({ data, count }) => {
          this.list = data
          this.total = +count
          this.multipleSelection = []
        })
        .catch((e) => console.error(e))
        .finally(() => (this.$store.loading = false))
    },
    del(id) {
      let ids = id || this.multipleSelection?.join?.(',')
      if (!ids) {
        return this.msgError('请选择需要删除的数据')
      }
      this.$confirm()
        .then(() => {
          this.$store.loading = true
          return del(ids).then((res) => {
            this.msgSuccess('删除成功')
            this.getList()
          })
        })
        .catch((e) => {
          console.error(e)
        })
    },

    distributeUserCode(id){
      this.$confirm('是否将当前活码下发给员工, 是否继续?', '提示', {
          confirmButtonText: '是',
          cancelButtonText: '否',
          type: 'warning'
        }).then(() => {
          distributeUserCode(id).then((res) => {
            this.msgSuccess('已通知')
          })
        }).catch((e) => {  
           console.error(e)       
        });
  
    },

    async submit() {
      this.loading = true
      await this.$refs.aev.submit()
      this.loading = false
      this.dialogVisible = false
      this.getList()
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
        <el-button type="primary" @click=";(form = {}), (dialogVisible = true)">新建</el-button>
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
            <el-button text @click=";(form = JSON.parse(JSON.stringify(row))), (dialogVisible = true)">编辑</el-button>
            <el-button text @click="del(row.id)">删除</el-button>
            <el-button text @click="distributeUserCode(row.id)">通知</el-button>
            <el-button text @click="downloadBlob(row.codeUrl, row.codeName)">活码下载</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="query.page"
        v-model:limit="query.size"
        @pagination="getList()" />
    </div>

    <el-dialog :title="form.id ? '编辑' : '新建'" v-model="dialogVisible" width="50%">
      <aev :data="form" ref="aev"></aev>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit" v-loading="loading">确定</el-button>
      </template>
    </el-dialog>
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
