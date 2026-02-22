<script setup lang="ts">
import { createFriendCircle, updateFriendCircle, getFriendCircleDetail } from './api'
import { dictMsgType } from '@/utils/index'

const MessageContentForm = defineAsyncComponent(() => import('@/components/MessageContentForm'))

// 创建内容表单引用
const contentForm = ref([])
const bottom = ref(null)

const $router = useRouter()
const $route = useRoute()

import stores from '@/stores'
const $store = stores()

const active = ref(0)
const max = ref(1)

const info = ref({
  name: '',
  content: '',
  annexLists: []
})

// 编辑获取详情
;(function getDetailFun(id = $route.query?.id) {
  if (!id) return
  $store.loading = true
  getFriendCircleDetail(id)
    .then(({ data }) => {
      Object.assign(info.value, data)
      // 确保annexLists存在
      if (!info.value.annexLists) {
        info.value.annexLists = []
      }
    })
    .finally(() => {
      $store.loading = false
    })
})()

async function submit(submitFn, formRef) {
  let form = JSON.parse(JSON.stringify(info.value))
  
  if (!form.name) {
    return $sdk.msgError('请输入朋友圈名称')
  }
  
  if (!form.content) {
    return $sdk.msgError('请输入文本内容')
  }

  // 处理附件表单
  if (form.annexLists && form.annexLists.length > 0) {
    let tasks = form.annexLists.map(async (e, i) => {
      if (contentForm[i]) {
        let contentFormData = await contentForm[i].submit()
        if (contentFormData) {
          e[e.msgtype] = Object.assign(e[e.msgtype] || {}, contentFormData)
          return true
        } else {
          return false
        }
      }
      return true
    })
    await Promise.all(tasks)
  }

  const saveFn = info.value.id ? updateFriendCircle : createFriendCircle
  submitFn(() => saveFn(form), $router.back)
}

function add(msgtype) {
  info.value.annexLists.push({ msgtype, [msgtype]: {} })
  active.value = info.value.annexLists.length - 1
  setTimeout(() => {
    scrollIntoView($refs.bottom)
  }, 100)
}

function remove(nameIndex) {
  $sdk.confirm().then(() => {
    info.value.annexLists.splice(nameIndex, 1)
    if (nameIndex <= active.value) {
      active.value = info.value.annexLists.length - 1
    }
  })
}

function scrollIntoView(el) {
  if (el) {
    el.scrollIntoViewIfNeeded
      ? el.scrollIntoViewIfNeeded(false)
      : el.scrollIntoView({ behavior: 'smooth', block: 'end' })
  }
}
</script>
<template>
  <BaForm class="h100 flexCol" v-model="info" ref="BaFormRef">
    <template #default="{ form, isDetail, formRef }">
      <div class="flex-auto overflow-auto">
        <div class="g-card">
          <div class="g-card-title">基础信息</div>
          <BaInput label="朋友圈名称" prop="name" required v-model="form.name" maxlength="50"></BaInput>
          <BaFormItem label="文本内容" prop="content" required>
            <el-input
              v-model="form.content"
              type="textarea"
              :rows="5"
              placeholder="请输入朋友圈文本内容"
              :disabled="isDetail"
            ></el-input>
          </BaFormItem>
          
          <BaFormItem label="附件内容">
            <el-popover
              trigger="hover"
              content="只能上传一个附件（图片、链接、视频三选一）"
              placement="top-start"
              :disabled="form.annexLists?.length < max"
            >
              <template #reference>
                <el-dropdown @command="add" :disabled="form.annexLists?.length >= max || isDetail">
                  <el-button type="primary" class="mb10">添加</el-button>
                  <template #dropdown>
                    <el-dropdown-menu trigger="click">
                      <el-dropdown-item v-for="type in ['image', 'link', 'video']" :key="type" :command="type">
                        <el-button text>{{ dictMsgType[type].name }}</el-button>
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </template>
            </el-popover>
            <el-alert
              title="注: 1.图片:10MB,支持JPG,PNG格式; 2.视频:10MB,支持MP4格式"
              type="error"
              :closable="false"
            ></el-alert>
            <br />
            <el-tabs
              ref="tabs"
              v-model="active"
              type="card"
              class=""
              closable
              @tab-remove="remove"
              :disabled="isDetail"
            >
              <el-tab-pane
                v-for="(item, index) in form.annexLists"
                :key="index"
                :label="dictMsgType[item.msgtype].name"
                :name="index"
              >
                <MessageContentForm :type="item.msgtype" :ref="el => contentForm[index] = el" :form="item[item.msgtype]" />
              </el-tab-pane>
            </el-tabs>
            <div ref="bottom"></div>
          </BaFormItem>
        </div>
      </div>
    </template>
    <template #operate="{ form, isDetail, formRef, submitFn }">
      <BaFormBar>
        <el-form>
          <ElButton type="primary" plain @click="$router.back()">取消</ElButton>
          <ElButton
            type="primary"
            v-loading="$store.loading"
            :disabled="$store.loading"
            @click="submit(submitFn, formRef)"
          >
            确定
          </ElButton>
        </el-form>
      </BaFormBar>
    </template>
  </BaForm>
</template>
