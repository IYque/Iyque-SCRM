<script>
import * as api from './api'
import AddTag from '@/components/AddTag'

export default {
  components: { AddTag },
  props: {},
  data() {
    return {
      api,
      // 遮罩层
      loading: false,
      dialogVisible: false,
      // 标签分组类型(1:客户企业标签;2:客群标签)
      groupTagType: 2,
      // 表单参数
      form: {
        groupName: '',
        weTags: [],
        groupTagType: 2,
      },
      // 添加标签输入框
      newInput: '',
      // 添加标签显隐
      newInputVisible: false,
      // 表单验证规则
      rules: Object.freeze({
        groupName: [{ required: true, message: '必填项', trigger: 'blur' }],
      }),
      // 非多个禁用
      multiple: true,
    }
  },
  computed: {},
  created() {},
  methods: {
    edit(data, type) {
      this.form = JSON.parse(JSON.stringify(Object.assign({ groupTagType: this.groupTagType, weTags: [] }, data || {})))
      this.dialogVisible = true
    },
  },
}
</script>
<template>
  <div class="groupTag">
    <RequestChartTable ref="rct" :request="api.getList" searchBtnType="icon">
      <template #query="{ query }">
        <BaInput label="标签组名称" prop="groupName" v-model="query.groupName"></BaInput>
      </template>

      <template #operate="{ goRoute }">
        <el-button type="primary" @click="edit()">新建标签组</el-button>
      </template>

      <template #operation="{ goRoute, apiConfirm }">
        <div class="flex justify-end">
          <el-button type="danger" plain @click="apiConfirm(api.del)">批量删除</el-button>
        </div>
      </template>

      <template #table="{ apiConfirm, goRoute }">
        <el-table-column label="标签组" align="center" prop="groupName" />
        <el-table-column label="标签" align="center" prop="weTags">
          <template #default="{ row }">
            <div v-if="row.weTags">
              <TagEllipsis :list="row.weTags"></TagEllipsis>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center">
          <template #default="{ row, index }">
            <el-button text @click="edit(row, index)">编辑</el-button>
            <el-button text @click="apiConfirm(api.del, row.groupId)">删除</el-button>
          </template>
        </el-table-column>
      </template>
    </RequestChartTable>

    <!-- 弹窗 -->
    <AddTag
      v-model:visible="dialogVisible"
      :form="form"
      :key="dialogVisible"
      @success="$refs.rct.getList(!form.groupId && 1)"
      module="groupTag" />
  </div>
</template>
<style lang="scss" scoped></style>