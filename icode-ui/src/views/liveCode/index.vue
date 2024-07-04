<script>
import { getList, del, distributeUserCode } from './api'
import { env } from '../../../sys.config'
import aev from './aev.vue'
export default {
  data() {
    return {
      activeName: 'first',
      query: { page: 0, size: 10 },
      list: '',
      total: 0,
      multipleSelection: [], // 多选数据
      loading: false,
      dialogVisible: false, // 弹窗显示控制
      form: {},

      query2: {
        value3: '',
      },
      options: [
        {
          value: 'Option1',
          label: 'Option1',
        },
        {
          value: 'Option2',
          label: 'Option2',
        },
        {
          value: 'Option3',
          label: 'Option3',
        },
        {
          value: 'Option4',
          label: 'Option4',
        },
        {
          value: 'Option5',
          label: 'Option5',
        },
      ],
      xData: [],
      series: [],
    }
  },
  components: { aev },
  watch: {},
  created() {
    this.getList()
    this.getData()
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

    distributeUserCode(id) {
      this.$confirm('是否将当前活码下发给员工, 是否继续?', '提示', {
        confirmButtonText: '是',
        cancelButtonText: '否',
        type: 'warning',
      })
        .then(() => {
          distributeUserCode(id).then((res) => {
            this.msgSuccess('已通知')
          })
        })
        .catch((e) => {
          console.error(e)
        })
    },

    downloadBlob(url, fileName) {
      // 检查url是否以http或https开头
      if (!url.startsWith('http') && !url.startsWith('https')) {
        url = env.BASE_API + '/file/fileView/' + url
      }

      // 接下来是原有的downloadBlob方法逻辑
      const link = document.createElement('a')
      link.href = url
      link.download = fileName
      link.click()
    },

    submit() {
      this.loading = true
      this.$refs.aev
        .submit()
        .then(() => {
          this.dialogVisible = false
          this.getList()
        })
        .catch((e) => console.error(e))
        .finally(() => (this.loading = false))
    },

    getData() {
      // this.$store.loading = true
      this.xData = ['2021-12-01', '2021-12-02', '2021-12-03', '2021-12-03', '2021-12-04', '2021-12-05', '2021-12-06']
      this.series = [
        [120, 132, 101, 134, 90, 230, 210],
        [220, 182, 191, 234, 290, 330, 310],
      ]
      // getList(this.query2)
      //   .then(({ data, count }) => {
      //     this.list = data
      //     this.total = +count
      //     this.multipleSelection = []
      //   })
      //   .catch((e) => console.error(e))
      //   .finally(() => (this.$store.loading = false))
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

    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="渠道码配置" name="first">
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
                <el-button text @click=";(form = JSON.parse(JSON.stringify(row))), (dialogVisible = true)">
                  编辑
                </el-button>
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
      </el-tab-pane>

      <el-tab-pane label="渠道码统计" name="second">
        <el-form class="searchForm" ref="searchForm" :model="query2" label-width="" inline>
          <el-form-item label="活码名称" prop="value3">
            <el-select
              v-model="query2.value3"
              multiple
              collapse-tags
              collapse-tags-tooltip
              :max-collapse-tags="2"
              placeholder="Select"
              style="width: 260px">
              <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getData">查询</el-button>
            <el-button @click="$refs.searchForm.resetFields(), getData">重置</el-button>
          </el-form-item>
        </el-form>
        <div class="g-card">
          <el-row :gutter="20">
            <el-col :span="6">
              <div>
                <el-statistic value-style="font-size:20px;" title="新增客户总数">
                  <template slot="formatter">456/2</template>
                </el-statistic>
              </div>
              <br />
              <div>
                <el-statistic title="今日新增客户数">
                  <template slot="formatter">456/2</template>
                </el-statistic>
              </div>
            </el-col>
            <el-col :span="6">
              <div>
                <el-statistic title="流失客户总数">
                  <template slot="formatter">456/2</template>
                </el-statistic>
              </div>
              <br />
              <div>
                <el-statistic title="今日流失客户数">
                  <template slot="formatter">456/2</template>
                </el-statistic>
              </div>
            </el-col>
            <el-col :span="6">
              <div>
                <el-statistic title="员工删除客户总数">
                  <template slot="formatter">456/2</template>
                </el-statistic>
              </div>
              <br />
              <div>
                <el-statistic title="今日员工删除客户数">
                  <template slot="formatter">456/2</template>
                </el-statistic>
              </div>
            </el-col>
            <el-col :span="6">
              <div>
                <el-statistic title="净增客户总数">
                  <template slot="formatter">456/2</template>
                </el-statistic>
              </div>
              <br />
              <div>
                <el-statistic title="今日净增客户数">
                  <template slot="formatter">456/2</template>
                </el-statistic>
              </div>
            </el-col>
          </el-row>
        </div>

        <div class="g-card">
          <ChartLine :xData="xData" :legend="['新增客户', '新增客户1']" :series="series"></ChartLine>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog :title="form.id ? '编辑' : '新建'" v-model="dialogVisible" width="80%">
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

.like {
  cursor: pointer;
  font-size: 25px;
  display: inline-block;
}
</style>
