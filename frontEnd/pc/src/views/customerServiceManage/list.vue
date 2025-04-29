<template>
  <div
    :_="$store.setBusininessDesc(`<div>微信客服支持企业在微信内、外各场景中接入，由用户发起咨询，企业进行回复</div>`)">
    <RequestChartTable ref="rctRef" :request="getList" searchBtnType="icon">
      <template #query="{ query }">
        <el-form-item label="客服名称" prop="kfName">
          <el-input v-model="query.kfName" placeholder="请输入" clearable />
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
          width="630"
          :formProps="{ 'label-width': '100px' }"
          :rules="{
            kfName: $sdk.ruleRequiredBlur,
            kfPicUrl: $sdk.ruleRequiredChange,
            welcomeMsg: $sdk.ruleRequiredBlur,
            kId: $sdk.ruleRequiredChange,
            switchType: $sdk.ruleRequiredChange,
            switchText: $sdk.ruleRequiredBlur,
            switchUserIds: $sdk.ruleRequiredChange,
            swichQrUrl: $sdk.ruleRequiredChange,
          }"
          @confirm="() => $refs.dialogRef.confirm(save, $refs.rctRef.getList)">
          <template #form="{ form }">
            <el-form-item prop="kfName" label="客服名称">
              <el-input v-model="form.kfName" placeholder="请输入" maxlength="15" show-word-limit clearable></el-input>
            </el-form-item>
            <el-form-item prop="kfPicUrl" label="客服头像">
              <Upload v-model:fileUrl="form.kfPicUrl" type="image">
                <template #tip><div>建议大小 2M 以内，仅支持 png/jpg 等图片类型</div></template>
              </Upload>
            </el-form-item>
            <el-form-item prop="welcomeMsg" required label="欢迎语">
              <TextareaExtend
                v-model="form.welcomeMsg"
                maxlength="200"
                show-word-limit
                placeholder="请输入"
                :autosize="{ minRows: 5, maxRows: 20 }"
                clearable />
            </el-form-item>
            <el-form-item label="客服知识库" prop="kId">
              <el-select v-model="form.kId" :popper-append-to-body="false">
                <el-option v-for="(item, key) in knowledges" :key="key" :label="item.kname" :value="item.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="" required prop="switchType">
              <div class="--Padding --RadiusSmall bg-(--BgBlack9)">
                <div>当知识库无匹配数据时：</div>
                <el-radio-group v-model="form.switchType" class="">
                  <el-radio :value="1">文字回复</el-radio>
                  <el-radio :value="2">转人工</el-radio>
                  <el-radio :value="3">员工活码</el-radio>
                  <el-radio :value="4">AI 大模型回复</el-radio>
                </el-radio-group>
                <el-form-item label="" required prop="switchText" v-if="form.switchType == 1">
                  <TextareaExtend
                    v-model="form.switchText"
                    maxlength="200"
                    show-word-limit
                    placeholder="请输入"
                    :autosize="{ minRows: 5, maxRows: 20 }"
                    clearable />
                </el-form-item>
                <el-form-item label="" required prop="switchUserIds" v-if="form.switchType == 2">
                  <SelectStaff
                    v-model="form._switchUser"
                    :multiple="false"
                    @modelValueUpdate="(val) => ((form.switchUserIds = val.userId), (form.switchUserNames = val.name))"
                    title="选择员工"></SelectStaff>
                </el-form-item>
                <el-form-item label="" required prop="swichQrUrl" v-if="form.switchType == 3">
                  <div>
                    <el-button type="primary" plain @click="$refs.SelectStaffQrCodeRef.dialogRef.visible = true">
                      选择活码
                    </el-button>
                    <br />
                    <el-image class="w-[200px] mt10" v-if="form.swichQrUrl" :src="form.swichQrUrl"></el-image>

                    <SelectStaffQrCode
                      isSigleSelect
                      append-to-body
                      width="800"
                      ref="SelectStaffQrCodeRef"
                      title="选择活码"
                      @confirm="
                        ({ visible, loading, selected }) => (
                          (form.swichQrUrl = selected[0].codeUrl), (visible.value = false), (loading.value = false)
                        )
                      "></SelectStaffQrCode>
                  </div>
                </el-form-item>
              </div>
            </el-form-item>
          </template>
        </BaseDialog>
      </template>

      <template #table="{ data }">
        <el-table-column prop="kfName" label="客服名称"></el-table-column>
        <el-table-column prop="complainUserPhone" label="客服二维码">
          <template #default="{ row }">
            <el-popover placement="bottom" trigger="hover">
              <template #reference><el-image :src="row.kfQrUrl" style="width: 50px"></el-image></template>
              <el-image :src="row.kfQrUrl" style="width: 200px"></el-image>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="kfUrl" label="客服链接"></el-table-column>
        <!-- <el-table-column prop="handleUserName" label="接待员工"></el-table-column> -->
        <el-table-column prop="updateTime" label="最近更新时间"></el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <TableOperateBtn type="" @click="$refs.dialogRef.action(row)">编辑</TableOperateBtn>
            <TableOperateBtn type="" @click="$sdk.downloadBlob(row.kfUrl, row.kfName + '.png', 'image')">
              二维码下载
            </TableOperateBtn>
            <TableOperateBtn type="" @click="$refs.rctRef.apiConfirm(del, row.id)">删除</TableOperateBtn>
          </template>
        </el-table-column>
      </template>
    </RequestChartTable>
  </div>
</template>
<script setup>
import { getList, save, del } from './api'
import * as knowApi from '../KBM/api'
const knowledges = ref([]) // 知识库列表
// 获取指标数据
;(function getKnowledgeAll() {
  knowApi
    .getKnowledgeAll()
    .then(({ data }) => {
      knowledges.value = data
      console.log(data)
    })
    .finally(() => {})
})()
</script>
<style scoped lang="scss"></style>
