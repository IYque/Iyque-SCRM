<script setup lang="js">
const props = defineProps({
  data: {
    type: Object,
    default: () => ({})
  }
})
</script>

<template>
  <div class="g-card">
    <div class="g-card-title">
      二维码
      <div class="desc">适用于微信内部生态、线上或线下海报等场景，不适用于微信外部等公域平台</div>
    </div>
    <div class="ac">
      <BaImage :src="data.h5QrUrl" style="width: 160px"></BaImage>
      <el-button text @click="downloadBlob(data.h5QrUrl, data.name + '.png', 'image')">下载</el-button>
    </div>
  </div>
  <div class="g-card">
    <div class="g-card-title">
      H5链接
      <div class="desc">适用于微信内部生态、活动分享或按钮等场景，不适用于微信外部等公域平台</div>
    </div>
    <div class="flex">
      <el-input v-model="data.h5Url" disabled></el-input>
      <el-button class="ml10" @click="$copyText(data.h5Url)" text>复制</el-button>
    </div>
  </div>
</template>

<style lang="scss" scoped></style>
