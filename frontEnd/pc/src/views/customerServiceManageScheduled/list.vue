<script setup>
import { getList, save, del } from './api'
function saveFn(form) {
  form.switchUserIds = form.users.map((e) => e.userId).join(',')
  form.switchUserNames = form.users.map((e) => e.name).join(',')
  form.workCycle = form.workCycle.join(',')
  return save(form)
}
function action(form, dialogRef) {
  form.switchUserIds = form.switchUserIds.split(',')
  form.switchUserNames = form.switchUserNames.split(',')
  form.users = []
  form.switchUserIds?.forEach((e, index) => {
    form.users.push({
      userId: e,
      name: form.switchUserNames[index],
    })
  })
  form.workCycle = form.workCycle.split(',')
  dialogRef.action(form)
}
</script>
<template>
  <div :_="$store.setBusininessDesc(`<div>支持排班的微信客服，更智能</div>`)">
    <RequestChartTable ref="rctRef" :request="getList" searchBtnType="icon">
      <template #query="{ query }">
        <BaInput label="客服名称" prop="kfName" v-model="query.kfName"></BaInput>
      </template>

      <template #operation="{ selectedIds, getList, goRoute, apiConfirm }">
        <div class="mid-action mb0">
          <el-button type="primary" @click="$refs.dialogRef.action()">新建客服</el-button>

          <el-button type="primary" plain :disabled="!selectedIds?.length" @click="apiConfirm(del)">批量删除</el-button>
        </div>

        <BaDialog
          ref="dialogRef"
          dynamicTitle="客服"
          width="900"
          :formProps="{ 'label-width': '100px' }"
          @confirm="({ confirm }) => confirm(saveFn, getList)">
          <template #form="{ form }">
            <BaInput label="客服名称" prop="kfName" required v-model="form.kfName" maxlength="15"></BaInput>
            <BaFormItem prop="kfPicUrl" label="客服头像" required>
              <Upload v-model:fileUrl="form.kfPicUrl" type="image">
                <template #tip><div>建议大小 2M 以内，仅支持 png/jpg 等图片类型</div></template>
              </Upload>
            </BaFormItem>
            <BaFormItem label="接待员工" prop="users" required>
              <SelectStaff v-model="form.users"></SelectStaff>
            </BaFormItem>
            <BaFormItem label="接待时间" prop="" required>
              <el-card class="box-card roster-card">
                <BaCheckboxGroup
                  label="工作周期"
                  prop="workCycle"
                  required
                  v-model="form.workCycle"
                  :options="[
                    { label: '周一', value: '1' },
                    { label: '周二', value: '2' },
                    { label: '周三', value: '3' },
                    { label: '周四', value: '4' },
                    { label: '周五', value: '5' },
                    { label: '周六', value: '6' },
                    { label: '周日', value: '7' },
                  ]"></BaCheckboxGroup>

                <BaFormItem label="时间段">
                  <el-time-select
                    v-model="form.beginTime"
                    start="00:00"
                    end="23:59"
                    step="00:30"
                    style="width: 160px"
                    :max-time="form.endTime"
                    placeholder="选择时间"></el-time-select>
                  ——
                  <el-time-select
                    start="00:00"
                    end="23:59"
                    step="00:30"
                    style="width: 160px"
                    :min-time="form.beginTime || null"
                    v-model="form.endTime"
                    placeholder="选择时间"></el-time-select>
                </BaFormItem>
                <BaFormItem label="接待语">
                  <TextareaExtend
                    v-model="form.welcomeMsg"
                    maxlength="200"
                    show-word-limit
                    placeholder="请输入接待语"
                    :autosize="{ minRows: 5, maxRows: 20 }"
                    clearable />
                </BaFormItem>
              </el-card>
            </BaFormItem>
            <BaFormItem label="非工作时间接待语" prop="oorWelcome" required>
              <TextareaExtend
                v-model="form.oorWelcome"
                maxlength="200"
                show-word-limit
                placeholder="请输入"
                :autosize="{ minRows: 5, maxRows: 20 }"
                clearable />
            </BaFormItem>
          </template>
        </BaDialog>
      </template>

      <template #table="{ apiConfirm, goRoute }">
        <el-table-column prop="kfName" label="客服名称"></el-table-column>
        <el-table-column prop="complainUserPhone" label="客服二维码">
          <template #default="{ row }">
            <el-image
              class="size-[50px]"
              :preview-src-list="[row.kfQrUrl]"
              :src="row.kfQrUrl"
              fit="fill"
              :lazy="true"></el-image>
          </template>
        </el-table-column>
        <el-table-column prop="kfUrl" label="客服链接"></el-table-column>
        <el-table-column prop="switchUserNames" label="接待员工">
          <template #default="{ row }">
            <TagEllipsis :list="row.switchUserNames" emptyText="-"></TagEllipsis>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="最近更新时间"></el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <TableOperateBtn type="" @click="action(row, $refs.dialogRef)">编辑</TableOperateBtn>
            <TableOperateBtn type="" @click="$sdk.downloadBlob(row.kfQrUrl, row.kfName + '.png', 'image')">
              二维码下载
            </TableOperateBtn>
            <TableOperateBtn type="" @click="apiConfirm(del, row.id)">删除</TableOperateBtn>
          </template>
        </el-table-column>
      </template>
    </RequestChartTable>
  </div>
</template>
<style scoped lang="scss"></style>
