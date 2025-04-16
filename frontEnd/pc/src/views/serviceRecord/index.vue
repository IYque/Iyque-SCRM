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
    <RequestChartTable ref="rct" :request="getList" searchBtnType="icon">
      <template #query="{ query }">
        <el-form-item label="所属客服" prop="complainType">
          <el-select v-model="query.handleState" :popper-append-to-body="false">
            <el-option v-for="(value, key) in handleState" :key="key" :label="value" :value="key" />
          </el-select>
        </el-form-item>
      </template>

      <template #table="{ data }">
        <el-table-column label="咨询客户" fixed="left" align="center" prop="" min-width="140">
          <template #default="{ row }">
            <ShowCustomerService :url="row.customerAvatar" :name="row.customerName"></ShowCustomerService>
          </template>
        </el-table-column>
        <el-table-column label="所属客服" align="center" min-width="200" prop="list">
          <template #default="{ row }">
            <ShowCustomerService :url="row.kfAvatar" :name="row.kfName" showicon></ShowCustomerService>
          </template>
        </el-table-column>
        <el-table-column label="接待员工" align="center" min-width="100" prop="userName">
          <template #default="{ row }">
            {{ row.userName ? row.userName : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="咨询开始时间" align="center" prop="sessionStartTime" width="180"></el-table-column>
        <el-table-column label="咨询结束时间" align="center" prop="sessionEndTime" width="180"></el-table-column>
        <el-table-column label="咨询时长" align="center" prop="duration" width="140"></el-table-column>
        <el-table-column label="操作" align="center" fixed="right" width="130">
          <template #default="{ row }">
            <el-button text @click="showResultList(row)">咨询记录</el-button>
          </template>
        </el-table-column>
      </template>
    </RequestChartTable>

    <BaseDialog ref="dialogRefDetail" title="咨询记录" width="500">
      <PhoneChatList />
    </BaseDialog>
  </div>
</template>

<style scoped lang="scss"></style>
