<script setup lang="ts">
import { getList } from './api'
// import * as api from './api'

defineProps({
  type: {
    type: String,
    default: '0', // 当前状态(0:待添加；1:待通过;2:已通过)
  },
})
</script>

<template>
  <div class="pad10 bg-(--BgBlack9) h100">
    <PullRefreshScrollLoadList
      ref="loadList"
      :request="getList"
      :params="{ currentState: $props.type }"
      :dealDataFun="(data, { total }) => $emit('updateNum', total)"
      :isQuery="false"
      :disabled="false">
      <template v-slot="{ item }">
        <div class="flex justify-between border-b border-(--BorderBlack10)">
          <div class="w-[30%] flex-none">
            {{ item.phoneNumber }}
          </div>
          <div class="">
            {{ item.customerName }}
          </div>
          <div class="--Color" @click="($copyText(item.phoneNumber), ww.navigateToAddCustomer())">复制</div>
        </div>
      </template>
    </PullRefreshScrollLoadList>
  </div>
</template>

<style lang="less" scoped></style>
