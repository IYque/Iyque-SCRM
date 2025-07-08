<script setup>
import { getList, findOperateLogTypes, synchOperateLog } from './api'
import { ref } from 'vue'

const props = defineProps({
  type: { type: String, default: '1' },
})

const categoryList = ref([])
function findOperateLogTypesFn() {
 
  findOperateLogTypes({
 operateType: props.type
  }).then(({ data }) => {
    categoryList.value = data
  })
}
findOperateLogTypesFn()
</script>

<template>
  <div>
    <RequestChartTable ref="rctRef" :params="{ operateType: type }" :request="getList" searchBtnType="icon">
      <template #query="{ query }">
        <el-form-item label="员工名称" prop="userName">
          <el-input v-model="query.userName" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="类型" prop="operateTypeSub">
          <el-select v-model="query.operateTypeSub" :popper-append-to-body="false">
            <el-option v-for="item in categoryList" :key="item.val" :label="item.value" :value="item.subCode" />
          </el-select>
        </el-form-item>

        <el-form-item label="操作时间" prop="dateRange">
          <el-date-picker
            clearable
            v-model="query.dateRange"
            value-format="YYYY-MM-DD"
            type="daterange"
            v-bind="pickerOptions"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"></el-date-picker>
        </el-form-item>
      </template>

      <template #operation="{ selectedIds }" v-if="!isSelect">
        <el-button type="primary" @click="synchOperateLog({ operateType: type }).then(() => $sdk.msgSuccess())">
          同步
        </el-button>
      </template>

      <template #="{ data }">
        <el-table class="align-center" :data="data">
          <el-table-column label="操作时间" prop="createTime"></el-table-column>
          <el-table-column label="操作人" prop="userName"></el-table-column>
          <el-table-column label="操作类型" prop="operateTypeSub">
            <template #default="{ row }">
              {{ categoryList?.find((e) => e.subCode == row.operateTypeSub)?.value }}
            </template>
          </el-table-column>
          <el-table-column label="操作内容" prop="operateContent"></el-table-column>
          <el-table-column label="操作者IP" prop="operateIp"></el-table-column>
        </el-table>
      </template>
    </RequestChartTable>
  </div>
</template>

<style lang="less" scoped></style>
