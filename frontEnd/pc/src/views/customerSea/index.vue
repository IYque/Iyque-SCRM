<script setup>
import * as api from './api'

const props = defineProps({
  type: { type: String, default: '1' },
})

const categoryList = ref({
  0: '待添加',
  1: '待通过',
  2: '已通过',
})
function findOperateLogTypesFn() {
  findOperateLogTypes({
    operateType: props.type,
  }).then(({ data }) => {
    categoryList.value = data
  })
}
// findOperateLogTypesFn()
</script>

<template>
  <div>
    <RequestChartTable ref="rctRef" :params="{ operateType: type }" :request="api.getList" searchBtnType="icon">
      <template #query="{ query }">
        <el-form-item label="电话号码" prop="phoneNumber">
          <el-input v-model="query.phoneNumber" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="客户备注" prop="customerName">
          <el-input v-model="query.customerName" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="员工名称" prop="userName">
          <el-input v-model="query.userName" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="添加状态" prop="currentState">
          <el-select v-model="query.currentState" :popper-append-to-body="false">
            <el-option v-for="(item, key) in categoryList" :key="key" :label="item" :value="key" />
          </el-select>
        </el-form-item>
      </template>

      <template #operation="{ selectedIds }" v-if="!isSelect">
        <div class="flex justify-between items-center">
          <el-button type="primary" @click="$refs.ImportDialogRef?.$refs.dialogRef.action()">导入客户</el-button>
          <ImportDialog
            ref="ImportDialogRef"
            title="导入客户"
            :downloadTemplateRequest="api.getExport"
            downloadFileName="导入客户模板.xlsx"
            :formProps="{
              rules: {
                // file: [$sdk.ruleRequiredBlur],
                users: [$sdk.ruleRequiredChange],
              },
            }"
            @confirm="
              ({ form, loading, visible }) =>
                api
                  .importData({
                    allocateUsers: form.value.users.map((e) => ({ userId: e.id, name: e.name })),
                    file: form.value.file,
                  })
                  .then(() => {
                    $refs.rctRef?.getList(1)
                    visible.value = false
                  })
                  .finally(() => {
                    loading.value = false
                  })
            ">
            <template #default="{ form }">
              <el-form-item label="选择员工" prop="users">
                <SelectStaff v-model="form.users" title="选择员工"></SelectStaff>
                <div class="g-tip">公海客户平均分配给选择的员工</div>
              </el-form-item>
            </template>
          </ImportDialog>

          <div>
            <el-button
              type="primary"
              plain
              :disabled="!selectedIds?.length"
              @click="$refs.rctRef?.apiConfirm(api.distribute, undefined, '是否确认批量提醒?')">
              批量提醒
            </el-button>
            <el-button type="primary" plain :disabled="!selectedIds?.length" @click="$refs.rctRef?.apiConfirm(api.del)">
              批量删除
            </el-button>
          </div>
        </div>
      </template>

      <template #="{ data }">
        <el-table class="align-center" :data="data">
          <el-table-column label="电话号码" prop="phoneNumber"></el-table-column>
          <el-table-column label="客户备注" prop="customerName"></el-table-column>
          <el-table-column label="分配员工" prop="allocateUserName"></el-table-column>
          <el-table-column label="当前状态" prop="operateTypeSub">
            <template #default="{ row }">
              {{ categoryList[row.currentState] }}
              <!-- {{ categoryList?.find((e) => e.subCode == row.operateTypeSub)?.value }} -->
            </template>
          </el-table-column>
          <el-table-column label="导入时间" prop="createTime"></el-table-column>
          <el-table-column label="操作" fixed="right">
            <template #default="{ row }">
              <TableOperateBtn @click="$refs.rctRef?.apiConfirm(api.distribute, row.id, '是否确认提醒?')">
                提醒
              </TableOperateBtn>
              <TableOperateBtn @click="$refs.rctRef.apiConfirm(api.del, row.id)">删除</TableOperateBtn>
            </template>
          </el-table-column>
        </el-table>
      </template>
    </RequestChartTable>
  </div>
</template>

<style lang="less" scoped></style>
