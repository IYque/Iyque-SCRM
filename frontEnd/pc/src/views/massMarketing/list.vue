<script setup lang="ts">
import { getList, del, save } from './api'

defineProps({
  type: { type: String, default: 'single' },
})

const sendType = { '1': '立即发送', '2': '定时发送' }

// const form = ref({})
const addAttachmentRef = ref({})

async function submit(form) {
  form = JSON.parse(JSON.stringify(form.value))
  form.chatType = type.value
  form.periodAnnexLists = []
  let tasks = form.annexLists.map(async (e, i) => {
    let contentForm = await addAttachmentRef.value[i].submit()
    if (contentForm) {
      e[e.msgtype] = Object.assign(e[e.msgtype] || {}, contentForm)
      return true
    } else {
      return false
    }
  })
  let validate1 = tasks ? await Promise.all(tasks) : true
  return form
}
</script>

<template>
  <div :_="$store.setBusininessDesc(`<div>通过企业微信群发工具触达客户</div>`)">
    <RequestChartTable ref="rct" :request="getList" searchBtnType="icon">
      <template #query="{ query }">
        <el-form-item label="群发内容" prop="groupMsgName">
          <el-input v-model="query.groupMsgName" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="发送类型" prop="complainType">
          <el-select v-model="query.sendType" :popper-append-to-body="false">
            <el-option v-for="(value, key) in sendType" :key="key" :label="value" :value="key" />
          </el-select>
        </el-form-item>
      </template>

      <template #operation="{ selectedIds }">
        <el-button type="primary" @click="$refs.dialogRef.action()">新建任务</el-button>

        <BaseDialog
          ref="dialogRef"
          title="新建任务"
          width="1000"
          :formProps="{ 'label-width': 'auto' }"
          :rules="{
            scopeType: [$sdk.ruleRequiredChange],
            sendType: [$sdk.ruleRequiredChange],
            complainTime: [$sdk.ruleRequiredChange],
            content: [$sdk.ruleRequiredBlur],
            groupMsgName: [$sdk.ruleRequiredBlur],
          }"
          @confirm="
            ({ form, visible, loading }) => {
              submit(form)
            }
          ">
          <template #form="{ form }">
            <el-form-item label="群发任务名" prop="groupMsgName">
              <el-input
                v-model="form.groupMsgName"
                clearable
                autocomplete="off"
                maxlength="15"
                show-word-limit></el-input>
            </el-form-item>
            <template v-if="type == 'single'">
              <el-form-item label="群发客户群" prop="scopeType">
                <el-radio-group v-model="form.scopeType">
                  <el-radio :label="0">全部群</el-radio>
                  <el-radio :label="1">部分群</el-radio>
                </el-radio-group>
              </el-form-item>
              <template v-if="form.scopeType == 1">
                <el-form-item label="选择客群" prop="scopeType">
                  <el-button type="primary" @click="$refs.selectHotWordRef.dialogRef.visible = true">
                    选择客群
                  </el-button>
                </el-form-item>
              </template>
            </template>
            <template v-if="type == 'group'">
              <el-form-item label="群发客户" prop="scopeType">
                <el-radio-group v-model="form.scopeType">
                  <el-radio :label="0">全部客户</el-radio>
                  <el-radio :label="1">部分客户</el-radio>
                </el-radio-group>
              </el-form-item>
              <template v-if="form.scopeType == 1">
                <el-form-item label="选择客户" prop="scopeType">
                  <el-button type="primary" @click="$refs.selectHotWordRef.dialogRef.visible = true">
                    选择客户
                  </el-button>
                </el-form-item>
              </template>
            </template>

            <el-form-item label="发送类型" prop="sendType">
              <el-radio-group v-model="form.sendType">
                <el-radio :label="index" v-for="(item, index) in sendType" :key="index">{{ item }}</el-radio>
                <!-- <el-radio :label="1" disabled>定时发送</el-radio> -->
              </el-radio-group>
            </el-form-item>
            <template v-if="form.sendType == 1">
              <el-form-item label="发送时间" prop="complainTime">
                <el-date-picker
                  v-model="form.complainTime"
                  type="datetime"
                  placeholder="选择日期时间"
                  :picker-options="{
                    disabledDate(time) {
                      return time.getTime() < Date.now()
                    },
                  }"
                  :default-time="['00:00:00', '23:59:59']"
                  format="yyyy-MM-dd HH:mm:ss"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  clearable></el-date-picker>
              </el-form-item>
            </template>
            <el-form-item label="群发内容" prop="content" :required="form.annexLists?.length > 0">
              <TextareaExtend
                v-model="form.content"
                :toolbar="['emoji']"
                maxlength="200"
                show-word-limit
                placeholder="请输入"
                :autosize="{ minRows: 5, maxRows: 20 }"
                clearable
                :autofocus="false" />
            </el-form-item>
            <el-form-item label="群发附件" prop="">
              <addAttachment ref="addAttachmentRef" :form="form" @remove="remove" />
            </el-form-item>
          </template>
        </BaseDialog>
      </template>

      <template #table="{ data }">
        <el-table-column prop="groupMsgName" label="群发任务名"></el-table-column>
        <el-table-column prop="content" label="群发内容"></el-table-column>
        <el-table-column prop="sendType" label="发送类型">
          <template #default="{ row }">
            {{ sendType[row.sendType] }}
          </template>
        </el-table-column>
        <el-table-column prop="complainTime" label="发送时间"></el-table-column>
        <el-table-column prop="handleTime" label="最近更新时间"></el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <TableOperateBtn type="" @click="$refs.dialogRefDetail.action(row)">详情</TableOperateBtn>
            <!-- <TableOperateBtn type="" @click="$refs.dialogRefDetail.action(row)">统计</TableOperateBtn> -->
          </template>
        </el-table-column>
      </template>
    </RequestChartTable>
  </div>
</template>

<style scoped lang="scss">
.custom-label {
  color: #ff0000; /* 修改为你想要的颜色 */
  font-size: 16px; /* 修改为你想要的字体大小 */
}
.code-image {
  width: 200px;
  height: 200px;
}

.code-image--small {
  width: 50px;
  height: 50px;
}
</style>
