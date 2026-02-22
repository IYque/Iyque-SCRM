<script setup lang="ts">
const $router = useRouter()
const $route = useRoute()
import { getFriendCircleDetail } from './api'
import { dictMsgType } from '@/utils/index'

import stores from '@/stores'
const $store = stores()

const info = ref({})
const active = ref(0)

// 获取详情
;(function getDetailFun(id = $route.query?.id) {
  if (!id) return
  $store.loading = true
  getFriendCircleDetail(id)
    .then(({ data }) => {
      Object.assign(info.value, data)
      // 确保active值在有效范围内
      if (info.value.annexLists && info.value.annexLists.length > 0) {
        active.value = 0
      }
    })
    .finally(() => {
      $store.loading = false
    })
})()
</script>

<template>
  <div class="h100 flexCol">
    <div class="flex-auto overflow-auto">
      <div class="g-card">
        <div class="g-card-title">朋友圈详情</div>
        <BaInput label="朋友圈名称" prop="name" v-model="info.name" :disabled="true"></BaInput>
        <BaFormItem label="文本内容" prop="content">
          <el-input
            v-model="info.content"
            type="textarea"
            :rows="5"
            :disabled="true"
          ></el-input>
        </BaFormItem>
        
        <BaFormItem label="附件内容">
          <div v-if="info.annexLists && info.annexLists.length > 0">
            <el-alert
              title="注: 1.图片:10MB,支持JPG,PNG格式; 2.视频:10MB,支持MP4格式"
              type="error"
              :closable="false"
            ></el-alert>
            <br />
            <el-tabs
              v-model="active"
              type="card"
              class=""
              :closable="false"
            >
              <el-tab-pane
                v-for="(item, index) in info.annexLists"
                :key="index"
                :label="dictMsgType[item.msgtype].name"
                :name="index"
              >
                <div v-if="item.msgtype === 'image'">
                  <BaImage :src="item.image.picUrl" fit="cover" style="width: 200px; height: 200px;"></BaImage>
                </div>
                <div v-else-if="item.msgtype === 'video'">
                  <video :src="item.video.videoUrl" controls width="100%"></video>
                </div>
                <div v-else-if="item.msgtype === 'link'">
                  <div class="mb10">
                    <strong>链接标题:</strong> {{ item.link.title }}
                  </div>
                  <div class="mb10">
                    <strong>链接描述:</strong> {{ item.link.desc }}
                  </div>
                  <div class="mb10">
                    <strong>链接封面:</strong>
                    <BaImage v-if="item.link.picUrl" :src="item.link.picUrl" fit="cover" class="mt10"></BaImage>
                  </div>
                  <div class="mb10">
                    <strong>链接地址:</strong>
                    <a :href="item.link.url" target="_blank" class="text-blue-500">{{ item.link.url }}</a>
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
          <div v-else class="g-tip">无附件</div>
        </BaFormItem>
        
      </div>
    </div>
  
  </div>
</template>
