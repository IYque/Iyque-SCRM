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
  <div
    :_="$store.setBusininessDesc(`<div>微信客服支持企业在微信内、外各场景中接入，由用户发起咨询，企业进行回复</div>`)">
    <RequestChartTable ref="rct" :request="getList" searchBtnType="icon">
      <template #query="{ query }">
        <el-form-item label="客服名称" prop="complainType">
          <el-select v-model="query.handleState" :popper-append-to-body="false">
            <el-option v-for="(value, key) in handleState" :key="key" :label="value" :value="key" />
          </el-select>
        </el-form-item>
      </template>

      <template #operation="{ selectedIds }">
        <div class="mid-action mb0">
          <el-button type="primary" @click="$refs.dialogRef.action()">新建客服</el-button>

          <el-button type="primary" plain :disabled="!selectedIds?.length" @click="$refs.rctRef?.apiConfirm(del)">
            批量删除
          </el-button>
        </div>

        <BaseDialog
          ref="dialogRef"
          dynamicTitle="客服"
          width="500"
          :formProps="{ 'label-width': '100px' }"
          :rules="{ users: { required: true, message: '必选项', trigger: 'change' } }"
          @confirm="
            () => $refs.dialogRef.confirm(() => setIYQueComplaintTip(form.users?.map((e) => ({ userId: e.userId }))))
          ">
          <template #form="{ form }">
            <el-form-item prop="hotWord" label="客服名称">
              <el-input v-model="form.name" maxlength="15" show-word-limit clearable></el-input>
            </el-form-item>
            <el-form-item label="客服头像">
              <Upload v-model:fileUrl="form.coverUrl" v-model:imgSize="form.memorySize" type="0">
                <template #tip><div>建议大小 2M 以内，仅支持 png/jpg 等图片类型</div></template>
              </Upload>
            </el-form-item>
            <el-form-item required label="欢迎语:">
              <TextareaExtend
                v-model="form.content"
                maxlength="200"
                show-word-limit
                placeholder="请输入"
                :autosize="{ minRows: 5, maxRows: 20 }"
                clearable />
            </el-form-item>
            <el-form-item prop="users" class="" label="接待员工">
              <SelectStaff v-model="form.users" title="选择员工"></SelectStaff>
              <div class="g-tip">每个客服账号最多添加500个接待员工</div>
            </el-form-item>
            <el-form-item label="分配方式:" required prop="receptionType">
              <div class="flex">
                <div>
                  <el-radio v-model="form.allocationWay" :label="1">轮流分配</el-radio>
                  <div class="g-tip">新咨询轮流分配给所有接待员工</div>
                </div>
                <div style="margin-left: 20px">
                  <el-radio v-model="form.allocationWay" :label="2">空闲分配</el-radio>
                  <div class="g-tip">新咨询分配给当前接待人数最少的接待员工</div>
                </div>
              </div>
            </el-form-item>
          </template>
        </BaseDialog>
      </template>

      <template #table="{ data }">
        <el-table-column prop="complainTypeContent" label="客服名称"></el-table-column>
        <el-table-column prop="complainUserPhone" label="客服二维码">
          <template #default="{ row }">
            <el-popover placement="bottom" trigger="hover">
              <template #reference><el-image :src="row.chatCodeUrl" style="width: 50px"></el-image></template>
              <el-image :src="row.chatCodeUrl" style="width: 200px"></el-image>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="complainTime" label="客服链接"></el-table-column>
        <el-table-column prop="handleUserName" label="接待员工"></el-table-column>
        <el-table-column prop="handleTime" label="最近更新时间"></el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <TableOperateBtn type="" @click="$refs.dialogRef.action(row)">编辑</TableOperateBtn>
            <TableOperateBtn type="" @click="$sdk.downloadBlob(row.keywordGroupQrUrl, row.title + '.png', 'image')">
              二维码下载
            </TableOperateBtn>
            <TableOperateBtn type="" @click="$refs.rctRef.apiConfirm(del, row.id)">删除</TableOperateBtn>
          </template>
        </el-table-column>
      </template>
    </RequestChartTable>
  </div>
</template>

<style scoped lang="scss"></style>
