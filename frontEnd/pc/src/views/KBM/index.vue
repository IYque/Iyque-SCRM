<script setup lang="ts">
import { getList, setIYQueComplaintTip, distributeHandle, updateStatus, findIYQueComplaintTips } from './api'
const handleState = { '1': '未处理', '2': '已处理' }

const employees = ref([])
const form = ref({})

const selectedEmployeeName = computed(() => {
  console.log(employees.value)
  if (employees.value && employees.value.trim() !== '') {
    return employees.value
  }
  return '未设置'
})
onMounted(async () => {
  try {
    const response = await findIYQueComplaintTips()
    if (response.data && Array.isArray(response.data)) {
      const userNames = response.data
        .filter((item) => item && typeof item.userName === 'string')
        .map((item) => item.userName)
        .join(', ')
      employees.value = userNames
      form.value.users = response.data
    } else {
      // ElMessageBox.warning('获取员工数据格式不正确');
    }
  } catch (error) {
    console.error('获取员工数据失败:', error)
  }
})
</script>

<template>
  <div :_="$store.setBusininessDesc(`<div>查看所有客服场景中客户咨询的详细记录</div>`)">
    <RequestChartTable ref="rctRef" :request="getList" searchBtnType="icon">
      <template #table="{ data }">
        <el-table-column label="编号" fixed="left" prop="" min-width="140">
          <template #default="{ row }">
            <ShowCustomerService :url="row.customerAvatar" :name="row.customerName"></ShowCustomerService>
          </template>
        </el-table-column>
        <el-table-column label="知识名称" min-width="200" prop="list">
          <template #default="{ row }">
            <ShowCustomerService :url="row.kfAvatar" :name="row.kfName" showicon></ShowCustomerService>
          </template>
        </el-table-column>
        <el-table-column label="知识描述" min-width="100" prop="userName">
          <template #default="{ row }">
            {{ row.userName ? row.userName : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="130">
          <template #default="{ row }">
            <TableOperateBtn type="" @click="$refs.rctRef.apiConfirm(del, row.id)">删除</TableOperateBtn>
            <TableOperateBtn type="" @click="$refs.dialogRefFile.action(row)">附件</TableOperateBtn>
          </template>
        </el-table-column>
      </template>
    </RequestChartTable>

    <BaseDialog ref="dialogRefFile" title="知识库附件" width="1000">
      <RequestChartTable ref="rctRefFile" :request="getList" searchBtnType="icon">
        <template #operation="{ query }">
          <Upload v-model:fileUrl="form.coverUrl" v-model:imgSize="form.memorySize" type="0">文件上传</Upload>
        </template>

        <template #table="{ data }">
          <el-table-column label="文档编号" fixed="left" prop="" min-width="140">
            <template #default="{ row }">
              <ShowCustomerService :url="row.customerAvatar" :name="row.customerName"></ShowCustomerService>
            </template>
          </el-table-column>
          <el-table-column label="文档名称" min-width="200" prop="list">
            <template #default="{ row }">
              <ShowCustomerService :url="row.kfAvatar" :name="row.kfName" showicon></ShowCustomerService>
            </template>
          </el-table-column>
          <el-table-column label="文档类型" min-width="100" prop="userName">
            <template #default="{ row }">
              {{ row.userName ? row.userName : '-' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right" width="130">
            <template #default="{ row }">
              <TableOperateBtn type="" @click="$refs.rctRefFile.apiConfirm(del, row.id)">删除</TableOperateBtn>
              <TableOperateBtn type="" @click="$refs.dialogRefFragments.action(row)">知识片段</TableOperateBtn>
            </template>
          </el-table-column>
        </template>
      </RequestChartTable>
    </BaseDialog>

    <BaseDialog ref="dialogRefFragments" title="知识片段" width="1000">
      <RequestChartTable ref="rct" :request="getList" searchBtnType="icon">
        <template #table="{ data }">
          <el-table-column label="片段编号" fixed="left" prop="" min-width="140"></el-table-column>
          <el-table-column label="片段内容" min-width="200" prop="list">
            <template #default="{ row }"></template>
          </el-table-column>
        </template>
      </RequestChartTable>
    </BaseDialog>
  </div>
</template>

<style scoped lang="scss"></style>
