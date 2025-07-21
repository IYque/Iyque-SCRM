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
  <PullRefreshScrollLoadList
    ref="loadList"
    :request="getList"
    :params="{ currentState: $props.type }"
    :dealDataFun="({ total }) => $emit('change', total)">
    <template #item="{ item }">
      <div class="h-[60px] bgWhite pad20 flex justify-between items-center border-t border-(--BorderBlack10)">
        <div class="w-[40%] flex-none truncate">
          {{ item.phoneNumber || '-' }}
        </div>
        <div class="flex-auto truncate">
          {{ item.customerName || '-' }}
        </div>
        <div class="--Color" @click="($sdk.copyText(item.phoneNumber), ww.navigateToAddCustomer())">复制</div>
      </div>
    </template>
  </PullRefreshScrollLoadList>
</template>

<style lang="less" scoped></style>
