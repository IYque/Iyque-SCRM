<script setup lang="ts">
import { getList, save } from './api'
// import * as api from './api'

defineProps({
  type: {
    type: String,
    default: '0', // 当前状态(0:待添加；1:待通过;2:已通过)
  },
})

const $emit = defineEmits(['change', 'refresh'])
const loadListRef = ref()
async function add(item) {
  if (!item.phoneNumber) {
    return $sdk.msgError('客户手机号不能为空')
  }
  await $sdk.copyText(item.phoneNumber)
  $sdk.loading()
  try {
    await save({ id: item.id, currentState: 1 })
    if (item.currentState == 0) {
      loadListRef.value.getList(1)
      $emit('refresh')
    }
  } catch (error) {
    $sdk.closeMsg()
    return
  }
  if (navigator.userAgent.includes('Mobile')) {
    // 跳转到添加客户页面
    ww.navigateToAddCustomer()
  } else {
    $sdk.msgSuccess('复制成功，请前往手机端添加客户')
  }
}
defineExpose({ loadListRef })
</script>

<template>
  <PullRefreshScrollLoadList
    ref="loadListRef"
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
        <div class="--Color" @click="add(item)" v-if="item.currentState != 2">复制</div>
      </div>
    </template>
  </PullRefreshScrollLoadList>
</template>

<style lang="less" scoped></style>
