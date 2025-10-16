<script setup lang="ts">
import { getDetail } from './api'
const form = ref({})

const $route = useRoute()
// 获取详情
;(function getDetailFun(id = $route.query?.id) {
  if (!id) return
  getDetail(id).then(({ data }) => {
    Object.assign(form.value, data)
  })
})()
</script>

<template>
  <div class="H5Marketing relative">
    <!-- H5底图 -->
    <img class="size-[100%] --RadiusSmall --BorderBlack11" :src="form.backgroundUrl" fit="fill" />

    <!-- 客服控件,链接控件 -->
    <div
      class="h5Card fixed bottom-[20px] w-[90%] mx-[5%] g-card --BorderBlack9 --RadiusSmall --BgBlack9 !overflow-visible"
      v-if="[3, 1].includes(form.controlType)">
      <div class="truncate weight5 font16">{{ form.title }}</div>
      <div class="truncate my-[15px]">{{ form.titleSub }}</div>
      <a :href="form.controlUrl">
        <div class="bgWhite text-center text-[red] weight5 pad10 --RadiusSmall">{{ form.guideTip }}</div>
      </a>
    </div>

    <!-- 活码控件 -->
    <div
      class="h5Card fixed bottom-[20px] w-[90%] mx-[5%] g-card --BorderBlack9 --RadiusSmall --BgBlack9 !overflow-visible"
      v-if="form.controlType == 2">
      <div class="mr10 flex items-center gap10">
        <BaImage class="size-[76px] pad5 bgWhite flex-none --RadiusSmall" :src="form.controlUrl" alt="" />
        <div>
          <div class="truncate weight5 font16">{{ form.title }}</div>
          <div class="truncate mt15">{{ form.titleSub }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.h5Card {
  box-shadow: 0 0 12px rgba(0, 0, 0, 0.1);
}
</style>
