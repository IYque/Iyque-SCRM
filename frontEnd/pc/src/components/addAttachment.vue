<script setup lang="ts">
import { dictMsgType } from '@/utils/index'

const props = defineProps({
  form: { type: Object, default: () => {} },
  max: { type: [String, Number], default: 9 },
  isDetail: { type: Boolean, default: false },
})

const bottomRef = ref()
const contentFormRef = ref()
const active = ref(0)

function add(msgtype, item, index) {
  props.form.annexLists ??= []
  active.value = props.form.annexLists?.push({ msgtype, [msgtype]: {} }) - 1
  setTimeout(() => {
    scrollIntoView(bottomRef.value)
  }, 100)
}

function remove(nameIndex, item, index) {
  $sdk.confirm().then(() => {
    props.form.annexLists?.splice(nameIndex, 1)
    if (nameIndex <= active.value) {
      active.value = props.form.annexLists?.length - 1
    }
    // if (props.form.annexLists?.length == 0) {
    //   $refs.value.form.clearValidate('weclomeMsg')
    // }
  })
}

function scrollIntoView(el) {
  el.scrollIntoViewIfNeeded ? el.scrollIntoViewIfNeeded(false) : el.scrollIntoView({ behavior: 'smooth', block: 'end' })
}

defineExpose({
  contentFormRef,
})
</script>

<template>
  <div>
    <el-popover
      v-if="!isDetail"
      trigger="hover"
      :content="'最多添加' + max + '个'"
      placement="top-start"
      :disabled="form.annexLists?.length < max">
      <template #reference>
        <el-dropdown @command="add" :disabled="form.annexLists?.length >= max">
          <el-button type="primary" class="mb10">添加</el-button>
          <template #dropdown>
            <el-dropdown-menu trigger="click">
              <el-dropdown-item v-for="(item, index) in dictMsgType" :key="index" :command="item.type">
                <el-button text>{{ item.name }}</el-button>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>
    </el-popover>
    <el-alert
      v-if="!isDetail"
      class="mb15"
      title="注: 1.图片:10MB,支持JPG,PNG格式; 2.视频:10MB,支持MP4格式; 3.普通文件:20MB"
      type="error"
      :closable="false"></el-alert>
    <el-tabs
      v-if="form.annexLists?.length"
      ref="tabs"
      v-model="active"
      type="card"
      class=""
      closable
      @tab-remove="remove">
      <el-tab-pane
        v-for="(item, index) in form.annexLists"
        :key="item.msgtype"
        :label="dictMsgType[item.msgtype].name"
        :name="index">
        <MessageContentForm :type="item.msgtype" ref="contentFormRef" :form="item[item.msgtype]" />
      </el-tab-pane>
    </el-tabs>
    <div v-else-if="isDetail">暂无附件</div>
    <div ref="bottomRef"></div>
  </div>
</template>

<style lang="scss" scoped></style>
