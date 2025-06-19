<script>
import { getList, del } from '../commonMaterial/api'
import AddOrEditMaterialDialog from './AddOrEditMaterialDialog'

export default {
  components: {
    AddOrEditMaterialDialog,
    // AddOrEditMaterialDialog: defineAsyncComponent(() => import('./AddOrEditMaterialDialog')),
  },
  props: {
    // 0:图片；4:文本；7：热词分类；9：图文
    type: {
      type: String,
      default: '0',
    },
    // 选中的素材ids
    selected: {
      type: [Array, String],
      default: '',
    },
  },
  data() {
    return {
      getList,
      del,
      // 查询条件
      query: {},
    }
  },
  watch: {},
  computed: {},
  created() {
    this.query.msgtype = $sdk.dictMaterialType[this.type]?.msgtype
  },
  mounted() {},
  methods: {
    // 素材添加/编辑
    edit(data) {
      this.$refs.dialogRef.action(Object.assign({}, data || { categoryId: this.query.categoryId }))
    },
  },
}
</script>

<template>
  <LeftRightGroupListPage
    ref="lrPageRef"
    :isDisabledAction="isSelect"
    :type="type"
    moduleType="4"
    @currentChange="(data) => $nextTick(() => ((query.categoryId = data.id), $refs.rctRef.getList(1)))">
    <RequestChartTable
      ref="rctRef"
      :isCreateRequest="false"
      :params="query"
      :request="getList"
      searchBtnType="icon"
      @selectionChange="(val) => $emit('selectionChange', val)">
      <template #query="{ query }">
        <el-form-item label="素材标题" prop="title">
          <el-input v-model="query.title" :placeholder="'请输入素材标题'" clearable @keyup.enter="getList(1)" />
        </el-form-item>
      </template>

      <template #operation="{ selectedIds }" v-if="!isSelect">
        <div class="flex justify-between items-center">
          <el-button type="primary" @click="edit()">新建{{ $sdk.dictMaterialType[type]?.name }}</el-button>
          <el-button type="primary" plain :disabled="!selectedIds?.length" @click="$refs.rctRef?.apiConfirm(del)">
            批量删除
          </el-button>
        </div>
      </template>

      <template #table="{ data }">
        <slot v-bind:list="data"></slot>

        <el-table-column label="最近更新" align="center">
          <template #default="{ row }">
            <div>{{ row.updateBy }}</div>
            <span>{{ row.updateTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center">
          <template #default="{ row }">
            <TableOperateBtn type="edit" @click="edit(row)">编辑</TableOperateBtn>
            <TableOperateBtn type="delete" @click="$refs.rctRef.apiConfirm(del, row.id)">删除</TableOperateBtn>
          </template>
        </el-table-column>
      </template>
    </RequestChartTable>

    <!-- 添加或修改素材对话框 -->
    <AddOrEditMaterialDialog
      ref="dialogRef"
      :type="type"
      :dynamicTitle="$sdk.dictMaterialType[type]?.name"
      @success="$refs.rctRef.getList(1)"></AddOrEditMaterialDialog>
  </LeftRightGroupListPage>
</template>

<style lang="scss" scoped></style>
